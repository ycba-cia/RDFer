/*
 	(c) Copyright 2005 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 	All rights reserved - see end of file.
 	$Id: TestModelQuery.java,v 1.2 2010/03/26 14:50:33 chris-dollin Exp $
*/

package com.hp.hpl.jena.extras.tools.query.test;

import static com.hp.hpl.jena.rdf.model.test.ModelTestBase.resource;
import static com.hp.hpl.jena.rdf.model.test.ModelTestBase.property;
import static com.hp.hpl.jena.rdf.model.test.ModelTestBase.rdfNode;
import static com.hp.hpl.jena.rdf.model.test.ModelTestBase.nodeList;
import static com.hp.hpl.jena.rdf.model.test.ModelTestBase.assertDiffer;
import static com.hp.hpl.jena.rdf.model.test.ModelTestBase.setOfOne;
import static com.hp.hpl.jena.rdf.model.test.ModelTestBase.statement;
import static com.hp.hpl.jena.rdf.model.test.ModelTestBase.triple;
import static com.hp.hpl.jena.rdf.model.test.ModelTestBase.listOfOne;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.hp.hpl.jena.extras.tools.query.*;
import com.hp.hpl.jena.graph.*;
import com.hp.hpl.jena.graph.query.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.test.ModelTestBase;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.extras.tools.query.ModelQuery;

