/*
 	(c) Copyright 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 	All rights reserved.
 	$Id: TestFileInsertion.java,v 1.5 2010/03/26 14:50:32 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.util.test;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.hp.hpl.jena.eyeball.util.FileInsertion;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.test.ModelTestBase;
import com.hp.hpl.jena.util.FileManager;

@RunWith(JUnit4.class) public class TestFileInsertion extends ModelTestBase
    {
    public TestFileInsertion()
        { super( "TestFileInsertion" ); }

    @Test public void testTokenisation()
        {
        FileInsertion f = new FileInsertion();
        assertEquals( listOfStrings( "" ), f.tokenise( "" ) );
        assertEquals( listOfStrings( "spoo" ), f.tokenise( "spoo" ) );
        assertEquals( listOfStrings( "abc|@def| ghi", "|" ), f.tokenise( "abc@def ghi" ) );
        assertEquals( listOfStrings( "abc @@ def"), f.tokenise( "abc@@def" ) );
        assertEquals( listOfStrings( "@'/home/kers/included' stuff" ), f.tokenise( "@'/home/kers/included'stuff" ) );
        }
    
    @Test public void testWithoutInsertion()
        { 
        FileInsertion f = new FileInsertion();
        testWithoutInsertion( f, "" );
        testWithoutInsertion( f, "spoo" );
        testWithoutInsertion( f, "!boot" );
        testWithoutInsertion( f, "'chat'@en-uk" );
        }
    
    @Test public void testEscapeDuplicate()
        {
        FileInsertion f = new FileInsertion();
        assertEquals( "abc@def", f.insert( "abc@@def" ) );
        }
    
    @Test public void testSingleFilenameWithInsertion()
        {
        FileManager fm = new FakeFileManager( "spoo", "XXX" );
        FileInsertion f = new FileInsertion( fm );
        assertEquals( "XXX", f.insert( "@'spoo'" ) );
        assertEquals( "abcXXXdefXXXghi", f.insert( "abc@'spoo'def@'spoo'ghi" ) );
        assertEquals( "abcXXXdefXXXghi", f.insert( "abc@\"spoo\"def@'spoo'ghi" ) );
        assertEquals( "abcXXXdefXXXghi", f.insert( "abc@'spoo'def@\"spoo\"ghi" ) );
        }
    
    @Test public void testMultipleFilenamesWithInsertion()
        {
        FileManager fm = new FakeFileManager( "d", "Dirac" ).set( "q", "quincunx" );
        FileInsertion f = new FileInsertion( fm );
        assertEquals( "abc|Dirac|def|quincunx|ghi", f.insert( "abc|@'d'|def|@'q'|ghi" ) );
        }

    private void testWithoutInsertion( FileInsertion f, String template )
        { assertEquals( template, f.insert( template ) ); }
    
    public static List<String> listOfStrings( String s, String separator )
        {
        List<String> result = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer( s, separator );
        while (st.hasMoreTokens()) result.add( st.nextToken() );
        return result;
        }
    
    public static final class FakeFileManager extends FileManager
        {
        protected final Map<String, String> stringMap = new HashMap<String, String>();
        protected final Map<String, Model> modelMap = new HashMap<String, Model>();
        
        public FakeFileManager( String name, String contents )
            { set( name, contents ); }
        
        public FakeFileManager( String name, Model contents )
            { modelMap.put( name, contents ); }
        
        public FakeFileManager set( String name, String contents )
            { stringMap.put( name, contents ); return this;  }

        public Model loadModel( String name )
            { return modelMap.get( name ); }
        
        public String readWholeFileAsUTF8( String name )
            {
            assertTrue( "FakeFileManager must contain key " + name, stringMap.containsKey( name ) );
            return stringMap.get( name );
            }
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