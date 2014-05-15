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

package org.deri.any23.extractor.csv;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Test case for {@link CSVReaderBuilder}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class CSVReaderBuilderTest {

    /**
     * Tests positive CSV detection.
     *
     * @throws IOException
     */
    @Test
    public void testPositiveCSVDetection() throws IOException {
        Assert.assertTrue(
                "Builder cannot detect CVS stream.",
                CSVReaderBuilder.isCSV( this.getClass().getResourceAsStream("test-comma.csv") )
        );
        Assert.assertTrue(
                "Builder cannot detect CVS stream.",
                CSVReaderBuilder.isCSV( this.getClass().getResourceAsStream("test-semicolon.csv") )
        );
        Assert.assertTrue(
                "Builder cannot detect CVS stream.",
                CSVReaderBuilder.isCSV( this.getClass().getResourceAsStream("test-tab.csv") )
        );
    }

    /**
     * Tests negative CSV detection.
     *
     * @throws IOException
     */
    @Test
    public void testNegativeCSVDetection() throws IOException {
        Assert.assertFalse(
                "Wrong CSV detection.",
                CSVReaderBuilder.isCSV( this.getClass().getResourceAsStream("/application/nquads/test1.nq") )
        );
        Assert.assertFalse(
                "Wrong CSV detection.",
                CSVReaderBuilder.isCSV( this.getClass().getResourceAsStream("/application/nquads/test2.nq") )
        );
        Assert.assertFalse(
                "Wrong CSV detection.",
                CSVReaderBuilder.isCSV(
                        this.getClass().getResourceAsStream("/org/deri/any23/extractor/rdf/example-ntriples.nt")
                )
        );
    }

}
