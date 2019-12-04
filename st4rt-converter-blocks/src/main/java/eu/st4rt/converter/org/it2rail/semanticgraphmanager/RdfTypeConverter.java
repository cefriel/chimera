package eu.st4rt.converter.org.it2rail.semanticgraphmanager;

import java.beans.PropertyDescriptor;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class RdfTypeConverter {
	
	public static Object convert(PropertyDescriptor descriptor, Object value) {
		
		Object _result = null;
		
	    String descClass = descriptor.getPropertyType().getSimpleName();
	    String objClass = value.getClass().getSimpleName();
	    
	    //System.out.println("Called convert for property descr "+descriptor+" with value "+value);
	    
	    if(descClass.equalsIgnoreCase(objClass)) _result =  value;
	    
	    else if(descClass.equalsIgnoreCase("String") && objClass.equals("Float")) _result = String.valueOf(value);
	    else if(descClass.equalsIgnoreCase("Float") && objClass.equals("String")) _result = Float.valueOf((String)value);
	    
	    else if(descClass.equalsIgnoreCase("String") && objClass.equals("Integer")) _result = String.valueOf(value);
        else if(descClass.equalsIgnoreCase("Integer") && objClass.equals("String")) _result = Integer.valueOf((String)value);
        	
		
		return _result;
		
	}
	
	public static Object convert(AccessibleObject method, Object value ) {
	    Object _result = null;
        System.out.println("Called convert for accessible obj "+method+" with value "+value);

        String parameterType = null;
        String objClass = value.getClass().getName();
        
        if (method instanceof Field) {
           parameterType = ((Field)method).getType().getName();
        }
        else if (method instanceof Method) {
	        parameterType = ((Method)method).getParameterTypes()[0].getName();
	    }
        _result =  value;

        if(parameterType.equalsIgnoreCase("java.lang.String") && objClass.equals("java.lang.Float")) _result = String.valueOf(value);
        else if(parameterType.equalsIgnoreCase("java.lang.Float") && objClass.equals("java.lang.String")) _result = Float.valueOf((String)value);
        
        else if(parameterType.equalsIgnoreCase("java.lang.String") && objClass.equals("java.lang.Integer")) _result = String.valueOf(value);
        else if(parameterType.equalsIgnoreCase("java.lang.Integer") && objClass.equals("java.lang.String")) _result = Integer.valueOf((String)value);
        
        
	    return _result;
	}

	
}
