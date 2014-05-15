/*
 	(c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
    All rights reserved - see end of file.
    $Id: TestArguments.java,v 1.3 2010/03/26 14:50:31 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.hp.hpl.jena.shared.DoesNotExistException;
import com.hp.hpl.jena.test.JenaTestBase;

import jena.Arguments;
import junit.framework.TestSuite;

/**
 @author kers
 */
@RunWith(JUnit4.class) public class TestArguments extends JenaTestBase
    {
    public TestArguments()
        { super( "TestArguments" ); }

    public static TestSuite suite()
        { return new TestSuite( TestArguments.class ); }

    @Test public void testUnspecifiedAreNull()
        {
        Arguments a = new Arguments( "foo" );
        assertEquals( null, a.valueFor( "foo" ) );
        assertEquals( null, a.valueFor( "spring" ) );
        }

    @Test public void testSpecifiedByFollowing()
        {
        Arguments a = new Arguments( "bill ben" );
        a.processArgs( new String [] { "-bill", "william", "-ben", "weed" } );
        assertEquals( "william", a.valueFor( "bill" ) );
        assertEquals( "weed", a.valueFor( "ben" ) );
        }

    @Test public void testFailureOfUnknownOptions()
        {
        Arguments a = new Arguments( "ok sane" );
        try
            { a.processArgs( new String [] { "-notok", "-insane" } );
            fail( "" ); }
        catch (DoesNotExistException e)
            { pass(); }
        }

    @Test public void testListArguments()
        {
        Arguments a = new Arguments( "lots" );
        a.processArgs( new String [] { "-lots", "A", "B", "C" } );
        assertEquals( listOfStrings( "A B C" ), a.listFor( "lots" ) );
        }

    @Test public void testListArgumentsDefault()
        {
        Arguments a = new Arguments( "lots" );
        a.processArgs( new String[0] );
        assertEquals( listOfStrings( "A B C" ), a.listFor( "lots", "A B C" ) );
        }
    }


/*
 * (c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
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
