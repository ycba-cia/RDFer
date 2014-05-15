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

package org.deri.any23;

import junit.framework.Assert;
import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionParameters;
import org.deri.any23.extractor.Extractor;
import org.deri.any23.extractor.microdata.MicrodataExtractor;
import org.deri.any23.filter.IgnoreAccidentalRDFa;
import org.deri.any23.filter.IgnoreTitlesOfEmptyDocuments;
import org.deri.any23.http.DefaultHTTPClient;
import org.deri.any23.http.HTTPClient;
import org.deri.any23.source.DocumentSource;
import org.deri.any23.source.FileDocumentSource;
import org.deri.any23.source.HTTPDocumentSource;
import org.deri.any23.source.StringDocumentSource;
import org.deri.any23.util.StreamUtils;
import org.deri.any23.vocab.DCTERMS;
import org.deri.any23.writer.CompositeTripleHandler;
import org.deri.any23.writer.CountingTripleHandler;
import org.deri.any23.writer.NTriplesWriter;
import org.deri.any23.writer.RDFXMLWriter;
import org.deri.any23.writer.ReportingTripleHandler;
import org.deri.any23.writer.RepositoryWriter;
import org.deri.any23.writer.TripleHandler;
import org.deri.any23.writer.TripleHandlerException;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailException;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.deri.any23.extractor.ExtractionParameters.ValidationMode;

/**
 * Test case for {@link org.deri.any23.Any23} facade.
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 */
@SuppressWarnings("unchecked")
public class Any23Test {

    private static final DCTERMS vDCTERMS = DCTERMS.getInstance();

    private static final String PAGE_URL = "http://bob.com";

    private static final Logger logger = LoggerFactory.getLogger(Any23Test.class);

    @Test
    public void testTTLDetection() throws Exception {
        assertDetection("<a> <b> <c> .", "rdf-turtle");
    }

    @Test
    public void testN3Detection1() throws Exception {
        assertDetection("<Bob><brothers>(<Jim><Mark>).", "rdf-turtle");
    }

    @Test
    public void testN3Detection2() throws Exception {
        assertDetection(
                "<http://example.org/path> <http://foo.com> <http://example.org/Document/foo#> .",
                "rdf-nt"
        );
    }

    @Test
    public void testHTMLBruteForceDetection() throws Exception {
        assertDetection("<html><body><div class=\"vcard fn\">Joe</div></body></html>");
    }

    /**
     * This tests the behavior of <i>Any23</i> to execute the extraction explicitly specifying the charset
     * encoding of the input.
     *
     * @throws ExtractionException
     * @throws IOException
     * @throws SailException
     * @throws RepositoryException
     */
    @Test
    public void testExplicitEncoding()
    throws Exception {
        assertEncodingDetection(
                "UTF-8",
                new File("src/test/resources/html/encoding-test.html"),
                "Knud M\u00F6ller"
        );
    }

    /**
     * This tests the behavior of <i>Any23</i> to perform the extraction without passing it any charset encoding.
     * The encoding is therefore guessed using {@link org.deri.any23.encoding.TikaEncodingDetector} class.
     *
     * @throws ExtractionException
     * @throws IOException
     * @throws SailException
     * @throws RepositoryException
     * @throws org.deri.any23.writer.TripleHandlerException
     */
    @Test
    public void testImplicitEncoding()
    throws Exception {
        assertEncodingDetection(
                null, // The encoding will be auto detected.
                new File("src/test/resources/html/encoding-test.html"),
                "Knud M\u00F6ller"
        );
    }

    @Test
    public void testRDFXMLDetectionAndExtraction()
    throws Exception {
        String rdfXML =
                "<?xml version='1.0'?> " +
                "<rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#' " +
                        "xmlns:dc='http://purl.org/dc/elements/1.1/'>" +
                "<rdf:Description rdf:about='http://www.example.com'>" +
                "<dc:title>x</dc:title>" +
                "</rdf:Description>" +
                "</rdf:RDF>";
        assertDetectionAndExtraction(rdfXML);
    }

    @Test
    public void testNTriplesDetectionAndExtraction() throws Exception {
        String n3 = "<http://www.example.com> <http://purl.org/dc/elements/1.1/title> \"n3 . appo\" .";
        assertDetectionAndExtraction(n3);
    }

