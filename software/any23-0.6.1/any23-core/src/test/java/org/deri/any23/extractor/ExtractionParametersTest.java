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

package org.deri.any23.extractor;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link ExtractionParameters}.
 */
public class ExtractionParametersTest {

    @Test
    public void testReadDefaultConfig() {
        final ExtractionParameters extractionParameters = ExtractionParameters.getDefault();
        Assert.assertEquals(
                false,
                extractionParameters.getFlag("any23.microdata.strict")
        );
        Assert.assertEquals(
                "http://rdf.data-vocabulary.org/",
                extractionParameters.getProperty("any23.microdata.ns.default")
        );
    }

    @Test
    public void testReadOverridenConfig() {
        final ExtractionParameters extractionParameters = ExtractionParameters.getDefault();
        extractionParameters.setFlag("any23.microdata.strict", true);
        final String CUSTOM_PROP = "http://fake/property";
        extractionParameters.setProperty(
                "any23.microdata.ns.default",
                CUSTOM_PROP
        );

        Assert.assertEquals(
                true,
                extractionParameters.getFlag("any23.microdata.strict")
        );
        Assert.assertEquals(
                CUSTOM_PROP,
                extractionParameters.getProperty("any23.microdata.ns.default")
        );
    }

}
