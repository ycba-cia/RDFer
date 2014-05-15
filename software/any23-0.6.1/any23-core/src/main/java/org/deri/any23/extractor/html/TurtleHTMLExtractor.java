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

import org.deri.any23.extractor.ErrorReporter;
import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionParameters;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.Extractor;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.deri.any23.extractor.rdf.RDFParserFactory;
import org.deri.any23.rdf.PopularPrefixes;
import org.openrdf.model.URI;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.turtle.TurtleParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

/**
 * Extractor for <i>Turtle/N3</i> format embedded within <i>HTML</i>
 * <i>script</i> tags.
 *
 * See specification draft <a href="http://esw.w3.org/N3inHTML">here</a>. 
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TurtleHTMLExtractor implements Extractor.TagSoupDOMExtractor {

    public final static String NAME = "html-script-turtle";

    public final static ExtractorFactory<TurtleHTMLExtractor> factory =
            SimpleExtractorFactory.create(
                    NAME,
                    PopularPrefixes.get(),
                    Arrays.asList("text/html;q=0.02", "application/xhtml+xml;q=0.02"),
                    null,
                    TurtleHTMLExtractor.class
            );

    private TurtleParser turtleParser;

    public void run(
            ExtractionParameters extractionParameters,
            ExtractionContext extractionContext,
            Document in,
            ExtractionResult out
    ) throws IOException, ExtractionException {
        List<Node> scriptNodes;
        HTMLDocument htmlDocument = new HTMLDocument(in);
        final URI documentURI = extractionContext.getDocumentURI();

        scriptNodes = htmlDocument.findAll(".//SCRIPT[contains(@type,'text/turtle')]");
        processScriptNodes(documentURI, extractionContext, out, scriptNodes);

        scriptNodes = htmlDocument.findAll(".//SCRIPT[contains(@type,'text/n3')]");
        processScriptNodes(documentURI, extractionContext, out, scriptNodes);

        scriptNodes = htmlDocument.findAll(".//SCRIPT[contains(@type,'text/plain')]");
        processScriptNodes(documentURI, extractionContext,out, scriptNodes);
    }

    public ExtractorDescription getDescription() {
        return factory;
    }

    /**
     * Processes a list of <i>html script</i> nodes retrieving the N3 / Turtle content.
     *
     * @param documentURI the URI of the original HTML document.
     * @param er the extraction result used to store triples.
     * @param ns the list of script nodes.
     */
    private void processScriptNodes(URI documentURI, ExtractionContext ec, ExtractionResult er, List<Node> ns) {
        if(ns.size() > 0 && turtleParser == null) {
            turtleParser = RDFParserFactory.getInstance().getTurtleParserInstance(true, false, ec, er);
        }
        for(Node n : ns) {
            processScriptNode(turtleParser, documentURI, n, er);
        }
    }

    /**
     * Processes a single <i>html script</i> node.
     *
     * @param turtleParser the parser used to digest node content.
     * @param documentURI the URI of the original HTML document.
     * @param n the script node.
     * @param er the extraction result used to store triples.
     */
    private void processScriptNode(TurtleParser turtleParser, URI documentURI, Node n, ExtractionResult er) {
        final Node idAttribute = n.getAttributes().getNamedItem("id");
        final String graphName =
                documentURI.stringValue() +
                ( idAttribute == null ? "" : "#" +   idAttribute.getTextContent() ); 
        try {
            turtleParser.parse( new StringReader(n.getTextContent()), graphName );
        } catch (RDFParseException rdfpe) {
            er.notifyError(
                    ErrorReporter.ErrorLevel.ERROR,
                    String.format(
                            "An error occurred while parsing turtle content within script node: %s",
                            Arrays.toString( DomUtils.getXPathListForNode(n) )
                    ),
                    rdfpe.getLineNumber(), rdfpe.getColumnNumber()
            );
        } catch (Exception e) {
            er.notifyError(ErrorReporter.ErrorLevel.ERROR, "An error occurred while processing RDF data.", -1 , -1);
        }
    }

}