    @Test
    public void testNturtleDetectionAndExtraction() throws Exception {
        String nTurtle =
                "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
                "@prefix dc: <http://purl.org/dc/elements/1.1/> .\n" +
                "@prefix ex: <http://example.org/stuff/1.0/> .\n" +
                "\n" +
                "<http://www.w3.org/TR/rdf-syntax-grammar>\n" +
                "  dc:title \"RDF/XML Syntax Specification (Revised)\" ;\n" +
                "  ex:editor [\n" +
                "    ex:fullname \"Dave Beckett\";\n" +
                "    ex:homePage <http://purl.org/net/dajobe/>\n" +
                "  ] .";
        assertDetectionAndExtraction(nTurtle);
    }

    /**
     * Tests out the first code snipped used in <i>Developer Manual</i>.
     *
     * @throws IOException
     * @throws ExtractionException
     */
    @Test
    public void testDemoCodeSnippet1() throws Exception {
        /*1*/ Any23 runner = new Any23();
        /*2*/ final String content = "@prefix foo: <http://example.org/ns#> .   " +
                                     "@prefix : <http://other.example.org/ns#> ." +
                                     "foo:bar foo: : .                          " +
                                     ":bar : foo:bar .                           ";
        // The second argument of StringDocumentSource() must be a valid URI.
        /*3*/ DocumentSource source = new StringDocumentSource(content, "http://host.com/service");
        /*4*/ ByteArrayOutputStream out = new ByteArrayOutputStream();
        /*5*/ TripleHandler handler = new NTriplesWriter(out);
        /*6*/ runner.extract(source, handler);
        /*7*/ String n3 = out.toString("UTF-8");

        /*
            <http://example.org/ns#bar> <http://example.org/ns#> <http://other.example.org/ns#> .
            <http://other.example.org/ns#bar> <http://other.example.org/ns#> <http://example.org/ns#bar> .
         */
        logger.debug("n3: " + n3);
        Assert.assertTrue(n3.length() > 0);
    }

    /**
     * Tests out the second code snipped used in <i>Developer Manual</i>.
     *
     * @throws IOException
     * @throws ExtractionException
     */
    // Deactivated to avoid test dependency on external resources.
    // @Test
    public void testDemoCodeSnippet2()
    throws IOException, ExtractionException, URISyntaxException, SailException, RepositoryException {
        /*1*/ Any23 runner = new Any23();
        /*2*/ runner.setHTTPUserAgent("test-user-agent");
        /*3*/ HTTPClient httpClient = runner.getHTTPClient();
        /*4*/ DocumentSource source = new HTTPDocumentSource(
                 httpClient,
                 "http://www.rentalinrome.com/semanticloft/semanticloft.htm"
              );
        /*5*/ ByteArrayOutputStream out = new ByteArrayOutputStream();
        /*6*/ TripleHandler handler = new NTriplesWriter(out);
        /*7*/ runner.extract(source, handler);
        /*8*/ String n3 = out.toString("UTF-8");

        /*
            <http://www.rentalinrome.com/semanticloft/semanticloft.htm>
            <http://purl.org/dc/terms/title>
            "Semantic Loft (beta) - Trastevere apartments | Rental in Rome - rentalinrome.com" .
            [...]
         */
        logger.debug("N3 "+ n3);
        Assert.assertTrue(n3.length() > 0);
    }

