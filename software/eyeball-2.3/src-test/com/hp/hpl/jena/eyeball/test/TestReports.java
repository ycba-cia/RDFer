/*
 	(c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 	All rights reserved - see end of file.
 	$Id: TestReports.java,v 1.3 2010/03/26 14:50:31 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.rdf.model.*;

@RunWith(JUnit4.class) public class TestReports extends EyeballTestBase
    {
    public TestReports()
        {}

    @Test public void testReportItemExists()
        {
        @SuppressWarnings("unused") ReportItem item = new Report() .createItem();
        }

    @Test public void testCreatePhantomItemWithoutStatement()
        {
        Report r = new Report();
        r.createItem();
        assertIsoModels( empty, r.model() );
        }

//    @Test public void testCreatePhantomItemWithStatement()
//        {
//        Report r = new Report();
//        r.createItem( statement( "S P O" ) );
//        assertIsoModels( empty, r.model() );
//        }

    @Test public void testCreateItemWithoutStatement()
        {
        Report r = new Report();
        r.createItem().addProperty( property( "P" ), rdfNode( empty, "O" ) );
        assertIsoModels( model( "_x rdf:type eye:Item; _x P O" ), r.model() );
        }

    @Test public void testCreateItemWithStatement()
        {
        Report r = new Report();
        Statement S = statement( "S P O" );
        ReportItem item = r.createItem( S ).addProperty( property( "P" ), rdfNode( empty, "O" ) );
        Model wanted = model( "_x rdf:type eye:Item; _x P O" );
        wanted.add( resource( "_x" ), EYE.onStatement, wanted.createReifiedStatement( S ) );
        assertIsoModels( wanted, r.model() );
        }

    @Test public void testCreateStatementJustOnce()
        {
        Report r = new Report();
        Statement S = statement( "S P O" );
        ReportItem i = r.createItem( S ), j = r.createItem( S );
        Resource iStatement = i.getStatement();
        Resource jStatement = j.getStatement();
        assertEquals( iStatement, jStatement );
        }

    @Test public void testAddMainProperty()
        {
        Model m = model();
        ReportItem i = ReportItem.create( m );
        i.addMainProperty( property( "P" ), resource( "O" ) );
        assertIsoModels( model( "[rdf:type eye:Item & P O & eye:mainProperty P]" ), m );
        }
    
    @Test public void testAddRDFNodeProperty()
        {
        Model m = model();
        ReportItem i = ReportItem.create( m );
        i.addProperty( property( "P" ), resource( "O" ) );
        assertIsoModels( model( "[rdf:type eye:Item & P O]" ), m );
        }

    @Test public void testAddLiteralProperty()
        {
        Model m = model();
        ReportItem i = ReportItem.create( m );
        i.addProperty( property( "P" ), rdfNode( m, "'thing'" ) );
        assertIsoModels( model( "[rdf:type eye:Item & P 'thing']" ), m );
        }

    @Test public void testGetStatement()
        {
        Model m = model();
        Statement s = statement( "S P O" );
        ReifiedStatement rs = m.createReifiedStatement( s );
        ReportItem i = ReportItem.create( m, rs );
        assertEquals( rs, i.getStatement() );
        }

    @Test public void testRequiredProperty()
        {
        Model m = model();
        Property P = property( "P" );
        RDFNode O = resource( "object" );
        ReportItem i = ReportItem.create( m ).addProperty( P, O );
        assertEquals( O, i.getRequiredProperty( P ) );
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
