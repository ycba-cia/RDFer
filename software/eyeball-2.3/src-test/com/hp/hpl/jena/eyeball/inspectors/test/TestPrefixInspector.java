/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 	All rights reserved - see end of file.
 	$Id: TestPrefixInspector.java,v 1.5 2010/03/26 14:50:31 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.inspectors.test;

import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.inspectors.PrefixInspector;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.vocabulary.RDF;

@RunWith(JUnit4.class) public class TestPrefixInspector extends InspectorTestBase
    {
    public TestPrefixInspector()
        {}

    protected Class<? extends Inspector> getInspectorClass()
        { return PrefixInspector.class; }

    @Test public void testPrefixInspectorDeclaresPredicates()
        {
        Report r = new Report();
        new PrefixInspector().begin( r, ontModel() );
        List<Property> predicates = r.getPredicateRegister().getRegisteredPredicates();
        assertEquals( eyeResourceSet( "jenaPrefixFound forNamespace multiplePrefixesForNamespace forReason onPrefix badNamespaceURI expected" ), new HashSet<Property>( predicates ) );
        assertOrder( "jenaPrefixFound forNamespace", predicates );
        assertOrder( "badNamespaceURI onPrefix forReason expected", predicates );
        assertOrder( "multiplePrefixesForNamespace onPrefix", predicates );
        }
    
    @Test public void testJenaPrefixReported()
        {
        testJenaPrefixReported( "j.0", "eh:/prefix/" );
        testJenaPrefixReported( "j.1", "http://nowhere.com/" );
        testJenaPrefixReported( "j.2", "ftp://hello.there/" );
        testJenaPrefixReported( "j.10", "urn:x-hp-jena:fred:" );
        testJenaPrefixReported( "j.1234567890", "eh:/prefix/" );
        }
    
    @Test public void testNoJenaPrefixReported()
        {
        testNoJenaPrefixReported( "j", "eh:/prefix/" );
        testNoJenaPrefixReported( "j.", "eh:/prefix/other/stuff#" );
        testNoJenaPrefixReported( "j10", "ftp://ftp.ftp.com/" );
        testNoJenaPrefixReported( "j.a", "eh:/prefix/" );
        testNoJenaPrefixReported( "j.10a", "eh:/prefix/" );
        testNoJenaPrefixReported( "jena", "eh:/prefix/" );
        testNoJenaPrefixReported( "j", "eh:/prefix/" );
        }
    
    private void testNoJenaPrefixReported( String prefix, String namespace )
        {
        OntModel m = ontModelWithPrefix( prefix, namespace );
        Report r = new Report();
        new PrefixInspector().inspectModel( r, m );
        assertIsoModels( model(), r.model() );
        }
    
    private void testJenaPrefixReported( String prefix, String namespace )
        {
        OntModel m = ontModelWithPrefix( prefix, namespace );
        Report r = new Report();
        String modelString = 
            "[eye:mainProperty eye:jenaPrefixFound & eye:jenaPrefixFound '<a>' & eye:forNamespace '<b>']"
            .replaceAll( "<a>", prefix )
            .replaceAll( "<b>", namespace )
            ;
        Model wanted = itemModel( modelString );
        new PrefixInspector().inspectModel( r, m );
        assertIsoModels( wanted, r.model() );
        }

    private OntModel ontModelWithPrefix( String prefix, String namespace )
        {
        OntModel m = ModelFactory.createOntologyModel();
        m.setNsPrefix( prefix, namespace );
        return m;
        }

    @Test public void testReportsUnterminatedNamespace()
        {
        Report r = new Report();
        OntModel m = ontModelWithPrefix( "any", "http://domain.com/atom" );
        new PrefixInspector().inspectModel( r, m );
        String modelString = 
            "[eye:mainProperty eye:badNamespaceURI & eye:onPrefix 'any'"
            + " & eye:badNamespaceURI 'http://domain.com/atom'"
            + " & eye:forReason eye:namespaceEndsWithNameCharacter]";
        assertIsoModels( itemModel( modelString ), r.model() );
        }
    
    @Test public void testReportsRepeatedNamespaces()
        {
        OntModel m = ontModel();
        Report r = new Report();
        String someURI = "http://some.domain.com/directory#";
        m.setNsPrefix( "a", someURI ).setNsPrefix( "b", someURI );
        new PrefixInspector().inspectModel( r, m );
        String modelString = 
            "[eye:mainProperty eye:multiplePrefixesForNamespace"
            + " & eye:multiplePrefixesForNamespace 'http://some.domain.com/directory#'"
            + " & eye:onPrefix 'a' & eye:onPrefix 'b']";
        assertIsoModels( itemModel( modelString ), r.model() );
        }
    
    @Test public void testReportsMultipleRepeatedNamespaces()
        {
        OntModel m = ontModel();
        Report r = new Report();
        String someURI = "http://some.domain.com/directory#";
        String otherURI = "urn:xyz:preamble/";
        m.setNsPrefix( "a", someURI ).setNsPrefix( "b", someURI );
        m.setNsPrefix( "c", otherURI ).setNsPrefix( "d", otherURI );
        new PrefixInspector().inspectModel( r, m );
        String modelString = 
            "[eye:mainProperty eye:multiplePrefixesForNamespace"
            + " & eye:multiplePrefixesForNamespace 'http://some.domain.com/directory#'"
            + " & eye:onPrefix 'a' & eye:onPrefix 'b']"
            + " ; [eye:mainProperty eye:multiplePrefixesForNamespace & eye:multiplePrefixesForNamespace 'urn:xyz:preamble/'"
            + " & eye:onPrefix 'c' & eye:onPrefix 'd']";
        assertIsoModels( itemModel( modelString ), r.model() );
        }
    
    @Test public void testKnownPrefixesCorrect()
        {
        testKnownPrefixCorrect( "rdf", "http://bad/rdf#" );
        testKnownPrefixCorrect( "rdfs", "http://wrong/rdfs#" );
        testKnownPrefixCorrect( "dc", "http://bad/dc#" );
        testKnownPrefixCorrect( "daml", "http://bad/rdamldf#" );
        testKnownPrefixCorrect( "owl", "http://bad/rowkdf#" );
        testKnownPrefixCorrect( "xsd", "http://bad/xsd#" );
        testKnownPrefixCorrect( "rss", "http://bad/rss#" );
        testKnownPrefixCorrect( "vcard", "http://bad/vcard#" );
        testKnownPrefixCorrect( "ja", "http://bad/ja#" );
        testKnownPrefixCorrect( "eg", "http://bad/eg#" );
        }

    public void testKnownPrefixCorrect( String prefix, String uri )
        {
        OntModel m = ontModelWithPrefix( prefix, uri );
        Model wanted = ModelFactory.createDefaultModel()
            .createResource()
            .addProperty( EYE.forReason, "non-standard namespace for prefix" )
            .addProperty( EYE.onPrefix, prefix )
            .addProperty( EYE.badNamespaceURI, uri )
            .addProperty( EYE.mainProperty, EYE.badNamespaceURI )
            .addProperty( EYE.expected, PrefixMapping.Extended.getNsPrefixURI( prefix ) )
            .addProperty( RDF.type, EYE.Item )
            .getModel()
            ;
        Report r = new Report();
        new PrefixInspector().inspectModel( r, m );
        assertFalse( r.valid() );
        assertIsoModels( wanted, r.model() );
        }
    }


/*
 * (c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/