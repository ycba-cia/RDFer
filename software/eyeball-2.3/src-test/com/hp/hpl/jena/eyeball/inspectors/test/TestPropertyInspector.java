/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 	All rights reserved - see end of file.
 	$Id: TestPropertyInspector.java,v 1.5 2010/03/26 14:50:31 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.inspectors.test;

import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.inspectors.PropertyInspector;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;

@RunWith(JUnit4.class) public class TestPropertyInspector extends InspectorTestBase
    {
    public TestPropertyInspector()
        {}

    protected Class<? extends Inspector> getInspectorClass()
        { return PropertyInspector.class; }
    
    protected final PropertyInspector ins = new PropertyInspector();
    
    protected final Report r = new Report();
    
    @Test public void testPropertyInspectorDeclaresPredicates()
        {
        Report r = new Report();
        ins.begin( r, ontModel() );
        List<Property> predicates = r.getPredicateRegister().getRegisteredPredicates();
        assertEquals( eyeResourceSet( "unknownPredicate" ), new HashSet<Property>( predicates ) );
        }
    
    @Test public void testKnownRDFProperties()
        {
        assertKnownPredicate( ins, RDF.type );
        assertKnownPredicate( ins, RDF.first );
        assertKnownPredicate( ins, RDF.rest );
        assertKnownPredicate( ins, RDF.subject );
        assertKnownPredicate( ins, RDF.predicate );
        assertKnownPredicate( ins, RDF.object );
        assertKnownPredicate( ins, RDF.value );
        }
    
    @Test public void testKnownRDFSProperties()
        {
        assertKnownPredicate( ins, RDFS.comment );
        assertKnownPredicate( ins, RDFS.label );
        assertKnownPredicate( ins, RDFS.domain );
        assertKnownPredicate( ins, RDFS.range );
        assertKnownPredicate( ins, RDFS.isDefinedBy );
        assertKnownPredicate( ins, RDFS.seeAlso );
        assertKnownPredicate( ins, RDFS.member );
        assertKnownPredicate( ins, RDFS.subClassOf );
        assertKnownPredicate( ins, RDFS.subPropertyOf );
        }
    
    @Test public void testUnknownProperties()
        {
        assertFalse( ins.knownProperty( property( "eh:/plunder" ) ) );
        assertFalse( ins.knownProperty( property( "rdf:noSuchProperty" ) ) );
        assertFalse( ins.knownProperty( property( "rdfs:notThisOne" ) ) );
        assertFalse( ins.knownProperty( property( "p" ) ) );
        }

    @Test public void testAssumeDomainProvidesKnownProperty()
        { testAssumeContentDeclaresPropertyP( "p rdfs:domain y" ); }

    @Test public void testAssumeRangeProvidesKnownProperty()
        { testAssumeContentDeclaresPropertyP( "p rdfs:range y" ); }
    
    @Test public void testAssumeTypeProvidesKnownProperty()
        { testAssumeContentDeclaresPropertyP( "p rdf:type rdf:Property" ); }

    private void testAssumeContentDeclaresPropertyP( String content )
        {
        OntModel assume = ontModel( content );
        ins.begin( new Report(), assume );
        assertTrue( "(" + content + ") should declare 'p' as a property", ins.knownProperty( property( "p" ) ) );
        }

    @Test public void testImportsUpdateKnownProperties()
        {
        OntModel model = ontModel( "" );
        model.addSubModel( model( "X rdf:type rdf:Property" ) );
        model.addSubModel( model( "Y rdf:type rdf:Property" ) );
        ins.inspectModel( new Report(), model );
        assertTrue( ins.knownProperty( property( "X" ) ) );
        assertTrue( ins.knownProperty( property( "Y" ) ) );
        }
    
    private void assertKnownPredicate( PropertyInspector ins, Property p )
        {
        if (!ins.knownProperty( p )) 
            fail( "the property " + p + " should be known as a builtin" );
        }

    @Test public void testDoesNotReportKnownPredicate()
        {
        ins.inspectStatement( r, statement( "s rdf:type o" ) );
        assertIsoModels( model( "" ), r.model() );
        }
    
    @Test public void testCountsLocalPropertyAsDeclared()
        {
        OntModel model = ontModel( "P rdf:type rdf:Property" );
        ins.inspectModel(  new Report(), model );
        assertTrue( ins.knownProperty( property( "P" ) ) );
        }
    
    @Test public void testReportsUnknownPredicate()
        {
        Statement source = statement( "s P o" );
        ins.inspectStatement( r, source );
        assertIsoModels( itemModel( "[eye:mainProperty eye:unknownPredicate & eye:unknownPredicate P]", source ), r.model() );
        }
    
    @Test public void testReportsMultipleUnknownPredicates()
        {
        Statement sourceA = statement( "s P o" ), sourceB = statement( "x Q y" );
        Model modelA = itemModel( "[eye:mainProperty eye:unknownPredicate & eye:unknownPredicate P]", sourceA );
        Model modelB = itemModel( "[eye:mainProperty eye:unknownPredicate & eye:unknownPredicate Q]", sourceB );
        ins.inspectStatement( r, sourceA );
        ins.inspectStatement( r, sourceB );
        assertIsoModels( ModelFactory.createUnion( modelA, modelB ), r.model() );
        }
    
    @Test public void testReportsUnknownPredicateOnce()
        {
        Statement source = statement( "s P o" );
        ins.inspectStatement( r, source );
        ins.inspectStatement( r, statement( "a P b" ) );
        assertIsoModels( itemModel( "[eye:mainProperty eye:unknownPredicate & eye:unknownPredicate P]", source ), r.model() );
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