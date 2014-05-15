/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 	All rights reserved.
 	$Id: TestSignMaster_0_1.java,v 1.4 2010/03/26 14:50:32 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.sign.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.hp.hpl.jena.eyeball.sign.*;
import com.hp.hpl.jena.graph.*;

@RunWith(JUnit4.class) public class TestSignMaster_0_1 extends AbstractTestSignMaster
    {
    public TestSignMaster_0_1()
        { super( "TestSignMaster_0_1" ); }

    protected SignMaster getSignMaster()
        { return new SignMaster_0_1(); }

    protected final SignMaster_0_1 sm01 = (SignMaster_0_1) sm;
    
    protected static final long LITERAL_MULTIPLIER = 19853;
    
    protected static final long SUBJECT_MULTIPLIER = 39709;
    protected static final long PREDICATE_MULTIPLIER = 307;
    protected static final long OBJECT_MULTIPLIER = 1;
    
    @Test public void testSignatureURINodes()
        {
        testSignatureURINode( node( "x" ) );
        testSignatureURINode( node( "rdf:Property" ) );
        testSignatureURINode( node( "http://none.uk/empty" ) );
        testSignatureURINode( node( "urn:x-hp:flumbleweed:me" ) );
        }

    private void testSignatureURINode( Node node )
        { assertEquals( node.hashCode(), sm01.signature( node ) ); }
    
    @Test public void testSignatureLiteralNodes()
        {
        testSignatureLiteralNode( node( "17" ) );
        testSignatureLiteralNode( node( "'literal'" ) );
        testSignatureLiteralNode( node( "'chat'en" ) );
        testSignatureLiteralNode( node( "'1234'xsd:integer" ) );
        }
    
    private void testSignatureLiteralNode( Node node )
        { assertEquals( node.hashCode() * LITERAL_MULTIPLIER, sm01.signature( node ) ); }
    
    @Test public void testSignatureBlankNodes()
        {
        assertEquals( 0, sm01.signature( node( "_foo" ) ) );
        assertEquals( 0, sm01.signature( node( "_foo:bar" ) ) );
        assertEquals( 0, sm01.signature( node( "_:foo/bar" ) ) );
        assertEquals( 0, sm01.signature( node( "_123:456" ) ) );
        assertEquals( 0, sm01.signature( node( "_94628549693" ) ) );
        }
    
    @Test public void testMultiplierConstants()
        {
        assertEquals( SUBJECT_MULTIPLIER, SignMaster_0_1.SUBJECT_MULTIPLIER );
        assertEquals( PREDICATE_MULTIPLIER, SignMaster_0_1.PREDICATE_MULTIPLIER );
        assertEquals( OBJECT_MULTIPLIER, SignMaster_0_1.OBJECT_MULTIPLIER );
        }
    
    @Test public void testSignatureTriples()
        {
        testSignatureTriple( triple( "a p b" ) );
        testSignatureTriple( triple( "a p 1" ) );
        testSignatureTriple( triple( "a p 'hello'" ) );
        testSignatureTriple( triple( "a p 'goodbye'fr" ) );
        testSignatureTriple( triple( "a p _b" ) );
        testSignatureTriple( triple( "_a p b" ) );
        testSignatureTriple( triple( "17 p b" ) );
        testSignatureTriple( triple( "a _p b" ) );
        }
    
    private void testSignatureTriple( Triple t )
        {
        long expected = 
            sm01.signature( t.getSubject() ) * SUBJECT_MULTIPLIER 
            + sm01.signature(  t.getPredicate()  ) * PREDICATE_MULTIPLIER
            + sm01.signature(  t.getObject() ) * OBJECT_MULTIPLIER
            ;
        assertEquals( expected, sm01.tripleSignature(  t  ) );
        }
    
    @Test public void testOrderSensitivity()
        {
        assertDiffer( sm01.signature( node( "x" ) ), sm01.signature( node( "y" ) ) );
        assertDiffer( sm01.signature( node( "x" ) ), sm01.signature( node( "P" ) ) );
        assertDiffer( sm01.signature( node( "y" ) ), sm01.signature( node( "P" ) ) );
        assertDiffer( sm01.tripleSignature( triple( "x P y" ) ), sm01.tripleSignature( triple( "y P x" ) ) );
        assertDiffer( sm01.tripleSignature( triple( "x P y" ) ), sm01.tripleSignature( triple( "x y P" ) ) );
        assertDiffer( sm01.tripleSignature( triple( "x P y" ) ), sm01.tripleSignature( triple( "y x P" ) ) );
        }
    
    @Test public void testWhy()
        { assertDifferentSignatures( "a P b; b P a", "a P a; b P b" ); }
    
    @Test public void testSignatureEmptyModelIsZero()
        { assertEquals( 0, sm01.signature( model( "" ) ) ); }
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
