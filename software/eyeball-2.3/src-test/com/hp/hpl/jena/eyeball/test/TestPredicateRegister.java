/*
    (c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
    All rights reserved - see end of file.
    $Id: TestPredicateRegister.java,v 1.4 2010/03/26 14:50:31 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.test;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.test.ModelTestBase;

/**
 * @author hedgehog
 */
@RunWith(JUnit4.class) public class TestPredicateRegister extends ModelTestBase
    {
    public TestPredicateRegister()
        { super( "TestPredicateRegister" ); }

    public PredicateRegister getRegister()
        { return new SimplePredicateRegister(); }

    @Test public void testRegisterAddsPredicate()
        {
        PredicateRegister r = getRegister();
        assertEquals( new ArrayList<Property>(), r.getRegisteredPredicates() );
        Property property = empty.createProperty( "eh:/something" );
        r.register( property );
        assertEquals( one( property ), r.getRegisteredPredicates() );
        }

    @Test public void testRegisterDropsDuplicates()
        {
        PredicateRegister r = getRegister();
        Property property = empty.createProperty( "eh:/something" );
        r.register( property );
        r.register( property );
        assertEquals( one( property ), r.getRegisteredPredicates() );
        }
    
    @Test public void testRegisterKeepsSeveral()
        {
        PredicateRegister r = getRegister();
        Property p1 = property( "p1" );
        Property p2 = property( "p2" );
        r.register( p1 );
        r.register( p2 );
        assertEquals( new HashSet<Property>( two( p1, p2 ) ), new HashSet<Property>( r.getRegisteredPredicates() ) );
        }

    @Test public void testResisterOrderingA()
        {
        PredicateRegister r = getRegister();
        Property p1 = property( "p1" );
        Property p2 = property( "p2" );
        r.order( p1, p2 );
        assertEquals( two( p1, p2 ), r.getRegisteredPredicates() );
        }

    @Test public void testResisterOrderingB()
        {
        PredicateRegister r = getRegister();
        Property p1 = property( "p1" );
        Property p2 = property( "p2" );
        Property p3 = property( "p3" );
        r.order( p1, p2 );
        r.order( p3, p1 );
        assertEquals( three( p3, p1, p2 ), r.getRegisteredPredicates() );
        }

    protected <T> List<T> one( T x )
        {
        List<T> result = new ArrayList<T>();
        result.add( x );
        return result;
        }

    protected <T> List<T> two( T x, T y )
        { List<T> result = one( x );
        result.add( y );
        return result;
        }

    protected <T> List<T> three( T x, T y, T z )
        {
        List<T> result = two( x, y );
        result.add( z );
        return result;
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
