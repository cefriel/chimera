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

import java.util.Properties;

/**
 * <p>Simple builder class for creating instances of {@link Properties}</p>
 *
 * @author Michael Grove
 * @since 0.3
 * @version 2.2.1
 */
public final class PropertiesBuilder {

	/**
	 * The instances of the properties being built
	 */
	private Properties mProperties = new Properties();

	/**
	 * Hidden constructor
	 */
	private PropertiesBuilder() {
	}

	/**
	 * Create a new PropertiesBuilder
	 * @return a new builder
	 */
	public static PropertiesBuilder create() {
		return new PropertiesBuilder();
	}

	/**
	 * Create a new PropertiesBuilder
	 * @param theProps create a new builder starting from the provided set of properties.  A copy of these properties are made, the original remains unaltered by the builder.
	 * @return a new builder
	 */
	public static PropertiesBuilder create(final Properties theProps) {
		PropertiesBuilder aBuilder = new PropertiesBuilder();
		aBuilder.mProperties.putAll(theProps);
		return aBuilder;
	}

	/**
	 * Set the specified property key-value pair
	 * @param theKey the key
	 * @param theValue the value
	 * @return this biulder
	 */
	public PropertiesBuilder set(final String theKey, final String theValue) {
		mProperties.setProperty(theKey, theValue);
		return this;
	}

	public PropertiesBuilder setTrue(final String theKey) {
		return set(theKey, "true");
	}

	public PropertiesBuilder setFalse(final String theKey) {
		return set(theKey, "false");
	}

	public PrefixedPropertiesBuilder withPrefix(final String thePrefix) {
		return new PrefixedPropertiesBuilder(thePrefix);
	}

	/**
	 * Return the created Properties instance
	 * @return the properties
	 */
	public Properties build() {
		Properties aProps = new Properties();
		aProps.putAll(mProperties);
		return aProps;
	}

	public final class PrefixedPropertiesBuilder {
		private final String mPrefix;

		public PrefixedPropertiesBuilder(final String thePrefix) {
			mPrefix = thePrefix;
		}

		public PrefixedPropertiesBuilder set(final String theKey, final String theValue) {
			mProperties.setProperty(mPrefix + theKey, theValue);
			return this;
		}

		public PrefixedPropertiesBuilder setTrue(final String theKey) {
			return set(theKey, "true");
		}

		public PrefixedPropertiesBuilder setFalse(final String theKey) {
			return set(theKey, "false");
		}
	}
}
