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

package com.cefriel.chimera.example;

import org.apache.camel.spring.Main;

import java.io.File;

public class ConverterApplication {

	public static void main(String[] args) throws Exception {
		Main main = new Main();
		String path = "routes/camel-context.xml";
		File f = new File("./" + path);
		if(f.exists() && !f.isDirectory()) {
			main.setFileApplicationContextUri("./" + path);
		} else {
			main.setApplicationContextUri("routes/camel-context.xml");
		}
		main.run();
	}

}