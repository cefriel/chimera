package eu.st4rt.converter.exception;

import st4rt.convertor.empire.annotation.SupportsRdfId;

/**
 * Created by amalirova on 24.1.18.
 */
public class RdfIdSupportException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4082956845829936425L;

	public RdfIdSupportException(Class clazz) {
        super("Class " + clazz.getName() + " does not implements " + SupportsRdfId.class.getName());
    }
}
