/*
    (c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
    All rights reserved - see end of file.
    $Id: TestAllTypedInspector.java,v 1.4 2010/03/26 14:50:31 chris-dollin Exp $
*/
package com.hp.hpl.jena.eyeball.inspectors.test;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.inspectors.AllTypedInspector;
import com.hp.hpl.jena.rdf.model.*;

@RunWith(JUnit4.class) public class TestAllTypedInspector extends InspectorTestBase 
    {
    public TestAllTypedInspector() 
        {}

    protected Class<? extends Inspector> getInspectorClass() 
        { return AllTypedInspector.class; }

    protected final AllTypedInspector ins = new AllTypedInspector();

    protected final Report r = new Report();

    @Test public void testCardinalityInspectorDeclaresPredicates()
        {
        Report r = new Report();
        ins.begin( r, ontModel() );
        List<Property> predicates = r.getPredicateRegister().getRegisteredPredicates();
        assertEquals( eyeResourceSet( "hasNoType" ), new HashSet<Property>( predicates ) );
        }    
    
    @Test public void testUntypedItemProducesReport()
        {
        Statement xPy = statement( "x P y" );
        ins.inspectStatement( r, xPy );
        assertIsoModels( itemModel( "[eye:hasNoType x & eye:mainProperty eye:hasNoType]", xPy ), r.model() );
        }
    
    @Test public void testTypedItemDoesNotProduceReport()
        {
        Model m = model( "x rdf:type T" );
        ins.inspectStatement( r, statement( m, "x P y" ) );
        assertIsoModels( model( "" ), r.model() );
        }
    
    @Test public void testUntypedItemOnlyReportedOnce()
        {
        Statement xPy = statement( "x P y" );
        ins.inspectStatement( r, xPy );
        ins.inspectStatement( r, statement( "x Q z" ) );
        assertIsoModels( itemModel( "[eye:hasNoType x & eye:mainProperty eye:hasNoType]", xPy ), r.model() );
        }
    
    @Test public void testUntypedLiteralNotReportedByDefault()
        {
        Model m = model( "a rdf:type U" );
        ins.inspectStatement(  r, statement( m, "a P 'untypedString'" ) );
        assertIsoModels( model( "" ), r.model() );
        }
    
    @Test public void testUntypedLiteralReportedWhenConfigured()
        {
        AllTypedInspector ins = new AllTypedInspector( resourceInModel( "root eye:checkLiteralTypes eye:true" ) );
        Model m = model( "a rdf:type U" );
        Statement source = statement( m, "a P 'untypedString'" );
        ins.inspectStatement(  r, source );
        assertIsoModels( itemModel( "[eye:mainProperty eye:hasNoType & eye:hasNoType 'untypedString']", source ), r.model() );
        }
    
    @Test public void testLanguagedLiteralNotReported()
        {
        AllTypedInspector ins = new AllTypedInspector( resourceInModel( "root eye:checkLiteralTypes eye:true" ) );
        Model m = model( "a rdf:type U" );
        ins.inspectStatement(  r, statement( m, "a P 'untypedString'en-UK" ) );
        assertIsoModels( model( "" ), r.model() );
        }

    @Test public void testUntypedObjectsNotReportedByDefault()
        {
        Model m = model( "a rdf:type U" );
        ins.inspectStatement(  r, statement( m, "a P b" ) );
        assertIsoModels( model( "" ), r.model() );
        }

    @Test public void testUntypedObjectsReportedIfConfigured()
        {
        AllTypedInspector ins = new AllTypedInspector( resourceInModel( "root eye:checkObjectTypes eye:true" ) );
        Model m = model( "a rdf:type U" );
        Statement aPb = statement( m, "a P b" );
        ins.inspectStatement(  r, aPb );
        assertIsoModels( itemModel( "[eye:mainProperty eye:hasNoType & eye:hasNoType b]", aPb ), r.model() );
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