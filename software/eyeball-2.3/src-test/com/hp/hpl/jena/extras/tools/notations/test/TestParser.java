/*
    (c) Copyright 2005 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
    All rights reserved.
    [See end of file]
    $Id: TestParser.java,v 1.2 2010/03/26 14:50:31 chris-dollin Exp $
*/
package com.hp.hpl.jena.extras.tools.notations.test;


import org.junit.Test;

import com.hp.hpl.jena.extras.tools.notations.likeN3.Parser;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.ReificationStyle;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.hp.hpl.jena.rdf.model.test.ModelTestBase.*;

/**
 * @author hedgehog

     S P O (;|\n) S P O ...
     S P O, O2, O3 ...
     S P O & Q O2 & R O3
     [P O ....]
     (A B C)
     S

     model = statement ** ";"
     statement = subject [predication (&predication)*]
     predication = predicate object [, object]*
     subject = name | "(" subject* ")" | "[" predication (&predication)* "]"

 */
@RunWith(JUnit4.class) public class TestParser
    {    
    @Test public void testNoStatements()
        { testGeneratedModel( "", "" ); }

    @Test public void testSimpleStatement()
        { testGeneratedModel( "s p o", "s p o" ); }

    @Test public void testSemidStatements()
        { testGeneratedModel( "s p o; a b c", "s p o; a b c" ); }

    @Test public void testExtraPredications()
        { testGeneratedModel( "s p x; s q y", "s p x & q y" ); }

    @Test public void testExtraObjects()
        { testGeneratedModel( "a r b; a r c; a r d", "a r b, c, d" ); }

    @Test public void testEmptyList()
        { testGeneratedModel( "a p rdf:nil", "a p ()" ); }

    @Test public void testSingletonList()
        { testGeneratedModel( "a p _x; _x rdf:rest rdf:nil; _x rdf:first X", "a p (X)" ); }

    @Test public void testListAsSubject()
        { testGeneratedModel( "_a rdf:rest rdf:nil; _a rdf:first x; _a p y", "(x) p y" ); }

    @Test public void testLongishList()
        { Model wanted = m
            ( "a p _a; _a rdf:rest _b; _b rdf:rest _c; _c rdf:rest _d; _d rdf:rest rdf:nil"
            + "; _a rdf:first A; _b rdf:first B; _c rdf:first C; _d rdf:first D" );
        assertIsoModels( wanted, parse( "a p (A B C D)"  ) ); }

    @Test public void testBNode()
        { testGeneratedModel( "_x p y", "[] p y" ); }

    @Test public void testDifferentBNodes()
        { testGeneratedModel( "_x p y; _y q z", "[] p y; [] q z" ); }

    @Test public void testBNodeWithProperties()
        { testGeneratedModel( "_x p y; _x q z", "[p y] q z" ); }

    @Test public void testBNodeWithManyProperties()
        { testGeneratedModel( "_x p y; _x p z; _x q a; _x q b; _x i j", "[p y, z & q a, b] i j" ); }

    @Test public void testBnodeBugWithSemicolon()
        { try { testGeneratedModel( "", "[p x; q y]" ); fail( "should detect syntax error" ); }
        catch (RuntimeException e) { pass(); } }

    @Test public void testLoneSubject()
        { testGeneratedModel( "", "lost" ); }

    @Test public void testLoneList()
        { testGeneratedModel( "", "()" );
        testGeneratedModel( "_x rdf:rest rdf:nil; _x rdf:first X", "(X)" );
        testGeneratedModel( "_x rdf:rest _y; _y rdf:rest rdf:nil; _x rdf:first X; _y rdf:first Y", "(X Y)" ); }

    @Test public void testLoneBNode()
        { testGeneratedModel( "", "[]" );
        testGeneratedModel( "_x p y", "[p y]" );
        testGeneratedModel( "_x p y; _x q z", "[p y & q z]" ); }

    @Test public void testBigger()
        {
        Model o = parse
            ( "State rdfs:subClassOf [owl:onProperty size & owl:cardinality 1]"
            + "    & rdfs:subClassOf [owl:onProperty raidLevel & owl:cardinality 1]"
            + "    & rdfs:subClassOf [owl:onProperty status & owl:cardiality 1]"
            + "    & rdfs:subClassOf [owl:onProperty status & owl:allValuesFrom Status]"
            + "    ;"
            + "Goal rdfs:subClassOf State"
            + "    & rdfs:subClassOf [owl:onProperty size & owl:hasValue 100]"
            + "" );
        o.getGraph(); // TODO some testing
        }

    @Test public void testOutput()
        {
        // parse( "(A B C D) p (D E F G), H, I, J &q (K L M N), O, P" ).write( System.out, "N3" );
        }

    protected void testGeneratedModel( String wanted, String toParse )
        { assertIsoModels( m( wanted ), parse( toParse ) ); }

    protected Model m( String s )
        { return modelWithStatements( s ); }

    protected Model parse( String s )
        { Model m = createModel( ReificationStyle.Standard );
        Model mDash = new Parser( s ).parseInto( m );
        assertSame( m, mDash );
        return m; }
    }
/*
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP
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