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

import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.util.Enumeration;
import java.text.MessageFormat;

/**
 * <p>Simple extension to {@link ResourceBundle} that handles message formatting via {@link MessageFormat} for values in the resource bundle.</p>
 *
 * @author Michael Grove
 * @version 2.0
 * @since 2.0
 */
public final class FormattingResourceBundle extends ResourceBundle {

	/**
	 * The resource bundle
	 */
	private final ResourceBundle mResourceBundle;

	/**
	 * Create a new FormattingResourceBundle
	 * @param theResourceBundle the actual bundle
	 */
	public FormattingResourceBundle(final ResourceBundle theResourceBundle) {
		mResourceBundle = theResourceBundle;
	}

	/**
	 * Create a new ResourceBundle
	 * @param theName the name of the bundle to load
	 */
	public FormattingResourceBundle(final String theName) {
		mResourceBundle = ResourceBundle.getBundle(theName);
	}

	/**
	 * Return the string from the {@link ResourceBundle}, optionally applying the provided args to the message via {@link String#format}.
	 * @param theKey the message key
	 * @param theArgs the optiona list of args to apply to the message
	 * @return the message or null if a value w/ the key does not exist
	 */
	public String get(final String theKey, final Object... theArgs) {
		try {
			String aMsg = mResourceBundle.getString(theKey);

			if (aMsg == null) {
				return theKey;
			}
			else if (theArgs != null && theArgs.length > 0) {
				return new MessageFormat(aMsg).format(theArgs);
			}
			else {
				return aMsg;
			}
		}
		catch (MissingResourceException e) {
			return theKey;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object handleGetObject(final String key) {
		return mResourceBundle.getObject(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Enumeration<String> getKeys() {
		return mResourceBundle.getKeys();
	}
}
