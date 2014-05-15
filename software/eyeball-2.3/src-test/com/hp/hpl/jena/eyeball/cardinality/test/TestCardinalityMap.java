/*
 	(c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 	All rights reserved - see end of file.
 	$Id: TestCardinalityMap.java,v 1.3 2010/03/26 14:50:33 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.cardinality.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.hp.hpl.jena.eyeball.cardinality.*;
import com.hp.hpl.jena.eyeball.test.EyeballTestBase;
import com.hp.hpl.jena.rdf.model.*;

@RunWith(JUnit4.class) public class TestCardinalityMap extends EyeballTestBase
    {
    public TestCardinalityMap()
        {}

    @Test public void testEmptySchemeKnowsNoClasses()
        {
        Model m = model( "" );
        CardinalityMap c = CardinalityMap.create( m );
        assertEquals( true, c.isEmpty() );
        assertSame( null, c.cardinalities( resource( "x" ) ) );
        }

    @Test public void testIrelevantSchemaKnowsNoClasses()
        {
        Model m = model( "q rdf:type Queue" );
        CardinalityMap c = CardinalityMap.create( m );
        assertEquals( true, c.isEmpty() );
        assertSame( null, c.cardinalities( resource( "x" ) ) );
        }

    @Test public void testSingletonSchemaKnowsSomeClasses()
        {
        Model m = model( "x rdfs:subClassOf [owl:onProperty P & owl:cardinality 1]" );
        CardinalityMap c = CardinalityMap.create( m );
        assertEquals( false, c.isEmpty() );
        assertDiffer( null, c.cardinalities( resource( "x" ) ) );
        assertEquals( null, c.cardinalities( resource( "y" ) ) );
        }

    @Test public void testSingletonSchemaKnowsItsProperty()
        {
        Model m = model( "x rdfs:subClassOf [owl:onProperty P & owl:cardinality 1]" );
        CardinalityMap cm = CardinalityMap.create( m );
        assertEquals( false, cm.isEmpty() );
        ClassProperties c = cm.cardinalities( resource( "x" ) );
        assertEquals( setOfOne( resource( "P" ) ), c.onProperties() );
        }

    @Test public void testSingletonSchemaKnowsBothProperties()
        {
        Model m = model( "x rdfs:subClassOf [owl:onProperty P & owl:cardinality 1], [owl:onProperty Q & owl:cardinality 1]" );
        CardinalityMap cm = CardinalityMap.create( m );
        assertEquals( false, cm.isEmpty() );
        ClassProperties c = cm.cardinalities( resource( "x" ) );
        assertEquals( resourceSet( "P Q" ), c.onProperties() );
        }

    @Test public void testMinCardinality()
        {
        Model m = model( "x rdfs:subClassOf [owl:onProperty P & owl:minCardinality 2]" );
        CardinalityMap cm = CardinalityMap.create( m );
        assertEquals( false, cm.isEmpty() );
        ClassProperties c = cm.cardinalities( resource( "x" ) );
        assertEquals( resourceSet( "P" ), c.onProperties() );
        PropertyCardinality pc = c.get( resource( "P" ) );
        assertEquals( 2, pc.minCardinality() );
        assertEquals( Integer.MAX_VALUE, pc.maxCardinality() );
        }

    @Test public void testMaxCardinality()
        {
        Model m = model( "x rdfs:subClassOf [owl:onProperty P & owl:maxCardinality 3]" );
        CardinalityMap cm = CardinalityMap.create( m );
        assertEquals( false, cm.isEmpty() );
        ClassProperties c = cm.cardinalities( resource( "x" ) );
        assertEquals( resourceSet( "P" ), c.onProperties() );
        PropertyCardinality pc = c.get( resource( "P" ) );
        assertEquals( 0, pc.minCardinality() );
        assertEquals( 3, pc.maxCardinality() );
        }

    @Test public void testMultipleCardinalities_ClassProperties()
        {
        ClassProperties cp = new ClassProperties();
        cp.addProperty( resource( "p" ), 5, 100 );
        cp.addProperty( resource( "p" ), 10, 200 );
        PropertyCardinality pc = cp.get( resource( "p" ) );
        assertEquals( 10, pc.minCardinality() );
        assertEquals( 100, pc.maxCardinality() );
        }

    @Test public void testCardinalitiesOne_ClassProperties()
        {
        ClassProperties c = new ClassProperties().addProperty( resource( "x" ), 1, 1 );
        PropertyCardinality pc = c.get( resource( "x" ) );
        assertEquals( 1, pc.minCardinality() );
        assertEquals( 1, pc.maxCardinality() );
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
