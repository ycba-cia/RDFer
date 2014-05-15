/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 	All rights reserved - see end of file.
 	$Id: TestEyeballAssembler.java,v 1.4 2010/03/26 14:50:32 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.assemblers.test;

import java.util.*;

import org.junit.Test;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.hp.hpl.jena.assembler.*;
import com.hp.hpl.jena.assembler.assemblers.AssemblerBase;
import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.assemblers.EyeballAssembler;
import com.hp.hpl.jena.eyeball.inspectors.*;
import com.hp.hpl.jena.eyeball.test.EyeballTestBase;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.util.FileManager;

@RunWith(JUnit4.class) public class TestEyeballAssembler extends EyeballTestBase
    {
    public TestEyeballAssembler()
        {}

    @Test public void testMinimalEyeball()
        {
        Resource r = resourceInModel( "x rdf:type eye:Eyeball" );
        Eyeball e = (Eyeball) new EyeballAssembler().open( null, r );
        assertInstanceOf( Eyeball.class, e );
        }
    
    @Test public void testEyeballWithInspector()
        {
        Resource r = resourceInModel( "x rd:type eye:Eyeball & eye:inspector _x" );
        Inspector i = new NullInspector();
        Assembler track = new NamedObjectAssembler( resource( "_x" ), i );
        Eyeball e = (Eyeball) new EyeballAssembler().open( track, r );
        assertSame( i, e.getInspector() );
        }
    
    static class NamedObjectsAssembler extends AssemblerBase
        {
        protected Map<Resource, Object> map = new HashMap<Resource, Object>();
        
        public NamedObjectsAssembler( Resource root, Object o )
            { map.put(  root, o ); }
        
        public NamedObjectsAssembler add( Resource root, Object o )
            {
            map.put(  root, o );
            return this;
            }
        
        public Object open( Assembler sub, Resource root, Mode mode )
            {
            Object result = map.get(  root  );
            if (result == null) throw new JenaException( "missing root " + root );
            return result;
            }
        }
    
    static class LoggingInspector extends InspectorBase implements Inspector
        {
        public List<String> history = new ArrayList<String>();
        
        @Test public void begin( Report r, OntModel assume )
            { history.add( "begin" ); }    
        }
    
    @Test public void testEyeballWithMultipleInspectors()
        {
        Resource r = resourceInModel( "x rd:type eye:Eyeball & eye:inspector _x & eye:inspector _y" );
        LoggingInspector i = new LoggingInspector(), j = new LoggingInspector();
        Assembler track = new NamedObjectsAssembler( resource( "_x" ), i ).add( resource( "_y" ), j );
        Eyeball e = (Eyeball) new EyeballAssembler().open( track, r );
        e.getInspector().begin(  new Report(), ModelFactory.createOntologyModel() );
        assertEquals( listOfStrings( "begin" ), i.history );
        assertEquals( listOfStrings( "begin" ), j.history );
        }
    
    @Test public void testEyeballWithRenderer()
        {
        Resource r = resourceInModel( "x rd:type eye:Eyeball & eye:renderer _r" );
        Renderer rend = new MockRendererA( r );
        Assembler track = new NamedObjectAssembler( resource( "_r" ), rend );
        Eyeball e = (Eyeball) new EyeballAssembler().open( track, r );
        assertSame( rend, e.getRenderer() );
        }
    
    @Test public void testEyeballWithAssumptions()
        {
        Resource r = resourceInModel( "x rdf:type eye:Eyeball & eye:assume _x & eye:assume _y" );
        Model modelX = model( "a P b" ), modelY = model( "c Q d" );
        Assembler track = new NamedObjectsAssembler( resource( "_x" ), modelX ).add( resource( "_y" ), modelY );
        Eyeball e = (Eyeball) new EyeballAssembler().open(  track, r  );
        assertIsoModels( model( "a P b; c Q d" ), e.getAssumptions().getBaseModel() );
        }
    
    @Test public void testEyeballNamedAssumptions()
        {
        final Model expected = model( "sing something simple" );
        Resource r = resourceInModel( "x rdf:type eye:Eyeball & eye:assume 'modelName'" );
        FileManager fm = new FileManager()
            {
            public Model loadModel( String name )
                {
                assertEquals( "modelName", name );
                return expected; 
                }
            };
        Eyeball e = (Eyeball) new EyeballAssembler( fm ).open( null, r  );
        assertIsoModels( expected, e.getAssumptions().getBaseModel() );
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