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

import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionParameters;
import org.deri.any23.extractor.ExtractionResultImpl;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.rdf.RDFUtils;
import org.deri.any23.vocab.DCTERMS;
import org.deri.any23.vocab.FOAF;
import org.deri.any23.vocab.REVIEW;
import org.deri.any23.vocab.VCARD;
import org.deri.any23.writer.RepositoryWriter;
import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.w3c.dom.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Reference Test class for various mixed extractors.
 *
 * @author Davide Palmisano (dpalmisano@gmail.com)
 *
 * @see org.deri.any23.extractor.html.GeoExtractor
 * @see org.deri.any23.extractor.html.AdrExtractor
 * @see org.deri.any23.extractor.html.HCardExtractor
 * @see org.deri.any23.extractor.html.HReviewExtractor
 */
public class RDFMergerTest extends AbstractExtractorTestCase {

    private static final DCTERMS vDCTERMS = DCTERMS.getInstance();
    private static final FOAF    vFOAF    = FOAF.getInstance();
    private static final REVIEW  vREVIEW  = REVIEW.getInstance();
    private static final VCARD   vVCARD   = VCARD.getInstance();

    @Override
    protected ExtractorFactory<?> getExtractorFactory() {
        return null;
    }

    @Test
	public void testNoMicroformats() throws RepositoryException, ExtractionException, IOException {
		extract("html-without-uf.html");
		Assert.assertTrue(conn.isEmpty());
	}

    @Test
    public void test01XFNFoaf() throws RepositoryException {
		assertExtracts("mixed/01-xfn-foaf.html");
        Assert.assertFalse(conn.isEmpty());
        assertStatementsSize(RDF.TYPE, vVCARD.VCard, 1);
        Resource vcard = findExactlyOneBlankSubject(RDF.TYPE, vVCARD.VCard);
        RepositoryResult<Statement> statements = conn.getStatements(null, vFOAF.topic, vcard, false);

        try {
            while(statements.hasNext()) {
                Statement statement = statements.next();
                Resource person = statement.getSubject();
                Resource blank = findExactlyOneBlankSubject(OWL.SAMEAS, person);
		        assertContains(blank, RDF.TYPE, vFOAF.Person);

            }

        } finally {
            statements.close();
        }


		
	}

    @Test
	public void testAbbrTitleEverything() throws ExtractionException, IOException, RepositoryException {
		extractHCardAndRelated("microformats/hcard/23-abbr-title-everything.html");

		assertContains(vVCARD.fn, "John Doe");
		assertContains(vVCARD.nickname, "JJ");

		assertContains(vVCARD.given_name, "Jonathan");
		assertContains(vVCARD.additional_name, "John");
		assertContains(vVCARD.family_name, "Doe-Smith");
		assertContains(vVCARD.honorific_suffix, "Medical Doctor");
        assertContains(vVCARD.title, "President");
		assertContains(vVCARD.role, "Chief");
		assertContains(vVCARD.tz, "-0700");
		assertContains(vVCARD.bday, "2006-04-04");
		assertContains(vVCARD.tel, RDFUtils.uri("tel:415.555.1234"));
		assertContains(vVCARD.uid, "abcdefghijklmnopqrstuvwxyz");
		assertContains(vVCARD.class_, "public");
		assertContains(vVCARD.note, "this is a note");
		assertContains(vVCARD.organization_name, "Intellicorp");
		assertContains(vVCARD.organization_unit, "Intelligence");
        assertContains(RDF.TYPE, vVCARD.Location);
		assertContains(vVCARD.geo, (Resource) null);
		assertContains(vVCARD.latitude, "37.77");
		assertContains(vVCARD.longitude, "-122.41");
        assertContains(vVCARD.post_office_box, "Box 1234");
		assertContains(vVCARD.extended_address, "Suite 100");
		assertContains(vVCARD.street_address, "123 Fake Street");
		assertContains(vVCARD.locality, "San Francisco");
		assertContains(vVCARD.region, "California");
		assertContains(vVCARD.postal_code, "12345-6789");
		assertContains(vVCARD.country_name, "United States of America");
		assertContains(vVCARD.addressType, "work");
	}

