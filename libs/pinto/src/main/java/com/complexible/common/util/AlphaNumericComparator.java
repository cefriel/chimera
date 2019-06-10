/*
 * Copyright (c) 2005-2011 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

import java.util.Comparator;

/**
 * This is a comparator to perform a mix of alphabetical+numeric comparison. For
 * example, if there is a list {"test10", "test2", "test150", "test25", "test1"}
 * then what we generally expect from the ordering is the result {"test1",
 * "test2", "test10", "test25", "test150"}. However, standard lexigraphic
 * ordering does not do that and "test10" comes before "test2". This class is
 * provided to overcome that problem. This functionality is useful to sort the
 * benchmark files (like the ones in in DL-benchmark-suite) from smallest to the
 * largest. Comparisons are done on the String values retuned by toString() so
 * care should be taken when this comparator is used to sort arbitrary Java
 * objects.
 *
 *
 * @author Evren Sirin
 */
public class AlphaNumericComparator<T> implements Comparator<T> {
    /**
     * A static instantiation of a case sensitive AlphaNumericComparator
     */
    public static final AlphaNumericComparator CASE_SENSITIVE   = new AlphaNumericComparator<Object>(true);
    /**
     * A static instantiation of a case insensitive AlphaNumericComparator
     */
    public static final AlphaNumericComparator CASE_INSENSITIVE = new AlphaNumericComparator<Object>(false);

    private boolean caseSensitive;

    /**
     * Create a case sensitive AlphaNumericComparator
     *
     */
    public AlphaNumericComparator() {
        this.caseSensitive = true;
    }

    /**
     * Create an AlphaNumericComparator
     *
     * @param caseSensitive if true comparisons are case sensitive
     */
    public AlphaNumericComparator(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

	/**
	 * {@inheritDoc}
	 */
    public int compare(T o1, T o2) {
        String s1 = o1.toString();
        String s2 = o2.toString();
        int n1 = s1.length(), n2 = s2.length();
        int i1 = 0, i2 = 0;
        for(; i1 < n1 && i2 < n2; i1++, i2++) {
            char c1 = s1.charAt(i1);
            char c2 = s2.charAt(i2);
            if(c1 != c2) {
                if(Character.isDigit(c1) && Character.isDigit(c2)) {
                    int value1 = 0, value2 = 0;
                    while(i1 < n1 && Character.isDigit(c1 = s1.charAt(i1++)))
                        value1 = 10 * value1 + (c1 - '0');
                    while(i2 < n2 && Character.isDigit(c2 = s2.charAt(i2++)))
                        value2 = 10 * value2 + (c2 - '0');
                    if(value1 != value2) 
                        return value1 - value2;
                }
                if(!caseSensitive) {
                    c1 = Character.toUpperCase(c1);
                    c2 = Character.toUpperCase(c2);
                    if(c1 != c2) {
                        c1 = Character.toLowerCase(c1);
                        c2 = Character.toLowerCase(c2);
                        if(c1 != c2) {
                            return c1 - c2;
                        }
                    }
                }
                else {
                    return c1 - c2;
                }
            }
        }

        return n1 - n2;
    }
}