    /**
     * This test checks the extraction behavior when the library is used programatically.
     * This test is related to the issue #45, to verify the different behaviors between Maven and Ant.
     * The behavior was related to a 2nd-level dependency introduced by Maven.
     *
     * @throws ExtractionException
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testProgrammaticExtraction() throws ExtractionException, IOException, URISyntaxException {
        Any23 any23 = new Any23();
        any23.setHTTPUserAgent("Any23-Servlet");
        any23.setHTTPClient(new DefaultHTTPClient() {
            @Override
            protected int getConnectionTimeout() {
                return 5000;
            }

            @Override
            protected int getSoTimeout() {
                return 2000;
            }
        });
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        TripleHandler handler = new RDFXMLWriter(byteArrayOutputStream);
        TripleHandler rdfWriter = new IgnoreAccidentalRDFa(handler);
        ReportingTripleHandler reporting = new ReportingTripleHandler(rdfWriter);

        DocumentSource source = new FileDocumentSource(
                new File("src/test/resources/html/rdfa/ansa_2010-02-26_12645863.html"),
                    "http://host.com/service");

        Assert.assertTrue( any23.extract(source, reporting).hasMatchingExtractors() );
        try {
            handler.close();
        } catch (TripleHandlerException e) {
            Assert.fail(e.getMessage());
        }

        String bufferContent = byteArrayOutputStream.toString();
        logger.debug(bufferContent);
        int i = 0;
        int counter = 0;
        while( i < bufferContent.length() ) {
            i = bufferContent.indexOf("\n", i);
            if(i == -1) {
                break;
            }
            counter++;
            i++;
        }
        Assert.assertSame("Unexpected number of triples.", 43, counter);
        
    }

    /**
     * This test checks if a URL that is supposed to be GZIPPED is correctly opend and parsed with
     * the {@link org.deri.any23.Any23} facade.
     *
     * @throws IOException
     * @throws URISyntaxException
     * @throws ExtractionException
     */
    // Deactivated to avoid test dependency on external resources.    
    //@Test
    public void testGZippedContent() throws IOException, URISyntaxException, ExtractionException {
        Any23 runner = new Any23();
        runner.setHTTPUserAgent("test-user-agent");
        HTTPClient httpClient = runner.getHTTPClient();
        DocumentSource source = new HTTPDocumentSource(
                httpClient,
                "http://products.semweb.bestbuy.com/y/products/7590289/"
        );
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        TripleHandler handler = new NTriplesWriter(out);
        runner.extract(source, handler);
        String n3 = out.toString("UTF-8");

        logger.debug("N3 " + n3);
        Assert.assertTrue(n3.length() > 0);

    }

