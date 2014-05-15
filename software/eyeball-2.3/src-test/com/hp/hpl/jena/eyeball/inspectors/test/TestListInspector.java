/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 	All rights reserved.
 	$Id: TestListInspector.java,v 1.4 2010/03/26 14:50:31 chris-dollin Exp $
*/
package com.hp.hpl.jena.eyeball.inspectors.test;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.inspectors.ListInspector;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;

@RunWith(JUnit4.class) public class TestListInspector extends InspectorTestBase
    {
    public TestListInspector()
        {}

    protected Class<? extends Inspector> getInspectorClass()
        { return ListInspector.class; }

    protected final ListInspector ins = new ListInspector();
    
    protected final Report r = new Report();

    protected final OntModel xListT = ontModel
        ( "x rdfs:subClassOf rdf:List"
        + "; x rdfs:subClassOf [owl:onProperty rdf:first & owl:allValuesFrom T]"
        + "; x rdfs:subClassOf [owl:onProperty rdf:rest & owl:allValuesFrom x]" 
        );

    protected final OntModel yListU = ontModel
        ( "y rdfs:subClassOf rdf:List"
        + "; y rdfs:subClassOf [owl:onProperty rdf:first & owl:allValuesFrom U]"
        + "; y rdfs:subClassOf [owl:onProperty rdf:rest & owl:allValuesFrom y]" 
        );
    
    @Test public void testPredicatesDeclared()
        {
        OntModel m = ontModel( "" );
        ins.inspectModel( r, m );
        List<Property> predicates = r.getPredicateRegister().getRegisteredPredicates();
        assertEquals( eyeResourceSet( "illFormedList because suspectListIdiom" ), new HashSet<Property>( predicates ) );
        }
    
    protected final Map<Resource, Resource> justList = maplet( RDF.List, RDFS.Resource );

    @Test public void testNoListTypesDeclared()
        {
        OntModel m = ontModel( "x P y" );
        ins.inspectModel( r, m );
        assertEquals( justList, ins.getIdiomaticListTypes() );
        }

    @Test public void testFindsNoBrokenLists()
        {
        OntModel om = ontModel( "" );
        ins.inspectModel( r, om );
        assertEquals( resourceSet( "" ), ins.getSuspectListTypes() );
        }
    
    private Map<Resource, Resource> maplet( Resource list, Resource type )
        {
        Map<Resource, Resource> result = new HashMap<Resource, Resource>();
        result.put( RDF.List, type );
        return result;
        }

    /**
         Test that partial descriptions (a) don't result in the detection of typed
         lists but (b) do result in the detection of "broken" lists.
    <p>
        We get the partial descriptions by deleting a single statement from
        the template full description.
    */
    @Test public void testPartialDescriptionsMakeBrokenNotTypedLists()
        {
        StmtIterator it = xListT.getBaseModel().listStatements();
        while (it.hasNext())
            {
            Statement s = it.nextStatement();
            OntModel m = ModelFactory.createOntologyModel();
            m.add( xListT.getBaseModel() );
            m.remove( s );
            ListInspector x = new ListInspector();
            x.inspectModel( new Report(), m );
            if (!s.equals(  statement( "x rdfs:subClassOf rdf:List" ) ))
                assertEquals( "BROKEN for missing " + s, 1, x.getSuspectListTypes().size() );
            if (x.getIdiomaticListTypes().size() > 1)
                fail( "list type mistakenly found from partial description " + nice( m.getBaseModel().getGraph(), new HashMap<Node, Object>() ) );
            }
        }
    
    @Test public void testPartialListGeneratesReport()
        {
        String partialDesc = 
            "B rdfs:subClassOf rdf:List"
            + "; B rdfs:subClassOf [owl:onProperty rdf:first & owl:allValuesFrom T]"
            + "; B rdfs:subClassOf [owl:onProperty rdf:first & owl:allValuesFrom B]"; 
        OntModel om = ontModel( partialDesc );
        ins.inspectModel( r, om );
        String expected = "[eye:mainProperty eye:suspectListIdiom & eye:suspectListIdiom B]";
        assertIsoModels( itemModel( expected ), r.model() );
        }
    
    @Test public void testOneListTypeDetected()
        {
        ins.inspectModel( r, xListT );
        assertEquals( 2, ins.getIdiomaticListTypes().size() );
        assertTrue( ins.getIdiomaticListTypes().containsKey( resource( "x" ) ) );
        }
    
    @Test public void testTwoListTypesDetected()
        {
        OntModel m = ModelFactory.createOntologyModel();
        m.getBaseModel().add( xListT.getBaseModel() );
        m.getBaseModel().add( yListU.getBaseModel() );
        ins.inspectModel( r, m );
        assertEquals( 3, ins.getIdiomaticListTypes().size() );
        assertTrue( ins.getIdiomaticListTypes().containsKey( resource( "x" ) ) );
        assertTrue( ins.getIdiomaticListTypes().containsKey( resource( "y" ) ) );
        }
    
    @Test public void testFindsElementTypeT()
        {
        ins.inspectModel( r, xListT );
        assertEquals( resource( "T" ), ins.getIdiomaticListTypes().get( resource( "x" ) ) );
        }
    
    @Test public void testFindsElementTypeU()
        {
        ins.inspectModel( r, yListU );
        assertEquals( resource( "U" ), ins.getIdiomaticListTypes().get( resource( "y" ) ) );
        }
    
    @Test public void testFindsNoPropertiesWithListRange()
        {
        OntModel om = ontModel( "" );
        ins.inspectModel( r, om );
        assertEquals( Collections.EMPTY_MAP, ins.getIdiomaticListProperties() );
        }
    
    @Test public void testFindsPropertiesWithListRange()
        {
        OntModel om = (OntModel) ontModel( "P rdfs:range x; Q rdfs:range z" ).add( xListT );
        ins.inspectModel( r, om );
        assertEquals( resourceSet( "P" ), ins.getIdiomaticListProperties().keySet() );
        }
    
    @Test public void testInspectStatementCallsCheckListIfPropertyListful()
        {
        final Set<Resource> history = new HashSet<Resource>();
        Inspector x = new ListInspector()
            {
            @Test public void inspectModel( Report r, OntModel assume )
                { propertiesWithListRange.put( resource( "P" ), resource( "T" ) ); }
            
            @Test public void inspectList( Report r, Statement s, Resource root, Resource type )
                { history.add( root ); }
            };
        x.inspectModel( r, ontModel( "" ) );
        x.inspectStatement( r, statement( "a P b" ) );
        x.inspectStatement( r, statement( "c Q d" ) );
        assertEquals( resourceSet( "b" ), history );
        }
    
    @Test public void testChecksNilWellFormed()
        {
        OntModel om = ontModel( "a P rdf:nil" );
        ins.inspectList( r, statement( "a P rdf:nil"), resource( om, "rdf:nil" ) );
        assertIsoModels( model(), r.model() );
        }
    
    @Test public void testChecksSingletonListWellFormed()
        {
        OntModel om = ontModel( "a P b; b rdf:first F; b rdf:rest rdf:nil" );
        ins.inspectList( r, statement( "a P b"), resource( om, "b" ) );
        assertIsoModels( model(), r.model() );
        }
    
    @Test public void testReportsFirstlessList()
        {
        OntModel om = ontModel( "a P b; b rdf:rest rdf:nil" );
        ins.inspectList( r, statement( "a P b"), resource( om, "b" ) );
        String expected = 
            "[eye:mainProperty eye:illFormedList & eye:illFormedList b"
            + " & eye:because [eye:element 1 & eye:hasNoFirst b]] rdf:type eye:Item";
        assertIsoModels( itemModel( expected, statement( "a P b" ) ), r.model() );
        }

    @Test public void testReportsMultipleFirstList()
        {
        OntModel om = ontModel( "a P b; b rdf:first X & rdf:first Y & rdf:rest rdf:nil" );
        ins.inspectList( r, statement( "a P b"), resource( om, "b" ) );
        String expected = 
            "[eye:mainProperty eye:illFormedList & eye:illFormedList b"
            +" & eye:because [eye:element 1 & eye:hasMultipleFirsts b]] rdf:type eye:Item";
        assertIsoModels( itemModel( expected, statement( "a P b" ) ), r.model() );
        }
    
    @Test public void testReportsFirstlessListOnSubsequentElement()
        {
        OntModel om = ontModel( "a P b; b rdf:first X & rdf:rest c; c rdf:rest rdf:nil" );
        ins.inspectList( r, statement( "a P b"), resource( om, "b" ) );
        String expected = 
            "[eye:mainProperty eye:illFormedList & eye:illFormedList c"
            + " & eye:because [eye:element 2 & eye:hasNoFirst c]] rdf:type eye:Item";
        assertIsoModels( itemModel( expected, statement( "a P b" ) ), r.model() );
        }

    @Test public void testReportsRestlessList()
        {
        OntModel om = ontModel( "a P b; b rdf:first F" );
        ins.inspectList( r, statement( "a P b"), resource( om, "b" ) );
        String expected = 
            "[eye:mainProperty eye:illFormedList & eye:illFormedList b"
            + " & eye:because [eye:element 1 & eye:hasNoRest b]] rdf:type eye:Item";
        assertIsoModels( itemModel( expected, statement( "a P b" ) ), r.model() );
        }
    
    @Test public void testReportsMultipleRestsList()
        {
        OntModel om = ontModel( "a P b; b rdf:first F & rdf:rest A & rdf:rest B" );
        ins.inspectList( r, statement( "a P b"), resource( om, "b" ) );
        String expected = 
            "[eye:mainProperty eye:illFormedList & eye:illFormedList b"
            + " & eye:because [eye:element 1 & eye:hasMultipleRests b]] rdf:type eye:Item";
        assertIsoModels( itemModel( expected, statement( "a P b" ) ), r.model() );
        }
    
    @Test public void testReportsTypeElementFailure()
        {
        OntModel om = ontModel( "a P b; b rdf:first F & rdf:rest rdf:nil" );
        Resource LT = resource( "LT" ), T = resource( "T" );
        ins.getIdiomaticListTypes().put( LT, T );
        ins.inspectList( r, statement( "a P b"), resource( om, "b" ), resource( "T" ) );
        String expected = 
            "[eye:mainProperty eye:illTypedListElement & eye:illTypedListElement b"
            + " & eye:because [eye:element 1 & eye:shouldHaveType T]] rdf:type eye:Item";
        assertIsoModels( itemModel( expected, statement( "a P b" ) ), r.model() );
        }
    }

