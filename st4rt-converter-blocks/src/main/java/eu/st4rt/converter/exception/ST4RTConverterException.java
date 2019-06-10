package eu.st4rt.converter.exception;

/**
 * Created by amalirova on 16.3.18.
 */
public class ST4RTConverterException extends Exception {
    private static final long serialVersionUID = 1L;

    public ST4RTConverterException() {
    }

    public ST4RTConverterException(String message) {
        super(message);
    }

    public ST4RTConverterException(Throwable cause) {
        super(cause);
    }

    public ST4RTConverterException(String message, Throwable cause) {
        super(message, cause);
    }
}
