package eu.st4rt.converter.exception;

/**
 * Created by amalirova on 14.3.18.
 */
public class ConvertorEmptyOutputClassesException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3033792277676490757L;

	public ConvertorEmptyOutputClassesException(Class inputClass) {
        super("ST4RT convertor list of output classes for input " + inputClass.getCanonicalName() + " is empty.");
    }
}
