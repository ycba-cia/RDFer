/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 	All rights reserved - see end of file.
 	$Id: TestLoader.java,v 1.4 2010/03/26 14:50:31 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.test;

import java.util.*;
import java.util.regex.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.hp.hpl.jena.eyeball.loaders.*;
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.test.ModelTestBase;
import com.hp.hpl.jena.shared.NotFoundException;

@RunWith(JUnit4.class) public class TestLoader extends ModelTestBase
    {
    public TestLoader()
        { super( "TestLoader"); }

    static class FakeLoader implements Loader
        {
        public Map<String, Model> loaded = new HashMap<String, Model>();
        
        public Model load( String s )
            {
            Model result = ModelFactory.createDefaultModel();
            loaded.put( s, result );
            return result;
            }
        }
    
    @Test public void testPassThrough()
        {
        FakeLoader base = new FakeLoader();
        OntModel om = new OntLoader( base ).ontLoad( "whatever" );
        assertSame( base.loaded.get( "whatever" ).getGraph(), om.getBaseModel().getGraph() );
        }
    
    @Test public void testSpotsOnt()
        {
        FakeLoader base = new FakeLoader();
        OntModel om = new OntLoader( base ).ontLoad( "ont:OWL_DL_MEM:whatever" );
        assertSame( base.loaded.get( "whatever" ).getGraph(), om.getBaseModel().getGraph() );
        assertSame( OntModelSpec.OWL_DL_MEM, om.getSpecification() );
        }
    
    @Test public void testSpotsBadOntModelSpec()
        {
        FakeLoader base = new FakeLoader();
        try { new OntLoader( base ).ontLoad( "ont:NoSuchOntModelSpec:whatever" ); fail( "BOOM" ); }
        catch (NotFoundException e) { assertEquals( "NoSuchOntModelSpec", e.getMessage() ); }
        }
    
    static class FakeDBLoader implements Loader
        {
        String jdbc;
        String modelName;
        Model model;

        public Model  load( String s )
            {
            Pattern p = Pattern.compile( "(jdbc:[^:]+:[^:]+):(.*)" );
            Matcher m = p.matcher( s );
            m.find();
            jdbc = m.group(1);
            modelName = m.group(2);
            return model = ModelFactory.createDefaultModel();
            }
        }
    
    @Test public void testSpotsJDBC()
        {
        FakeLoader base = new FakeLoader();
        FakeDBLoader db = new FakeDBLoader();
        Loader L = new JDBCSensitiveLoader( db, base );
        Model m = L.load( "jdbc:thing:whassit:modelName" );
        assertTrue( base.loaded.isEmpty() );
        assertEquals( "jdbc:thing:whassit", db.jdbc );
        assertEquals( "modelName", db.modelName );
        assertSame( db.model, m );
        }
    
    @Test public void testWithoutJDBC()
        {
        FakeLoader base = new FakeLoader();
        FakeDBLoader db = new FakeDBLoader();
        Loader L = new JDBCSensitiveLoader( db, base );
        Model m = L.load( "plain:thing:whassit:modelName" );
        assertSame( base.loaded.get( "plain:thing:whassit:modelName" ), m );
        assertEquals( null, db.jdbc );
        assertEquals( null, db.modelName );
        assertSame( null, db.model );
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