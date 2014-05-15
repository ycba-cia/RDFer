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

package org.deri.any23.extractor.rdf;

import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.openrdf.rio.helpers.RDFParserBase;

import java.util.Arrays;

/**
 * Concrete implementation of {@link org.deri.any23.extractor.Extractor.ContentExtractor}
 * handling <a href="http://sw.deri.org/2008/07/n-quads/">N-Quads</a> format.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class NQuadsExtractor extends BaseRDFExtractor {

    public final static ExtractorFactory<NQuadsExtractor> factory =
        SimpleExtractorFactory.create(
                "rdf-nq",
                null,
                Arrays.asList(
                        "text/rdf+nq;q=0.1",
                        "text/nq;q=0.1",
                        "text/nquads;q=0.1",
                        "text/n-quads;q=0.1"
                ),
                "example-nquads.nq",
                NQuadsExtractor.class
        );

    public NQuadsExtractor(boolean verifyDataType, boolean stopAtFirstError) {
        super(verifyDataType, stopAtFirstError);
    }

    public NQuadsExtractor() {
        this(false, false);
    }

    public ExtractorDescription getDescription() {
        return factory;
    }

    @Override
    protected RDFParserBase getParser(ExtractionContext extractionContext, ExtractionResult extractionResult) {
        return RDFParserFactory.getInstance().getNQuadsParser(
                isVerifyDataType(), isStopAtFirstError(), extractionContext, extractionResult
        );
    }

}
