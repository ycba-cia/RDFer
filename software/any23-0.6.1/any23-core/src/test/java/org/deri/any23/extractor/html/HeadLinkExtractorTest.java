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

package org.deri.any23.extractor.html;

import org.deri.any23.extractor.ExtractorFactory;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.RepositoryException;

/**
 * Test case for {@link HeadLinkExtractor}
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class HeadLinkExtractorTest extends AbstractExtractorTestCase {

    @Override
    protected ExtractorFactory<?> getExtractorFactory() {
        return HeadLinkExtractor.factory;
    }

    @Test
    public void testLinkExtraction() throws RepositoryException {
        assertExtracts("html/html-head-link-extractor.html");
        assertModelNotEmpty();
        final ValueFactory valueFactory = new ValueFactoryImpl();
        final URI externalLinkURI = valueFactory.createURI("http://www.myexperiment.org/workflows/16.rdf");
        assertContains(
                AbstractExtractorTestCase.baseURI,
                valueFactory.createURI("http://www.w3.org/1999/xhtml/vocab#alternate"),
                externalLinkURI

        );
        assertContains(
                externalLinkURI,
                valueFactory.createURI("http://purl.org/dc/terms/title"),
                valueFactory.createLiteral("RDF+XML")

        );
        assertContains(
                externalLinkURI,
                valueFactory.createURI("http://purl.org/dc/terms/format"),
                valueFactory.createLiteral("application/rdf+xml")

        );
    }
}
