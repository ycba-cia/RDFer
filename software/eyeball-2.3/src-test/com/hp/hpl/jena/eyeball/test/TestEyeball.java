/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 	All rights reserved - see end of file.
 	$Id: TestEyeball.java,v 1.5 2010/03/26 14:50:31 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.test;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.assemblers.test.MockRendererA;
import com.hp.hpl.jena.eyeball.test.EyeballTestBase;

@RunWith(JUnit4.class) public class TestEyeball extends EyeballTestBase
    {
    public TestEyeball()
        {}
    
    public static class LoggingInspector implements Inspector
        {
        public List<String> history = new ArrayList<String>();
        public OntModel model = null;
        public OntModel assume = null;
        public Set<Statement> statements = new HashSet<Statement>();
        public Report report = null;
        
        public void begin( Report r, OntModel assume )
            { history.add( "begin" ); this.report = r; this.assume = assume; }

        public void inspectModel( Report r, OntModel m )
            { assertSame( report, r ); history.add( "im" ); model = m; }

        public void inspectStatement( Report r, Statement s )
            { assertSame( report, r ); history.add( "is" ); statements.add( s ); }

        public void end( Report r )
            { assertSame( report, r ); history.add( "end" ); }
        }

    @Test public void testEmptyModelFollowsProtocolAndProducesNoReports() 
        { 
        OntModel assume = ModelFactory.createOntologyModel();
        LoggingInspector morse = new LoggingInspector();
        Renderer rend = new MockRendererA( null );
        Eyeball e = new Eyeball( morse, assume, rend ); 
        OntModel m = ModelFactory.createOntologyModel();
        Report r = new Report();
        assertSame( r, e.inspect( r, m ) );
        assertSame( assume, morse.assume );
        assertEquals( Collections.EMPTY_SET, morse.statements );
        assertIsoModels( model( "" ), r.model() );
        assertEquals( listOfStrings( "begin im end" ), morse.history );
        }    

    @Test public void testNonEmptyModelScansStatementsAndProducedNoReports() 
        { 
        OntModel assume = ModelFactory.createOntologyModel();
        LoggingInspector morse = new LoggingInspector();
        Renderer rend = new MockRendererA( null );
        Eyeball e = new Eyeball( morse, assume, rend );
        String modelString = "a P b; c Q d";
        Model data = modelWithStatements( modelString );
        OntModel m = ModelFactory.createOntologyModel( OntModelSpec.RDFS_MEM, data );
        Report r = new Report();
        assertSame( r, e.inspect( r, m ) );
        assertSame( assume, morse.assume );
        assertIsoModels( model( "" ), r.model() );
        assertEquals( listOfStrings( "begin im is is end" ), morse.history );
        Set<Statement> expected = new HashSet<Statement>( Arrays.asList( statements( m, modelString ) ) );
        assertEquals( expected, morse.statements );
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