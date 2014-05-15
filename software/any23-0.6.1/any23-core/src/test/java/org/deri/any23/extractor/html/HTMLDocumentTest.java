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

package org.deri.any23.extractor.html;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link org.deri.any23.extractor.html.HTMLDocument}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class HTMLDocumentTest {

    /**
     * Verifies the correct extraction of the rel tag value.
     */
    @Test
    public void testExtractRelTag() {
        Assert.assertEquals(
                "http://technorati.com/tag/Technology",
                HTMLDocument.extractRelTag("http://technorati.com/tag/Technology/")
        );
        Assert.assertEquals(
                "http://technorati.com/tag/Technology",
                HTMLDocument.extractRelTag("http://technorati.com/tag/Technology")
        );
    }

}
