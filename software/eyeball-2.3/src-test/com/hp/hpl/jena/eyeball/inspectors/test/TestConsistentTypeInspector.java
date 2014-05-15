/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 	All rights reserved - see end of file.
 	$Id: TestConsistentTypeInspector.java,v 1.4 2010/03/26 14:50:31 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.inspectors.test;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.inspectors.ConsistentTypeInspector;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.*;

@RunWith(JUnit4.class) public class TestConsistentTypeInspector extends InspectorTestBase
    {
    public TestConsistentTypeInspector()
        {}

    protected Class<? extends Inspector> getInspectorClass()
        { return ConsistentTypeInspector.class; }
    
    protected final ConsistentTypeInspector ins = new ConsistentTypeInspector();
    
    protected final Report r = new Report();
    
    @Test public void testConsistentTypeInspectorDeclaresPredicates()
        {
        ins.begin( r, ontModel() );
        List<Property> predicates = r.getPredicateRegister().getRegisteredPredicates();
        assertEquals( eyeResourceSet( "noConsistentTypeFor hasAttachedType" ), new HashSet<Property>( predicates ) );
        assertOrder( "noConsistentTypeFor hasAttachedType", predicates );
        }   
    
    @Test public void testSingleType()
        { testPossibleTypes( "x", "T", "", "x rdf:type T" ); }

    @Test public void testTwoDisjointTypes()
        { testPossibleTypes( "y", "", "", "y rdf:type U; y rdf:type V" ); }

    @Test public void testTwoLinearTypes()
        { testPossibleTypes( "z", "U", "", "z rdf:type U; z rdf:type V; U rdfs:subClassOf V" ); }

    public static final String bigSchema =
        "PT rdfs:domain T; PU rdfs:domain U"
        + "; PV rdfs:domain V; PW rdfs:domain W"
        ;

    public static final String bigModel =
        "a PT 1"
        + "; b PT 2; b PU 3"
        + "; V rdfs:subClassOf T; W rdfs:subClassOf T"
        + "; c PV 4"
        ;

    @Test public void testMultipleTypes()
        {
        testPossibleTypes( "a", "T V W", bigSchema, bigModel );
        testPossibleTypes( "b", "", bigSchema, bigModel );
        testPossibleTypes( "c", "V", bigSchema, bigModel );
        }

    @Test public void testSingleReportPerSubject()
        {
        ins.begin( r, ontModel( "R rdfs:domain A; S rdfs:domain B" ) );
        OntModel data = ontModel( "sub R 1; sub S 2" );
        ins.inspectModel( r, data );
        inspectAllStatements( data );
        assertEquals( 1, iteratorToSet( r.model().listSubjects() ).size() );
        }

    /**
        Assert that the possible types of <code>root</code>, as defined by
        <code>model</code>, form the set <code>resultSet</code>. The "possible
        types" are defined to be the intersection of the sets of all subclasses
        of the explicit types of <code>root</code>.
    */
    protected void testPossibleTypes( String root, String resultSet, String schema, String model )
        {
        OntModel assume = ontModel( schema );
        ins.begin( r, assume );
        OntModel data = ontModel( model );
        ins.inspectModel( r, data );
        inspectAllStatements( data );
        assertEquals( resourceSet( resultSet ), ins.consistentSubtypesOf( resource( root ) ) );
        }

    protected Model m( String s )
        { return modelWithStatements( s ); }

    private void inspectAllStatements( Model data )
        { 
        for (StmtIterator it = data.listStatements(); it.hasNext();) 
            ins.inspectStatement( r, it.nextStatement() );
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