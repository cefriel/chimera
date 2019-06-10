/*
 * Copyright (c) 2009-2015 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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
 * <p>Constants for the concepts in the FOAF vocabulary</p>
 *
 * @author  Michael Grove
 * @since   0.1
 * @version 1.1
 */
public final class FOAF extends Vocabulary {
	private static FOAF INSTANCE;
	
    private static final java.net.URI FOAF_IRI = java.net.URI.create("http://xmlns.com/foaf/0.1/");

	private FOAF() {
		super(FOAF_IRI.toString());
	}
	
	public static FOAF ontology() {
		if (INSTANCE == null) {
			INSTANCE = new FOAF();
		}
		
		return INSTANCE;
	}

    public final IRI Person = term("Person");
    public final IRI Organization = term("Organization");
    public final IRI Image = term("Image");
	public final IRI Agent = term("Agent");
	public final IRI Group = term("Group");
	public final IRI Document = term("Document");

    public final IRI givenName = term("givenName");
    public final IRI familyName = term("familyName");
    public final IRI firstName = term("firstName");
    public final IRI surname = term("surname");
	public final IRI name = term("name");
    public final IRI mbox = term("mbox");
    public final IRI depicts = term("depicts");
    public final IRI depiction = term("depiction");
    public final IRI maker = term("maker");
    public final IRI phone = term("phone");
    public final IRI fax = term("fax");
    public final IRI based_near = term("based_near");
	public final IRI thumbnail = term("thumbnail");
	public final IRI homepage = term("homepage");
	public final IRI birthday = term("birthday");
	public final IRI knows = term("knows");
	public final IRI lastName = term("lastName");
	public final IRI title = term("title");
	public final IRI openId = term("openId");
	public final IRI pastProject = term("pastProject");
	public final IRI topic_interest = term("topic_interest");
	public final IRI age = term("age");
	public final IRI member = term("member");
	public final IRI primaryTopic = term("primaryTopic");
	public final IRI made = term("made");
	public final IRI logo = term("logo");
	public final IRI currentProject = term("currentProject");
}
