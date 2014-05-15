package org.deri.any23.extractor.html;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Test case for
 * {@link org.deri.any23.extractor.html.SpanCloserInputStream}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class SpanCloserInputStreamTest {

    @Test
    public void testSpanPositiveReplacement() throws IOException {
        processInput(
                "pre<span attr1=\"value\" attr2/>post",
                "pre<span attr1=\"value\" attr2></span>post"
        );
    }

    @Test
    public void testSpanNegativeReplacement() throws IOException {
        processInput(
                "pre<span attr1=\"value\" attr2>mid</span>post",
                "pre<span attr1=\"value\" attr2>mid</span>post"
        );
    }

    @Test
    public void testSubsequentSpanReplacement() throws IOException {
        processInput(
                "<span/><span/><span a=\"v\"/><span/>",
                "<span></span><span></span><span a=\"v\"></span><span></span>"
        );
        processInput(
                "<span name=\"span1\"/><span name=\"span2\"/>",
                "<span name=\"span1\"></span><span name=\"span2\"></span>"
        );
    }

    @Test
    public void testNestedSpanReplacement() throws IOException {
        processInput(
                "<span name=\"outer\"><span name=\"inner\"/></span>",
                "<span name=\"outer\"><span name=\"inner\"></span></span>"
        );
        processInput(
                "<span name=\"outer1\"><span name=\"outer2\"><span name=\"inner\"/></span></span>",
                "<span name=\"outer1\"><span name=\"outer2\"><span name=\"inner\"></span></span></span>"
        );
    }

    @Test
    public void testMixedReplacement() throws IOException {
        processInput(
                "<span name=\"outer1\">" +
                        "<span name=\"outer2\">" +
                            "<span name=\"inner1\"/>" +
                            "<span name=\"inner2\"></span>" +
                            "<span a=\"inner3\"/>" +
                        "</span>" +
                "</span>",
                "<span name=\"outer1\">" +
                        "<span name=\"outer2\">" +
                            "<span name=\"inner1\"></span>" +
                            "<span name=\"inner2\"></span>" +
                            "<span a=\"inner3\"></span>" +
                        "</span>" +
                "</span>"
        );
    }

    @Test
    public void testRealSpanReplacement() throws IOException {
        processInput(
                "<span about=\"#me\" xmlns:foaf=\"http://xmlns.com/foaf/0.1/\">\n" +
                    "  <span rel=\"foaf:homepage\" resource=\"http://richard.cyganiak.de/\" />\n" +
                    "  <span property=\"foaf:nick\" content=\"cygri\" />\n" +
                "</span>",
                "<span about=\"#me\" xmlns:foaf=\"http://xmlns.com/foaf/0.1/\">\n" +
                    "  <span rel=\"foaf:homepage\" resource=\"http://richard.cyganiak.de/\" ></span>\n" +
                    "  <span property=\"foaf:nick\" content=\"cygri\" ></span>\n" +
                "</span>"
        );
    }

    @Test
    public void testCloserTransparency() throws IOException {
        final String TEST_FILE = "/html/encoding-test.html";
        final InputStream original = this.getClass().getResourceAsStream(TEST_FILE);
        final InputStream wrapped = new SpanCloserInputStream(
                this.getClass().getResourceAsStream(TEST_FILE)
        );
        int intc;
        try {
            while((intc = original.read()) != -1)
            {
                Assert.assertEquals(intc, wrapped.read());
            }
        } finally {
            original.close();
            wrapped.close();
        }
    }

    private void processInput(String inStr, String expected) throws IOException {
        final ByteArrayInputStream in = new ByteArrayInputStream( inStr.getBytes() );
        SpanCloserInputStream scis = new SpanCloserInputStream(in);
        int c;
        final StringBuilder sb = new StringBuilder();
        while( (c = scis.read()) != -1 ) {
            sb.append( (char) c );
        }
        final String out = sb.toString();
        Assert.assertEquals("Unexpected replacement.", expected, out);
    }
}
