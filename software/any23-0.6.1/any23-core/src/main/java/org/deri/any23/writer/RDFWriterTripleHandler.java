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
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;

/**
 * A {@link org.deri.any23.writer.TripleHandler} that writes
 * triples to a Sesame {@link org.openrdf.rio.RDFWriter},
 * eg for serialization using one of Sesame's writers.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
class RDFWriterTripleHandler implements TripleHandler {

    private final RDFWriter writer;

    private boolean closed = false;

    private ExtractionContext currentContext;

    RDFWriterTripleHandler(RDFWriter destination) {
        writer = destination;
        try {
            writer.startRDF();
        } catch (RDFHandlerException e) {
            throw new RuntimeException(e);
        }
    }

    public void startDocument(URI documentURI) throws TripleHandlerException {
        // Empty.
    }

    public void openContext(ExtractionContext context) throws TripleHandlerException {
        currentContext = context;
    }

    public void receiveTriple(Resource s, URI p, Value o, URI g, ExtractionContext context)
    throws TripleHandlerException {
        final URI graph = g == null ? context.getDocumentURI() : g;
        try {
            writer.handleStatement(
                    ValueFactoryImpl.getInstance().createStatement(s, p, o, graph));
        } catch (RDFHandlerException ex) {
            throw new TripleHandlerException(
                    String.format("Error while receiving triple: %s %s %s %s", s, p, o, graph),
                    ex
            );
        }
    }

    public void receiveNamespace(String prefix, String uri, ExtractionContext context)
    throws TripleHandlerException {
        try {
            writer.handleNamespace(prefix, uri);
        } catch (RDFHandlerException ex) {
            throw new TripleHandlerException(String.format("Error while receiving namespace: %s:%s", prefix, uri),
                    ex
            );
        }
    }

    public void closeContext(ExtractionContext context) throws TripleHandlerException {
        currentContext = null;
    }

    public void close() throws TripleHandlerException {
        if (closed) return;
        closed = true;
        try {
            writer.endRDF();
        } catch (RDFHandlerException e) {
            throw new TripleHandlerException("Error while closing the triple handler.", e);
        }
    }

    public void endDocument(URI documentURI) throws TripleHandlerException {
        // Empty.
    }

    public void setContentLength(long contentLength) {
        // Empty.
    }
    
}
