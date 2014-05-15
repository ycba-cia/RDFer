/*
 	(c) Copyright 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 	All rights reserved.
 	$Id: TestFileLoader.java,v 1.3 2010/03/26 14:50:33 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.loaders.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.hp.hpl.jena.assembler.exceptions.NoSpecificTypeException;
import com.hp.hpl.jena.eyeball.loaders.FileLoader;
import com.hp.hpl.jena.eyeball.util.test.TestFileInsertion;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.test.ModelTestBase;
import com.hp.hpl.jena.shared.NotFoundException;
import com.hp.hpl.jena.util.FileManager;

@RunWith(JUnit4.class) public class TestFileLoader extends ModelTestBase
    {
    public TestFileLoader()
        { super( "TestFileLoader" ); }
    
    @Test public void testFileLoaderConstruction()
        {
        FileLoader fl = new FileLoader();
        assertSame( FileManager.get(), fl.getFileManager() );
        }
    
    @Test public void testFileLoaderConstructionWithFileManager()
        {
        Model m = modelWithStatements( "" );
        TestFileInsertion.FakeFileManager fm = new TestFileInsertion.FakeFileManager( "A", m );
        FileLoader fl = new FileLoader( fm );
        assertSame( fm, fl.getFileManager() );
        assertSame( m, fl.load( "A" ) );
        }
    
    @Test public void testFileLoaderRespectsJA()
        {
        Model af = modelWithStatements( "my:root rdf:type ja:MemoryModel; my:root ja:quotedContent a; a P b" );
        TestFileInsertion.FakeFileManager fm = new TestFileInsertion.FakeFileManager( "af.ttl", af );
        FileLoader fl = new FileLoader( fm );
        Model actual = fl.load( "ja:my:root@af.ttl" );
        assertNotNull( "must find the specified model", actual );
        assertIsoModels( modelWithStatements( "a P b" ), actual );
        }
    
    @Test public void testMissingJAFileThrowsException()
        {
        Model af = modelWithStatements( "" );
        TestFileInsertion.FakeFileManager fm = new TestFileInsertion.FakeFileManager( "af.ttl", af );
        FileLoader fl = new FileLoader( fm );
        try 
            { 
            fl.load( "ja:my:root@doesNotExist" );
            fail( "should throw NotFoundException" );
            }
        catch (NotFoundException e)
            {
            assertEquals( "doesNotExist", e.getMessage() );
            }
        }
    
    @Test public void testBadRootThrowsException()
        {
        Model af = modelWithStatements( "my:root rdf:type ja:MemoryModel" );
        TestFileInsertion.FakeFileManager fm = new TestFileInsertion.FakeFileManager( "af.ttl", af );
        FileLoader fl = new FileLoader( fm );
        try 
            { 
            fl.load( "ja:no:root@af.ttl" );
            fail( "should throw NotFoundException" );
            }
        catch (NoSpecificTypeException e)
            {
            assertTrue( e.getMessage().contains( "no:root" ) );
            }
        }
    }


/*
 * (c) Copyright 2005, 2006, 2007 Hewlett-Packard Development Company, LP
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