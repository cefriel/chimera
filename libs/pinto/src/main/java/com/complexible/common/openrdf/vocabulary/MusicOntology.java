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

package com.complexible.common.openrdf.vocabulary;

import org.eclipse.rdf4j.model.IRI;

/**
 * <p>Term constants for the DC music ontology</p>
 *
 * @author Michael Grove
 * @since 0.1
 */
public class MusicOntology extends Vocabulary {
    public static final String ONT_IRI = "http://purl.org/ontology/mo/";

    private static MusicOntology INSTANCE = null;

    private MusicOntology() {
        super(ONT_IRI);
    }

    public static MusicOntology ontology() {
        if (INSTANCE == null) {
            INSTANCE = new MusicOntology();
        }

        return INSTANCE;
    }

    // properties
    public final IRI track = term("track");
    public final IRI release_type = term("release_type");
    public final IRI release_status = term("release_status");
    public final IRI track_number = term("track_number");
    public final IRI length = term("length");
    public final IRI made = term("made");
    public final IRI musicbrainz = term("musicbrainz");
    public final IRI olga = term("olga");
    public final IRI genre = term("genre");
    public final IRI sample_rate = term("sample_rate");
    public final IRI bitsPerSample = term("bitsPerSample");

    // cp properties
    public final IRI rating = term("rating");
    public final IRI albumRating = term("albumRating");
    public final IRI year = term("year");
    public final IRI location = term("location");

    // classes
    public final IRI Genre = term("Genre");
    public final IRI Record = term("Record");
    public final IRI Track = term("Track");
    public final IRI MusicArtist = term("MusicArtist");
    public final IRI MusicGroup = term("MusicGroup");

    // individuals
    public final IRI Metal = FACTORY.createIRI(Genre.stringValue() + "/Metal");
    public final IRI Rock = FACTORY.createIRI(Genre.stringValue() + "/Rock");
    public final IRI Alternative = FACTORY.createIRI(Genre.stringValue() + "/Alternative");
    public final IRI Pop = FACTORY.createIRI(Genre.stringValue() + "/Pop");
    public final IRI Punk = FACTORY.createIRI(Genre.stringValue() + "/Punk");
    public final IRI Funk = FACTORY.createIRI(Genre.stringValue() + "/Funk");
    public final IRI Soundtrack = FACTORY.createIRI(Genre.stringValue() + "/Soundtrack");
    public final IRI Blues = FACTORY.createIRI(Genre.stringValue() + "/Blues");
    public final IRI Jazz = FACTORY.createIRI(Genre.stringValue() + "/Jazz");
    public final IRI Vocal = FACTORY.createIRI(Genre.stringValue() + "/Vocal");
	public final IRI Country = FACTORY.createIRI(Genre.stringValue() + "/Country");

    public final IRI album = term("album");
    public final IRI official = term("official");
}
