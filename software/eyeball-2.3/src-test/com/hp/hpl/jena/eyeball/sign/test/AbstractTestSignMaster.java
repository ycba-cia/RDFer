/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 	All rights reserved.
 	$Id: AbstractTestSignMaster.java,v 1.3 2010/03/26 14:50:32 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.sign.test;

import org.junit.Test;

import com.hp.hpl.jena.eyeball.sign.SignMaster;
import com.hp.hpl.jena.eyeball.test.EyeballTestBase;

public abstract class AbstractTestSignMaster extends EyeballTestBase
    {
    public AbstractTestSignMaster( String name )
        {}

    protected abstract SignMaster getSignMaster();
    
    protected final SignMaster sm = getSignMaster();
    
    public static void assertDiffer( long unexpected, long actual )
        {
        if (unexpected == actual)
            fail( "actual value should not have been " + actual );
        }
    
    /**
         Test that various different (but similar) models have different
         signatures.
     */
    @Test public void testDistinguishesPermutedSingleTripleModels()
        {
        assertDifferentSignatures( "a P b", "a b P" );
        assertDifferentSignatures( "a P b", "b P a" );
        assertDifferentSignatures( "a P b", "b a P" );
        assertDifferentSignatures( "a P b", "P a b" );
        assertDifferentSignatures( "a P b", "P b a" );
        }
    
    @Test public void testDistinguishesByEachPosition()
        {
        assertDifferentSignatures( "a P b", "a P c" );
        assertDifferentSignatures( "a P b", "a Q b" );
        assertDifferentSignatures( "a P b", "b P c" );
        }
    
    @Test public void testDistinguishesLiterals()
        {
        assertDifferentSignatures( "it Is 1", "it Is '1'" );
        assertDifferentSignatures( "it Is a", "it Is 'a'" );
        assertDifferentSignatures( "it Is my:/z", "it Is 'my:/z'" );
        assertDifferentSignatures( "it Is 1", "it Is '01'" );
        }
    
    @Test public void testDistinguishesDoubleTripleModels()
        {
         assertDifferentSignatures( "a P b; b P a", "a P a; b P b" );
        }
    
    @Test public void testConflatesIndistinguishableBnodes()
        {
        assertSameSignature( "a P _x", "a P _y" );
        assertSameSignature( "_a P _x", "_b P _y" );
        assertSameSignature( "_a P x", "_b P x" );
        }

    /**
        Test that the signatures differ when properties are moved around
        between blank nodes. [It is /possible/ that this test will fail by
        coincidence, depending on the details of the node hashes. But it
        is unlikely.]
    */
    @Test public void testSignatureModelWithBlanks()
        {
        assertDifferentSignatures( "_x P a; _x P b", "_x P a; _y P b" );
        assertDifferentSignatures( "_x P _y; _y Q _z", "_x Q _y; _y P _z" );
        }
    
    protected void assertDifferentSignatures( String A, String B )
        { assertDiffer( sm.signature( model( A ) ), sm.signature(  model( B ) ) ); }
    
    protected void assertSameSignature( String A, String B )
        { assertEquals( sm.signature( model( A ) ), sm.signature(  model( B ) ) ); }
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
