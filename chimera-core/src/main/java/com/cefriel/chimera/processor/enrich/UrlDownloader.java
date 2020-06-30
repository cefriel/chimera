/*
 * Copyright 2020 Cefriel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.cefriel.chimera.processor.enrich;

import com.cefriel.chimera.util.ProcessorConstants;
import com.cefriel.chimera.util.UniLoader;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.io.InputStream;

public class UrlDownloader implements Processor {

	private String additionalSourcesUrl;

	public void process(Exchange exchange) throws Exception {
		String additionalSource = exchange.getMessage().getHeader(ProcessorConstants.ADDITIONAL_SOURCE, String.class);
		if (additionalSource != null)
			additionalSourcesUrl = additionalSource;

		String token = exchange.getProperty(ProcessorConstants.JWT_TOKEN, String.class);

		InputStream data = UniLoader.open(additionalSourcesUrl, token);
		exchange.getMessage().setBody(data, InputStream.class);
	}

	public String getAdditionalSourcesUrl() {
		return additionalSourcesUrl;
	}

	public void setAdditionalSourcesUrl(String additionalSourcesUrl) {
		this.additionalSourcesUrl = additionalSourcesUrl;
	}
}