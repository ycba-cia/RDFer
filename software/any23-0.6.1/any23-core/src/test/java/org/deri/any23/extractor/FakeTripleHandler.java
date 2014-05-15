/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.deri.any23.extractor;

import org.deri.any23.writer.TripleHandler;
import org.deri.any23.writer.TripleHandlerException;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 * Trivial implementation of {@link org.deri.any23.writer.TripleHandler} doing nothing.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: FakeTripleHandler.java 498 2010-04-23 14:10:14Z michele.mostarda $
 */
public class FakeTripleHandler implements TripleHandler {

    public void startDocument(URI documentURI) {
        // Empty.
    }

    public void openContext(ExtractionContext context) {
        // Empty.
    }

    public void receiveTriple(Resource s, URI p, Value o, URI g, ExtractionContext context)
    throws TripleHandlerException {
        // Empty.
    }

    public void receiveTriple(Resource s, URI p, Value o, ExtractionContext context) {
        // Empty.
    }

    public void receiveNamespace(String prefix, String uri, ExtractionContext context) {
        // Empty.
    }

    public void closeContext(ExtractionContext context) {
        // Empty.
    }

    public void endDocument(URI documentURI) {
        // Empty.
    }

    public void setContentLength(long contentLength) {
        // Empty.
    }

    public void close() {
        // Empty.
    }

}
