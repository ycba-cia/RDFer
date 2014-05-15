/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 	All rights reserved.
 	$Id: TestTextRendering.java,v 1.3 2010/03/26 14:50:32 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.renderers.test;

import java.io.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.renderers.*;
import com.hp.hpl.jena.eyeball.test.EyeballTestBase;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;

@RunWith(JUnit4.class) public class TestTextRendering extends EyeballTestBase
    {
    public TestTextRendering()
        {}
    
    @Test public void testDeliversDefaultItemRenderer()
        { 
        Resource config = resourceInModel( "root p A" );
        PredicateRegister register = new SimplePredicateRegister();
        ExperimentalTextRenderer et = new ExperimentalTextRenderer( config, register );
        assertInstanceOf( ItemRenderer.class, et.rendererFor( resource( "eh:spoo" )  )  );
        assertInstanceOf( FormattedItemRenderer.class, et.rendererFor( resource( "eh:spoo" )  )  );
        }
    
    @Test public void testDefaultRendererSPOO()
        {
        Resource config = resourceInModel( "_config property value" );
        Resource spoo = resourceInModel( "_bnode property value" );
        PredicateRegister register = new SimplePredicateRegister();
        register.register( property( "property" ) );
        ExperimentalTextRenderer et = new ExperimentalTextRenderer( spoo, register );
        ItemRenderer ir = new FormattedItemRenderer( config, et, register );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ir.render( new PrintStream( baos ), spoo );
        String resultText = baos.toString();
        assertTrue( resultText.indexOf( "property" ) > 0 );
        }
    
    @Test public void testTextRendererConfiguresVocabulary()
        {
        Resource root = resourceInModel( "root eye:labels 'spoo'" );
        FileManager fm = new FileManager()
            {
            public Model loadModel( String name )
                {
                assertEquals( "spoo", name );
                return model( "example rdfs:label 'label-for-example'" );
                }
            };
        TextRenderer tr = new TextRenderer( root, fm );
        assertEquals( "eh:/notInVocabulary", tr.labelFor( property( "notInVocabulary" ) ) );
        assertEquals( "label-for-example", tr.labelFor( property( "example" ) ) );
        }
    }

/*
    (c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions
    are met:

    1. Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.

    2. Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.

    3. The name of the author may not be used to endorse or promote products
       derived from this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
    IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
    OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
    IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
    INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
    NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
    DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
    THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
    THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
