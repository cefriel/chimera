package st4rt.convertor.empire.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Mohammad Mehdi Pourhashem Kallehbasti
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Queries {
	
	/**
	 * @return the list of @Query associated to a Java class
	 */
	public Query[] value();
}