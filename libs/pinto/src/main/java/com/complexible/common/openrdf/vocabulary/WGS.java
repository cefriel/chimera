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
 * <p>Term constants for the WGS ontology</p>
 *
 * @author Michael Grove
 * @since 0.1
 */
public class WGS extends Vocabulary {
    private static WGS VOCAB = new WGS("http://www.w3.org/2003/01/geo/wgs84_pos#");

    private WGS(String theURI) {
        super(theURI);
    }

    public static WGS ontology() {
        return VOCAB;
    }

    public final IRI lat = term("lat");
    public final IRI _long = term("long");
    public final IRI alt = term("alt");
    public final IRI lat_long = term("lat_long");
    public final IRI Point = term("Point");
    public final IRI SpatialThing = term("SpatialThing");
}
