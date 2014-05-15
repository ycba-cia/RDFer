/*
 	(c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 	All rights reserved - see end of file.
 	$Id: TestFinder.java,v 1.4 2010/03/26 14:50:31 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.test;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.hp.hpl.jena.eyeball.Finder;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.test.ModelTestBase;
import com.hp.hpl.jena.shared.*;

@RunWith(JUnit4.class) public class TestFinder extends ModelTestBase
    {
    public TestFinder()
        { super( "TestFinder" ); }

    @Test public void testEmptyConfig()
        {
        Model m = m( "" );
        try { new Finder( m ).getClassNames( "absent" ); }
        catch (NotFoundException e ) { assertEquals( "absent", e.getMessage() ); }
        }

    @Test public void testIrrelevantConfig()
        {
        Model m = m( "_x eye:shortName 'present'" );
        try { new Finder( m ).getClassNames( "absent" ); }
        catch (NotFoundException e ) { assertEquals( "absent", e.getMessage() ); }
        }

    @Test public void testNoClassesConfig()
        {
        Model m = m( "_x eye:shortName 'absent'" );
        assertEquals( new ArrayList<String>(), new Finder( m ).getClassNames( "absent" ) );
        }

    @Test public void testSomeDirectClasses()
        {
        Model m = m
            ( "_x eye:shortName 'bill'; _x eye:inspector 'spoo.Smoke'"
            + "; _x eye:inspector 'spoo.Fire'" );
        Set<String> wanted = setOfStrings( "spoo.Smoke spoo.Fire" );
        assertEquals( wanted, getNamesAsSet( m, "bill" ) );
        }

    @Test public void testSomeIrrelevantClasses()
        {
        Model m = m
            ( "_x eye:shortName 'bill'"
            + "; _x eye:inspector 'spoo.Smoke'; _x eye:inspector 'spoo.Fire'"
            + "; _y eye:inspector 'bad.news.Quillan'" );
        Set<String> wanted = setOfStrings( "spoo.Smoke spoo.Fire" );
        assertEquals( wanted, getNamesAsSet( m, "bill" ) );
        }

    @Test public void testIndirectClasses()
        {
        Model m = m
            ( "_x eye:shortName 'bill'"
            + "; _x eye:include _y"
            + "; _y eye:inspector 'ys.Class'" );
        assertEquals( setOfStrings( "ys.Class" ), getNamesAsSet( m, "bill" ) );
        }

    @Test public void testIndirectByName()
        {
        Model m = m
            ( "_x eye:shortName 'bill'"
            + "; _x eye:includeByName 'overThere'"
            + "; _y eye:shortName 'overThere'; _y eye:inspector 'ys.Class'" );
        assertEquals( setOfStrings( "ys.Class" ), getNamesAsSet( m, "bill" ) );
        }

    @Test public void testDeepIndirectClasses()
        {
        Model m = m
            ( "_x eye:shortName 'bill'"
            + "; _x eye:include _y"
            + "; _y eye:inspector 'ys.Class'"
            + "; _y eye:include _z"
            + "; _z eye:inspector 'zs.Class'" );
        assertEquals( setOfStrings( "ys.Class zs.Class" ), getNamesAsSet( m, "bill" ) );
        }

    @Test public void testCircularSpecs()
        {
        Model m = m
            ( "_x eye:shortName 'ben'"
            + "; _x eye:inspector 'xs.Class'; _x eye:include _y"
            + "; _y eye:inspector 'ys.Class'; _y eye:include _x" );
        assertEquals( setOfStrings( "xs.Class ys.Class" ), getNamesAsSet( m, "ben" ) );
        }

    protected Set<String> getNamesAsSet( Model m, String name )
        { return new HashSet<String>( new Finder( m ).getClassNames( name ) ); }

    protected Model m( String facts )
        {
        Model result = ModelFactory.createDefaultModel();
        result
            .setNsPrefixes( PrefixMapping.Extended )
            .setNsPrefix( "eye", EYE.uri )
            ;
        modelAdd( result, facts );
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
