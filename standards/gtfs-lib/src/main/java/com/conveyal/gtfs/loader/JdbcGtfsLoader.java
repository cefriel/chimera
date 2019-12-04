package com.conveyal.gtfs.loader;

import com.conveyal.gtfs.error.NewGTFSError;
import com.conveyal.gtfs.error.SQLErrorStorage;
import com.conveyal.gtfs.storage.StorageException;
import com.csvreader.CsvReader;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.*;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.conveyal.gtfs.error.NewGTFSErrorType.*;
import static com.conveyal.gtfs.model.Entity.human;
import static com.conveyal.gtfs.util.Util.randomIdString;

/**
 * This class loads CSV tables from zipped GTFS into an SQL relational database management system with a JDBC driver.
 * By comparing the GTFS specification for a table with the headers present in the GTFS CSV, it dynamically builds up
 * table definitions and SQL statements to interact with those tables. It retains all columns present in the GTFS,
 * including optional columns, known extensions, and unrecognized proprietary extensions.
 *
 * It supports several ways of putting the data into the tables: batched prepared inserts or loading from an
 * intermediate tab separated text file.
 *
 * Our previous approach involved loading GTFS CSV tables into Java objects and then using an object-relational mapping
 * to put those objects into a database. In that case a fixed number of fields are represented. If the GTFS feed
 * contains extra proprietary fields, they are lost immediately on import. The Java model objects must contain fields
 * for all GTFS columns that will ever be retrieved, and memory and database space are required to represent all those
 * fields even when they are not present in a particular feed. The same would be true of a direct-to-database approach
 * if multiple feeds were loaded into the same tables: a "lowest common denominator" set of fields would need to be
 * selected. However, we create one set of tables per GTFS feed loaded into the same database and namespace them with
 * what the SQL spec calls "schemas".
 *
 * The structure of the CSV file and the data it contains are validated line by line during the load process. This
 * allows us to handle error recovery ourselves, recording detailed messages about the largest possible number of errors
 * rather than failing at the first error.
 *
 * This is important because of the generally long turnaround for GTFS publication, repair, and validation. If a newly
 * submitted feed fails to import because of a missing file, we don't want to report that single error to the feed
 * producer, only to discover and report additional errors when the repaired feed is re-submitted weeks later.
 *
 * The fact that existing libraries would abort a GTFS import upon encountering very common, recoverable errors was the
 * original motivation for creating gtfs-lib. Error recovery is all the more important when bulk-loading data into
 * database systems - the Postgres 'copy' import is particularly brittle and does not provide error messages that would
 * help the feed producer repair their feed.
 *
 * The validation performed during CSV loading includes:
 * - columns are present for all required fields
 * - all rows have the same number of fields as there are headers
 * - fields do not contain problematic characters
 * - field contents can be converted to the target data types and are in range
 * - TODO referential integrity
 */
public class JdbcGtfsLoader {

    public static final long INSERT_BATCH_SIZE = 500;
    private static final Logger LOG = LoggerFactory.getLogger(JdbcGtfsLoader.class);

    private String gtfsFilePath;
    protected ZipFile zip;

    private File tempTextFile;
    private PrintStream tempTextFileStream;
    private PreparedStatement insertStatement = null;

    private final DataSource dataSource;

    // These fields will be filled in once feed loading begins.
    private Connection connection;
    private String tablePrefix;
    private SQLErrorStorage errorStorage;

    public JdbcGtfsLoader(String gtfsFilePath, DataSource dataSource) {
        this.gtfsFilePath = gtfsFilePath;
        this.dataSource = dataSource;
    }


    // Hash to uniquely identify files.
    // We can't use CRC32, the probability of collision on 10k items is about 1%.
    // https://stackoverflow.com/a/1867252
    // http://preshing.com/20110504/hash-collision-probabilities/
    // On the full NL feed:
    // MD5 took 820 msec,    cabb18e43798f92c52d5d0e49f52c988
    // Murmur took 317 msec, 5e5968f9bf5e1cdf711f6f48fcd94355
    // SHA1 took 1072 msec,  9fb356af4be2750f20955203787ec6f95d32ef22

