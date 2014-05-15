/*
    (c) Copyright 2005 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
    All rights reserved.
    [See end of file]
    $Id: TestLexing.java,v 1.2 2010/03/26 14:50:31 chris-dollin Exp $
*/
package com.hp.hpl.jena.extras.tools.notations.test;

import static org.junit.Assert.*;

import java.util.StringTokenizer;

import org.junit.Test;

import com.hp.hpl.jena.extras.tools.notations.likeN3.*;
import com.hp.hpl.jena.extras.tools.notations.likeN3.Lexer.Type;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
/**
 * @author hedgehog
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
@RunWith(JUnit4.class) public class TestLexing
    {
    @Test public void testLexNone()
        { Lexer L = new Lexer( "" ); 
        testAtEnd( L ); }
    
    @Test public void testLexSpaces()
        { Lexer L = new Lexer( "" ); 
        testAtEnd( L ); }

    @Test public void testLexSingleItem()
        { Lexer L = new Lexer( "X" );
        assertFalse( L.atEnd() );
        testAtToken( L, Type.ID , "X" ); }

    @Test public void testAdvanceOnce()
        { Lexer L = new Lexer( "X" );
        L.advance();
        testAtEnd( L ); }
    
    @Test public void testTwoTokens()
        { Lexer L = new Lexer( "X Y" );
        testAtToken( L, Type.ID, "X" );
        L.advance();
        testAtToken( L, Type.ID, "Y" ); 
        L.advance();
        testAtEnd( L ); }
    
    @Test public void testLBoxToken()
        { Lexer L = new Lexer( "[Stuff" );
        testAtToken( L, Type.LBOX, "[" );
        L.advance();
        testAtToken( L, Type.ID, "Stuff" ); }
    
    @Test public void testRBoxToken()
        { Lexer L = new Lexer( "]More" );
        testAtToken( L, Type.RBOX, "]" );
        L.advance();
        testAtToken( L, Type.ID, "More" ); }
    
    @Test public void testBoxesTokenSequence()
        { testTokenSequence( "[][foo][[]]", "[[ ]] [[ Afoo ]] [[ [[ ]] ]]" ); }

    @Test public void testBracketsTokenSequence()
        { testTokenSequence( "(Spoo)(([))]", "(( ASpoo )) (( (( [[ )) )) ]]" ); }
    
    @Test public void testComma()
        { testTokenSequence( ", ,, ,,, [,]", ",, ,, ,, ,, ,, ,, [[ ,, ]]" ); }
    
    @Test public void testBraces()
        { testTokenSequence( "{}{{}}", "{{ }} {{ {{ }} }}" ); }
    
    @Test public void testIdentifierToken()
        { testTokenSequence( "Foo BOO spoo gl00", "AFoo ABOO Aspoo Agl00" ); 
        testTokenSequence( "bigPicture little_men x___y", "AbigPicture Alittle_men Ax___y" ); }
    
    @Test public void testQuasiQnames()
        {
        testTokenSequence( "foo:A foo:B(C) bar:ZZ", "Afoo:A Afoo:B(C) Abar:ZZ" );
        testTokenSequence( "_:foo bar:bill; [new:goo]", "A_:foo Abar:bill ;; [[ Anew:goo ]]" );
        }
    
    @Test public void testDigitTokens()
        { testTokenSequence( "0 1 2 3 4 5 6 7 8 9", "00 01 02 03 04 05 06 07 08 09" ); }
    
    @Test public void testNumbericTokens()
        { testTokenSequence( "12345 123 987 123456789", "012345 0123 0987 0123456789" ); }
    
    @Test public void testNumbericTokensWithUnderbars()
        { testTokenSequence( "1_234 123_4 1_ 1_____2", "01_234 0123_4 01_ 01_____2" ); }
    
    @Test public void testSigns()
        { testTokenSequence( "+ ++ - -- +- -+", "++ +++ +- +-- ++- +-+" ); 
        testTokenSequence( "* ** = == ^ ^^ & &&", "+* +** += +== +^ +^^ +& +&&" );
        testTokenSequence( "@ @= ~ +~ / =/@", "+@ +@= +~ ++~ +/ +=/@" );
        testTokenSequence( "*=^&+-", "+*=^&+-" ); }
    
    @Test public void testString()
        { 
        testTokenSequence( "\"hello\" \" world \"", "\"\"hello\" \"\"\\sworld\\s\"" ); 
        testTokenSequence( "\"\\\"\"", "\"\"\\\"\"" ); 
        testTokenSequence( "\"f(x)\" \"<L(R)>\"", "\"\"f(x)\" \"\"<L(R)>\"" ); 
        }
    
    @Test public void testStringsWithExtras()
        {
        testTokenSequence( "'hello'en 'world'fr-fr", "''hello'en ''world'fr-fr" );
        testTokenSequence( "'something'xsd:integer", "''something'xsd:integer" );
        testTokenSequence( "'something'http://bells/and/whistles", "''something'http://bells/and/whistles" );
        }
    
    protected void testTokenSequence( String toLex, String tokens )
        { Lexer L = new Lexer( toLex );
        StringTokenizer st = new StringTokenizer( tokens );
        while (st.hasMoreTokens())
            { String token = st.nextToken().replaceAll( "\\\\s", " " );
            assertFalse( L.atEnd() );
            assertEq( token.substring(1), L.current() );
            assertEquals( token.charAt(0), L.type() );
            L.advance(); }
        assertTrue( L.atEnd() ); }
    
    protected void testAtEnd( Lexer L )
        { assertTrue( L.atEnd() );
        assertEquals( "", L.current() );
        assertEquals( Type.END, L.type() ); }
    
    protected void testAtToken( Lexer L, char type, String content )
        { assertEquals( content, L.current() );
        assertEquals( type, L.type() ); }
    
    protected void assertEq( String want, String got )
        { if (want.equals( got ) == false)
            fail( "failure: expected <" + want + "> but got <" + got + ">" ); }    
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