/*
 	(c) Copyright 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 	All rights reserved.
 	$Id: TestSparqlDrivenInspector.java,v 1.6 2010/03/26 14:50:31 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.inspectors.test;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.inspectors.SparqlDrivenInspector;
import com.hp.hpl.jena.eyeball.util.FileInsertion;
import com.hp.hpl.jena.eyeball.util.test.TestFileInsertion;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;

@RunWith(JUnit4.class) public class TestSparqlDrivenInspector extends InspectorTestBase
    {
    public TestSparqlDrivenInspector()
        {}

    protected Class<? extends Inspector> getInspectorClass()
        { return SparqlDrivenInspector.class; }
    
    @Test public void testPrefixInspectorDeclaresPrefixes()
        {
        Report r = new Report();
        Resource root = resourceInModel( "root rdf:type some" );
        SparqlDrivenInspector s = new SparqlDrivenInspector( root );
        s.begin( r, ontModel() );
        List<Property> predicates = r.getPredicateRegister().getRegisteredPredicates();
        assertEquals( eyeResourceSet( "sparqlRequireFailed sparqlProhibitFailed" ), new HashSet<Property>( predicates ) );
        }
    
    @Test public void testReportsSPOViolation()
        {
        Report r = new Report();
        String sparql = "select * where {?s ?p ?o}";
        Resource root = resourceInModel( "root eye:require '<S>'".replaceAll( "<S>", sparql ) );
        SparqlDrivenInspector s = new SparqlDrivenInspector( root );
        s.inspectModel( r, ontModel( "" ) );
        String expect =
            "[eye:mainProperty eye:sparqlRequireFailed & eye:sparqlRequireFailed '<S>']"
            .replaceAll(  "<S>", sparql )
            ;
        assertIsoModels( itemModel( expect ), r.model() );
        }
    
    @Test public void testReportsSPOViolationWithMessage()
        {
        Report r = new Report();
        String sparql = "select * where {?s ?p ?o}";
        Resource root = resourceInModel( "root eye:sparql [eye:require '<S>' & eye:message 'WHOOPS']".replaceAll( "<S>", sparql ) );
        SparqlDrivenInspector s = new SparqlDrivenInspector( root );
        s.inspectModel( r, ontModel( "" ) );
        String expect = "[eye:mainProperty eye:sparqlRequireFailed & eye:sparqlRequireFailed 'WHOOPS']" ;
        assertIsoModels( itemModel( expect ), r.model() );
        }
    
    @Test public void testAcceptsSPOPresence()
        {
        Report r = new Report();
        String sparql = "select * where {?s ?p ?o}";
        Resource root = resourceInModel( "root eye:require '<S>'".replaceAll( "<S>", sparql ) );
        SparqlDrivenInspector s = new SparqlDrivenInspector( root );
        s.inspectModel( r, ontModel( "my count 17" ) );
        assertIsoModels( itemModel( "" ), r.model() );
        }
    
    @Test public void testRejectsSPOPresence()
        {
        Report r = new Report();
        String sparql = "select * where {?s ?p ?o}";
        Resource root = resourceInModel( "root eye:prohibit '<S>'".replaceAll( "<S>", sparql ) );
        SparqlDrivenInspector s = new SparqlDrivenInspector( root );
        s.inspectModel( r, ontModel( "my count 17" ) );
        String expect =
            "[eye:mainProperty eye:sparqlProhibitFailed & eye:sparqlProhibitFailed '<S>']"
            .replaceAll(  "<S>", sparql )
            ;
        assertIsoModels( "should report prohibition violation", itemModel( expect ), r.model() );
        }
    
    @Test public void testRejectsSPOPresenceWithMessage()
        {
        Report r = new Report();
        String sparql = "select * where {?s ?p ?o}";
        Resource root = resourceInModel( "root eye:sparql [eye:prohibit '<S>' & eye:message 'SPOO']".replaceAll( "<S>", sparql ) );
        SparqlDrivenInspector s = new SparqlDrivenInspector( root );
        s.inspectModel( r, ontModel( "my count 17" ) );
        String expect =
            "[eye:mainProperty eye:sparqlProhibitFailed & eye:sparqlProhibitFailed 'SPOO']"
            .replaceAll(  "<S>", sparql )
            ;
        assertIsoModels( "should report prohibition violation", itemModel( expect ), r.model() );
        }
    
    @Test public void testUsesFileInsertion()
        {
        Report r = new Report();
        String sparql = "select * where {?s ?p ?o}";
        Resource root = resourceInModel( "root eye:prohibit '@\"NAME\"'" );
        SparqlDrivenInspector s = new InsertingInspector( root, "NAME", sparql );
        s.inspectModel( r, ontModel( "my count 17" ) );
        String expect =
            "[eye:mainProperty eye:sparqlProhibitFailed & eye:sparqlProhibitFailed '@\"NAME\"']";
        assertIsoModels( "should report prohibition violation", itemModel( expect ), r.model() );        
        }
    
    protected static final class InsertingInspector extends SparqlDrivenInspector
        {
        protected final FileManager fm;
        
        private InsertingInspector( Resource root, String name, String query )
            { 
            super( root ); 
            fm = new TestFileInsertion.FakeFileManager( name, query );
            }
        
        protected FileInsertion getFileInsertion()
            { return new FileInsertion( fm ); }
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