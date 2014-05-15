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

import java.util.Collection;
import java.util.HashSet;

/**
 * A {@link org.deri.any23.writer.TripleHandler} that collects
 * various information about the extraction process, such as
 * the extractors used and the total number of triples.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ReportingTripleHandler implements TripleHandler {

    private final TripleHandler wrapped;

    private final Collection<String> extractorNames = new HashSet<String>();
    private int totalTriples   = 0;
    private int totalDocuments = 0;

    public ReportingTripleHandler(TripleHandler wrapped) {
        if(wrapped == null) {
            throw new NullPointerException("wrapped cannot be null.");
        }
        this.wrapped = wrapped;
    }

    public Collection<String> getExtractorNames() {
        return extractorNames;
    }

    public int getTotalTriples() {
        return totalTriples;
    }

    public int getTotalDocuments() {
        return totalDocuments;
    }

    public void startDocument(URI documentURI) throws TripleHandlerException {
        totalDocuments++;
        wrapped.startDocument(documentURI);
    }

    public void openContext(ExtractionContext context) throws TripleHandlerException {
        wrapped.openContext(context);
    }

    public void receiveNamespace(
            String prefix,
            String uri,
            ExtractionContext context
    ) throws TripleHandlerException {
        wrapped.receiveNamespace(prefix, uri, context);
    }

    public void receiveTriple(
            Resource s,
            URI p,
            Value o,
            URI g,
            ExtractionContext context
    ) throws TripleHandlerException {
        extractorNames.add(context.getExtractorName());
        totalTriples++;
        wrapped.receiveTriple(s, p, o, g, context);
    }

    public void setContentLength(long contentLength) {
        wrapped.setContentLength(contentLength);
    }

    public void closeContext(ExtractionContext context) throws TripleHandlerException {
        wrapped.closeContext(context);
    }

    public void endDocument(URI documentURI) throws TripleHandlerException {
        wrapped.endDocument(documentURI);
    }

    public void close() throws TripleHandlerException {
        wrapped.close();
    }

}