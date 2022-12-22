package com.cefriel.util;

import com.cefriel.template.io.Formatter;
import com.cefriel.template.io.rdf.RDFFormatter;
import com.cefriel.template.io.xml.XMLFormatter;

import static com.cefriel.util.Utils.*;

public class MapTComponentUtils {

    public static Formatter getFormatter(String format) {
        if (isSupportedRDFFormat(format)) {
            return new RDFFormatter(getRDFFormat(format));
        }
        else if (format.equals("xml")) {
            return new XMLFormatter();
        }
        else
            throw new IllegalArgumentException("No formatter for format: " + format + "available");
    }
}
