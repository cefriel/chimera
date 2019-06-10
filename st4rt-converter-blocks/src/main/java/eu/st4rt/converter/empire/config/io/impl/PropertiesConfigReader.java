/*
 * Copyright (c) 2009-2010 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

package eu.st4rt.converter.empire.config.io.impl;

import com.complexible.common.util.EnhancedProperties;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import eu.st4rt.converter.empire.config.ConfigKeys;
import eu.st4rt.converter.empire.config.EmpireConfiguration;
import eu.st4rt.converter.empire.config.io.ConfigReader;
import eu.st4rt.converter.empire.util.BeanReflectUtil;
import eu.st4rt.converter.empire.util.EmpireAnnotationProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * <p>Implementation of a {@link ConfigReader} for reading an Empire configuration from a standard properties file.</p>
 *
 * @author  Michael Grove
 * @since   0.6.2
 * @version 0.7
 * @see EmpireConfiguration
 */
public class PropertiesConfigReader implements ConfigReader, ConfigKeys {

	/**
	 * The logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigReader.class.getName());

	public static final String KEY_NAME = ConfigKeys.NAME;
	public static final String KEY_ANNOTATION_PROVIDER = "annotation.provider";

	/**
	 * @inheritDoc
	 */
	public EmpireConfiguration read(InputStream theStream) throws IOException {
		return read(new InputStreamReader(theStream));
	}

	/**
	 * @inheritDoc
	 */
	public EmpireConfiguration read(Reader theReader) throws IOException {
		Properties aProps = new EnhancedProperties();

		try {
			aProps.load(theReader);
		}
		catch (Exception e) {
			throw new IOException(e);
		}

		int aIndex = 0;

		Map<String, String> aGeneralConfig = new HashMap<String, String>();
		Map<String, Map<String, String>> aUnitConfig = new HashMap<String, Map<String, String>>();

		while (aProps.containsKey(aIndex+"."+KEY_NAME)) {
			String aPrefix = aIndex+".";
			Map<String, String> aUnit = new HashMap<String, String>();
			
			for (Object aOrigKey : Collections2.filter(aProps.keySet(), new PrefixPredicate(aPrefix))) {
				String aKey = aOrigKey.toString();

				aKey = aKey.substring(aPrefix.length());

				aUnit.put(aKey, aProps.getProperty(aOrigKey.toString()));
			}

			aUnitConfig.put(aProps.getProperty(aIndex+"."+KEY_NAME), aUnit);

			aIndex++;
		}

		for (Object aOrigKey : Collections2.filter(aProps.keySet(), new UnitFilterPredicate())) {
			aGeneralConfig.put(aOrigKey.toString(), aProps.getProperty(aOrigKey.toString()));
		}

		EmpireConfiguration aConfig = new EmpireConfiguration(aGeneralConfig, aUnitConfig);

		if (aGeneralConfig.containsKey(KEY_ANNOTATION_PROVIDER)) {
			try {
				@SuppressWarnings("unchecked")
				Class<EmpireAnnotationProvider> aClass = (Class<EmpireAnnotationProvider>) BeanReflectUtil.loadClass(aGeneralConfig.get(KEY_ANNOTATION_PROVIDER));

				aConfig.setAnnotationProvider(aClass);
			}
			catch (ClassNotFoundException e) {
				LOGGER.warn("Annotation provider implementation '{}' cannot be found, please check your classpath.", aGeneralConfig.get(KEY_ANNOTATION_PROVIDER));
			}
			catch (ClassCastException e) {
				LOGGER.warn("Specified annotation provider implementation '{}' is not a valid EmpireAnnotationProvider.", aGeneralConfig.get(KEY_ANNOTATION_PROVIDER));
			}
		}

		return aConfig;
	}

	/**
	 * Filter for returning only non-unit property keys
	 */
	private static class UnitFilterPredicate implements Predicate<Object> {

		/**
		 * @inheritDoc
		 */
		public boolean apply(final Object theValue) {
			String aStr = theValue.toString();

			// the standard format for unit properties in the file is <index>.<property_name>
			// all these properties have been put into the appropriate unit config map.
			// so we want to get rid of them and only include the properties which do not
			// fit this format in the general config.  if they don't have a period, they are
			// fine, and if they do have a period, but the stuff before the period is not a
			// number, that's good too.  otherwise, it's a unit property or something that
			// *looks* like a unit property, so we'll filter that out

			if (aStr.indexOf(".") == -1) {
				return true;
			}
			else {
				String aPrefix = aStr.substring(0, aStr.indexOf("."));
				try {
					Integer.parseInt(aPrefix);
					return false;
				}
				catch (NumberFormatException e) {
					return true;
				}
			}
		}
	}

	/**
	 * Filter for only returning values whose toString() starts with the specified prefix.  Used to filter out
	 * property keys not in the current unit.
	 */
	private static class PrefixPredicate implements Predicate<Object> {
		private String mPrefix;

		private PrefixPredicate(final String thePrefix) {
			mPrefix = thePrefix;
		}

		/**
		 * @inheritDoc
		 */
		public boolean apply(final Object theValue) {
			return theValue.toString().startsWith(mPrefix);
		}
	}

	public static void main(String[] args) throws Exception {
		String aConfig = "annotation.provider = empire.util.PropertiesAnnotationProvider\n" +
						 "0.name = context1\n" +
						 "0.factory = empire.sesame.RepositoryDataSourceFactory\n" +
						 "0.url = http://localhost:8080/openrdf-sesame\n" +
						 "0.repo = mem-rdf-db\n" +
						 "\n" +
						 "1.name = context2\n" + 
						 "1.factory = empire.jena.JenaTestDataSourceFactory\n";

		new PropertiesConfigReader().read(new java.io.StringReader(aConfig));
	}
}
