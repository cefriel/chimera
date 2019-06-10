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

import java.io.IOException;
import java.util.List;

import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryResultHandlerException;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.query.resultio.BooleanQueryResultFormat;
import org.eclipse.rdf4j.query.resultio.BooleanQueryResultWriter;



/**
 * <p>{@link BooleanQueryResultWriter} base for creating custom writer implementations which throws {@link UnsupportedOperationException}
 * exceptions for the non-boolean result format methods which are incorrectly in the {@link org.openrdf.query.resultio.QueryResultWriter} interface.</p>
 *
 * @author  Michael Grove
 * @since   1.0
 * @version 1.0
 */
public abstract class AbstractBooleanQueryResultWriter extends AbstractQueryResultWriter implements BooleanQueryResultWriter {

    protected AbstractBooleanQueryResultWriter(final BooleanQueryResultFormat theFormat) {
        super(theFormat);
    }

    protected abstract void writeBooleanValue(final boolean theValue) throws QueryResultHandlerException;

    /**
     * @inheritDoc
     */
    @Override
    public void startQueryResult(final List<String> theStrings) throws TupleQueryResultHandlerException {
        throw new UnsupportedOperationException();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void endQueryResult() throws TupleQueryResultHandlerException {
        throw new UnsupportedOperationException();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void handleSolution(final BindingSet theBindings) throws TupleQueryResultHandlerException {
        throw new UnsupportedOperationException();
    }

    /**
     * @inheritDoc
     */
    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public final void write(final boolean theValue) throws IOException {
        try {
            handleBoolean(theValue);
        }
        catch (QueryResultHandlerException e) {
            throw new IOException(e);
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public final void handleBoolean(final boolean theValue) throws QueryResultHandlerException {
        writeBooleanValue(theValue);
    }

    /**
     * @inheritDoc
     */
    @Override
    public final BooleanQueryResultFormat getBooleanQueryResultFormat() {
        return (BooleanQueryResultFormat) getQueryResultFormat();
    }
}
