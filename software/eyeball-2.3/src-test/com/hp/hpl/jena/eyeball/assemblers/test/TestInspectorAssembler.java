/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 	All rights reserved - see end of file.
 	$Id: TestInspectorAssembler.java,v 1.7 2010/03/26 14:50:32 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.assemblers.test;

import org.junit.Test;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.assemblers.InspectorAssembler;
import com.hp.hpl.jena.eyeball.inspectors.*;
import com.hp.hpl.jena.eyeball.test.EyeballTestBase;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class) public class TestInspectorAssembler extends EyeballTestBase
    {
    public TestInspectorAssembler()
        {}

    @Test public void testEmptyInspectors()
        {
        Resource r = resourceInModel( "x rdf:type eye:Inspector" );
        Inspector i = (Inspector) new InspectorAssembler().open( r );
        assertInstanceOf( NullInspector.class, i );
        }
    
//    @Test public void testSingleInspector()
//        {
//        Resource r = resourceInModel
//            ( "x rdf:type eye:Inspector; x eye:inspector _i"
//            + "; _i eye:shortName 'name'; _i eye:className 'class'" );
//        Inspector i = (Inspector) new InspectorAssembler().open( r );
//        System.err.println( ">> " + i );
////        assertEquals( setOfStrings( "name" ), i.getMap().keySet() );
////        assertEquals( setOfStrings( "class" ), i.getMap().get( "name" ) );
//        }    
    
    @Test public void testAssemblesInspectorByClassName()
        {
        testAssemblesInspectorByName( InspectorMorse.class );
        testAssemblesInspectorByName( InspectorLewis.class );
        }

    private void testAssemblesInspectorByName( Class<? extends Inspector> inspectorClass )
        {
        String morse = inspectorClass.getName();
        Resource r = resourceInModel
            ( "x rdf:type eye:Inspector; x eye:className '" + morse + "'" );
        Inspector i = (Inspector) new InspectorAssembler().open( r ); 
        assertInstanceOf( inspectorClass, i );
        }
    
    
    @Test public void testIncludeByName()
        {
        String A = InspectorMorse.class.getName(), B = InspectorLewis.class.getName();
        Resource r = resourceInModel
            ( "x rdf:type eye:Inspector & eye:include A & eye:include B"
            + "; A rdf:type eye:Inspector & eye:className '" + A + "'"
            + "; B rdf:type eye:Inspector & eye:className '" + B + "'" );
        Inspector i = (Inspector) new InspectorAssembler().open( r );
        Report report = new Report();
        i.end( report );
        String expected = 
            "[eye:mainProperty inspectorWasHere & inspectorWasHere 'lewis']"
            + "; [eye:mainProperty inspectorWasHere & inspectorWasHere 'morse']";
        assertIsoModels( itemModel( expected ), report.model() );
        }
    
    @Test public void testIncludeByShortname()
        {
        String A = InspectorMorse.class.getName(), B = InspectorLewis.class.getName();
        Resource r = resourceInModel
            ( "x rdf:type eye:Inspector; x eye:includeByName 'nameA'; x eye:includeByName 'nameB'"
            + "; A rdf:type eye:Inspector & eye:shortName 'nameA' & eye:className '" + A + "'"
            + "; B rdf:type eye:Inspector & eye:shortName 'nameB' & eye:className '" + B + "'" );
        Inspector i = (Inspector) new InspectorAssembler().open( r );
        Report report = new Report();
        i.end( report );
        String expected = 
            "[eye:mainProperty inspectorWasHere & inspectorWasHere 'lewis']"
            + "; [eye:mainProperty inspectorWasHere & inspectorWasHere 'morse']";
        assertIsoModels( itemModel( expected ), report.model() );
        }
    
    static final Property inspectorWasHere = property( "inspectorWasHere" );
    
    public static class InspectorMorse extends InspectorBase 
        {
        public InspectorMorse( Resource root ) {}
        
        public void end( Report r ) 
            { r.createItem().addMainProperty( inspectorWasHere, "morse" ); }
        }
    
    public static class InspectorLewis extends InspectorBase 
        {
        public InspectorLewis( Resource root ) {}
        
        public void end( Report r ) { r.createItem().addMainProperty( inspectorWasHere, "lewis" ); }
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