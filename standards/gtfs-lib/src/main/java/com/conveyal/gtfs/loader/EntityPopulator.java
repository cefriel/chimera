package com.conveyal.gtfs.loader;

import com.conveyal.gtfs.error.NewGTFSErrorType;
import com.conveyal.gtfs.model.*;
import com.conveyal.gtfs.storage.StorageException;
import gnu.trove.map.TObjectIntMap;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeParseException;

import static com.conveyal.gtfs.model.Entity.INT_MISSING;

/**
 * For now we will copy all available fields into Java model objects.
 *
 * We could also have entity cursors with accessor functions that move along the results, and only look at
 * the fields we need. We could even select only the required fields from the backend database but that gets
 * messy. Anecdotal evidence suggests this would give about a 1/3 speedup. But other observations show no speedup at all.
 * The main benefit would be grabbing arbitrary extension columns.
 *
 * TODO associate EntityPopulator more closely with Entity types and Table instances, so you can get one from the other.
 * e.g. getEntityPopulator() and getTableSpec() on Entity classes.
 *
 * TODO maybe instantiate EntityCreators with a columnForName map to avoid passing them around and make them implement Iterable<T>
 * Could even initialize with the resultSet and call it a Factory
 *
 * // FIXME URLs?
 *
 * This might also be useable with Commons DBUtils as a result row processor.
 *
 * // FIXME add this interface to Entity itself. And add reader / writer functions to entity as well?
 */
public interface EntityPopulator<T> {

    public T populate (ResultSet results, TObjectIntMap<String> columnForName) throws SQLException;

    public static final EntityPopulator<Agency> AGENCY = (result, columnForName) -> {
        Agency agency = new Agency();
        agency.agency_id       = getStringIfPresent(result, "agency_id", columnForName);
        agency.agency_name     = getStringIfPresent(result, "agency_name", columnForName);
        agency.agency_url      = getUrlIfPresent   (result, "agency_url", columnForName);
        agency.agency_timezone = getStringIfPresent(result, "agency_timezone", columnForName);
        agency.agency_lang     = getStringIfPresent(result, "agency_lang", columnForName);
        agency.agency_phone    = getStringIfPresent(result, "agency_phone", columnForName);
        agency.agency_fare_url = getUrlIfPresent   (result, "agency_fare_url", columnForName);
        agency.agency_email    = getStringIfPresent(result, "agency_email", columnForName);
        agency.agency_branding_url = null; // FIXME
        return agency;
    };

    public static final EntityPopulator<Calendar> CALENDAR = (result, columnForName) -> {
        Calendar calendar = new Calendar();
        calendar.service_id = getStringIfPresent(result, "service_id", columnForName);
        calendar.start_date = getDateIfPresent  (result, "start_date", columnForName);
        calendar.end_date   = getDateIfPresent  (result, "end_date",   columnForName);
        calendar.monday     = getIntIfPresent   (result, "monday",     columnForName);
        calendar.tuesday    = getIntIfPresent   (result, "tuesday",    columnForName);
        calendar.wednesday  = getIntIfPresent   (result, "wednesday",  columnForName);
        calendar.thursday   = getIntIfPresent   (result, "thursday",   columnForName);
        calendar.friday     = getIntIfPresent   (result, "friday",     columnForName);
        calendar.saturday   = getIntIfPresent   (result, "saturday",   columnForName);
        calendar.sunday     = getIntIfPresent   (result, "sunday",     columnForName);
        return calendar;
    };

    public static final EntityPopulator<CalendarDate> CALENDAR_DATE = (result, columnForName) -> {
        CalendarDate calendarDate   = new CalendarDate();
        calendarDate.service_id     = getStringIfPresent(result, "service_id",     columnForName);
        calendarDate.date           = getDateIfPresent  (result, "date",           columnForName);
        calendarDate.exception_type = getIntIfPresent   (result, "exception_type", columnForName);
        return calendarDate;
    };

    public static final EntityPopulator<Route> ROUTE = (result, columnForName) -> {
        Route route = new Route();
        route.route_id         = getStringIfPresent(result, "route_id",           columnForName);
        route.agency_id        = getStringIfPresent(result, "agency_id",          columnForName);
        route.route_short_name = getStringIfPresent(result, "route_short_name",   columnForName);
        route.route_long_name  = getStringIfPresent(result, "route_long_name",    columnForName);
        route.route_desc       = getStringIfPresent(result, "route_desc",         columnForName);
        route.route_type       = getIntIfPresent   (result, "route_type",         columnForName);
        route.route_color      = getStringIfPresent(result, "route_color",        columnForName);
        route.route_text_color = getStringIfPresent(result, "route_text_color",   columnForName);
        route.route_url          = getUrlIfPresent (result, "route_url",          columnForName);;
        route.route_branding_url = getUrlIfPresent (result, "route_branding_url", columnForName);;
        return route;
    };

    public static final EntityPopulator<Stop> STOP = (result, columnForName) -> {
        Stop stop = new Stop();
        stop.stop_id        = getStringIfPresent(result, "stop_id",        columnForName);
        stop.stop_code      = getStringIfPresent(result, "stop_code",      columnForName);
        stop.stop_name      = getStringIfPresent(result, "stop_name",      columnForName);
        stop.stop_desc      = getStringIfPresent(result, "stop_desc",      columnForName);
        stop.stop_lat       = getDoubleIfPresent(result, "stop_lat",       columnForName);
        stop.stop_lon       = getDoubleIfPresent(result, "stop_lon",       columnForName);
        stop.zone_id        = getStringIfPresent(result, "zone_id",        columnForName);
        stop.parent_station = getStringIfPresent(result, "parent_station", columnForName);
        stop.stop_timezone  = getStringIfPresent(result, "stop_timezone",  columnForName);
        stop.stop_url       = getUrlIfPresent   (result, "stop_url",       columnForName);
        stop.location_type  = getIntIfPresent   (result, "location_type",  columnForName);
        stop.wheelchair_boarding = Integer.toString(getIntIfPresent(result, "wheelchair_boarding", columnForName));
        return stop;
    };

