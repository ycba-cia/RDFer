/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 	All rights reserved - see end of file.
 	$Id: TestRendererAssembler.java,v 1.4 2010/03/26 14:50:32 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.assemblers.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.hp.hpl.jena.assembler.exceptions.NotUniqueException;
import com.hp.hpl.jena.eyeball.Renderer;
import com.hp.hpl.jena.eyeball.assemblers.RendererAssembler;
import com.hp.hpl.jena.eyeball.test.*;
import com.hp.hpl.jena.rdf.model.*;

@RunWith(JUnit4.class) public class TestRendererAssembler extends EyeballTestBase
    {
    public TestRendererAssembler()
        {}

    @Test public void testUsesClassPropertyAToDefineRendererClass()
        { testUsesClassPropertyToDefineRendererClass( MockRendererA.class ); }
    
    @Test public void testUsesClassPropertyBToDefineRendererClass()
        { testUsesClassPropertyToDefineRendererClass( MockRendererB.class ); }

    private void testUsesClassPropertyToDefineRendererClass( Class<? extends Renderer> rendClass )
        {
        Resource r = resourceInModel( "x rdf:type eye:Renderer; x eye:className '" + rendClass.getName() + "'" );
        Renderer rend = (Renderer) new RendererAssembler().open( r );
        assertInstanceOf( rendClass, rend );
        }
    
    @Test public void testChecksJustOneClassProperty()
        {
        Resource r = resourceInModel( "x rdf:type eye:Renderer; x eye:className 'A'; x eye:className 'B'" );
        try { new RendererAssembler().open( r ); fail( "should catch multiple classes" ); }
        catch (NotUniqueException e) { pass(); }
        }
    
    @Test public void testPassesInRootResource()
        {
        Resource r = resourceInModel( "x rdf:type eye:Renderer; x eye:className '" + MockRendererA.class.getName() + "'" );
        Renderer rend = (Renderer) new RendererAssembler().open( r );
        MockRendererA a = (MockRendererA) rend;
        assertSame( r, a.rootResource );
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