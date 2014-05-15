/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 	All rights reserved - see end of file.
 	$Id: TestURIInspector.java,v 1.8 2010/03/26 14:50:31 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.inspectors.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.inspectors.*;
import com.hp.hpl.jena.rdf.model.*;

@RunWith(JUnit4.class) public class TestURIInspector extends InspectorTestBase
    {
    public TestURIInspector()
        {}

    protected Class<? extends Inspector> getInspectorClass()
        { return URIInspector.class; }

    protected URIInspector ins = new URIInspector();
    
    protected Report r = new Report();

    @Test public void testURIInspectorDeclaresPredicates()
        {
        Report r = new Report();
        ins.begin( r, ontModel() );
        List<Property> predicates = r.getPredicateRegister().getRegisteredPredicates();
        assertEquals( eyeResourceSet( "badURI forReason" ), new HashSet<Property>( predicates ) );
        assertOrder( "badURI forReason", predicates );
        }
    
    @Test public void testStatementInspectorCallsURIInspector()
    	{
        final List<String> uris = new ArrayList<String>();
    	URIInspector i = new URIInspector() 
    		{
    		public void inspectURI( Report r, Statement s, String URI ) { uris.add( URI ); }
    		};
    	i.inspectStatement( r, statement( "S P O" ) );
        assertEquals( listOfStrings( "eh:/S eh:/P eh:/O" ), uris );
    	}
    
    @Test public void testRandomBadURIFails()
        {
        ins.inspectURI( r, S, "http://broken:80" );
        assertFalse( "should spot bad URI",  r.valid() );
        }
    
    protected final Statement S = statement( "subject Predicate object" );
    
    @Test public void testCatchesSpaces()
        {
        String URI = "http://domain.com/with space";
        ins.inspectURI( r, S, URI );
        Model wanted = itemModel( "[eye:mainProperty eye:badURI & eye:badURI '<a>' & eye:forReason eye:uriContainsSpaces]".replaceAll( "<a>", URI.replaceAll( " ", "\\\\\\\\s" ) ), S );
        assertIsoModels( wanted, r.model() );
        }
    
    @Test public void testCatchesNonLowercaseSchema()
        {
        String URI = "hTTp://domain.com/directory";
        ins.inspectURI( r, S, URI );
        Model wanted = itemModel( "[eye:mainProperty eye:badURI & eye:badURI '<a>' & eye:forReason eye:schemeShouldBeLowercase]".replaceAll( "<a>", URI ), S );
        assertIsoModels( wanted, r.model() );
        }
    
    @Test public void testCatchesMissingSchema()
        {
        String URI = "domain.com/directory";
        ins.inspectURI( r, S, URI );
        Model wanted = itemModel( "[eye:mainProperty eye:badURI & eye:badURI '<a>' & eye:forReason eye:uriHasNoScheme]".replaceAll( "<a>", URI ), S );
        assertIsoModels( wanted, r.model() );
        }
    
    @Test public void testCatchesMissingHttpAuthority()
        {
        String URI = "http:/whoops";
        ins.inspectURI( r, S, URI );
        Model wanted = itemModel( "[eye:mainProperty eye:badURI & eye:badURI '<a>' & eye:forReason eye:uriNoHttpAuthority]".replaceAll( "<a>", URI ), S );
        assertIsoModels( wanted, r.model() );
        }
    
    @Test public void testCatchesUnknownScheme()
        {
        String URI = "thereisnosuchscheme:/whoops";
        ins.inspectURI( r, S, URI );
        Model wanted = itemModel( "[eye:mainProperty eye:badURI & eye:badURI '<a>' & eye:forReason eye:unrecognisedScheme]".replaceAll( "<a>", URI ), S );
        assertIsoModels( wanted, r.model() );
        }
    
    @Test public void testDoesRepeatedURIJustOnce()
        {
        ins.inspectStatement( r, statement( "bad:A bad:A bad:A" ) );
        assertEquals( 1, r.model().listSubjects().toList().size() );
        }
    
    @Test public void testDefaultDoesNotReportURIsWithEmptyLocalName()
        {
        ins.inspectURI( r, S, "http://domain.com/" );
        Model wanted = itemModel( "[eye:mainProperty eye:badURI & eye:badURI '<a>' & eye:forReason eye:uriHasNoLocalname]".replaceAll( "<a>", "http://domain.com/" ), S );
        assertIsoModels( model(), r.model() );
        }
    
    @Test public void testReportsURIsWithEmptyLocalName()
        {
        Resource root = resourceInModel( "_ins eye:reportEmptyLocalNames 'true'" );
        URIInspector ins = new URIInspector( root );
        ins.inspectURI( r, S, "http://domain.com/" );
        Model wanted = itemModel( "[eye:mainProperty eye:badURI & eye:badURI '<a>' & eye:forReason eye:uriHasNoLocalname]".replaceAll( "<a>", "http://domain.com/" ), S );
        assertIsoModels( wanted, r.model() );
        }
    
    @Test public void testSingleProhibitedPattern()
        {
        String badURI = "urn:xxx:YYY", start = "urn:xxx:", prohibited = "YYY";
        Resource root = resourceInModel( configureProhibitOne( start, prohibited ) );
        URIInspector ins = new URIInspector( root );
        ins.inspectURI( r, S, badURI );
        String expected = 
            "[eye:mainProperty eye:badURI & eye:badURI '<U>' & eye:forReason '<R>']"
            .replace( "<U>", badURI )
            .replace( "<R>", failedProhibition( start, prohibited ) )
            ;
        assertIsoModels( itemModel( expected, S ), r.model() );
        }
    
    @Test public void testMultipleProhibitedPatterns()
        {
        String badURI = "urn:xxx:xAAyBBz";
        String start = "urn:xxx:";
        String prohibitAA = ".*AA.*", prohibitBB = ".*BB.*";
        String modelString = configureProhibitTwo( start, prohibitAA, prohibitBB );
        Resource root = resourceInModel( modelString );
        URIInspector ins = new URIInspector( root );
        ins.inspectURI( r, S, badURI );
        String expected = 
            "[eye:mainProperty eye:badURI & eye:badURI '<U>' & eye:forReason '<R1>' & eye:forReason '<R2>']"
            .replace( "<U>", badURI )
            .replace( "<R1>", failedProhibition( start, prohibitAA ) )
            .replace( "<R2>", failedProhibition( start, prohibitBB ) )
            ;
        assertIsoModels( itemModel( expected, S ), r.model() );
        }
    
    @Test public void testSingleRequiredPattern()
        {
        String badURI = "urn:xxx:ZZZ";
        String start = "urn:xxx:";
        String required = "YYY";
        String modelString = 
            "eye:ins eye:check [eye:prefix '<S>' & eye:require '<P>']"
            .replace( "<S>", start )
            .replace( "<P>", required )
            ;
        Resource root = resourceInModel( modelString );
        URIInspector ins = new URIInspector( root );
        ins.inspectURI( r, S, badURI );
        String contents = 
            "[eye:mainProperty eye:badURI & eye:badURI '<U>' & eye:forReason '<R>']"
            .replace( "<U>", badURI )
            .replace( "<R>", failedRequire( start, required ) )
            ;
        assertIsoModels( itemModel( contents, S ), r.model() );
        }
    
    @Test public void testMultipleRequiredPatterns()
        {
        String badURI = "urn:xxx:ZZZ";
        String start = "urn:xxx:";
        String requireYYY = "YYY", requireHHH = "HHH";
        String modelString = 
            "eye:ins eye:check [eye:prefix '<S>' & eye:require '<P1>' & eye:require '<P2>']"
            .replace( "<S>", start )
            .replace( "<P1>", requireYYY )
            .replace( "<P2>", requireHHH )
            ;
        Resource root = resourceInModel( modelString );
        URIInspector ins = new URIInspector( root );
        ins.inspectURI( r, S, badURI );
        String contents = 
            "[eye:mainProperty eye:badURI & eye:badURI '<U>' & eye:forReason '<R1>' & eye:forReason '<R2>']"
            .replace( "<U>", badURI )
            .replace( "<R1>", failedRequire( start, requireYYY ) )
            .replace( "<R2>", failedRequire( start, requireHHH ) )
            ;
        assertIsoModels( itemModel( contents, S ), r.model() );
        }

    private String configureProhibitOne( String start, String prohibited )
        {
        return "eye:ins eye:check [eye:prefix '<S>' & eye:prohibit '<P>']"
            .replace( "<S>", start )
            .replace( "<P>", prohibited )
            ;
        }

    private String configureProhibitTwo( String start, String prohibitA, String prohibitB )
        {
        return "eye:ins eye:check [eye:prefix '<S>' & eye:prohibit '<P1>' & eye:prohibit '<P2>']"
            .replace( "<S>", start )
            .replace( "<P1>", prohibitA )
            .replace(  "<P2>", prohibitB )
            ;
        }

    private String failedRequire( String start, String required )
        {
        return "did not match required pattern: <P> for prefix <S>"
            .replace( "<S>", start )
            .replace( "<P>", required )
            ;
        }

    private String failedProhibition( String start, String prohibited )
        {
        return "matched prohibited pattern: <P> for prefix <S>"
            .replace( "<S>", start )
            .replace( "<P>", prohibited )
            ;
        }
    
    @Test public void testSatisfiesOneOfMultipleRequiredPatterns()
        {
        String goodURI = "urn:xxx:ZZZ";
        String modelString = 
            "eye:ins eye:check [eye:prefix 'urn:xxx:' & eye:require 'YYY' & eye:require 'ZZZ']";
        URIInspector ins = new URIInspector( resourceInModel( modelString ) );
        ins.inspectURI( r, S, goodURI );
        assertIsoModels( model(), r.model() );
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