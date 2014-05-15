/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23.writer;

import org.deri.any23.extractor.ExtractionContext;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 * A simple {@link TripleHandler} that merely counts the number
 * of triples it has received.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class CountingTripleHandler implements TripleHandler {

    private int count = 0;

    public int getCount() {
        return count;
    }

    public void reset() {
        count = 0;
    }

    public void startDocument(URI documentURI) throws TripleHandlerException {
        // ignore
    }

    public void openContext(ExtractionContext context) throws TripleHandlerException {
        // ignore
    }

    public void closeContext(ExtractionContext context) throws TripleHandlerException {
        // ignore
    }

    public void receiveTriple(Resource s, URI p, Value o, URI g, ExtractionContext context)
    throws TripleHandlerException {
        count++;
    }

    public void receiveNamespace(String prefix, String uri, ExtractionContext context)
    throws TripleHandlerException {
        // ignore
    }

    public void close() throws TripleHandlerException {
        // ignore
    }

    public void endDocument(URI documentURI) throws TripleHandlerException {
        //ignore
    }

    public void setContentLength(long contentLength) {
        //ignore
    }
}
