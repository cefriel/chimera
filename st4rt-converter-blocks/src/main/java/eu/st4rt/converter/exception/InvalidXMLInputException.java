package eu.st4rt.converter.exception;

/**
 * Created by amalirova on 24.1.18.
 */
public class InvalidXMLInputException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8047878015600162535L;

	public InvalidXMLInputException() {
    }

    public InvalidXMLInputException(String message) {
        super(message);
    }
}
