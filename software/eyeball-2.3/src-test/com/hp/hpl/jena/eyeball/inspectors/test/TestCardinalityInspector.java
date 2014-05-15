/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 	All rights reserved - see end of file.
 	$Id: TestCardinalityInspector.java,v 1.4 2010/03/26 14:50:31 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.inspectors.test;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.cardinality.*;
import com.hp.hpl.jena.eyeball.inspectors.CardinalityInspector;
import com.hp.hpl.jena.rdf.model.*;

@RunWith(JUnit4.class) public class TestCardinalityInspector extends InspectorTestBase
    {
    public TestCardinalityInspector()
        {}

    protected Class<? extends Inspector> getInspectorClass()
        { return CardinalityInspector.class; }

    protected final CardinalityInspector ins = new CardinalityInspector();
    
    protected final Report r = new Report();

    @Test public void testCardinalityInspectorDeclaresPredicates()
        {
        Report r = new Report();
        ins.begin( r, ontModel() );
        List<Property> predicates = r.getPredicateRegister().getRegisteredPredicates();
        assertEquals( eyeResourceSet( "cardinalityFailure cardinality onProperty onType numValues values" ), new HashSet<Property>( predicates ) );
        assertOrder( "cardinalityFailure onProperty", predicates );
        assertOrder( "cardinalityFailure onType", predicates );
        assertOrder( "cardinalityFailure numValues", predicates );
        assertOrder( "cardinalityFailure values", predicates );
        assertOrder( "cardinalityFailure cardinality", predicates );
        }    
    
    @Test public void testInspectorCreatesCardinalityMap()
        {
        ins.begin( r, ontModel( "T rdfs:subClassOf [owl:onProperty P & owl:cardinality 1]" ) );
        ins.inspectModel( r, ontModel( "" ) );
        CardinalityMap cm = ins.getCardinalityMap();
        ClassProperties cp = cm.cardinalities( resource( "T" ) );
        PropertyCardinality pc = cp.get( resource( "P" ) );
        assertEquals( 1, pc.maxCardinality() );
        assertEquals( 1, pc.minCardinality() );
        }

    @Test public void testInspectorDetectsProblem()
        {
        ins.begin( r, ontModel( "T rdfs:subClassOf [owl:onProperty P & owl:cardinality 1]" ) );
        ins.inspectModel( r, ontModel( "" ) );
        inspectAllStatements( model( "x rdf:type T" ) );
        assertIsoModels( expect(), r.model() );
        }

    private Model expect()
        {
        return itemModel
            ( "[eye:mainProperty eye:cardinalityFailure"
            + " & eye:cardinalityFailure x & eye:onProperty P"
            + " & eye:onType T & eye:numValues 0 & eye:values [rdf:type eye:Set]"
            + " & eye:cardinality [eye:min 1 & eye:max 1]]" );
        }

    @Test public void testInspectorDetectsProblemOnce()
        {
        ins.begin(  r, ontModel( "T rdfs:subClassOf [owl:onProperty P & owl:cardinality 1]" ) );
        ins.inspectModel( r, ontModel( "" ) );
        inspectAllStatements( model( "x rdf:type T & rdf:value 17" ) );
        assertIsoModels( expect(), r.model() );
        }

    @Test public void testInspectorIgnoresNonProblem()
        {
        ins.begin(  r, ontModel( "T rdfs:subClassOf [owl:onProperty P & owl:cardinality 1]" ) );
        ins.inspectModel( r, ontModel( "" ) );
        inspectAllStatements( model( "y rdf:type U" ) );
        assertIsoModels( model( "" ), r.model() );
        }

    @Test public void testInspectorIgnoresCorrectProperties()
        {
        ins.begin( r, ontModel( "T rdfs:subClassOf [owl:onProperty P & owl:cardinality 1]" ) );
        ins.inspectModel( r, ontModel( "" ) );
        inspectAllStatements( model( "y rdf:type T & P someValue" ) );
        assertIsoModels( model( "" ), r.model() );
        }

    private void inspectAllStatements( Model data )
        { 
        for (StmtIterator it = data.listStatements(); it.hasNext();) 
            ins.inspectStatement( r, it.nextStatement() );
        }

    @Test public void testCardinalityConstraintsFromModelAreDetected()
        {
        ins.begin( r, ontModel( "" ) );
        ins.inspectModel( r, ontModel( "Mumps rdfs:subClassOf [owl:onProperty spoo & owl:cardinality 1]" ) );
        inspectAllStatements( model( "x rdf:type Mumps" ) );
        String expect =
            "[eye:mainProperty eye:cardinalityFailure & eye:cardinalityFailure x & eye:values [rdf:type eye:Set]"
            + " & eye:onProperty spoo & eye:onType Mumps & eye:numValues 0"
            + " & eye:cardinality [eye:min 1 & eye:max 1]]";
        assertIsoModels( itemModel( expect ), r.model() );
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