/*
 * Copyright (c) 2009-2015 Clark & Parsia, LLC. <http://www.clarkparsia.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.complexible.common.openrdf.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.BasicParserSettings;

import com.complexible.common.openrdf.util.ModelBuildingRDFHandler;
import com.complexible.common.openrdf.util.RDFByteSource;
import com.google.common.base.Charsets;


/**
 * <p>Support for IO for {@link Model models}/</p>
 *
 * @author  Michael Grove
 * @since   4.0
 * @version 4.0
 */
public final class ModelIO {
	public static final String DEFAULT_BASE_URI = "http://openrdf.clarkparsia.com/";

	private ModelIO() {
		throw new AssertionError();
	}

	/**
	 * Read an RDF graph from the specified file
	 * @param theFile	the file to read from
	 * @return			the RDF graph contained in the file
	 *
	 * @throws IOException			if there was an error reading from the file
	 * @throws RDFParseException	if the RDF could not be parsed
	 */
	public static Model read(final Path theFile) throws IOException, RDFParseException {
		return read(theFile, Rio.getParserFormatForFileName(theFile.getFileName().toString()).orElse(RDFFormat.TURTLE));
	}

	public static Model read(final Path theFile, final RDFFormat theFormat) throws IOException, RDFParseException {
		return read(new InputStreamReader(Files.newInputStream(theFile), getCharset(theFormat).orElse(Charsets.UTF_8)),
		            theFormat,
		            DEFAULT_BASE_URI);
	}

	private static Optional<Charset> getCharset(final RDFFormat theFormat) {
		return theFormat.hasCharset() ? Optional.of(theFormat.getCharset()) : Optional.empty();
	}

	public static Model read(final RDFByteSource theSource) throws IOException, RDFParseException {
		return read(theSource.asCharSource(getCharset(theSource.getFormat()).orElse(Charsets.UTF_8)).openStream(),
	                theSource.getFormat(),
	                theSource.getBaseURI());
	}

	/**
	 * Read an RDF graph from the stream using the specified format
	 * @param theInput the stream to read from
	 * @param theFormat the format the data is in
	 * @return the graph represented by the data from the stream
	 * @throws IOException if there is an error while reading
	 * @throws RDFParseException if there is an error while trying to parse the data as the specified format
	 */
	public static Model read(InputStream theInput, RDFFormat theFormat) throws IOException, RDFParseException {
		return read(new InputStreamReader(theInput, getCharset(theFormat).orElse(Charsets.UTF_8)), theFormat, DEFAULT_BASE_URI);
	}


	/**
	 * Read an RDF graph from the stream using the specified format
	 * @param theInput the stream to read from
	 * @param theFormat the format the data is in
	 * @param theBase the base url used for parsing
	 * @return the graph represented by the data from the stream
	 * @throws IOException if there is an error while reading
	 * @throws RDFParseException if there is an error while trying to parse the data as the specified format
	 */
	public static Model read(InputStream theInput, RDFFormat theFormat, final String theBase) throws IOException, RDFParseException {
		return read(new InputStreamReader(theInput, getCharset(theFormat).orElse(Charsets.UTF_8)), theFormat, theBase);
	}

	/**
	 * Read an RDF graph from the Reader using the specified format.  The reader is closed after parsing.
	 *
	 * @param theInput  the reader to read from
	 * @param theFormat the format the data is in
	 * @param theBase   the base url for parsing
	 *
	 * @return the graph represented by the data from the stream
	 *
	 * @throws IOException if there is an error while reading
	 * @throws RDFParseException if there is an error while trying to parse the data as the specified format
	 */
	public static Model read(final Reader theInput, final RDFFormat theFormat, final String theBase) throws IOException, RDFParseException {
		RDFParser aParser = Rio.createParser(theFormat);

		aParser.getParserConfig().set(BasicParserSettings.VERIFY_DATATYPE_VALUES, false);
		aParser.getParserConfig().set(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES, false);
		aParser.getParserConfig().set(BasicParserSettings.NORMALIZE_DATATYPE_VALUES, false);
		aParser.getParserConfig().set(BasicParserSettings.PRESERVE_BNODE_IDS, true);

		ModelBuildingRDFHandler aHandler = new ModelBuildingRDFHandler();

		aParser.setRDFHandler(aHandler);

		try {
			aParser.parse(theInput, theBase);
		}
		catch (RDFHandlerException e) {
			throw new RDFParseException(e);
		}
		finally {
			if (theInput != null) {
				theInput.close();
			}
		}

		return aHandler.getModel();
	}

	/**
	 * Read an RDF graph from the Reader using the specified format.  The reader is closed after parsing.
	 *
	 * @param theHandler the handler for the results of reading the data
	 * @param theInput the reader to read from
	 * @param theFormat the format the data is in
	 * @param theBase the base url for parsing
	 * @throws IOException if there is an error while reading
	 * @throws RDFParseException if there is an error while trying to parse the data as the specified format
	 */
	public static void read(RDFHandler theHandler, Reader theInput, RDFFormat theFormat, String theBase) throws IOException, RDFParseException {
		RDFParser aParser = Rio.createParser(theFormat);

		aParser.getParserConfig().set(BasicParserSettings.VERIFY_DATATYPE_VALUES, false);
		aParser.getParserConfig().set(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES, false);
		aParser.getParserConfig().set(BasicParserSettings.NORMALIZE_DATATYPE_VALUES, false);
		aParser.getParserConfig().set(BasicParserSettings.PRESERVE_BNODE_IDS, true);

		aParser.setRDFHandler(theHandler);

		try {
			aParser.parse(theInput, theBase);
		}
		catch (RDFHandlerException e) {
			throw new RDFParseException(e);
		}
		finally {
			if (theInput != null) {
				theInput.close();
			}
		}
	}
	/**
	 * Write the contents of the Graph to the stream in the specified RDF format
	 *
	 * @param theGraph  the graph to write
	 * @param theStream the stream to write to
	 * @param theFormat the RDF format to write in
	 *
	 * @throws IOException thrown if there is an error while writing
	 */
	public static void write(final Model theGraph, final OutputStream theStream, final RDFFormat theFormat) throws IOException {
		write(theGraph, new OutputStreamWriter(theStream), theFormat);
	}

	/**
	 * Write the contents of the Graph to the writer in the specified RDF format
	 *
	 * @param theGraph  the graph to write
	 * @param theWriter the stream to write to
	 * @param theFormat the RDF format to write in
	 *
	 * @throws IOException thrown if there is an error while writing
	 */
	public static void write(final Model theGraph, final Writer theWriter, final RDFFormat theFormat) throws IOException {
		write(theGraph, Rio.createWriter(theFormat, theWriter));
	}

	/**
	 * Write the Graph to a String in the given format
	 * @param theGraph	the graph to write
	 * @param theFormat	the RDF format to write in
	 *
	 * @return			the Graph as RDF
	 */
	public static String toString(final Model theGraph, final RDFFormat theFormat) {
		try {
			StringWriter aStringWriter = new StringWriter();
			write(theGraph, aStringWriter, theFormat);
			return aStringWriter.toString();
		}
		catch (IOException e) {
			// this should not happen w/ a StringWriter
			throw new RuntimeException(e);
		}
	}

	private static void write(final Model theGraph, final RDFWriter theWriter) throws IOException {
		try {
			theWriter.startRDF();

			for (Statement aStmt : theGraph) {
				theWriter.handleStatement(aStmt);
			}

			theWriter.endRDF();
		}
		catch (RDFHandlerException e) {
			throw new IOException(e);
		}
	}
}
