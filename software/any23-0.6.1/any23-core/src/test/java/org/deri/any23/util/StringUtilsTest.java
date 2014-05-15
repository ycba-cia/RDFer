/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.deri.any23.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link StringUtils}
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class StringUtilsTest {

    @Test
    public void testCountOccurrences() {
        final String container = "1 22 AAB 333 4444 AAAAB 5555 6666 AAAAAAA 777777";
        final String contained = "AA";
        Assert.assertEquals(6, StringUtils.countOccurrences(container, contained) );
    }

    @Test
    public void testEscapeDoubleQuotes() {
        Assert.assertEquals( "pre post"     , StringUtils.escapeDoubleQuotes("pre post"     ) );
        Assert.assertEquals( "pre \\\" post", StringUtils.escapeDoubleQuotes("pre \" post"  ) );
        Assert.assertEquals( "pre \\\" post", StringUtils.escapeDoubleQuotes("pre \\\" post") );
    }

    @Test
    public void testEscapeAsJSONString() {
        Assert.assertEquals( "pre \\\" mid \\n post", StringUtils.escapeAsJSONString("pre \" mid \n post") );
    }

}
