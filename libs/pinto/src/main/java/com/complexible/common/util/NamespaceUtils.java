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

/**
 * <p>Utility methods for working with namespaces</p>
 *
 * @author Evren Sirin
 * @since 0.1
 * @version 2.0
 */
public class NamespaceUtils {

	public static boolean isNameStartChar(char ch) {
		return (Character.isLetter(ch) || ch == '_');
	}

	public static boolean isNameChar(char ch) {
		return (isNameStartChar(ch) || Character.isDigit(ch) || ch == '.' || ch == '-');
	}

	public static int findNameStartIndex(String str) {
		char[] strChars = str.toCharArray();
		int nameStartIndex = -1;
		boolean foundNameChar = false;

		for (int strIndex = strChars.length - 1; strIndex >= 0; strIndex--) {
			char letter = strChars[strIndex];

			if (isNameStartChar(letter)) {
				nameStartIndex = strIndex;
				foundNameChar = true;
			}
			else if (foundNameChar && !isNameChar(letter)) {
				break;
			}
		}
		return nameStartIndex;
	}

	public static int findLastNameIndex(String str) {
		char[] strChars = str.toCharArray();
		int nameIndex = -1;

		for (int strIndex = strChars.length - 1; strIndex >= 0; strIndex--) {
			char letter = strChars[strIndex];
			if (isNameChar(letter)) {
				nameIndex = strIndex;
			}
			else {
				break;
			}
		}
		return nameIndex;
	}

	public static int findNextNonNameIndex(String str, int startIndex) {
		char[] strChars = str.toCharArray();
		int nameIndex = startIndex;
		for (nameIndex = startIndex; nameIndex < strChars.length; nameIndex++) {
			char letter = strChars[nameIndex];
			if (!isNameChar(letter) || letter == '.') {
				break;
			}
		}
		return nameIndex;
	}
}