    // There appears to be no advantage to loading tables in parallel, as the whole loading process is I/O bound.
    public FeedLoadResult loadTables () {

        // This result object will be returned to the caller to summarize the feed and report any critical errors.
        FeedLoadResult result = new FeedLoadResult();

        try {
            // We get a single connection object and share it across several different methods.
            // This ensures that actions taken in one method are visible to all subsequent SQL statements.
            // If we create a schema or table on one connection, then access it in a separate connection, we have no
            // guarantee that it exists when the accessing statement is executed.
            connection = dataSource.getConnection();
            File gtfsFile = new File(gtfsFilePath);
            this.zip = new ZipFile(gtfsFilePath);
            // Generate a unique prefix that will identify this feed.
            // Prefixes ("schema" names) based on feed_id and feed_version get very messy, so we use random unique IDs.
            // We don't want to use an auto-increment numeric primary key because these need to be alphabetical.
            // Although ID collisions are theoretically possible, they are improbable in the extreme because our IDs
            // are long enough to have as much entropy as a UUID. So we don't really need to check for uniqueness and
            // retry in a loop.
            // TODO handle the case where we don't want any prefix.
            this.tablePrefix = randomIdString();
            result.uniqueIdentifier = tablePrefix;
            registerFeed(gtfsFile);
            // Include the dot separator in the table prefix.
            // This allows everything to work even when there's no prefix.
            this.tablePrefix += ".";
            this.errorStorage = new SQLErrorStorage(connection, tablePrefix, true);
            long startTime = System.currentTimeMillis();
            // Load each table in turn, saving some summary information about what happened during each table load
            result.agency = load(Table.AGENCY);
            result.calendar = load(Table.CALENDAR);
            result.calendarDates = load(Table.CALENDAR_DATES);
            result.fareAttributes = load(Table.FARE_ATTRIBUTES);
            result.fareRules = load(Table.FARE_RULES);
            result.feedInfo = load(Table.FEED_INFO);
            result.frequencies = load(Table.FREQUENCIES);
            result.routes = load(Table.ROUTES);
            result.shapes = load(Table.SHAPES);
            result.stops = load(Table.STOPS);
            result.stopTimes = load(Table.STOP_TIMES);
            result.transfers = load(Table.TRANSFERS);
            result.trips = load(Table.TRIPS);
            result.errorCount = errorStorage.getErrorCount();
            // This will commit and close the single connection that has been shared between all preceding load steps.
            errorStorage.commitAndClose();
            zip.close();
            LOG.info("Loading tables took {} sec", (System.currentTimeMillis() - startTime) / 1000);
        } catch (Exception ex) {
            // TODO catch exceptions separately while loading each table so load can continue, store in TableLoadResult
            LOG.error("Exception while loading GTFS file: {}", ex.toString());
            ex.printStackTrace();
            result.fatalException = ex.getMessage();
        }
        return result;
    }

    /**
     * Add a line to the list of loaded feeds showing that this feed has been loaded.
     * We used to inspect feed_info here so we could make our table prefix based on feed ID and version.
     * Now we just load feed_info like any other table.
     *         // Create a row in the table of loaded feeds for this feed
     * Really this is not just making the table prefix - it's loading the feed_info and should also calculate hashes.
     *
     * Originally we were flattening all feed_info files into one root-level table, but that forces us to drop any
     * custom fields in feed_info.
     */
    private void registerFeed (File gtfsFile) {

        // FIXME is this extra CSV reader used anymore? Check comment below.
        // First, inspect feed_info.txt to extract the ID and version.
        // We could get this with SQL after loading, but feed_info, feed_id and feed_version are all optional.
        CsvReader csvReader = getCsvReader(Table.FEED_INFO);
        String feedId = "", feedVersion = "";
        if (csvReader != null) {
            // feed_info.txt has been found and opened.
            try {
                csvReader.readRecord();
                // csvReader.get() returns the empty string for missing columns
                feedId = csvReader.get("feed_id");
                feedVersion = csvReader.get("feed_version");
            } catch (IOException e) {
                LOG.error("Exception while inspecting feed_info: {}", e);
            }
            csvReader.close();
        }

        try {
            HashCode md5 = Files.hash(gtfsFile, Hashing.md5());
            String md5Hex = md5.toString();
            HashCode sha1 = Files.hash(gtfsFile, Hashing.sha1());
            String shaHex = sha1.toString();
            Statement statement = connection.createStatement();
            // TODO try to get the feed_id and feed_version out of the feed_info table
            // statement.execute("select * from feed_info");

            // FIXME do the following only on databases that support schemas.
            // SQLite does not support them. Is there any advantage of schemas over flat tables?
            statement.execute("create schema " + tablePrefix);
            // TODO load more stuff from feed_info and essentially flatten all feed_infos from all loaded feeds into one table
            // This should include date range etc. Can we reuse any code from Table for this?
            // This makes sense since the file should only have one line.
            // current_timestamp seems to be the only standard way to get the current time across all common databases.
            // Record total load processing time?
            statement.execute("create table if not exists feeds (namespace varchar primary key, md5 varchar, " +
                    "sha1 varchar, feed_id varchar, feed_version varchar, filename varchar, loaded_date timestamp)");
            PreparedStatement insertStatement = connection.prepareStatement(
                    "insert into feeds values (?, ?, ?, ?, ?, ?, current_timestamp)");
            insertStatement.setString(1, tablePrefix);
            insertStatement.setString(2, md5Hex);
            insertStatement.setString(3, shaHex);
            insertStatement.setString(4, feedId.isEmpty() ? null : feedId);
            insertStatement.setString(5, feedVersion.isEmpty() ? null : feedVersion);
            insertStatement.setString(6, zip.getName());
            insertStatement.execute();
            connection.commit();
            LOG.info("Created new feed namespace: {}", insertStatement);
        } catch (Exception ex) {
            LOG.error("Exception while creating unique prefix for new feed: {}", ex.getMessage());
            DbUtils.closeQuietly(connection);
        }
    }

