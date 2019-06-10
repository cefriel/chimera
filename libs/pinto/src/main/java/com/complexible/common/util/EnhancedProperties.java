/*
 * Copyright (c) 2005-2016 Clark & Parsia, LLC. <http://www.clarkparsia.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.complexible.common.util;

import java.util.Properties;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;
import java.io.IOException;
import java.util.function.Function;
import java.util.function.Predicate;

import com.complexible.common.base.Copyable;
import com.google.common.collect.Maps;

/**
 * <p>Extends the java.util.Properties stuff to provide typed accessors to get property values as boolean, int, etc.
 * Also provides a way to get a property value as a list, or as a map of values.  And it does variable substitution on
 * property values.</p>
 * <p>
 * Given the following property file
 * </p>
 * {@code
 * some_boolean_property = true
 * some_integer_property = 42
 * some_property = some_value
 * some_list = one, two, three, four
 * some_other_property = ${some_property}/foo
 * some_map = key_a, key_b, key_c
 * key_a = a
 * key_b = b
 * key_c = c
 * }
 * <p>
 * {@link #getPropertyAsBoolean(String) getPropertyAsBoolean("some_boolean_property")} yields the boolean value {@code true}.
 * {@link #getPropertyAsInt(String) getPropertyAsInt("some_integer_property")} yields the integer {@code 42}.
 * {@link #getPropertyAsList(String) getPropertyAsList("some_list")} yields a {@code List<String>} with the values {@code "one", "two", "three", "four"}.
 * {@link #getPropertyAsMap(String) getPropertyAsMap("some_map")} yields a {@code Map<String, String>} with the key value pairs:
 * {@code key_a => a, key_b => b, key_c => c}.  Lastly, getting the property "some_other_property" yields the value "some_value/foo" via
 * variable substitution.</p>
 *
 * @author  Michael Grove
 * @since   1.0
 * @version 5.0
 */
