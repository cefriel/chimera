package com.complexible.common.openrdf.vocabulary;

import org.eclipse.rdf4j.model.IRI;

/**
 * <p>Vocabulary for the DBPedia schemas</p>
 *
 * @author  Michael Grove
 * @since   0.3.1
 * @version 4.0
 */
public class DBPedia {

	private static final Ontology ONT = new Ontology();
	private static final Property PROP = new Property();
	private static final Resource RES = new Resource();

	public static Property property() {
		return PROP;
	}

	public static Resource resource() {
		return RES;
	}

	public static Ontology ontology() {
		return ONT;
	}

	public static class Property extends Vocabulary {
		private Property() {
			super("http://dbpedia.org/property/");
		}

		public final IRI countryCode = term("countryCode");
	}

	public static class Resource extends Vocabulary {
		private Resource() {
			super("http://dbpedia.org/resource/");
		}
	}

	public static class Ontology extends Vocabulary {
		private Ontology() {
			super("http://dbpedia.org/ontology/");
		}

		public final IRI Place = term("Place");
		public final IRI PopulatedPlace = term("PopulatedPlace");
		public final IRI Country = term("Country");
	}
}