    @Test
    public void testExtractionParameters() throws IOException, ExtractionException {
        Any23 runner = new Any23();
        DocumentSource source = new FileDocumentSource(
                new File("src/test/resources/org/deri/any23/validator/missing-og-namespace.html"),
                "http://www.test.com"
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        CountingTripleHandler cth1 = new CountingTripleHandler();
        NTriplesWriter ctw1 = new NTriplesWriter(baos);
        CompositeTripleHandler compositeTH1 = new CompositeTripleHandler();
        compositeTH1.addChild(cth1);
        compositeTH1.addChild(ctw1);
        runner.extract(new ExtractionParameters(ValidationMode.None), source, compositeTH1);
        logger.debug(baos.toString());
        Assert.assertEquals("Unexpected number of triples.", 5, cth1.getCount() );

        baos.reset();
        CountingTripleHandler cth2 = new CountingTripleHandler();
        NTriplesWriter ctw2 = new NTriplesWriter(baos);
        CompositeTripleHandler compositeTH2 = new CompositeTripleHandler();
        compositeTH2.addChild(cth2);
        compositeTH2.addChild(ctw2);
        runner.extract(new ExtractionParameters(ValidationMode.ValidateAndFix), source,  compositeTH2);
        logger.debug( baos.toString() );
        Assert.assertEquals("Unexpected number of triples.", 10, cth2.getCount() );
    }

    @Test
    public void testExtractionParametersWithNestingDisabled() throws IOException, ExtractionException {
        Any23 runner = new Any23();
        DocumentSource source = new FileDocumentSource(
                new File("src/test/resources/microformats/nested-microformats-a1.html"),
                "http://www.test.com"
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        CountingTripleHandler cth1 = new CountingTripleHandler();
        RDFXMLWriter ctw1 = new RDFXMLWriter(baos);
        CompositeTripleHandler compositeTH1 = new CompositeTripleHandler();
        compositeTH1.addChild(cth1);
        compositeTH1.addChild(ctw1);
        runner.extract(new ExtractionParameters(ValidationMode.None, true), source,  compositeTH1);
        logger.debug( baos.toString() );
        Assert.assertEquals("Unexpected number of triples.", 26, cth1.getCount() );

        baos.reset();
        CountingTripleHandler cth2 = new CountingTripleHandler();
        NTriplesWriter ctw2 = new NTriplesWriter(baos);
        CompositeTripleHandler compositeTH2 = new CompositeTripleHandler();
        compositeTH2.addChild(cth2);
        compositeTH2.addChild(ctw2);
        runner.extract(new ExtractionParameters(ValidationMode.ValidateAndFix, false), source,  compositeTH2);
        logger.debug( baos.toString() );
        Assert.assertEquals("Unexpected number of triples.", 23, cth2.getCount() );
    }

    @Test
    public void testExceptionPropagation() throws IOException {
        Any23 any23 = new Any23();
        DocumentSource source = new FileDocumentSource(
                new File("src/test/resources/application/turtle/geolinkeddata.ttl"),
                "http://www.test.com"
        );
        CountingTripleHandler cth1 = new CountingTripleHandler();
        try {
            any23.extract(source, cth1);
        } catch (ExtractionException e) {
            Assert.assertTrue(e.getCause() instanceof RDFParseException);
        }

    }

    /**
     * Test correct management of general <i>XML</i> content.
     *
     * @throws IOException
     * @throws ExtractionException
     */
    @Test
    public void testXMLMimeTypeManagement() throws IOException, ExtractionException {
        final String documentURI = "http://www.test.com/resource.xml";
        final String contentType = "application/xml";
        final String in = StreamUtils.asString( this.getClass().getResourceAsStream("any23-xml-mimetype.xml") );
        final DocumentSource doc = new StringDocumentSource(in, documentURI, contentType);
        final Any23 any23 = new Any23();
        final CountingTripleHandler cth = new CountingTripleHandler(){
            @Override
            public void receiveTriple(Resource s, URI p, Value o, URI g, ExtractionContext context)
            throws TripleHandlerException {
                super.receiveTriple(s, p, o, g, context);
                logger.debug( String.format("%s %s %s %s %s\n", s, p, o, g, context) );
            }
        };
        final ReportingTripleHandler rth = new ReportingTripleHandler(cth);
        final ExtractionReport report = any23.extract(doc, rth);
        Assert.assertFalse(report.hasMatchingExtractors());
        Assert.assertEquals(2, cth.getCount());
    }

    /**
     * Test correct management of general <i>XML</i> content from <i>URL</i> source.
     *
     * @throws IOException
     * @throws ExtractionException
     */
    // @Test Deactivated online test.
    public void testXMLMimeTypeManagementViaURL() throws IOException, ExtractionException {
        final Any23 any23 = new Any23();
        any23.setHTTPUserAgent("test-user-agent");
        final CountingTripleHandler cth = new CountingTripleHandler(){
            @Override
            public void receiveTriple(Resource s, URI p, Value o, URI g, ExtractionContext context)
            throws TripleHandlerException {
                super.receiveTriple(s, p, o, g, context);
                logger.debug( String.format("%s %s %s %s %s\n", s, p, o, g, context) );
            }
        };
        final ReportingTripleHandler rth = new ReportingTripleHandler(cth);
        final ExtractionReport report = any23.extract("http://www.nativeremedies.com/XML/combos.xml", rth);
        Assert.assertFalse(report.hasMatchingExtractors());
        Assert.assertEquals(2, cth.getCount());
    }

    @Test
    public void testBlankNodesViaURL() throws IOException, ExtractionException {
        final Any23 any23 = new Any23();
        any23.setHTTPUserAgent("test-user-agent");
        final CountingTripleHandler cth = new CountingTripleHandler(){
            @Override
            public void receiveTriple(Resource s, URI p, Value o, URI g, ExtractionContext context)
            throws TripleHandlerException {
                super.receiveTriple(s, p, o, g, context);
                logger.debug( String.format("%s %s %s %s %s\n", s, p, o, g, context) );
            }
        };
        final ReportingTripleHandler rth = new ReportingTripleHandler(cth);
        final ExtractionReport report = any23.extract("http://www.usarab.org/news/?tag=england", rth);
    }


    @Test
    public void testMicrodataSupport() throws Exception {
        final String htmlWithMicrodata = StreamUtils.asString(
                this.getClass().getResourceAsStream("/microdata/microdata-basic.html")
        );
        assertExtractorActivation(htmlWithMicrodata, MicrodataExtractor.class);
    }

    /**
     * Performs detection and extraction on the given input string
     * and return the {@link ExtractionReport}.
     *
     * @param in input string.
     * @return
     * @throws IOException
     * @throws ExtractionException
     */
    private ExtractionReport detectAndExtract(String in) throws Exception {
        Any23 any23 = new Any23();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ReportingTripleHandler outputHandler = new ReportingTripleHandler(
                new IgnoreAccidentalRDFa(
                        new IgnoreTitlesOfEmptyDocuments(
                                new NTriplesWriter(out)
                        )
                )
        );
        return any23.extract(in, "http://host.com/path", outputHandler);
    }

    /**
     * Asserts that a list an {@link Extractor} has been activated for the given input data.
     *
     * @param in input data as string.
     * @throws IOException
     * @throws ExtractionException
     */
    private void assertDetectionAndExtraction(String in) throws Exception {
        final ExtractionReport extractionReport = detectAndExtract(in);
        Assert.assertTrue(
                "Detection and extraction failed, no matching extractors.",
                extractionReport.hasMatchingExtractors()
        );
    }

    /**
     * Assert the correct activation of the given list of {@link Extractor}s for the given input string.
     *
     * @param in input data as string.
     * @param expectedExtractors
     * @throws IOException
     * @throws ExtractionException
     */
    private void assertExtractorActivation(String in, Class<? extends Extractor>... expectedExtractors)
    throws Exception {
        final ExtractionReport extractionReport = detectAndExtract(in);
        for (Class<? extends Extractor> expectedExtractorClass : expectedExtractors) {
            Assert.assertTrue(
                    String.format(
                            "Detection and extraction failed, expected extractor [%s] not found.",
                            expectedExtractorClass
                    ),
                    containsClass( extractionReport.getMatchingExtractors(), expectedExtractorClass )
            );
        }
    }

    /**
     * Asserts the correct encoding detection for a specified data.
     *
     * @param encoding the expected specified encoding, if <code>null</code> will be auto detected.
     * @param input
     * @param expectedContent
     * @throws Exception
     */
    private void assertEncodingDetection(String encoding, File input, String expectedContent)
    throws Exception {
        FileDocumentSource fileDocumentSource;
        Any23 any23;
        RepositoryConnection conn;
        RepositoryWriter repositoryWriter;

        fileDocumentSource = new FileDocumentSource(input);
        any23 = new Any23();
        Sail store = new MemoryStore();
        store.initialize();
        conn = new SailRepository(store).getConnection();
        repositoryWriter = new RepositoryWriter(conn);
        Assert.assertTrue( any23.extract(fileDocumentSource, repositoryWriter, encoding).hasMatchingExtractors() );

        RepositoryResult<Statement> statements = conn.getStatements(null, vDCTERMS.title, null, false);
        try {
            while (statements.hasNext()) {
                Statement statement = statements.next();
                printStatement(statement);
                org.junit.Assert.assertTrue(statement.getObject().stringValue().contains(expectedContent));
            }
        } finally {
            statements.close();
        }

        fileDocumentSource = null;
        any23 = null;
        conn.close();
        repositoryWriter.close();
    }

    /**
     * Will try to detect the <i>content</i> trying sequentially with all
     * specified parser.
     *
     * @param content
     * @param parsers
     * @throws Exception
     */
    private void assertDetection(String content, String... parsers) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Any23 runner = new Any23(parsers.length == 0 ? null : parsers);
        if (parsers.length != 0) {
            runner.setMIMETypeDetector(null);   // Use all the provided extractors.
        }
        runner.extract(new StringDocumentSource(content, PAGE_URL), new NTriplesWriter(out));
        String result = out.toString("us-ascii");
        Assert.assertNotNull(result);
        Assert.assertTrue(result.length() > 10);
    }

    private void printStatement(Statement statement) {
        logger.debug(String.format("%s\t%s\t%s",
                statement.getSubject(),
                statement.getPredicate(),
                statement.getObject()));
    }

    private boolean containsClass(List<?> list, Class clazz) {
        for(Object o : list) {
            if(o.getClass().equals(clazz)) {
                return true;
            }
        }
        return false;
    }

}
