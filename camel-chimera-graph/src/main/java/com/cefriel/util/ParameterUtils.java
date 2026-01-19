/*
 * Copyright (c) 2019-2022 Cefriel.
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

package com.cefriel.util;

import com.cefriel.component.GraphBean;
import com.cefriel.graph.RDFGraph;
import org.apache.camel.Exchange;

/**
 * Utility class for resolving operation parameters.
 * Implements header-over-endpoint precedence: header values take priority over endpoint configuration.
 */
public final class ParameterUtils {

    private ParameterUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Resolves a parameter value with header precedence over endpoint config.
     * @param headerValue value from exchange header (higher priority)
     * @param endpointValue value from endpoint configuration (lower priority)
     * @return header value if not null, otherwise endpoint value
     */
    public static <T> T resolveParam(T headerValue, T endpointValue) {
        return headerValue != null ? headerValue : endpointValue;
    }

    /**
     * Resolves a parameter value with header precedence and a default fallback.
     * @param headerValue value from exchange header (highest priority)
     * @param endpointValue value from endpoint configuration (medium priority)
     * @param defaultValue fallback value if both are null (lowest priority)
     * @return resolved value following precedence rules
     */
    public static <T> T resolveParam(T headerValue, T endpointValue, T defaultValue) {
        T resolved = resolveParam(headerValue, endpointValue);
        return resolved != null ? resolved : defaultValue;
    }

    /**
     * Resolves the effective named graph parameter.
     * Returns null if defaultGraph is true, otherwise uses header > endpoint > generated from graphID.
     */
    public static String resolveNamedGraph(Exchange exchange, GraphBean config) {
        if (config.isDefaultGraph()) {
            return null;
        }
        String headerValue = exchange.getMessage().getHeader(ChimeraConstants.CONTEXT_GRAPH, String.class);
        if (headerValue != null) {
            return headerValue;
        }
        if (config.getNamedGraph() != null) {
            return config.getNamedGraph();
        }
        String graphID = exchange.getMessage().getHeader(ChimeraConstants.GRAPH_ID, String.class);
        return ChimeraConstants.DEFAULT_BASE_IRI + (graphID != null ? graphID : exchange.getExchangeId());
    }

    /**
     * Resolves the effective base IRI parameter.
     */
    public static String resolveBaseIri(Exchange exchange, GraphBean config) {
        String headerValue = exchange.getMessage().getHeader(ChimeraConstants.BASE_IRI, String.class);
        return resolveParam(headerValue, config.getBaseIri(), ChimeraConstants.DEFAULT_BASE_IRI);
    }

    /**
     * Gets the RDFGraph from the exchange body and validates it's not null.
     * @throws IllegalArgumentException if graph is null
     */
    public static RDFGraph requireGraph(Exchange exchange) {
        RDFGraph graph = exchange.getMessage().getBody(RDFGraph.class);
        if (graph == null) {
            throw new IllegalArgumentException("RDFGraph in exchange body cannot be null");
        }
        return graph;
    }

}

