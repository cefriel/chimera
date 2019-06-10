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

import java.util.Map;
import java.util.Collection;

import com.google.common.collect.Maps;
import com.google.common.base.Preconditions;

/**
 * <p>A class to convert URI's to QNames.</p>
 * 
 * @author Evren Sirin
 * @author Michael Grove
 *
 * @since 2.0
 * @version 2.0
 */
public class PrefixMapping {
	public static final PrefixMapping GLOBAL = new PrefixMapping();

	/**
	 * Map of namespace URI's to prefixes
	 */
	private Map<String, String> uriToPrefix;

	/**
	 * Map of prefixes to namespace URIs
	 */
	private Map<String, String> prefixToUri;

	/**
	 * Create a new PrefixMapping adding defaults for the common namespaces, OWL, RDF, RDFS and XSD
	 */
	public PrefixMapping() {
		this(true);
	}

	/**
	 * Create a new PrefixMapping optionally adding defaults for the common (OWL, RDF, RDFS, XSD) namespaces
	 * @param theAddDefaults whether or not to add the default namespaces
	 */
	public PrefixMapping(final boolean theAddDefaults) {
		uriToPrefix = Maps.newHashMap();
		prefixToUri = Maps.newHashMap();

		if (theAddDefaults) {
			addMapping("owl", Namespaces.OWL);
			addMapping("rdf", Namespaces.RDF);
			addMapping("rdfs", Namespaces.RDFS);
			addMapping("xsd", Namespaces.XSD);
		}
	}

	/**
	 * Return the prefix for the namespace
	 * @param uri the namespace URI
	 * @return the prefix for the namespace or null if one does not exist
	 */
	public String getPrefix(String uri) {
		return uriToPrefix.get(uri);
	}

	/**
	 * Return the namespace for the given prefix
	 * @param prefix the prefix
	 * @return the namespace for the prefix, or null if one does not exist
	 */
	public String getNamespace(String prefix) {
		return prefixToUri.get(prefix);
	}

	/**
	 * Add a new prefix/namespace mapping.  This will not overrwrite existing mappings for the given prefix and namespace.  If you wish to overwrite an existing mapping,
	 * use {@link #setMapping}
	 * @param prefix the prefix
	 * @param uri the namespace URI for the prefix
	 * @return whether or not the mapping was added
	 */
	public boolean addMapping(String prefix, String uri) {
		Preconditions.checkNotNull(prefix, "Prefix cannot be null");
		Preconditions.checkNotNull(uri, "Namespace URI cannot be null");

		String currentUri = getNamespace(prefix);
		if (currentUri == null) {
			prefixToUri.put(prefix, uri);
			uriToPrefix.put(uri, prefix);
			return true;
		}
		else if (currentUri.equals(uri)) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Set a prefix/namespace mapping.  This will override any existing mapping.  To optionally add the mapping, use {@link #addMapping}
	 * @param prefix the prefix
	 * @param uri the namespace URI for the prefix
	 */
	public void setMapping(final String prefix, final String uri) {
		Preconditions.checkNotNull(prefix, "Prefix cannot be null");
		Preconditions.checkNotNull(uri, "Namespace URI cannot be null");

		prefixToUri.put(prefix, uri);
		uriToPrefix.put(uri, prefix);
	}

	/**
	 * Remove a prefix/namespace mapping.  Only the exact mapping is removed.
	 * @param prefix the prefix
	 * @param uri the uri
	 * @return true if removed, false otherwise
	 */
	public boolean removeMapping(final String prefix, final String uri) {
		String aPrefixMapping = prefixToUri.get(prefix);
		String aURIMapping = uriToPrefix.get(uri);

		if (aPrefixMapping.equals(uri) && aURIMapping.equals(prefix)) {
			prefixToUri.remove(prefix);
			uriToPrefix.remove(uri);
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Clears all existing prefix/namespace mappings
	 */
	public void clear() {
		uriToPrefix.clear();
		prefixToUri.clear();
	}

	/**
	 * Return the list of prefixes currently bound in this PrefixMapping
	 * @return the list of currently used prefixes
	 */
	public Collection<String> getPrefixes() {
		return prefixToUri.keySet();
	}

	/**
	 * Return the qname for the given URI.  This will use the existing prefix definition if possible, and if one does not exist a new prefix of the form ns(int) (ex. ns2)
	 * will be created, saved, and returned.  A null prefix returns a null qname.
	 * @param uri the URI
	 * @return the URI as a qname
	 */
	public String qname(String uri) {
		return qname(uri, true /* auto-generate */);
	}

	/**
	 * Return the URI as a qname.  A prefix for the URI will be auto generated if autoGenerate is set to true
	 * and a prefix does not already exist for the namespace of the URI.  A null prefix returns a null qname.
	 *
	 * @param uri the URI to shorten
	 * @param autoGenerate whether or not to auto-generate a qname prefix for the URI if one does not exist
	 * @return the qname of the URI, or the URI itself if a prefix mapping does not exist and autoGenerate is false.
	 */
	public String qname(final String uri, final boolean autoGenerate) {
		if (uri == null) {
			return null;
		}

		int splitPos = NamespaceUtils.findLastNameIndex(uri);
		
		if (splitPos <= 0) {
			return uri;
		}

		String nameSpace = uri.substring(0, splitPos);
		String localName = uri.substring(splitPos);
		String prefix = getPrefix(nameSpace);
		
		if (prefix != null) {
			return prefix + ":" + localName;
		}
		else if (!autoGenerate) {
			return uri;
		}

		int prefixStart = NamespaceUtils.findNameStartIndex(nameSpace);
		if (prefixStart < 0) {
			prefix = "ns"; 
		}
		else {
			int prefixEnd = NamespaceUtils.findNextNonNameIndex(nameSpace, prefixStart + 1);
			prefix = uri.substring(prefixStart, prefixEnd);
			
			if (prefix.length() > 1 && Character.isUpperCase(prefix.charAt(0))
			                && !Character.isUpperCase(prefix.charAt(1))) {
				prefix = Character.toLowerCase(prefix.charAt(0)) + prefix.substring(1, prefix.length());
			}
		}
		
		prefix = setAutoIncrementPrefix(prefix, nameSpace);

		return prefix + ":" + localName;
	}
	
	private String setAutoIncrementPrefix(String prefix, String namespace) {
		String result = prefix;
		int mod = 0;
		while (!addMapping(result, namespace)) {
			result = prefix + mod;
			mod++;
		}	
		return result;
	}


	/**
	 * Return the full URI for the provided qname.  If there is no prefix mapping for the provided qname, the qname is returned, otherwise the prefix is replaced
	 * with the namespace as indicated by {@link #addMapping} or if it was added via {@link #qname}.  A null qname returns a null URI.
	 * @param qname the qname to expand
	 * @return the expanded qname, or the qname if it cannot be expanded
	 */
	public String uri(String qname) {
		if (qname == null) {
			return qname;
		}

		String[] str = qname.split(":");

		String aNamespace = getNamespace(str[0]);

		if (aNamespace == null) {
			return qname;
		}
		else {
			return aNamespace + str[1];
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return prefixToUri.toString();
	}
}