public class EnhancedProperties extends Properties implements Copyable<EnhancedProperties> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1650700835243405747L;

	/**
	 * Create a new EnhancedProperties
	 */
	public EnhancedProperties() {
		super();
	}

	/**
	 * Create a new EnhancedProperties
	 * @param theInput the inputstream to load property data from
	 * @throws IOException thrown if there is an error reading properties data
	 */
	public EnhancedProperties(final InputStream theInput) throws IOException {
		super();

		load(theInput);
	}

	/**
	 * Copy constructor
	 * @param theProps the properties to copy from
	 */
	public EnhancedProperties(final Properties theProps) {
		super();

		for (Object aKey : theProps.keySet()) {
			put(aKey, theProps.get(aKey));
		}
	}

    /**
     * Return the value of the property as a boolean
     * @param theProp the property to retrieve
     * @return the value of the property as a boolean, or false if the property does not exist
     */
    public boolean getPropertyAsBoolean(String theProp) {
        return super.getProperty(theProp) != null && Boolean.valueOf(super.getProperty(theProp));
    }

	/**
	 * Return the value of the property as a boolean.
	 * @param theProperty the property to retrieve
	 * @param theDefault the default value if the property does not exist
	 * @return the value of the property if it exists, otherwise the default value
	 */
	public boolean getPropertyAsBoolean(final String theProperty, final boolean theDefault) {
		return super.getProperty(theProperty) == null ? theDefault : Boolean.valueOf(super.getProperty(theProperty));
	}

    /**
     * Returns the value of the given property
     * @param theProp the property to retrieve
     * @return the value of the property, or null if one is not found
     */
    @Override
    public String getProperty(String theProp) {
        String aValue = super.getProperty(theProp);
        if (aValue != null) {
            aValue = replaceVariables(aValue);
        }

        return aValue;
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object put(Object theKey, Object theValue) {
		if (theKey != null && theValue != null) {
			return super.put(theKey.toString(), theValue.toString());
		}
		else {
			return null;
		}
	}

    /**
     * Return the value of the property as an int
     * @param theProp the property to retrieve
     * @param theDefault the default property value
	 * @return the value of the property as an int
     * @throws NumberFormatException thrown if the value is not a valid integer value
     */
    public int getPropertyAsInt(String theProp, int theDefault) throws NumberFormatException {
        return Integer.parseInt(getProperty(theProp, String.valueOf(theDefault)));
    }

    /**
     * Return the value of the property as an int
     * @param theProp the property to retrieve
     * @return the value of the property as an int
     * @throws NumberFormatException thrown if the value is not a valid integer value
     */
    public int getPropertyAsInt(String theProp) throws NumberFormatException {
        return Integer.parseInt(getProperty(theProp));
    }

	/**
	 * Return the value of the property as a long
	 * @param theProperty the property to retrieve
	 * @param theDefault the default value if the property does not exist
	 * @return the value of the property as a long
	 * @throws NumberFormatException thrown if the value is not a value long value
	 */
	public long getPropertyAsLong(final String theProperty, final long theDefault) throws NumberFormatException {
		return Long.parseLong(getProperty(theProperty, String.valueOf(theDefault)));
	}

	/**
	 * Return the value of the property as a long
	 * @param theProperty the property to retrieve
	 * @return the value of the property as a long
	 * @throws NumberFormatException thrown if the value is not a value long value
	 */
	public long getPropertyAsLong(final String theProperty) throws NumberFormatException {
		return Long.parseLong(getProperty(theProperty));
	}

	/**
	 * Return the value of the property as a float
	 * @param theProperty the property to retrieve
	 * @param theDefault the default value to return if the property does not exist
	 * @return the value of the property as a float, or the default value
	 * @throws NumberFormatException thrown if the value is not a valid float value
	 */
	public float getPropertyAsFloat(final String theProperty, final float theDefault) throws NumberFormatException {
		return Float.parseFloat(getProperty(theProperty, String.valueOf(theDefault)));
	}

	/**
	 * Return the value of the property as a float
	 * @param theProperty the property to retrieve
	 * @return the value of the property as a float
	 * @throws NumberFormatException thrown if the value is not a valid float value
	 */
	public float getPropertyAsFloat(final String theProperty) throws NumberFormatException {
		return Float.parseFloat(getProperty(theProperty));
	}

	/**
	 * Return the value of the property as a double
	 * @param theProperty the property to retrieve
	 * @return the value of the property as a double
	 * @throws NumberFormatException thrown if the value is not a valid double value
	 */
	public double getPropertyAsDouble(final String theProperty) throws NumberFormatException {
		return Double.parseDouble(getProperty(theProperty));
	}

	/**
	 * Return the value of the property as a double
	 * @param theProperty the property to retrieve
	 * @param theDefault the default value for the property if it does not exist
	 * @return the value of the property, as a double, or the default value if the property does not exist
	 * @throws NumberFormatException thrown if the value is not a valid double value
	 */
	public double getPropertyAsDouble(final String theProperty, double theDefault) throws NumberFormatException {
		return Double.parseDouble(getProperty(theProperty, String.valueOf(theDefault)));
	}

    /**
     * Returns the value of a property as a list.  The value of the property must be comma separated:
     * <pre>
     * mylist = one, two, three, four
     * </pre>
     * Would yield a list of four elements, "one", "two", "three" and "four".
     * @param theProp the property key
     * @return the value as a list, or null if the key is not in the properties. 
     */
    public List<String> getPropertyAsList(String theProp) {
        String aValue = getProperty(theProp);

        if (aValue == null) {
            return null;
        }

        List<String> aList = new ArrayList<String>();

        String[] aElems = aValue.split(",");
        for (String aElem : aElems) {
            aList.add(aElem.trim());
        }

        return aList;
    }

    /**
     * Returns the value of the property as a map.  The way this works is if you have some properties like this:
     * <pre>
     * map = foo, baz, boz
     * foo = bar
     * baz = biz
     * boz = buzz
     * </pre>
     * Getting the key "map" as a map will yield a map with three keys "foo", "baz", and "boz" with the values "bar",
     * "biz" and "buzz" respectively.  The keys of the map MUST be comma separated.  If a key does not have a corresponding
     * value, it is not added to the result map.
     * @param theProp the property key which has the key values of the map as its value
     * @return the property value as a map.
     */
    public Map<String, String> getPropertyAsMap(String theProp) {
        List<String> aList = getPropertyAsList(theProp);

        Map<String, String> aMap = new HashMap<String, String>();

        for (String aKey : aList) {
            String aValue = getProperty(aKey);

            if (aValue != null) {
                aMap.put(aKey, aValue);
            }
        }

        return aMap;
    }

    /**
     * Given a property value, resolve any variable references in the value
     * @param theValue the value to resolve
     * @return the value, with any valid variable references resolved.
     */
    private String replaceVariables(String theValue) {
        StringBuffer aNewValue = new StringBuffer(theValue);
        int aIndex = 0;
        while (aNewValue.indexOf("${", aIndex) != -1) {

            String aVar = aNewValue.substring(aNewValue.indexOf("${", aIndex) + 2, aNewValue.indexOf("}", aIndex));
			int end = aNewValue.indexOf("}", aIndex);

            if (super.getProperty(aVar) != null) {
            	int aLength = aNewValue.length();
                aNewValue.replace(aNewValue.indexOf("${", aIndex), aNewValue.indexOf("}", aIndex)+1, replaceVariables(super.getProperty(aVar)));
                int aNewLength = aNewValue.length();
                end += aNewLength - aLength;
            }

            //aIndex = aNewValue.indexOf("}", aIndex);
			aIndex = end;
        }
        return aNewValue.toString();
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EnhancedProperties copy() {
		return new EnhancedProperties(this);
	}

	/**
	 * Return a subset of this Properties object by selecting all the keys which pass the predicate filter and
	 * inserting those key-value objects into the returned value.
	 *
	 * @param theKeySelector	the filter for key values
	 *
	 * @return					an EnhancedProperties containing all key-value pairs whose keys pass the filter
	 */
	public EnhancedProperties select(final Predicate<String> theKeySelector) {
		final EnhancedProperties aProps = new EnhancedProperties();

		for (Object aKey : keySet()) {
			if (theKeySelector.test(aKey.toString())) {
				aProps.put(aKey, get(aKey));
			}
		}

		return aProps;
	}

	public EnhancedProperties transformKeys(final Function<String, String> theKeyFunction) {
		final EnhancedProperties aProps = new EnhancedProperties();

		for (Object aKey : keySet()) {
			aProps.put(theKeyFunction.apply(aKey.toString()), get(aKey));
		}

		return aProps;
	}


	public EnhancedProperties transformValues(final Function<String, String> theValueFunction) {
		final EnhancedProperties aProps = new EnhancedProperties();

		for (Object aKey : keySet()) {
			aProps.put(aKey, theValueFunction.apply(getProperty(aKey.toString())));
		}

		return aProps;
	}

	public Iterable<EnhancedProperties> partitionByKeys(final Function<String, String> thePartitionFunction) {
		Map<String, EnhancedProperties> aMap = Maps.newHashMap();
		for (Object aKey : keySet()) {
			String aKeyStr = aKey.toString();
			String aMapKey = thePartitionFunction.apply(aKeyStr);
			EnhancedProperties aProps = aMap.get(aMapKey);
			if (aProps == null) {
				aProps = new EnhancedProperties();
				aMap.put(aMapKey, aProps);
			}

			aProps.put(aKey, get(aKey));
		}

		return aMap.values();
	}
}

