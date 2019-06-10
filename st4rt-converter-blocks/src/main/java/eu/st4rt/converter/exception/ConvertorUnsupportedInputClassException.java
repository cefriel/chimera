package eu.st4rt.converter.exception;

/**
 * Created by amalirova on 24.1.18.
 */
public class ConvertorUnsupportedInputClassException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2912668371267604826L;

	public ConvertorUnsupportedInputClassException(String rootElementName) {
        super("ST4RT convertor unsupported root element exception: " + rootElementName);
    }
}
