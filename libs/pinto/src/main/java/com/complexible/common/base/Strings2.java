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

package com.complexible.common.base;

import com.google.common.io.BaseEncoding;

import java.nio.charset.Charset;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.common.base.Charsets;

/**
 * <p>String utilities not provided by {@link com.google.common.base.Strings}.</p>
 *
 * @author  Michael Grove
 * @since   2.0
 * @version 2.5
 */
public final class Strings2 {
	private static final Random RANDOM = new Random();

	private Strings2() {
        throw new AssertionError();
	}

	/**
	 * Convert a string to title case.  So if you have "this is a sentence" this function will return "This Is A Sentence"
	 * @param theStr the string to convert
	 * @return the converted string
	 */
	public static String toTitleCase(String theStr) {
		char[] charArray = theStr.toLowerCase().toCharArray();

		Pattern pattern = Pattern.compile("\\b([A-Za-z])");
		Matcher matcher = pattern.matcher(theStr);

		while(matcher.find()){
			int index = matcher.end(1) - 1;
			charArray[index] = Character.toTitleCase(charArray[index]);
		}

		return String.valueOf(charArray);
	}

	/**
     * Return the hex string value of the byte array
	 * @param theArray the input array
	 * @return the hex value of the bytes
	 */
	public static String hex(byte[] theArray) {
		StringBuffer sb = new StringBuffer();
		for (byte aByte : theArray) {
			sb.append(Integer.toHexString((aByte & 0xFF) | 0x100).toUpperCase().substring(1, 3));
		}
		return sb.toString();
	}

	/**
     * Returns the md5 representation of a string
     * @param theString the string to md5
     * @return the byte representation of the md5 sum of the string
     */
    public static byte[] md5(String theString) {
		return md5(theString.getBytes(Charsets.UTF_8));
	}

	/**
     * Returns the md5 representation of a string
     * @param theBytes the bytes to md5
     * @return the byte representation of the md5 sum of the string
     */
    public static byte[] md5(byte[] theBytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return md.digest(theBytes);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Return a string of random characters of the specified length.
     * @param theLength the size of the random string to create
     * @return a string of random letters
     */
    public static String getRandomString(int theLength) {
        StringBuffer aBuffer = new StringBuffer();

        for (int i = 0; i < theLength; i++) {
            int index = 97 + RANDOM.nextInt(26);
            char c = (char)index;
            aBuffer.append(c);
        }
        return aBuffer.toString();
    }

	/**
	 * URL encode the string using the UTF8 charset
	 * @param theString the string to encode
	 * @return the url encoded string
	 */
	public static String urlEncode(String theString) {
		return urlEncode(theString, Charsets.UTF_8);
	}

	/**
	 * URL encode the given string using the given Charset
	 * @param theString the string to encode
	 * @param theCharset the charset to encode the string using
	 * @return the string encoded with the given charset
	 */
	public static String urlEncode(String theString, Charset theCharset) {
		// saves us a couple ms using a custom encoder rather than the one built into the jdk. todo unfortunately, we're ignoring the charset.
//		return FastURLEncode.encode(theString);
		try {
			return URLEncoder.encode(theString, theCharset.displayName());
		}
		catch (UnsupportedEncodingException e) {
			// this can be safely ignored, you would not have a charset object for an unsupported charset
			return null;
		}
	}

	/**
	 * URL decode the string using the UTF8 charset
	 * @param theString the string to decode
	 * @return the decoded string
	 */
	public static String urlDecode(String theString) {
		return urlDecode(theString, Charsets.UTF_8);
	}

	/**
	 * URL decode the given string using the given Charset
	 * @param theString the string to decode
	 * @param theCharset the charset to decode the string using
	 * @return the string decoded with the given charset
	 */
	public static String urlDecode(String theString, Charset theCharset) {
		try {
			return URLDecoder.decode(theString, theCharset.displayName());
		}
		catch (UnsupportedEncodingException e) {
			// this can be safely ignored, you would not have a charset object for an unsupported charset
			return null;
		}
	}

	/**
	 * Base64 encodes the given byte array.  This utility is provided to abstract over the Sun implementation which
	 * is deprecated and marked for deletion.  The implementation of this method will always work; so if the existing
	 * Sun implementation goes away, this will be switched to an appropriate implementation without requiring any
	 * changes to dependant code.
	 * @param theArrayToEncode the bytes to encode
	 * @return the bytes base64 encoded
	 */
	public static String base64Encode(byte[] theArrayToEncode) {
        return BaseEncoding.base64().encode(theArrayToEncode);
	}

	/**
	 * Base64 encodes the given byte array.  This utility is provided to abstract over the Sun implementation which
	 * is deprecated and marked for deletion.  The implementation of this method will always work; so if the existing
	 * Sun implementation goes away, this will be switched to an appropriate implementation without requiring any
	 * changes to dependant code.
	 * @param theStringToDecode the string to decode
	 * @return the bytes base64 decoded
	 * @throws java.io.IOException throw if there is an error while decoding
	 */
	public static byte[] base64Decode(String theStringToDecode) throws IOException {
		return BaseEncoding.base64().decode(theStringToDecode);
	}
}
