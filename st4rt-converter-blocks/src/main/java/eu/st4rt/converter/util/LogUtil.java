package eu.st4rt.converter.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {

    private static final Logger CONVERSION_DURATION_LOGGER = LoggerFactory.getLogger("conversion_duration_logger");


    public static void logDuration(String message, long start) {
        CONVERSION_DURATION_LOGGER.debug(message + (System.currentTimeMillis() - start) + " ms");
    }
}
