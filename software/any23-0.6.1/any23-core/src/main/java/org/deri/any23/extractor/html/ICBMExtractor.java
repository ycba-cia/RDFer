/**
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
 *
 */

package org.deri.any23.extractor.html;

import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionParameters;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.deri.any23.rdf.Any23ValueFactoryWrapper;
import org.deri.any23.rdf.PopularPrefixes;
import org.openrdf.model.BNode;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.w3c.dom.Document;

import java.io.IOException;
import java.util.Arrays;

/**
 * Extractor for "ICBM coordinates" provided as META headers in the head
 * of an HTML page.
 *
 * @author Gabriele Renzi
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ICBMExtractor implements TagSoupDOMExtractor {

    public final static ExtractorFactory<ICBMExtractor> factory =
            SimpleExtractorFactory.create(
                    "html-head-icbm",
                    PopularPrefixes.createSubset("geo", "rdf"),
                    Arrays.asList("text/html;q=0.01", "application/xhtml+xml;q=0.01"),
                    null,
                    ICBMExtractor.class
            );

    public void run(
            ExtractionParameters extractionParameters,
            ExtractionContext extractionContext,
            Document in,
            ExtractionResult out
    ) throws IOException, ExtractionException {

        // ICBM is the preferred method, if two values are available it is meaningless to read both
        String props = DomUtils.find(in, "//META[@name=\"ICBM\" or @name=\"geo.position\"]/@content");
        if ("".equals(props)) return;

        String[] coords = props.split("[;,]");
        float lat, lon;
        try {
            lat = Float.parseFloat(coords[0]);
            lon = Float.parseFloat(coords[1]);
        } catch (NumberFormatException nfe) {
            return;
        }

        final ValueFactory factory = new Any23ValueFactoryWrapper(ValueFactoryImpl.getInstance(), out);
        BNode point = factory.createBNode();
        out.writeTriple(extractionContext.getDocumentURI(), expand("dcterms:related"), point);
        out.writeTriple(point, expand("rdf:type"), expand("geo:Point"));
        out.writeTriple(point, expand("geo:lat"), factory.createLiteral(Float.toString(lat)));
        out.writeTriple(point, expand("geo:long"), factory.createLiteral(Float.toString(lon)));
    }

    private URI expand(String curie) {
        return factory.getPrefixes().expand(curie);
    }

    public ExtractorDescription getDescription() {
        return factory;
    }

}