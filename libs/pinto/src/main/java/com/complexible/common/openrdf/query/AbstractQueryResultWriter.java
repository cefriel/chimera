/*
 * Copyright (c) 2005-2013 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

package com.complexible.common.openrdf.query;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.rdf4j.query.QueryResultHandlerException;
import org.eclipse.rdf4j.query.resultio.QueryResultFormat;
import org.eclipse.rdf4j.query.resultio.QueryResultWriter;
import org.eclipse.rdf4j.rio.RioSetting;
import org.eclipse.rdf4j.rio.WriterConfig;



/**
 * <p>More useful base for creating custom {@link QueryResultWriter} implementations.
 * Provides no-op methods for the new parts of the API so existing implementations don't
 * have boilerplate no-op methods to ignore the stuff that should not be in the API.</p>
 *
 * @author  Michael Grove
 * @since   1.0
 * @version 1.0
 */
public abstract class AbstractQueryResultWriter implements QueryResultWriter {

    private final QueryResultFormat mFormat;

    private WriterConfig mWriterConfig = new WriterConfig();

    protected AbstractQueryResultWriter(final QueryResultFormat theFormat) {
        mFormat = theFormat;
    }

    /**
     * @inheritDoc
     */
    @Override
    public final QueryResultFormat getQueryResultFormat() {
        return mFormat;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void handleStylesheet(final String s) throws QueryResultHandlerException {
        // no-op
    }

    /**
     * @inheritDoc
     */
    @Override
    public void setWriterConfig(WriterConfig config) {
        this.mWriterConfig = config;
    }

    /**
     * @inheritDoc
     */
    @Override
    public WriterConfig getWriterConfig() {
        return this.mWriterConfig;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Collection<RioSetting<?>> getSupportedSettings() {
        return Collections.emptyList();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void handleLinks(final List<String> theStrings) throws QueryResultHandlerException {
        // no-op
    }

    /**
     * @inheritDoc
     */
    @Override
    public void startHeader() throws QueryResultHandlerException {
        // no-op
    }

    /**
     * @inheritDoc
     */
    @Override
    public void startDocument() throws QueryResultHandlerException {
        // no-op
    }

    /**
     * @inheritDoc
     */
    @Override
    public void handleNamespace(final String s, final String s2) throws QueryResultHandlerException {
        // no-op
    }

    /**
     * @inheritDoc
     */
    @Override
    public void endHeader() throws QueryResultHandlerException {
        // no-op
    }

    /**
     * @inheritDoc
     */
    @Override
    public void handleBoolean(final boolean b) throws QueryResultHandlerException {
        throw new UnsupportedOperationException();
    }
}
