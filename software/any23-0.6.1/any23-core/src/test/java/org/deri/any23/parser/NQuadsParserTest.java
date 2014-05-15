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

package org.deri.any23.parser;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.ParseLocationListener;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;

/**
 * Test case for {@link org.deri.any23.parser.NQuadsParser}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class NQuadsParserTest {

    private static final Logger logger = LoggerFactory.getLogger(NQuadsParser.class);

    private NQuadsParser parser;

    @Before
    public void setUp() {
        parser = new NQuadsParser();
        parser.setVerifyData(true);
        parser.setDatatypeHandling(RDFParser.DatatypeHandling.VERIFY);
        parser.setStopAtFirstError(true);
    }

    @After
    public void tearDown() {
        parser = null;
    }

    /**
     * Tests basic N-Quads parsing.
     *
     * @throws RDFHandlerException
     * @throws IOException
     * @throws RDFParseException
     */
    @Test
    public void testParseBasic() throws RDFHandlerException, IOException, RDFParseException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(
            "<http://www.v/dat/4b><http://www.w3.org/20/ica#dtend><http://sin/value/2><http://sin.siteserv.org/def/>."
            .getBytes()
        );
        final TestRDFHandler rdfHandler = new TestRDFHandler();
        parser.setRDFHandler(rdfHandler);
        parser.parse(bais, "http://test.base.uri");
        Assert.assertThat(rdfHandler.getStatements().size(), is(1));
        final Statement statement = rdfHandler.getStatements().get(0);
        Assert.assertEquals("http://www.v/dat/4b", statement.getSubject().stringValue());
        Assert.assertEquals("http://www.w3.org/20/ica#dtend", statement.getPredicate().stringValue());
        Assert.assertTrue(statement.getObject() instanceof URI);
        Assert.assertEquals("http://sin/value/2", statement.getObject().stringValue());
        Assert.assertEquals("http://sin.siteserv.org/def/", statement.getContext().stringValue());
    }

    /**
     * Tests basic N-Quads parsing with blank node.
     *
     * @throws RDFHandlerException
     * @throws IOException
     * @throws RDFParseException
     */
    @Test
    public void testParseBasicBNode() throws RDFHandlerException, IOException, RDFParseException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(
            "_:123456768<http://www.w3.org/20/ica#dtend><http://sin/value/2><http://sin.siteserv.org/def/>."
            .getBytes()
        );
        final TestRDFHandler rdfHandler = new TestRDFHandler();
        parser.setRDFHandler(rdfHandler);
        parser.parse(bais, "http://test.base.uri");
        Assert.assertThat(rdfHandler.getStatements().size(), is(1));
        final Statement statement = rdfHandler.getStatements().get(0);
        Assert.assertTrue(statement.getSubject() instanceof BNode);
        Assert.assertEquals("http://www.w3.org/20/ica#dtend", statement.getPredicate().stringValue());
        Assert.assertTrue(statement.getObject() instanceof URI);
        Assert.assertEquals("http://sin/value/2", statement.getObject().stringValue());
        Assert.assertEquals("http://sin.siteserv.org/def/", statement.getContext().stringValue());
    }

    /**
     * Tests basic N-Quads parsing with literal.
     *
     * @throws RDFHandlerException
     * @throws IOException
     * @throws RDFParseException
     */
    @Test
    public void testParseBasicLiteral() throws RDFHandlerException, IOException, RDFParseException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(
            "_:123456768<http://www.w3.org/20/ica#dtend>\"2010-05-02\"<http://sin.siteserv.org/def/>."
            .getBytes()
        );
        final TestRDFHandler rdfHandler = new TestRDFHandler();
        parser.setRDFHandler(rdfHandler);
        parser.parse(bais, "http://test.base.uri");
        Assert.assertThat(rdfHandler.getStatements().size(), is(1));
        final Statement statement = rdfHandler.getStatements().get(0);
        Assert.assertTrue(statement.getSubject() instanceof BNode);
        Assert.assertEquals("http://www.w3.org/20/ica#dtend", statement.getPredicate().stringValue());
        Assert.assertTrue(statement.getObject() instanceof Literal);
        Assert.assertEquals("2010-05-02", statement.getObject().stringValue());
        Assert.assertEquals("http://sin.siteserv.org/def/", statement.getContext().stringValue());
    }

    /**
     * Tests N-Quads parsing with literal and language.
     * 
     * @throws RDFHandlerException
     * @throws IOException
     * @throws RDFParseException
     */
    @Test
    public void testParseBasicLiteralLang() throws RDFHandlerException, IOException, RDFParseException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(
            "<http://www.v/dat/4b2-21><http://www.w3.org/20/ica#dtend>\"2010-05-02\"@en<http://sin.siteserv.org/def/>."
            .getBytes()
        );
        final TestRDFHandler rdfHandler = new TestRDFHandler();
        parser.setRDFHandler(rdfHandler);
        parser.parse(bais, "http://test.base.uri");
        final Statement statement = rdfHandler.getStatements().get(0);
        Assert.assertEquals("http://www.v/dat/4b2-21", statement.getSubject().stringValue());
        Assert.assertEquals("http://www.w3.org/20/ica#dtend", statement.getPredicate().stringValue());
        Assert.assertTrue(statement.getObject() instanceof Literal);
        Literal object = (Literal) statement.getObject();
        Assert.assertEquals("2010-05-02", object.stringValue());
        Assert.assertEquals("en", object.getLanguage());
        Assert.assertNull("en", object.getDatatype());
        Assert.assertEquals("http://sin.siteserv.org/def/", statement.getContext().stringValue());
    }

    /**
     * Tests N-Quads parsing with literal and datatype.
     * 
     * @throws RDFHandlerException
     * @throws IOException
     * @throws RDFParseException
     */
    @Test
    public void testParseBasicLiteraDatatype() throws RDFHandlerException, IOException, RDFParseException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(
            ("<http://www.v/dat/4b2-21>" +
             "<http://www.w3.org/20/ica#dtend>" +
             "\"2010\"^^<http://www.w3.org/2001/XMLSchema#integer>" +
             "<http://sin.siteserv.org/def/>."
            ).getBytes()
        );
        final TestRDFHandler rdfHandler = new TestRDFHandler();
        parser.setRDFHandler(rdfHandler);
        parser.parse(bais, "http://test.base.uri");
        final Statement statement = rdfHandler.getStatements().get(0);
        Assert.assertEquals("http://www.v/dat/4b2-21", statement.getSubject().stringValue());
        Assert.assertEquals("http://www.w3.org/20/ica#dtend", statement.getPredicate().stringValue());
        Assert.assertTrue(statement.getObject() instanceof Literal);
        Literal object = (Literal) statement.getObject();
        Assert.assertEquals("2010", object.stringValue());
        Assert.assertNull(object.getLanguage());
        Assert.assertEquals("http://www.w3.org/2001/XMLSchema#integer", object.getDatatype().toString());
        Assert.assertEquals("http://sin.siteserv.org/def/", statement.getContext().stringValue());
    }

    /**
     * Tests the correct support for literal escaping.
     *
     * @throws RDFHandlerException
     * @throws IOException
     * @throws RDFParseException
     */
    @Test
    public void testLiteralEscapeManagement1()
    throws RDFHandlerException, IOException, RDFParseException {
        TestParseLocationListener parseLocationListener = new TestParseLocationListener();
        TestRDFHandler rdfHandler = new TestRDFHandler();
        parser.setParseLocationListener(parseLocationListener);
        parser.setRDFHandler(rdfHandler);

        final ByteArrayInputStream bais = new ByteArrayInputStream(
            "<http://a> <http://b> \"\\\\\" <http://c> .".getBytes()
        );
        parser.parse(bais, "http://base-uri");

        rdfHandler.assertHandler(1);
        parseLocationListener.assertListener(1, 40);
    }

    /**
     * Tests the correct support for literal escaping.
     *
     * @throws RDFHandlerException
     * @throws IOException
     * @throws RDFParseException
     */
    @Test
    public void testLiteralEscapeManagement2()
    throws RDFHandlerException, IOException, RDFParseException {
        TestParseLocationListener parseLocationListener = new TestParseLocationListener();
        TestRDFHandler rdfHandler = new TestRDFHandler();
        parser.setParseLocationListener(parseLocationListener);
        parser.setRDFHandler(rdfHandler);

        final ByteArrayInputStream bais = new ByteArrayInputStream(
            "<http://a> <http://b> \"Line text 1\\nLine text 2\" <http://c> .".getBytes()
        );
        parser.parse(bais, "http://base-uri");

        rdfHandler.assertHandler(1);
        final Value object = rdfHandler.getStatements().get(0).getObject();
        Assert.assertTrue( object instanceof Literal);
        final String literalContent = ((Literal) object).getLabel();
        Assert.assertEquals("Line text 1\nLine text 2", literalContent);
    }

    /**
     * Tests the correct support for EOS exception.
     *
     * @throws RDFHandlerException
     * @throws IOException
     * @throws RDFParseException
     */
    @Test(expected = IOException.class)
    public void testEndOfStreamReached()
    throws RDFHandlerException, IOException, RDFParseException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(
            "<http://a> <http://b> \"\\\" <http://c> .".getBytes()
        );
        parser.parse(bais, "http://base-uri");
    }

    /**
     * Tests the parser with all cases defined by the NQuads grammar.
     *
     * @throws IOException
     * @throws RDFParseException
     * @throws RDFHandlerException
     */
    @Test
    public void testParserWithAllCases()
    throws IOException, RDFParseException, RDFHandlerException {

        TestParseLocationListener parseLocationListerner = new TestParseLocationListener();
        SpecificTestRDFHandler rdfHandler = new SpecificTestRDFHandler();
        parser.setParseLocationListener(parseLocationListerner);
        parser.setRDFHandler(rdfHandler);

        BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        this.getClass().getClassLoader().getResourceAsStream("application/nquads/test1.nq")
                ) 
        );
        parser.parse(
            br,
            "http://test.base.uri"
        );

        rdfHandler.assertHandler(5);
        parseLocationListerner.assertListener(7, 107);
    }

    /**
     * Tests parser with real data.
     *
     * @throws IOException
     * @throws RDFParseException
     * @throws RDFHandlerException
     */
    @Test
    public void testParserWithRealData()
    throws IOException, RDFParseException, RDFHandlerException {

        TestParseLocationListener parseLocationListener = new TestParseLocationListener();
        TestRDFHandler rdfHandler = new TestRDFHandler();
        parser.setParseLocationListener(parseLocationListener);
        parser.setRDFHandler(rdfHandler);

        parser.parse(
            this.getClass().getClassLoader().getResourceAsStream("application/nquads/test2.nq"),
            "http://test.base.uri"
        );

        rdfHandler.assertHandler(400);
        parseLocationListener.assertListener(400, 348);
    }

    private class TestParseLocationListener implements ParseLocationListener {

        private int lastRow, lastCol;

        public void parseLocationUpdate(int r, int c) {
            lastRow = r;
            lastCol = c;
        }

        private void assertListener(int row, int col) {
            Assert.assertEquals("Unexpected last row", row , lastRow);
            Assert.assertEquals("Unexpected last col", col , lastCol);
        }

    }

    private class TestRDFHandler implements RDFHandler {

        private boolean started = false;
        private boolean ended   = false;

        private final List<Statement> statements = new ArrayList<Statement>();

        protected List<Statement> getStatements() {
            return statements;
        }

        public void startRDF() throws RDFHandlerException {
            started = true;
        }

        public void endRDF() throws RDFHandlerException {
            ended = true;
        }

        public void handleNamespace(String s, String s1) throws RDFHandlerException {
            throw new UnsupportedOperationException();
        }

        public void handleStatement(Statement statement) throws RDFHandlerException {
            logger.debug(statement.toString());
            statements.add(statement);
        }

        public void handleComment(String s) throws RDFHandlerException {
            throw new UnsupportedOperationException();
        }

        public void assertHandler(int expected) {
            Assert.assertTrue("Never stated.", started);
            Assert.assertTrue("Never ended." , ended  );
            Assert.assertEquals("Unexpected number of statements.", expected, statements.size());
        }
    }

    private class SpecificTestRDFHandler extends TestRDFHandler {

        public void handleStatement(Statement statement) throws RDFHandlerException {
            int statements = getStatements().size();
            if(statements == 0){
                Assert.assertEquals(new URIImpl("http://example.org/alice/foaf.rdf#me"), statement.getSubject() );

            } else {
                Assert.assertTrue(statement.getSubject() instanceof BNode);
            }
            if( statements == 5 ) {
                Assert.assertEquals(new URIImpl("http://test.base.uri#like"), statement.getPredicate() );
            }
            Assert.assertEquals(
                    new URIImpl( String.format("http://example.org/alice/foaf%s.rdf", statements + 1) ),
                    statement.getContext()
            );

            super.handleStatement(statement);
        }
    }

}