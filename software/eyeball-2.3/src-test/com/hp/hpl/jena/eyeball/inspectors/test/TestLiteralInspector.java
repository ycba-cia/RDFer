/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 	All rights reserved - see end of file.
 	$Id: TestLiteralInspector.java,v 1.4 2010/03/26 14:50:31 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.inspectors.test;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.inspectors.LiteralInspector;
import com.hp.hpl.jena.rdf.model.*;

@RunWith(JUnit4.class) public class TestLiteralInspector extends InspectorTestBase
    {
    public TestLiteralInspector()
        {}

    protected Class<? extends Inspector> getInspectorClass()
        { return LiteralInspector.class; }

    final LiteralInspector ins = new LiteralInspector();
    
    final Report r = new Report();
    
    @Test public void testLiteralInspectorInspectsLiteralsWithInspectLiteral()
        {
        Literal L17 = literal( "17" );
        final List<Literal> literals = new ArrayList<Literal>();
        LiteralInspector ins = new LiteralInspector()
            {
            public void inspectLiteral( Report r, Statement s, Literal L ) { literals.add( L ); }
            };
        ins.inspectStatement( new Report(), statement( "s p 17" ) );
        assertEquals( listOfOne( L17 ), literals );
        }
    
    @Test public void testLiteralInspectorDeclaresPredicates()
        {
        ins.begin( r, ontModel() );
        List<Property> predicates = r.getPredicateRegister().getRegisteredPredicates();
        assertEquals( eyeResourceSet( "badLanguage onLiteral badLexicalForm badDatatypeURI forDatatype" ), new HashSet<Resource>( predicates ) );
        assertOrder( "badLanguage onLiteral", predicates );
        assertOrder( "badLexicalForm forDatatype", predicates );
        assertOrder( "badDatatypeURI onLiteral", predicates );
        }
    
    @Test public void testAllowsPlainStringLiterals()
        {
        ins.inspectLiteral( r, S, literal( "'hello'" ) );
        ins.inspectLiteral( r, S, literal( "'chat'" ) );
        ins.inspectLiteral( r, S, literal( "'hello\\sworld'" ) );
        ins.inspectLiteral( r, S, literal( "'helloThereWorld'" ) );
        ins.inspectLiteral( r, S, literal( "'3.1415926'" ) );
        assertIsoModels( model(), r.model() );
        }
    
    @Test public void testAllowsStringLiteralsWithLegalLanguageCodes()
        {
        ins.inspectLiteral( r, S, literal( "'hello'en" ) );
        ins.inspectLiteral( r, S, literal( "'chat'fr" ) );
        ins.inspectLiteral( r, S, literal( "'hello\\sworld'en-us" ) );
        ins.inspectLiteral( r, S, literal( "'helloThereWorld'de" ) );
        ins.inspectLiteral( r, S, literal( "'3.1415926'xyz-abc-de" ) );
        assertIsoModels( model(), r.model() );
        }
    
    @Test public void testReportsIllegalLanguageCodes()
        {
        String badLanguageCodes = "17 x-= !boot  #bash fr-@";
        StringTokenizer st = new StringTokenizer( badLanguageCodes );
        while (st.hasMoreTokens())
            {
            String badLanguageCode = st.nextToken();
            Report r = new Report();
            ins.inspectLiteral( r, S, literal( "'chat'" + badLanguageCode ) );
            String expected = "[eye:mainProperty eye:badLanguage & eye:badLanguage '<a>' & eye:onLiteral 'chat']".replaceAll( "<a>", badLanguageCode );
            assertIsoModels( "should report bad language code " + badLanguageCode, itemModel( expected, S ), r.model() );
            }
        }
    
    private final Statement S = statement( "s P o" );
    
    @Test public void testReportsBadDatatypeURI()
        {
        ins.inspectLiteral( r, S, literal( "'abc'#:bar" ) );
        assertIsoModels( itemModel( "[eye:mainProperty eye:badDatatypeURI & eye:onLiteral 'abc' & eye:badDatatypeURI '#:bar']", S ), r.model() );
        }
    
    @Test public void testReportsBadDatatypeURIOnce()
        {
        ins.inspectLiteral( r, S, literal( "'abc'#:bar" ) );
        ins.inspectLiteral( r, S, literal( "'abc'#:bar" ) );
        assertIsoModels( itemModel( "[eye:mainProperty eye:badDatatypeURI & eye:onLiteral 'abc' & eye:badDatatypeURI '#:bar']", S ), r.model() );
        }
    
    @Test public void testReportsIllegalLexicalForm()
        {
        ins.inspectLiteral( r, S, literal( "'abc'xsd:integer" ) );
        assertIsoModels( itemModel( "[eye:mainProperty eye:badLexicalForm & eye:badLexicalForm 'abc' & eye:forDatatype xsd:integer]", S ), r.model() );
        }
    
    protected Literal literal( String spelling )
        { return (Literal) rdfNode( model(), spelling ); }
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