    @Test
    public void testAdr() throws ExtractionException, IOException, RepositoryException {
		extractHRevAndRelated("microformats/hcard/22-adr.html");

		assertStatementsSize(RDF.TYPE, vVCARD.Address, 4);

		Map<String,String[]> addresses = new HashMap<String,String[]>(4);
        addresses.put(
                "1233 Main St.",
                new String[]{
                        "United States of America",
                        "Beverly Hills",
                        "90210",
                        "California"});
        addresses.put(
                "1232 Main St.",
                new String[]{
                        "United States of America",
                        "Beverly Hills",
                        "90210",
                        "California"});
        addresses.put(
                "1234 Main St.",
                new String[]{
                        "United States of America",
                        "Beverly Hills",
                        "90210",
                        "California"
                });
        addresses.put(
                "1231 Main St.",
                new String[]{
                        "United States of America",
                        "Beverly Hills",
                        "90210",
                        "California"});
        addresses.put(
                "Suite 100",
                new String[]{
                        "United States of America",
                        "Beverly Hills",
                        "90210",
                        "California"
                });

        RepositoryResult<Statement> statements = conn.getStatements(null, RDF.TYPE, vVCARD.Address, false);

        try {
            while (statements.hasNext()) {
                Resource adr = statements.next().getSubject();
                RepositoryResult<Statement> innerStatements = conn.getStatements(adr, vVCARD.street_address, null, false);
                try {
                    while (innerStatements.hasNext()) {
                        Value innerValue = innerStatements.next().getObject();
                        assertContains(adr, vVCARD.country_name, addresses.get(innerValue.stringValue())[0]);
                        assertContains(adr, vVCARD.locality, addresses.get(innerValue.stringValue())[1]);
                        assertContains(adr, vVCARD.postal_code, addresses.get(innerValue.stringValue())[2]);
                        assertContains(adr, vVCARD.region, addresses.get(innerValue.stringValue())[3]);
                    }

                } finally {
                    innerStatements.close();
                }

            }

        } finally {
            statements.close();
        }

		assertContains(vVCARD.post_office_box, "PO Box 1234");
        assertContains(vVCARD.addressType, "home");
	}

    @Test
	public void testGeoAbbr() throws ExtractionException, IOException, RepositoryException {
		extractHCardAndRelated("microformats/hcard/25-geo-abbr.html");
		Assert.assertFalse(conn.isEmpty());
		assertContains(vVCARD.fn, "Paradise");
		assertContains(RDF.TYPE, vVCARD.Organization);
		assertContains(vVCARD.organization_name, "Paradise");
		assertContains(RDF.TYPE, vVCARD.Location);
		assertContains(vVCARD.geo, (Resource) null);
		assertContains(vVCARD.latitude, "30.267991");
		assertContains(vVCARD.longitude, "-97.739568");
	}

    @Test
	public void testAncestors() throws ExtractionException, IOException, RepositoryException {
		extractHCardAndRelated("microformats/hcard/26-ancestors.html");
        Assert.assertFalse(conn.isEmpty());
		
        assertContains(vVCARD.fn, "John Doe");
		assertNotContains(null, vVCARD.fn,
		    "Mister Jonathan John Doe-Smith Medical Doctor");
		assertContains(vVCARD.nickname, "JJ");
		assertContains(RDF.TYPE, vVCARD.Address);
		assertContains(vVCARD.tz, "-0700");
		assertContains(vVCARD.title, "President");
		assertContains(vVCARD.role, "Chief");
		assertContains(vVCARD.organization_name, "Intellicorp");
		assertContains(vVCARD.organization_unit, "Intelligence");

		assertContains(vVCARD.tel, RDFUtils.uri("tel:415.555.1234"));
		assertContains(vVCARD.uid, "abcdefghijklmnopqrstuvwxyz");
		assertContains(vVCARD.note, "this is a note");
		assertContains(vVCARD.class_, "public");

		assertContains(RDF.TYPE, vVCARD.Location);
		assertContains(vVCARD.geo, (Resource) null);
		assertContains(null, vVCARD.latitude, "37.77");
		assertContains(null, vVCARD.longitude, "-122.41");

		assertContains(RDF.TYPE, vVCARD.Name);
		assertContains(vVCARD.additional_name, "John");
		assertContains(vVCARD.given_name, "Jonathan");
		assertContains(vVCARD.family_name, "Doe-Smith");
		assertContains(vVCARD.honorific_prefix, "Mister");
		assertContains(vVCARD.honorific_suffix, "Medical Doctor");

		assertContains(vVCARD.post_office_box, "Box 1234");
		assertContains(vVCARD.extended_address, "Suite 100");
		assertContains(vVCARD.street_address, "123 Fake Street");
		assertContains(vVCARD.locality, "San Francisco");
		assertContains(vVCARD.region, "California");
		assertContains(vVCARD.postal_code, "12345-6789");
		assertContains(vVCARD.country_name, "United States of America");
		assertContains(vVCARD.addressType, "work");
	}

