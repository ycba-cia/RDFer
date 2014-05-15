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

package org.deri.any23.plugin.htmlscraper;

import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.extractors.CanolaExtractor;
import de.l3s.boilerpipe.extractors.DefaultExtractor;
import de.l3s.boilerpipe.extractors.LargestContentExtractor;
import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionParameters;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.Extractor;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.deri.any23.vocab.SINDICE;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of content extractor for performing <i>HTML<i/> scraping.
 *
 * @see HTMLScraperPlugin
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class HTMLScraperExtractor implements Extractor.ContentExtractor {

    public final static String NAME = "html-scraper";

    public final static URI PAGE_CONTENT_DE_PROPERTY  =
            ValueFactoryImpl.getInstance().createURI(SINDICE.NS + "pagecontent/de");
    public final static URI PAGE_CONTENT_AE_PROPERTY  =
            ValueFactoryImpl.getInstance().createURI(SINDICE.NS + "pagecontent/ae");
    public final static URI PAGE_CONTENT_LCE_PROPERTY =
            ValueFactoryImpl.getInstance().createURI(SINDICE.NS + "pagecontent/lce");
    public final static URI PAGE_CONTENT_CE_PROPERTY  =
            ValueFactoryImpl.getInstance().createURI(SINDICE.NS + "pagecontent/ce");

    protected final static ExtractorFactory<HTMLScraperExtractor> factory =
            SimpleExtractorFactory.create(
                    NAME,
                    null,
                    Arrays.asList("text/html;q=0.02", "application/xhtml+xml;q=0.02"),
                    null,
                    HTMLScraperExtractor.class
            );

    private final List<ExtractionRule> extractionRules = new ArrayList<ExtractionRule>();

    public HTMLScraperExtractor() {
        loadDefaultRules();
    }

    public void addTextExtractor(String name, URI property, BoilerpipeExtractor extractor) {
        extractionRules.add( new ExtractionRule(name, property, extractor) );
    }

    public String[] getTextExtractors() {
        final List<String> extractors = new ArrayList<String>();
        for(ExtractionRule er : extractionRules) {
            extractors.add(er.name);
        }
        return extractors.toArray( new String[extractors.size()] );
    }

    public void run(
            ExtractionParameters extractionParameters,
            ExtractionContext extractionContext,
            InputStream inputStream,
            ExtractionResult extractionResult
    ) throws IOException, ExtractionException {
        try {
            final URI documentURI = extractionContext.getDocumentURI();
            for (ExtractionRule extractionRule : extractionRules) {
                final String content = extractionRule.boilerpipeExtractor.getText(new InputStreamReader(inputStream));
                extractionResult.writeTriple(
                        documentURI,
                        extractionRule.property,
                        ValueFactoryImpl.getInstance().createLiteral(content)
                );
            }
        } catch (BoilerpipeProcessingException bpe) {
            throw new ExtractionException("Error while applying text processor " + ArticleExtractor.class, bpe);
        }
    }

    public ExtractorFactory getDescription() {
        return factory;
    }

    public void setStopAtFirstError(boolean b) {
        // Ignored.
    }

    private void loadDefaultRules() {
        addTextExtractor("default-extractor"      , PAGE_CONTENT_DE_PROPERTY , DefaultExtractor.getInstance());
        addTextExtractor("article-extractor"      , PAGE_CONTENT_AE_PROPERTY , ArticleExtractor.getInstance());
        addTextExtractor("large-content-extractor", PAGE_CONTENT_LCE_PROPERTY, LargestContentExtractor.getInstance());
        addTextExtractor("canola-extractor"       , PAGE_CONTENT_CE_PROPERTY , CanolaExtractor.getInstance());
    }

    /**
     * This class associates a <i>BoilerPipe</i> extractor with the property going to host the extracted content.
     */
    class ExtractionRule {

        public final String name;
        public final URI property;
        public final BoilerpipeExtractor boilerpipeExtractor;

        ExtractionRule(String name, URI property, BoilerpipeExtractor boilerpipeExtractor) {
            if(name == null) {
                throw new NullPointerException("name cannot be null.");
            }
            if(property == null) {
                throw new NullPointerException("property cannot be null.");
            }
            if(boilerpipeExtractor == null) {
                throw new NullPointerException("extractor cannot be null.");
            }
            this.name = name;
            this.property = property;
            this.boilerpipeExtractor = boilerpipeExtractor;
        }

    }
}
