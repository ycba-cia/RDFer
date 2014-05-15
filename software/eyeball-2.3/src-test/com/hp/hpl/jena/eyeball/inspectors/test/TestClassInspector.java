/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 	All rights reserved - see end of file.
 	$Id: TestClassInspector.java,v 1.5 2010/03/26 14:50:31 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.inspectors.test;

import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.inspectors.ClassInspector;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;

@RunWith(JUnit4.class) public class TestClassInspector extends InspectorTestBase
    {
    public TestClassInspector()
        {}

    protected Class<? extends Inspector> getInspectorClass()
        { return ClassInspector.class; }

    protected final ClassInspector ins = new ClassInspector();
    
    protected final Report r = new Report();
    
    @Test public void testClassInspectorDeclaresPredicates()
        {
        Report r = new Report();
        ins.begin( r, ontModel() );
        List<Property> predicates = r.getPredicateRegister().getRegisteredPredicates();
        assertEquals( eyeResourceSet( "unknownClass" ), new HashSet<Property>( predicates ) );
        }
    
    @Test public void testBuiltinRDFKnownClasses()
        {
        assertKnownClass( ins, RDF.Alt );
        assertKnownClass( ins, RDF.Bag );
        assertKnownClass( ins, RDF.Seq );
        assertKnownClass( ins, RDF.List );
        assertKnownClass( ins, RDF.Property );
        assertKnownClass( ins, RDF.Statement );
        }

    @Test public void testBuiltinRDFSClasses()
        {
        assertKnownClass( ins, RDFS.Class );
        assertKnownClass( ins, RDFS.Datatype );
        assertKnownClass( ins, RDFS.Container );
        assertKnownClass( ins, RDFS.Literal );
        assertKnownClass( ins, RDFS.Resource );
        }

    @Test public void testBuiltinOWLClasses()
        {
        assertKnownClass( ins, OWL.Class );
        assertKnownClass( ins, OWL.DataRange );
        assertKnownClass( ins, OWL.Ontology );
        assertKnownClass( ins, OWL.DeprecatedClass );
        assertKnownClass( ins, OWL.AllDifferent );
        assertKnownClass( ins, OWL.DatatypeProperty );
        assertKnownClass( ins, OWL.SymmetricProperty );
        assertKnownClass( ins, OWL.TransitiveProperty );
        assertKnownClass( ins, OWL.DeprecatedProperty );
        assertKnownClass( ins, OWL.AnnotationProperty );
        assertKnownClass( ins, OWL.Restriction );
        assertKnownClass( ins, OWL.OntologyProperty );
        assertKnownClass( ins, OWL.ObjectProperty );
        assertKnownClass( ins, OWL.FunctionalProperty );
        assertKnownClass( ins, OWL.InverseFunctionalProperty );
        assertKnownClass( ins, OWL.Nothing );
        }

    @Test public void testBuiltinXSDClasses()
        {
        assertKnownClass( ins, XSD.xfloat );
        assertKnownClass( ins, XSD.xdouble );
        assertKnownClass( ins, XSD.xint );
        assertKnownClass( ins, XSD.xlong );
        assertKnownClass( ins, XSD.xshort );
        assertKnownClass( ins, XSD.xbyte );
        assertKnownClass( ins, XSD.unsignedByte );
        assertKnownClass( ins, XSD.unsignedShort );
        assertKnownClass( ins, XSD.unsignedInt );
        assertKnownClass( ins, XSD.unsignedLong );
        assertKnownClass( ins, XSD.decimal );
        assertKnownClass( ins, XSD.integer );
        assertKnownClass( ins, XSD.nonPositiveInteger );
        assertKnownClass( ins, XSD.nonNegativeInteger );
        assertKnownClass( ins, XSD.positiveInteger );
        assertKnownClass( ins, XSD.negativeInteger );
        assertKnownClass( ins, XSD.xboolean );
        assertKnownClass( ins, XSD.xstring );
        assertKnownClass( ins, XSD.normalizedString );
        assertKnownClass( ins, XSD.anyURI );
        assertKnownClass( ins, XSD.token );
        assertKnownClass( ins, XSD.Name );
        assertKnownClass( ins, XSD.QName );
        assertKnownClass( ins, XSD.language );
        assertKnownClass( ins, XSD.NMTOKEN );
        assertKnownClass( ins, XSD.ENTITY );
        assertKnownClass( ins, XSD.ID );
        assertKnownClass( ins, XSD.NCName );
        assertKnownClass( ins, XSD.IDREF );
        assertKnownClass( ins, XSD.NOTATION );
        assertKnownClass( ins, XSD.hexBinary );
        assertKnownClass( ins, XSD.base64Binary );
        assertKnownClass( ins, XSD.date );
        assertKnownClass( ins, XSD.time );
        assertKnownClass( ins, XSD.dateTime );
        assertKnownClass( ins, XSD.duration );
        assertKnownClass( ins, XSD.gDay );
        assertKnownClass( ins, XSD.gMonth );
        assertKnownClass( ins, XSD.gYear );
        assertKnownClass( ins, XSD.gYearMonth );
        assertKnownClass( ins, XSD.gMonthDay );
        }

    private void assertKnownClass( ClassInspector ins, Resource r )
        {
        if (!ins.knownClasses.contains( r ))
            fail( "a ClassInspector should know " + r + " by default" );
        }

    @Test public void testUnknownClasses()
        {
        assertFalse( ins.knownClasses.contains( resource( "A" ) ) );
        assertFalse( ins.knownClasses.contains( resource( "B" ) ) );
        assertFalse( ins.knownClasses.contains( resource( "Sporkle" ) ) );
        assertFalse( ins.knownClasses.contains( resource( "rdf:Sporkle" ) ) );
        assertFalse( ins.knownClasses.contains( resource( "rdfs:Sporkle" ) ) );
        assertFalse( ins.knownClasses.contains( resource( "owl:Sporkle" ) ) );
        assertFalse( ins.knownClasses.contains( resource( "xsd:Sporkle" ) ) );
        }
    
    @Test public void testAssumptionsUpdateKnownClasses()
        {
        OntModel assume = ontModel( "C rdf:type owl:Class; D rdf:type owl:Class" );
        ins.begin( new Report(), assume );
        assertTrue( ins.knownClasses.contains( resource( "C" ) ) );
        assertTrue( ins.knownClasses.contains( resource( "D" ) ) );
        }

    @Test public void testCountsLocalClassAsDeclared()
        {
        OntModel model = ontModel( "C rdf:type owl:Class" );
        ins.inspectModel(  new Report(), model );
        assertTrue( ins.knownClasses.contains( resource( "C" ) ) );
        }
    
    @Test public void testImportsUpdateKnownClasses()
        {
        OntModel model = ontModel( "" );
        model.addSubModel( model( "X rdf:type owl:Class" ) );
        model.addSubModel( model( "Y rdf:type owl:Class" ) );
        ins.inspectModel( new Report(), model );
        assertTrue( ins.knownClasses.contains( resource( "X" ) ) );
        assertTrue( ins.knownClasses.contains( resource( "Y" ) ) );
        }
    
    @Test public void testModelUpdatesKnownClasses()
        {
        OntModel model = ontModel( "X rdf:type owl:Class" );
        ins.inspectModel( new Report(), model );
        assertTrue( ins.knownClasses.contains( resource( "X" ) ) );
        }
    
    @Test public void testReportsUnknownClassAsType()
        { testReportsUnknownClassOnStatement( "X rdf:type A", "A" ); }

    @Test public void testReportsUnknownClassAsSubclass()
        { testReportsUnknownClassOnStatement( "A rdfs:subClassOf rdfs:Resource", "A" ); }    
    
    @Test public void testReportsUnknownClassAsSuperclass()
        { testReportsUnknownClassOnStatement( "rdfs:Resource rdfs:subClassOf S", "S" ); }
    
    @Test public void testReportsUnknownClassAsDomain()
        { testReportsUnknownClassOnStatement( "P rdfs:domain D", "D" ); }
    
    @Test public void testReportsUnknownClassAsRange()
        { testReportsUnknownClassOnStatement( "P rdfs:range R", "R" ); }

    private void testReportsUnknownClassOnStatement( String statementString, String badClass )
        {
        Statement xTypeA = statement( statementString );
        ins.inspectStatement( r, xTypeA );
        String reportString = "[eye:mainProperty eye:unknownClass & eye:unknownClass " + badClass + "]";
        assertIsoModels( itemModel( reportString, xTypeA ), r.model() );
        }
    
    @Test public void testMultipleNonClassesReported()
        {
        Statement sA = statement( "P rdfs:range A" ), sB = statement( "P rdfs:range B" );
        ins.inspectStatement( r, sA );
        ins.inspectStatement( r, sB );
        Model A = itemModel( "[eye:mainProperty eye:unknownClass & eye:unknownClass A]", sA );
        Model B = itemModel( "[eye:mainProperty eye:unknownClass & eye:unknownClass B]", sB );
        assertIsoModels( ModelFactory.createUnion( A, B ), r.model() );
        }
    
    @Test public void testRepeatedNonClassesReportedOnce()
        {
        Statement firstSource = statement( "P rdfs:range A" );
        ins.inspectStatement( r, firstSource );
        ins.inspectStatement( r, firstSource );
        ins.inspectStatement( r, statement( "A rdfs:subClassOf A" ) );
        assertIsoModels( itemModel( "[eye:mainProperty eye:unknownClass & eye:unknownClass A]", firstSource ), r.model() );
        }
    
    @Test public void testAnonynousClassesNotReported()
        {
        Statement root = statement( "rdf:Alt rdfs:subClassOf _c" );
        ins.inspectStatement( r, root );
        assertIsoModels( empty, r.model() );
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
