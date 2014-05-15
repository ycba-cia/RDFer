/*
    (c) Copyright 2005 Hewlett-Packard Development Company, LP
    All rights reserved - see end of file.
    $Id: Lexer.java,v 1.1 2010/03/19 14:34:05 chris-dollin Exp $
*/
package com.hp.hpl.jena.extras.tools.notations.likeN3;


public class Lexer
    {
    public static class Type
        {
        public static final char END = ' ';
        public static final char ID = 'A';
        public static final char NUM = '0';
        public static final char LBOX = '[';
        public static final char RBOX = ']';
        public static final char SYM = '+';
        }
    
    protected String content;
    protected int index;
    protected char type;
    protected String current;
    
    public static class Kind
        {
        public static final int UNCLASSED = 0;
        public static final int LETTER = 1;
        public static final int DIGIT = 2;
        public static final int ISOLATE = 3;
        public static final int WHITESPACE = 4;
        public static final int UNDER = 5;
        public static final int ENDSTRING = 6;
        public static final int SIGN = 7;
        public static final int STRINGQUOTE = 8;
        public static final int HYPHEN = 9;
        }
    
    public static class Sets
        {
        public static final int LETTER = 1 << Kind.LETTER;
        public static final int DIGIT = 1 << Kind.DIGIT;
        public static final int UNDER = 1 << Kind.UNDER;
        public static final int HYPHEN = 1 << Kind.HYPHEN;
        public static final int SIGN = (1 << Kind.SIGN) | Sets.HYPHEN;
        }
    
    protected static final int [] primaryClasses = createPrimaryClasses();
    
    protected static int [] createPrimaryClasses()
        {
        int [] result = new int [256 * 256];
        for (char ch = 'A'; ch <= 'Z'; ch += 1) result[ch] = Kind.LETTER;
        for (char ch = 'a'; ch <= 'z'; ch += 1) result[ch] = Kind.LETTER;
        for (char ch = '0'; ch <= '9'; ch += 1) result[ch] = Kind.DIGIT;
        result[ '[' ] = result[ ']' ] = result[ '(' ] = result[ ')' ] = result[ ',' ] = Kind.ISOLATE;
        result[ '{' ] = result[ '}' ] = result[ ';' ] = Kind.ISOLATE;
        result[ ' ' ] = Kind.WHITESPACE;
        result[ '_' ] = Kind.UNDER;
        result[ '"' ] = result[ '\'' ] = Kind.STRINGQUOTE;
        result[ '+' ] = result[ '*' ] = result[ '=' ] = result[ '&' ] = result[ '^' ] = Kind.SIGN;
        result[ '@' ] = result[ '~' ] = result[ '/' ] = Kind.SIGN;
        result[ '-' ] = Kind.HYPHEN;
        result[ 0 ] = Kind.ENDSTRING;
        return result;
        }
    
    public Lexer( String s )
        { this.content = s; this.index = 0; advance(); }
    
    public boolean atEnd()
        { return current.equals( "" ); }
    
    public String current()
        { return current; }
    
    public char type()
        { return type; }
    
    protected char getCh()
        { return getCh( index ); }
    
    protected char getCh( int index )
        { return index < content.length() ? content.charAt( index ) : 0; }
    
    protected boolean member( int klass, int bits )
        { return ((1 << klass) & bits) != 0; }
        
    public void advance()
        {
        char ch = getCh();
        while (ch == ' ') { index += 1; ch = getCh(); }
        int start = index;
        int primaryClass = primaryClasses[ch];
        switch (primaryClass)
            {
            default:
                System.err.println( "! unrecognised character '" + ch + "' treated as isolate" );
                // fall through
            
            case Kind.ISOLATE:
                index += 1;
                current = "" + ch;
                type = ch;
                break;
                
            case Kind.STRINGQUOTE:
                index += 1;
                while (getCh() != ch) 
                    {
                    char x = getCh();
                    if (x == '\\') index += 2; else index += 1;
                    }
                index += 1;
                if (continuesString( getCh() )) scanStringQualifier();
                formToken( start, ch );
                break;
                
            case Kind.LETTER:
            case Kind.UNDER:
                scanToken( start, Sets.LETTER | Sets.DIGIT | Sets.UNDER, Type.ID );
                break;
            
            case Kind.DIGIT:
                scanToken( start, Sets.LETTER | Sets.DIGIT | Sets.UNDER, Type.NUM );
                break;
            
            case Kind.HYPHEN:
            case Kind.SIGN:
                scanToken( start, Sets.SIGN, Type.SYM );
                break;
            
            case Kind.ENDSTRING:
                current = ""; 
                type = Type.END; 
                break;
            }
        }
    
    protected boolean continuesString( char ch )
        { return Character.isLetter( ch ); }

    protected void scanStringQualifier()
        {
        scanSimpleSequence( Sets.LETTER | Sets.DIGIT | Sets.UNDER );
        if (getCh( index ) == ':') scanFollowingColon( Type.ID );
        else if (getCh( index ) == '-') scanSimpleSequence( Sets.LETTER | Sets.HYPHEN );
        }
    
    protected void scanToken( int start, int set, char type )
        {
        scanSimpleSequence( set );
        scanFollowingColon( type );
        formToken( start, type );
        }

    /**
        Advance over the characters identified by <code>set</code>.
        If they're follwed by a ':', follow up with reading characters up
        to whitespace but excluding any final comma, semicolon, or close-box.
    */
    private void scanFollowingColon( char type )
        {
        if (type == Type.ID && getCh( index ) == ':')
            {
            while (true)
                {
                char ch = getCh( ++index );
                if (ch == ' ' || ch == '\n' || ch == '\t' || ch == 0) break;                
                }
            char last = getCh( index - 1 );
            if (last == ';' || last == ',' || last == ']') index -= 1;
            }
        }

    /**
     	Scan over the characters with types in the given set.
    */
    private void scanSimpleSequence( int set )
        {
        while (member( primaryClasses[ getCh( ++index ) ], set )) {};
        }

    /**
        Form a token of type <code>type</code> and current spelling with
        characters from <code>start</code> to <code>this.index</code>.
    */
    private void formToken( int start, char type )
        {
        current = content.substring( start, index );
        this.type = type;
        }
    }

/*
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP
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