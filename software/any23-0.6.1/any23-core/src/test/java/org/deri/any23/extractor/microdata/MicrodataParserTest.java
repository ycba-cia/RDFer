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

package org.deri.any23.extractor.microdata;

import org.deri.any23.extractor.html.TagSoupParser;
import org.deri.any23.util.StreamUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.GregorianCalendar;
import java.util.Properties;

/**
 * Test case for {@link MicrodataParser}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MicrodataParserTest {

    private static final Logger logger = LoggerFactory.getLogger(MicrodataParserTest.class);

    @Test
    public void testBasicFeatures() throws IOException {
        extractItemsAndVerifyJSONSerialization(
                "microdata-basic",
                "microdata-basic-expected"
        );
    }

    @Test
    public void testNestedMicrodata() throws IOException {
        extractItemsAndVerifyJSONSerialization(
                "microdata-nested",
                "microdata-nested-expected"
        );
    }

    @Test
    public void testAdvancedItemrefManagement() throws IOException {
        extractItemsAndVerifyJSONSerialization(
                "microdata-itemref",
                "microdata-itemref-expected"
        );
    }

    @Test
    public void testMicrodataJSONSerialization() throws IOException {
        final Document document = getMicrodataDom("microdata-nested");
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream ps = new PrintStream(baos);
        MicrodataParser.getMicrodataAsJSON(document, ps);
        ps.flush();
        final String expected = StreamUtils.asString(
                this.getClass().getResourceAsStream("/microdata/microdata-json-serialization.json")
        );
        Assert.assertEquals("Unexpected serialization for Microdata file.", expected, baos.toString());
    }

    @Test
    public void testGetContentAsDate() throws IOException, ParseException {
        final ItemScope target = extractItems("microdata-basic").getDetectedItemScopes()[4];
        final GregorianCalendar gregorianCalendar = new GregorianCalendar(2009, GregorianCalendar.MAY, 10); // 2009-05-10
        Assert.assertEquals(
                gregorianCalendar.getTime(),
                target.getProperties().get("birthday").get(0).getValue().getAsDate()
        );
    }

    /**
     * Test the main use case of {@link MicrodataParser#deferProperties(String...)}
     *
     * @throws IOException
     * @throws MicrodataParserException
     */
    @Test
    public void testDeferProperties() throws IOException, MicrodataParserException {
        final Document document = getMicrodataDom("microdata-itemref");
        final MicrodataParser parser = new MicrodataParser(document);
        final ItemProp[] deferred = parser.deferProperties("ip5", "ip4", "ip3", "unexisting");
        Assert.assertEquals(3, deferred.length);
    }

    /**
     * Tests the loop detection in {@link MicrodataParser#deferProperties(String...)}.
     *
     * @throws IOException
     * @throws MicrodataParserException
     */
    @Test(expected = MicrodataParserException.class)
    public void testDeferPropertiesLoopDetection1() throws IOException, MicrodataParserException {
        final Document document = getMicrodataDom("microdata-itemref");
        final MicrodataParser parser = new MicrodataParser(document);
        parser.setErrorMode(MicrodataParser.ErrorMode.StopAtFirstError);
        parser.deferProperties("loop0");
    }

    /**
     * Tests the deep loop detection in {@link MicrodataParser#deferProperties(String...)}.
     *
     * @throws IOException
     * @throws MicrodataParserException
     */
    @Test(expected = MicrodataParserException.class)
    public void testDeferPropertiesLoopDetection2() throws IOException, MicrodataParserException {
        final Document document = getMicrodataDom("microdata-itemref");
        final MicrodataParser parser = new MicrodataParser(document);
        parser.setErrorMode(MicrodataParser.ErrorMode.StopAtFirstError);
        parser.deferProperties("loop2");
    }

    /**
     * Tests that the loop detection works property even with multiple calls
     * of {@link MicrodataParser#deferProperties(String...)} over the same item props.
     *
     * @throws java.io.IOException
     * @throws MicrodataParserException
     */
    @Test
    public void testDeferPropertiesStateManagement() throws IOException, MicrodataParserException {
        final Document document = getMicrodataDom("microdata-itemref");
        final MicrodataParser parser = new MicrodataParser(document);
        Assert.assertEquals(1, parser.deferProperties("ip1").length);
        Assert.assertEquals(1, parser.deferProperties("ip1").length);
        Assert.assertEquals(1, parser.deferProperties("ip1").length);
    }

    private Document getDom(String document) throws IOException {
        final InputStream is = this.getClass().getResourceAsStream(document);
        try {
            final TagSoupParser tagSoupParser = new TagSoupParser(is, "http://test-document");
            return tagSoupParser.getDOM();
        } finally {
            is.close();
        }
    }

    private Document getMicrodataDom(String htmlFile) throws IOException {
         return getDom("/microdata/" + htmlFile + ".html");
    }

    private MicrodataParserReport extractItems(String htmlFile) throws IOException {
        final Document document = getMicrodataDom(htmlFile);
        return MicrodataParser.getMicrodata(document);
    }

    private void extractItemsAndVerifyJSONSerialization(String htmlFile, String expectedResult)
    throws IOException {
        final MicrodataParserReport report = extractItems(htmlFile);
        final ItemScope[]                items  = report.getDetectedItemScopes();
        final MicrodataParserException[] errors = report.getErrors();

        logger.debug("begin itemScopes");
        for(ItemScope item : items) {
            logger.debug( item.toJSON() );
        }
        logger.debug("end itemScopes");
        logger.debug("begin errors");
        for(MicrodataParserException error : errors) {
            logger.debug( error.toJSON() );
        }
        logger.debug("end errors");

        final Properties resultContent = new Properties();
        resultContent.load( this.getClass().getResourceAsStream("/microdata/" + expectedResult + ".properties") );

        final int expectedResults = getExpectedResultCount(resultContent);
        final int expectedErrors  = getExpectedErrorsCount(resultContent);
        Assert.assertEquals("Unexpected number of detect items.", expectedResults, items.length);
        Assert.assertEquals("Unexpected number of errors."      , expectedErrors, errors.length);

        for (int i = 0; i < items.length; i++) {
            Assert.assertEquals(
                    "Error while comparing result [" + i + "]",
                    resultContent.getProperty("result" + i),
                    items[i].toJSON()
            );
        }

        for(int i = 0; i < errors.length; i++) {
            Assert.assertEquals(
                    "Error while comparing error [" + i + "]",
                    resultContent.getProperty("error" + i),
                    errors[i].toJSON()
            );
        }
    }

    private int countKeysWithPrefix(Properties properties, String prefix) {
        int count = 0;
        for(Object key : properties.keySet()) {
            if(key.toString().indexOf(prefix) == 0) count++;
        }
        return count;
    }

    private int getExpectedResultCount(Properties properties) {
        return countKeysWithPrefix(properties, "result");
    }
    private int getExpectedErrorsCount(Properties properties) {
        return countKeysWithPrefix(properties, "error");
    }

}