    @Test
    public void testSingleton() throws ExtractionException, IOException, RepositoryException {
        extractHCardAndRelated("microformats/hcard/37-singleton.html");
        Assert.assertFalse(conn.isEmpty());
        assertStatementsSize(vVCARD.fn, (Value) null, 1);
        assertContains(vVCARD.fn, "john doe 1");
        assertStatementsSize(RDF.TYPE, vVCARD.Name, 1);
        assertStatementsSize(vVCARD.given_name, (Value) null, 1);
        assertContains(vVCARD.given_name, "john");
        assertStatementsSize(vVCARD.family_name, (Value) null, 1);
        assertContains(vVCARD.family_name, "doe");
        assertStatementsSize(vVCARD.sort_string, (Value) null, 1);
        assertContains(vVCARD.sort_string, "d");
        assertStatementsSize(vVCARD.bday, (Value) null, 1);
        assertContains(vVCARD.bday, "20060707");
        assertStatementsSize(vVCARD.rev, (Value) null, 1);
        assertContains(vVCARD.rev, "20060707");
        assertStatementsSize(vVCARD.class_, (Value) null, 1);
        assertContains(vVCARD.class_, "public");
        assertStatementsSize(vVCARD.tz, (Value) null, 1);
        assertContains(vVCARD.tz, "+0600");
        // 2 uf, one of them outside the card
        assertStatementsSize(RDF.TYPE, vVCARD.Location, 2);
        // one is actually used
        assertStatementsSize(vVCARD.geo, (Value) null, 2);
        assertContains(vVCARD.latitude, "123.45");
        assertContains(vVCARD.longitude, "67.89");
        assertStatementsSize(vVCARD.uid, (Value) null, 1);
        assertContains(vVCARD.uid, "unique-id-1");
    }

    @Test
	public void test01Basic() throws ExtractionException, IOException, RepositoryException {
		extractHRevAndRelated("microformats/hreview/01-spec.html");
        Assert.assertFalse(conn.isEmpty());

		assertStatementsSize(RDF.TYPE, vREVIEW.Review, 1);
		// reviewer, item
		assertStatementsSize(RDF.TYPE, vVCARD.VCard, 2);
		// there is one address in the item vcard
		assertStatementsSize(RDF.TYPE, vVCARD.Address, 1);

        RepositoryResult<Statement> reviews = conn.getStatements(null, RDF.TYPE, vREVIEW.Review, false);

        try {
            while(reviews.hasNext()) {
                Resource review = reviews.next().getSubject();
                assertContains(review, vREVIEW.rating, "5");
                assertContains(review, vREVIEW.title, "Crepes on Cole is awesome");
			    assertContains(review, vDCTERMS.date, "20050418T2300-0700");
			    assertContains(
					vREVIEW.text,
					"Crepes on Cole is one of the best little \n"
					+ "      creperies in San Francisco.\n      "
					+ "Excellent food and service. Plenty of tables in a variety of sizes\n"
					+ "      for parties large and small.  "
					+ "Window seating makes for excellent\n      "
					+ "people watching to/from the N-Judah which stops right outside.\n"
					+ "      I've had many fun social gatherings here, as well as gotten\n"
					+ "      plenty of work done thanks to neighborhood WiFi.");

			assertContains(null, vREVIEW.hasReview, review);
            }
        }
        finally {
            reviews.close();
        }

		// generic checks that vcards are correct, improve
		assertContains(vVCARD.fn, "Crepes on Cole");
		assertContains(vVCARD.fn, "Tantek");
		assertContains(vVCARD.locality, "San Francisco");
		assertContains(vVCARD.organization_name, "Crepes on Cole");

	}
    
