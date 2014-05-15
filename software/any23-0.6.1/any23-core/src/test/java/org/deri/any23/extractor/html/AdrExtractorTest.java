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

import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.vocab.VCARD;
import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;

import java.util.List;

/**
 * Test case for {@link org.deri.any23.extractor.html.AdrExtractor}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
public class AdrExtractorTest extends AbstractExtractorTestCase {

    private static final VCARD vVCARD = VCARD.getInstance();

    protected ExtractorFactory<?> getExtractorFactory() {
        return AdrExtractor.factory;
    }

    @Test
    public void testVCardMultiAddress() throws RepositoryException {
        assertExtracts("microformats/hcard/lastfm-adr-multi-address.html");
        assertModelNotEmpty();
        List<Resource> addresses = findSubjects(RDF.TYPE, vVCARD.Address);
        for (Resource address : addresses) {
            int size = getStatementsSize(address, null, null);
            Assert.assertTrue(
                    String.format("Unexpected statement size: %s", size),
                    size == 2 || size == 4 || size == 6
            );
        }
    }

}
