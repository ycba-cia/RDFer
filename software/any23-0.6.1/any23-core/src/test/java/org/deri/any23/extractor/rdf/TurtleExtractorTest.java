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

package org.deri.any23.extractor.rdf;

import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionParameters;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.ExtractionResultImpl;
import org.deri.any23.rdf.RDFUtils;
import org.deri.any23.writer.RDFXMLWriter;
import org.deri.any23.writer.TripleHandler;
import org.deri.any23.writer.TripleHandlerException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Test case for {@link org.deri.any23.extractor.rdf.NTriplesExtractor}.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: TurtleExtractorTest.java 1366 2011-07-17 01:30:44Z michele.mostarda $
 */
public class TurtleExtractorTest {

    private static final Logger logger = LoggerFactory.getLogger(TurtleExtractorTest.class);

    private TurtleExtractor extractor;

    @Before
    public void setUp() {
        extractor = new TurtleExtractor();
    }

    @After
    public void tearDown() {
        extractor = null;
    }

    /**
     * Tests the correct support for a typed literal with incompatible value.
     * 
     * @throws IOException
     * @throws ExtractionException
     * @throws TripleHandlerException
     */
    @Test
    public void testTypedLiteralIncompatibleValueSupport()
    throws IOException, ExtractionException, TripleHandlerException {
        final URI uri = RDFUtils.uri("http://host.com/test-malformed-literal.turtle");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final TripleHandler th = new RDFXMLWriter(baos);
        final ExtractionContext extractionContext = new ExtractionContext("turtle-extractor", uri);
        final ExtractionResult result = new ExtractionResultImpl(extractionContext, extractor, th);
        extractor.setStopAtFirstError(false);
        try {
            extractor.run(
                    ExtractionParameters.getDefault(),
                    extractionContext,
                    this.getClass().getResourceAsStream("/org/deri/any23/extractor/rdf/testMalformedLiteral"),
                    result
            );
        } finally {
            logger.debug(baos.toString());
            th.close();
            result.close();
        }
    }

}