    @Test
	public void test02RatedTags() throws ExtractionException, IOException, RepositoryException {
		extractHRevAndRelated("microformats/hreview/02-spec-2.html");

		assertStatementsSize(vREVIEW.reviewer, (Value) null, 1);
		assertStatementsSize(vREVIEW.hasReview, (Value) null, 1);
		assertModelNotEmpty();
		assertStatementsSize(RDF.TYPE, vREVIEW.Review, 1);
		// reviewer, item
		assertStatementsSize(RDF.TYPE, vVCARD.VCard, 2);
		// there is one address in the item vcard
		assertStatementsSize(RDF.TYPE, vVCARD.Address, 1);

        RepositoryResult<Statement> reviews = conn.getStatements(null, RDF.TYPE, vREVIEW.Review, false);

        try {
            while (reviews.hasNext()) {
                Resource review = reviews.next().getSubject();
                assertContains(review, vREVIEW.rating, "18");
                assertContains(review, vREVIEW.title, "Cafe Borrone");
                assertContains(review, vDCTERMS.date, "20050428T2130-0700");
                assertContains(null, vREVIEW.hasReview, review);
                assertContains(vREVIEW.type, "business");
            }

        } finally {
            reviews.close();
        }

		// generic checks that vcards are correct, improve
		assertContains(vVCARD.fn, "Cafe Borrone");
		assertContains(vVCARD.fn, "anonymous");
		assertContains(vVCARD.organization_name, "Cafe Borrone");
        
	}

    @Test
	public void test03NoHcardForItem() throws ExtractionException, IOException, RepositoryException {
		extractHRevAndRelated("microformats/hreview/03-spec-3.html");

        Assert.assertFalse(conn.isEmpty());
        assertStatementsSize(RDF.TYPE, vREVIEW.Review, 1);
		assertStatementsSize(RDF.TYPE, vVCARD.VCard, 1);

        RepositoryResult<Statement> reviews = conn.getStatements(null, RDF.TYPE, vREVIEW.Review, false);

        try {
            while (reviews.hasNext()) {
                Resource review = reviews.next().getSubject();
                assertContains(review, vREVIEW.rating, "5");
                assertNotContains(vREVIEW.title, null);
                assertContains(review, vDCTERMS.date, "200502");

                assertContains(
                        vREVIEW.text,
                        "\"The people thought they were just being rewarded for " +
                                "treating others\n       as they like to be treated, for " +
                                "obeying stop signs and curing diseases,\n       for mailing " +
                                "letters with the address of the sender... Don't wake me,\n " +
                                "      I plan on sleeping in...\"\n     \n     \"Nothing Better\"" +
                                " is a great track on this album, too...");

                RepositoryResult<Statement> whatHasAReview = conn.getStatements(null, vREVIEW.hasReview, review, false);

                try {
                    while(whatHasAReview.hasNext()) {
                        Resource subject = whatHasAReview.next().getSubject();
                        assertContains(subject, vVCARD.fn, "The Postal Service: Give Up");
				        assertContains(subject, vVCARD.url, RDFUtils.uri("http://www.amazon.com/exec/obidos/ASIN/B000089CJI/"));
				        assertContains(subject, vVCARD.photo, RDFUtils.uri("http://images.amazon.com/images/P/B000089CJI.01._SCTHUMBZZZ_.jpg"));
                    }

                } finally {
                    whatHasAReview.close();
                }

            }

        } finally {
            reviews.close();
        }

        assertContains(vVCARD.fn, "Adam Rifkin");
		assertContains(vVCARD.url, RDFUtils.uri("http://ifindkarma.com/blog/"));
	}