    /**
     * In GTFS feeds, all files are supposed to be in the root of the zip file, but feed producers often put them
     * in a subdirectory. This function will search subdirectories if the entry is not found in the root.
     * It records an error if the entry is in a subdirectory.
     * It then creates a CSV reader for that table if it's found.
     */
    private CsvReader getCsvReader (Table table) {
        final String tableFileName = table.name + ".txt";
        ZipEntry entry = zip.getEntry(tableFileName);
        if (entry == null) {
            // Table was not found, check if it is in a subdirectory.
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry e = entries.nextElement();
                if (e.getName().endsWith(tableFileName)) {
                    entry = e;
                    errorStorage.storeError(NewGTFSError.forTable(table, TABLE_IN_SUBDIRECTORY));
                    break;
                }
            }
        }
        if (entry == null) return null;
        try {
            InputStream zipInputStream = zip.getInputStream(entry);
            // Skip any byte order mark that may be present. Files must be UTF-8,
            // but the GTFS spec says that "files that include the UTF byte order mark are acceptable".
            InputStream bomInputStream = new BOMInputStream(zipInputStream);
            CsvReader csvReader = new CsvReader(bomInputStream, ',', Charset.forName("UTF8"));
            csvReader.readHeaders();
            return csvReader;
        } catch (IOException e) {
            LOG.error("Exception while opening zip entry: {}", e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This wraps the main internal table loader method to catch exceptions and figure out how many errors happened.
     */
    private TableLoadResult load (Table table) {
        // This object will be returned to the caller to summarize the contents of the table and any errors.
        TableLoadResult tableLoadResult = new TableLoadResult();
        int initialErrorCount = errorStorage.getErrorCount();
        try {
            tableLoadResult.rowCount = loadInternal(table);
        } catch (Exception ex) {
            tableLoadResult.fatalException = ex.getMessage();
        } finally {
            // Explicitly delete the tmp file now that load is finished (either success or failure).
            // Otherwise these multi-GB files clutter the drive.
            if (tempTextFile != null) {
                tempTextFile.delete();
            }
        }
        int finalErrorCount = errorStorage.getErrorCount();
        tableLoadResult.errorCount = finalErrorCount - initialErrorCount;
        return tableLoadResult;
    }

    /**
     * This function will throw any exception that occurs. Those exceptions will be handled by the outer load method.
     * @return number of rows that were loaded.
     */
    private int loadInternal (Table table) throws Exception {
        CsvReader csvReader = getCsvReader(table);
        if (csvReader == null) {
            // This GTFS table could not be opened in the zip, even in a subdirectory.
            if (table.isRequired()) errorStorage.storeError(NewGTFSError.forTable(table, MISSING_TABLE));
            return 0;
        }
        LOG.info("Loading GTFS table {}", table.name);
        // Use the Postgres text load format if we're connected to that DBMS.
        boolean postgresText = (connection.getMetaData().getDatabaseProductName().equals("PostgreSQL"));

        // TODO Strip out line returns, tabs in field contents.
        // By default the CSV reader trims leading and trailing whitespace in fields.
        // Build up a list of fields in the same order they appear in this GTFS CSV file.
        Field[] fields = new Field[csvReader.getHeaderCount()];
        Set<String> fieldsSeen = new HashSet<>();
        for (int h = 0; h < csvReader.getHeaderCount(); h++) {
            String header = sanitize(csvReader.getHeader(h));
            if (fieldsSeen.contains(header)) {
                errorStorage.storeError(NewGTFSError.forTable(table, DUPLICATE_HEADER).setBadValue(header));
                // TODO deal with missing (null) Field object below
                fields[h] = null;
            } else {
                fields[h] = table.getFieldForName(header);
                fieldsSeen.add(header);
            }
        }

        // Replace the GTFS spec Table with one representing the SQL table we will populate, with reordered columns.
        // FIXME this is confusing, we only create a new table object so we can call a couple of methods on it, all of which just need a list of fields.
        Table targetTable = new Table(tablePrefix + table.name, table.entityClass, table.required, fields);

        // NOTE H2 doesn't seem to work with schemas (or create schema doesn't work).
        // With bulk loads it takes 140 seconds to load the data and addditional 120 seconds just to index the stop times.
        // SQLite also doesn't support schemas, but you can attach additional database files with schema-like naming.
        // We'll just literally prepend feed indentifiers to table names when supplied.
        // Some databases require the table to exist before a statement can be prepared.
        targetTable.createSqlTable(connection);

        // TODO are we loading with or without a header row in our Postgres text file?
        if (postgresText) {
            // No need to output headers to temp text file, our SQL table column order exactly matches our text file.
            tempTextFile = File.createTempFile(targetTable.name, "text");
            tempTextFileStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(tempTextFile)));
            LOG.info("Loading via temporary text file at " + tempTextFile.getAbsolutePath());
        } else {
            insertStatement = connection.prepareStatement(targetTable.generateInsertSql());
            LOG.info(insertStatement.toString()); // Logs the SQL for the prepared statement
        }

        // When outputting text, accumulate transformed strings to allow skipping rows when errors are encountered.
        // One extra position in the array for the CSV line number.
        String[] transformedStrings = new String[fields.length + 1];

        while (csvReader.readRecord()) {
            // The CSV reader's current record is zero-based and does not include the header line.
            // Convert to a CSV file line number that will make more sense to people reading error messages.
            if (csvReader.getCurrentRecord() + 2 > Integer.MAX_VALUE) {
                errorStorage.storeError(NewGTFSError.forTable(table, TABLE_TOO_LONG));
                break;
            }
            int lineNumber = ((int) csvReader.getCurrentRecord()) + 2;
            if (lineNumber % 500_000 == 0) LOG.info("Processed {}", human(lineNumber));
            if (csvReader.getColumnCount() != fields.length) {
                String badValues = String.format("expected=%d; found=%d", fields.length, csvReader.getColumnCount());
                errorStorage.storeError(NewGTFSError.forLine(table, lineNumber, WRONG_NUMBER_OF_FIELDS, badValues));
                continue;
            }
            // The first field holds the line number of the CSV file. Prepared statement parameters are one-based.
            if (postgresText) transformedStrings[0] = Integer.toString(lineNumber);
            else insertStatement.setInt(1, lineNumber);
            for (int f = 0; f < fields.length; f++) {
                Field field = fields[f];
                String string = csvReader.get(f);
                if (string.isEmpty()) {
                    // TODO verify that CSV reader always returns empty strings, not nulls
                    if (field.isRequired()) {
                        errorStorage.storeError(NewGTFSError.forLine(table, lineNumber, MISSING_FIELD, field.name));
                    }
                    if (postgresText) transformedStrings[f + 1] = "\\N"; // Represents null in Postgres text format
                    // Adjust parameter index by two: indexes are one-based and the first one is the CSV line number.
                    else insertStatement.setNull(f + 2, field.getSqlType().getVendorTypeNumber());
                } else {
                    // Micro-benchmarks show it's only 4-5% faster to call typed parameter setter methods
                    // rather than setObject with a type code. I think some databases don't have setObject though.
                    // The Field objects throw exceptions to avoid passing the line number, table name etc. into them.
                    try {
                        // Validation and insertion step should probably happen separately.
                        // or the errors should not be signaled with exceptions.
                        // Perhaps we should not be converting any GTFS field values, and we
                        // should be saving it as-is in the database and converting upon load into our model objects.
                        if (postgresText) transformedStrings[f + 1] = field.validateAndConvert(string);
                        else field.setParameter(insertStatement, f + 2, string);
                    } catch (StorageException ex) {
                        // FIXME many exceptions don't have an error type
                        errorStorage.storeError(NewGTFSError.forLine(table, lineNumber, ex.errorType, ex.badValue));
                        if (postgresText) transformedStrings[f + 1] = "\\N"; // Represents null in Postgres text format
                        else insertStatement.setNull(f + 2, field.getSqlType().getVendorTypeNumber());
                        // FIXME should set transformedStrings or prepared statement param to null
                    }
                }
            }
            if (postgresText) {
                tempTextFileStream.printf(String.join("\t", transformedStrings));
                tempTextFileStream.print('\n');
            } else {
                insertStatement.addBatch();
                if (lineNumber % INSERT_BATCH_SIZE == 0) insertStatement.executeBatch();
            }
        }
        // Record number is zero based but includes the header record, which we don't want to count.
        // But if we are working with Postgres text file (without a header row) we have to add 1
        // Iteration over all rows has finished, so We are now one record past the end of the file.
        int numberOfRecordsLoaded = (int) csvReader.getCurrentRecord();
        if (postgresText) {
          numberOfRecordsLoaded = numberOfRecordsLoaded + 1;
        }
        csvReader.close();

        // Finalize loading the table, either by copying the pre-validated text file into the database (for Postgres)
        // or inserting any remaining rows (for all others).
        if (postgresText) {
            LOG.info("Loading into database table {} from temporary text file...", targetTable.name);
            tempTextFileStream.close();
            // Allows sending over network. This is only slightly slower than a local file copy.
            final String copySql = String.format("copy %s from stdin", targetTable.name);
            // FIXME we should be reading the COPY text from a stream in parallel, not from a temporary text file.
            InputStream stream = new BufferedInputStream(new FileInputStream(tempTextFile.getAbsolutePath()));
            // Our connection pool wraps the Connection objects, so we need to unwrap the Postgres connection interface.
            CopyManager copyManager = new CopyManager(connection.unwrap(BaseConnection.class));
            copyManager.copyIn(copySql, stream, 1024*1024);
            stream.close();
            // It is also possible to load from local file if this code is running on the database server.
            // statement.execute(String.format("copy %s from '%s'", table.name, tempTextFile.getAbsolutePath()));
        } else {
            insertStatement.executeBatch();
        }

        LOG.info("Indexing...");
        // We determine which columns should be indexed based on field order in the GTFS spec model table.
        // Not sure that's a good idea, this could use some abstraction. TODO getIndexColumns() on each table.
        String indexColumns = table.getIndexFields();
        // TODO verify referential integrity and uniqueness of keys
        // TODO create primary key and fall back on plain index (consider not null & unique constraints)
        // TODO use line number as primary key
        // Note: SQLITE requires specifying a name for indexes.
        String indexName = String.join("_", targetTable.name.replace(".", "_"), "idx");
        String indexSql = String.format("create index %s on %s (%s)", indexName, targetTable.name, indexColumns);
        //String indexSql = String.format("alter table %s add primary key (%s)", table.name, indexColumns);
        LOG.info(indexSql);
        connection.createStatement().execute(indexSql);
        // TODO add foreign key constraints, and recover recording errors as needed.


        // More indexing
        // TODO integrate with the above indexing code, iterating over a List<String> of index column expressions
        for (Field field : fields) {
            if (field.shouldBeIndexed()) {
                Statement statement = connection.createStatement();
                String sql = String.format("create index %s_%s_idx on %s (%s)", table.name, field.name, tablePrefix + table.name, field.name);
                LOG.info(sql);
                statement.execute(sql);
            }
        }

        LOG.info("Committing transaction...");
        connection.commit();
        LOG.info("Done.");
        return numberOfRecordsLoaded;
    }

    /**
     * Protect against SQL injection.
     * The only place we include arbitrary input in SQL is the column names of tables.
     * Implicitly (looking at all existing table names) these should consist entirely of
     * lowercase letters and underscores.
     *
     * TODO add a test including SQL injection text (quote and semicolon)
     */
    public String sanitize (String string) throws SQLException {
        String clean = string.replaceAll("[^\\p{Alnum}_]", "");
        if (!clean.equals(string)) {
            LOG.warn("SQL identifier '{}' was sanitized to '{}'", string, clean);
            if (errorStorage != null) {
                errorStorage.storeError(NewGTFSError.forFeed(COLUMN_NAME_UNSAFE, string));
            }
        }
        return clean;
    }

}
