/*
 	(c) Copyright 2006 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 	All rights reserved - see end of file.
 	$Id: TestParseResouces.java,v 1.2 2010/03/26 14:50:31 chris-dollin Exp $
*/

package com.hp.hpl.jena.extras.tools.notations.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.hp.hpl.jena.extras.tools.notations.likeN3.Parser;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.test.ModelTestBase;

import static com.hp.hpl.jena.rdf.model.test.ModelTestBase.assertIsoModels;
import static com.hp.hpl.jena.rdf.model.test.ModelTestBase.resource;
import static com.hp.hpl.jena.rdf.model.test.ModelTestBase.property;
import static com.hp.hpl.jena.rdf.model.test.ModelTestBase.modelWithStatements;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class) public class TestParseResouces
    {
    private static Model empty = ModelFactory.createDefaultModel();
    
    @Test public void testEmptyBlankNode()
        {
        Model m = ModelFactory.createDefaultModel();
        Resource r = new Parser( "[]" ).resourceIn( m );
        assertIsoModels( empty, m );
        assertTrue( r.isAnon() );
        }
    
    @Test public void testResourceURI()
        {
        Model m = ModelFactory.createDefaultModel();
        Resource r = new Parser( "noProperties" ).resourceIn( m );
        assertIsoModels( empty, m );
        assertEquals( ModelTestBase.resource( "noProperties" ), r );
        }
    
    @Test public void testWithProperties()
        {
        Resource r = parseAsResource( "x P y" );
        assertIsoModels( modelWithStatements( "x P y" ), r.getModel() );
        assertEquals( resource( "x" ), r );
        }
    
    @Test public void testDeliversSyntacticallyFirstResource()
        {
        Resource r = parseAsResource( "[P [Q y]]" );
        assertIsoModels( modelWithStatements( "_a P _b; _b Q y"), r.getModel() );
        assertTrue( r.hasProperty( property( "P" ) ) );
        }
    
    private Resource parseAsResource( String modelString )
        {
        Model m = ModelFactory.createDefaultModel();
        return new Parser( modelString ).resourceIn( m );
        }
    }


/*
 * (c) Copyright 2006 Hewlett-Packard Development Company, LP
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