	@Override
	protected void extract(String filename) throws ExtractionException, IOException {

        File file = new File(
                System.getProperty("test.data", "src/test/resources") +
                        "/html/" + filename);

        Document document = new TagSoupParser(new FileInputStream(file), baseURI.stringValue()).getDOM();
        HCardExtractor hCardExtractor = HCardExtractor.factory.createExtractor();
        ExtractionContext hcExtractionContext = new ExtractionContext(
                hCardExtractor.getDescription().getExtractorName(),
                baseURI
        );
        hCardExtractor.run(
                ExtractionParameters.getDefault(),
                hcExtractionContext,
                document,
                new ExtractionResultImpl(
                        hcExtractionContext,
                        hCardExtractor,
                        new RepositoryWriter(conn)
                )
        );
        XFNExtractor xfnExtractor = XFNExtractor.factory.createExtractor();
        ExtractionContext xfnExtractionContext = new ExtractionContext(
                xfnExtractor.getDescription().getExtractorName(),
                baseURI
        );
        xfnExtractor.run(
                        ExtractionParameters.getDefault(),
                        xfnExtractionContext,
                        document,
                        new ExtractionResultImpl(
                                xfnExtractionContext,
                                hCardExtractor,
                                new RepositoryWriter(conn)
                        )
                );
	}

    private void extractHCardAndRelated(String filename) throws IOException, ExtractionException {
        File file = new File(
                System.getProperty("test.data", "src/test/resources/") + filename);

        Document document = new TagSoupParser(new FileInputStream(file), baseURI.stringValue()).getDOM();
        HCardExtractor hCardExtractor = HCardExtractor.factory.createExtractor();
        ExtractionContext hCardExtractionContext = new ExtractionContext(
                hCardExtractor.getDescription().getExtractorName(), baseURI
        );
        hCardExtractor.run(
                ExtractionParameters.getDefault(),
                hCardExtractionContext,
                document,
                new ExtractionResultImpl(
                        hCardExtractionContext,
                        hCardExtractor, new RepositoryWriter(conn)
                )
        );

        GeoExtractor geoExtractor = GeoExtractor.factory.createExtractor();
        ExtractionContext geoExtractionContext = new ExtractionContext(
                geoExtractor.getDescription().getExtractorName(), baseURI
        );
        geoExtractor.run(
                ExtractionParameters.getDefault(),
                geoExtractionContext,
                document,
                new ExtractionResultImpl(
                        geoExtractionContext,
                        geoExtractor,
                        new RepositoryWriter(conn)
                )
        );

        AdrExtractor adrExtractor = AdrExtractor.factory.createExtractor();
        ExtractionContext adrExtractionContext = new ExtractionContext(
                adrExtractor.getDescription().getExtractorName(), baseURI
        );
        adrExtractor.run(
                ExtractionParameters.getDefault(),
                adrExtractionContext,
                document,
                new ExtractionResultImpl(
                        adrExtractionContext,
                        adrExtractor,
                        new RepositoryWriter(conn)
                )
        );

    }

    private void extractHRevAndRelated(String filename) throws ExtractionException, IOException {
        extractHCardAndRelated(filename);
        File file = new File(
                System.getProperty("test.data", "src/test/resources/") + filename);
        Document document = new TagSoupParser(new FileInputStream(file), baseURI.stringValue()).getDOM();
        HReviewExtractor hReviewExtractor = HReviewExtractor.factory.createExtractor();
        ExtractionContext hreviewExtractionContext = new ExtractionContext(
                hReviewExtractor.getDescription().getExtractorName(), baseURI
        );
        hReviewExtractor.run(
                ExtractionParameters.getDefault(),
                hreviewExtractionContext,
                document,
                new ExtractionResultImpl(
                        hreviewExtractionContext,
                        hReviewExtractor,
                        new RepositoryWriter(conn)
                )
        );
    }

}