    public static final EntityPopulator<Trip> TRIP = (result, columnForName) -> {
        Trip trip = new Trip();
        trip.trip_id         = getStringIfPresent(result, "trip_id", columnForName);
        trip.route_id        = getStringIfPresent(result, "route_id", columnForName);
        trip.service_id      = getStringIfPresent(result, "service_id", columnForName);
        trip.trip_headsign   = getStringIfPresent(result, "trip_headsign", columnForName);
        trip.trip_short_name = getStringIfPresent(result, "trip_short_name", columnForName);
        trip.block_id        = getStringIfPresent(result, "block_id", columnForName);
        trip.shape_id        = getStringIfPresent(result, "shape_id", columnForName);
        trip.direction_id    = getIntIfPresent   (result, "direction_id", columnForName);
        trip.bikes_allowed   = getIntIfPresent   (result, "bikes_allowed", columnForName);
        trip.wheelchair_accessible = getIntIfPresent(result, "wheelchair_accessible", columnForName);
        return trip;
    };

    public static final EntityPopulator<ShapePoint> SHAPE_POINT = (result, columnForName) -> {
        ShapePoint shapePoint = new ShapePoint();
        shapePoint.shape_id     = getStringIfPresent(result, "shape_id", columnForName);
        shapePoint.shape_pt_lat = getDoubleIfPresent(result, "shape_pt_lat", columnForName);
        shapePoint.shape_pt_lon = getDoubleIfPresent(result, "shape_pt_lon", columnForName);
        shapePoint.shape_pt_sequence = getIntIfPresent(result, "shape_pt_sequence", columnForName);
        shapePoint.shape_dist_traveled = getDoubleIfPresent(result, "shape_dist_traveled", columnForName);
        return shapePoint;
    };

    public static final EntityPopulator<StopTime> STOP_TIME = (result, columnForName) -> {
        StopTime stopTime = new StopTime();
        stopTime.trip_id        = getStringIfPresent(result, "trip_id", columnForName);
        stopTime.arrival_time   = getIntIfPresent   (result, "arrival_time", columnForName);
        stopTime.departure_time = getIntIfPresent   (result, "departure_time", columnForName);
        stopTime.stop_id        = getStringIfPresent(result, "stop_id", columnForName);
        stopTime.stop_sequence  = getIntIfPresent   (result, "stop_sequence", columnForName);
        stopTime.stop_headsign  = getStringIfPresent(result, "stop_headsign", columnForName);
        stopTime.pickup_type    = getIntIfPresent   (result, "pickup_type", columnForName);
        stopTime.drop_off_type  = getIntIfPresent   (result, "drop_off_type", columnForName);
        stopTime.timepoint      = getIntIfPresent   (result, "timepoint", columnForName);
        stopTime.shape_dist_traveled = getDoubleIfPresent(result, "shape_dist_traveled", columnForName);
        return stopTime;
    };

    // The reason we're passing in the columnForName map is that resultSet.getX(columnName) throws an exception
    // when the column is not present.
    // Exceptions should only be used in exceptional circumstances (ones that should be logged as errors).
    // Conceivably we could iterate over the fields present using ResultSetMetaData and set the object fields only
    // for those fields present. Or we could create cursor objects that allow accessing the fields of the ResultSet
    // in a typed way. Those cursor objects would make their own columnForName map when constructed.

    public static String getStringIfPresent (ResultSet resultSet, String columnName,
                                             TObjectIntMap<String> columnForName) throws SQLException {
        int columnIndex = columnForName.get(columnName);
        if (columnIndex == 0) return null;
        else return resultSet.getString(columnIndex);
    }

    public static LocalDate getDateIfPresent (ResultSet resultSet, String columnName,
                                             TObjectIntMap<String> columnForName) throws SQLException {
        int columnIndex = columnForName.get(columnName);
        if (columnIndex == 0) return null;
        String dateString = resultSet.getString(columnIndex);
        if (dateString == null) return null;
        try {
            LocalDate localDate = LocalDate.parse(dateString, DateField.GTFS_DATE_FORMATTER);
            return localDate;
        } catch (DateTimeParseException dtpex) {
            // We're reading out of the database here, not loading from GFTS CSV, so just return null for bad values.
            return null;
        }
    }

    public static URL getUrlIfPresent (ResultSet resultSet, String columnName,
                                       TObjectIntMap<String> columnForName) throws SQLException {
        int columnIndex = columnForName.get(columnName);
        if (columnIndex == 0) return null;
        try {
            URL url = new URL(resultSet.getString(columnIndex));
            return url;
        } catch (MalformedURLException e) {
            return null;
        }
    }

    static double getDoubleIfPresent (ResultSet resultSet, String columnName,
                                      TObjectIntMap<String> columnForName) throws SQLException {
        int columnIndex = columnForName.get(columnName);
        if (columnIndex == 0) return Entity.DOUBLE_MISSING;
        double value = resultSet.getDouble(columnIndex);
        if (resultSet.wasNull()) return Entity.DOUBLE_MISSING;
        else return value;
    }

    static int getIntIfPresent (ResultSet resultSet, String columnName,
                                TObjectIntMap<String> columnForName) throws SQLException {
        int columnIndex = columnForName.get(columnName);
        if (columnIndex == 0) return Entity.INT_MISSING;
        int value = resultSet.getInt(columnIndex);
        if (resultSet.wasNull()) return Entity.INT_MISSING;
        else return value;
    }

}
