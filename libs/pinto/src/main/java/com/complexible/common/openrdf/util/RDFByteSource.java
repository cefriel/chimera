package com.complexible.common.openrdf.util;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rdf4j.rio.RDFFormat;

import com.google.common.io.ByteSource;

/**
 * <p>A {@link ByteSource} whose contents is RDF serialized in {@link RDFFormat some format}.</p>
 *
 * @author  Michael Grove
 * @since   4.0
 * @version 4.0
 */
public abstract class RDFByteSource extends ByteSource {

	/**
	 * Return the RDF format used by the source
	 * @return  the format
	 */
	public abstract RDFFormat getFormat();

	/**
	 * Return the base uri that should be used when parsing this source
	 * @return the base
	 */
	public String getBaseURI() {
		return "http://openrdf.clarkparsia.com";
	}

	public static RDFByteSource create(final ByteSource theSource, final RDFFormat theFormat) {
		return new RDFByteSource() {
			@Override
			public RDFFormat getFormat() {
				return theFormat;
			}

			@Override
			public InputStream openStream() throws IOException {
				return theSource.openStream();
			}
		};
	}
}