@RunWith(JUnit4.class) public class TestModelQuery
    {   
    private ModelQuery mq = ModelQuery.create( createSpecialQuery );
    
    private static Model empty = ModelFactory.createDefaultModel();
    /**
        Variables should have the name they're given, or they should have a name
        constructed from their order-of-creation index.
    */
    @Test public void testQueryVariableName()
        {
        assertEquals( "_v0", mq.createVariable().toString() );
        assertEquals( "_v1", mq.createVariable().toString() );
        assertEquals( "spoo", mq.createVariable( "spoo" ).toString() );
        assertEquals( "_v3", mq.createVariable().toString() );
        assertEquals( "flarn", mq.createVariable( "flarn" ).toString() );
        }
    
    @Test public void testUniqueNamedVariables()
        {
        assertSame( mq.createVariable( "x" ), mq.createVariable( "x" ) );
        assertDiffer( mq.createVariable( "x" ), mq.createVariable( "y" ) );
        }
    
    @Test public void testVariableValues()
        {
        QueryVariable A = mq.createVariable(), B = mq.createVariable();
        assertEquals( resource( "A" ), A.value( ModelTestBase.nodeList( "A B C" ) ) );
        assertEquals( resource( "B" ), B.value( nodeList( "A B C" ) ) );
        assertEquals( rdfNode( empty, "42" ), A.value( nodeList( "42 'hello' C" ) ) );
        assertEquals( rdfNode( empty, "'hello'" ), B.value( nodeList( "42 'hello' D" ) ) );
        }    
    
    @Test public void testVariableResourceValues()
        {
        QueryVariable A = mq.createVariable(), B = mq.createVariable();
        assertEquals( resource( "A" ), A.resource( nodeList( "A B C" ) ) );
        assertEquals( resource( "B" ), B.resource( nodeList( "A B C" ) ) );
        }
    
    @Test public void testVariableLiteralValues()
        {
        QueryVariable A = mq.createVariable(), B = mq.createVariable();
        assertEquals( rdfNode( empty, "42" ), A.literal( nodeList( "42 'hello' C" ) ) );
        assertEquals( rdfNode( empty, "'hello'" ), B.literal( nodeList( "42 'hello' D" ) ) );
        }
    
    @Test public void testEmpty()
        {
        Set<? extends List<Node>> s = mq.run( model( "a B c" ) ).toSet();
        assertEquals( setOfOne( Collections.EMPTY_LIST ), s );
        }
    
    @Test public void testA()
        {
        mq.add( resource( "x" ), property( "p" ), resource( "y" ) );
        assertEquals( Collections.EMPTY_SET, mq.run( model( "" ) ).toSet() );
        assertEquals( Collections.EMPTY_SET, mq.run( model( "a Q b" ) ).toSet() );
        assertEquals( setOfOne( Collections.EMPTY_LIST ), mq.run( model( "x p y" ) ).toSet() );
        }
    
    @Test public void testEmptyQueryHasNoTriples()
        {
        assertEquals( Collections.EMPTY_LIST, getTriples( mq ) );
        }
    
    @Test public void testSingleStatementTriple()
        {
        mq.add( statement( "S P O" ) );
        assertEquals( listOfOne( triple( "S P O" ) ), getTriples( mq ) );
        }    
    
    @Test public void testMultiStatementTriples()
        {
        mq.add( statement( "S P O" ) ).add( statement( "A B C" ) ).add( statement( "I J K" ) );
        assertEquals( tripleList( "S P O; A B C; I J K" ), getTriples( mq ) );
        }
    
    @Test public void testVariableOrder()
        {
        QueryVariable A = mq.createVariable();
        QueryVariable B = mq.createVariable();
        QueryVariable C = mq.createVariable();
        mq.add( C, property( "p" ), resource( "x" ) );
        mq.add( resource( "a" ), B, resource( "b" ) );
        mq.add( resource( "h" ), property( "p" ), C );
        mq.run( model( "" ) );
        Node [] variables = getQuery( mq ).variables;
        assertEquals( mq.translate( A ), variables[0] );
        }
    
    @Test public void testVariables()
        {
        Statement [] statements = new Statement [] 
            { ResourceFactory.createStatement( resource( "a" ), property( "p" ), mq.createVariable() ) };
        check( statements );
        }
    
    @Test public void testVariables2()
        {
        QueryVariable Q = mq.createVariable();
        Statement [] statements = new Statement [] 
            { ResourceFactory.createStatement( resource( "a" ), property( "p" ), Q ),
            ResourceFactory.createStatement( Q, property( "q" ), resource( "b" ) ) };
        check( statements );
        }
    
    @Test public void testNoCrossover()
        {
        QueryVariable B = ModelQuery.create().createVariable();
        try { mq.add( B, B, B ); fail( "should detect mismatch" ); }
        catch (Exception e) {}
        }
    
    private void check( Statement[] statements )
        {
        List<Triple> triples = fillQuery( statements );
        check( statements, triples );
        }

    private List<Triple> fillQuery( Statement[] statements )
        {
        for (int i = 0; i < statements.length; i += 1) mq.add( statements[i] );
        List<Triple> triples = getTriples( mq );
        return triples;
        }

    private void check( Statement[] statements, List<Triple> triples )
        {
        assertEquals( statements.length, triples.size() );
        for (int i = 0; i < statements.length; i += 1)
            {
            Statement s = statements[i];
            Triple t = triples.get(i);
            check( s.getSubject().asNode(), t.getSubject() );
            check( s.getObject().asNode(), t.getObject() );
            check( s.getPredicate().asNode(), t.getPredicate() );
            }
        }

    private void check( Node original, Node translated )
        {
        if (!original.equals( translated ))
            {
            assertTrue( "original component must be blank node", original.isBlank() );
            assertTrue( "translated component must be variable", translated.isVariable() );
            assertEquals( "binding must be consistent", original,  lookup( translated, original ) );
            }
        }

    private Map<Node, Node> map = new HashMap<Node, Node>();
    
    private Node lookup( Node translated, Node original )
        {
        Node x = map.get( translated );
        return x == null ? insert( translated, original ) : x;
        }

    private Node insert( Node translated, Node original )
        {
        map.put( translated, original );
        return original;
        }

    private List<Triple> tripleList( String string )
        { return Arrays.asList( ModelTestBase.tripleArray( string ) ); }

    private List<Triple> getTriples( ModelQuery mq )
        { return getQuery( mq ).triples; }

    private SpecialQuery getQuery( ModelQuery mq )
        { return (SpecialQuery) mq.query; }

    private Model model( String facts )
        { return ModelTestBase.modelWithStatements( facts ); }    
    
    private static class SpecialQuery extends Query
        {
        public List<Triple> triples = new ArrayList<Triple>();
        public Node [] variables = null;
        
        public ExtendedIterator<Domain> executeBindings( Graph g, Node [] variables )
            {
            this.variables = variables;
            return super.executeBindings( g, variables );
            }
        
        public Query addMatch( Node S, Node P, Node O )
            {
            triples.add( Triple.create( S, P, O ) );
            return super.addMatch( S, P, O );
            }
        }
    
    private static ModelQuery.QueryFactory createSpecialQuery =
        new ModelQuery.QueryFactory() 
            { public Query create() { return new SpecialQuery(); }};
        
    }


/*
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP
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