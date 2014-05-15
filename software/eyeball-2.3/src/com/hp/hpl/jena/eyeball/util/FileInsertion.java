/*
 	(c) Copyright 2008 Hewlett-Packard Development Company, LP
 	All rights reserved.
 	$Id: FileInsertion.java,v 1.5 2009/01/29 12:20:14 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.util;

import java.util.*;
import java.util.regex.*;

import com.hp.hpl.jena.util.FileManager;

/**
    A FileInsertion object has an <code>insert</code> method that takes
    a template string and returns the value resulting from replacing filename
    insertion commands with the contents of the file. 
<p>
    Filename insertions are of the form @'filename'. The escape sequence @@
    is replaced by @. The sequence @LANG, where LANG is a sequence of
    letters, digits, and hyphens (such as one might find in a language code)
    is left unchanged.
<p>
    A FileInsertion object uses a FileManager passed to its constructor to 
    specify the mapping of filenames to content. If one is not supplied,
    FileManager.get() is used. 
    
 	@author kers
*/
public class FileInsertion
    {
    protected final FileManager fm;
    
    public FileInsertion()
        { this( FileManager.get() ); }
    
    public FileInsertion( FileManager fm )
        { this.fm = fm; }
    
    public String insert( String template )
        {
        StringBuffer result = new StringBuffer();
        List<String> parts = tokenise( template );
        for (int i = 0; i < parts.size(); i += 1) 
            result.append( insertPart( parts.get( i ) ) );
        return result.toString(); 
        }

    private String insertPart( String part )
        {
        if (part.equals( "@@" )) 
            return "@";   
        if (part.startsWith( "@'" ) || part.startsWith( "@\"" )) 
            return fm.readWholeFileAsUTF8( part.substring( 2, part.length() - 1 ) );
        return part;
        }

    public List<String> tokenise( String template )
        { 
        List<String> result = new ArrayList<String>();
        Pattern p = Pattern.compile( "@(@|'[^']*'|\"[^\"]*\"|[A-Za-z0-9-]+)" );
        Matcher m = p.matcher( template );
        int begin = 0;
        while (m.find())
            {
            int start = m.start(), end = m.end();
            if (begin < start) result.add( template.substring( begin, start ) );
            result.add( template.substring( start, end ) );
            begin = end;
            }
        if (begin < template.length()) result.add( template.substring( begin ) );
        return result; 
        }

    }


/*
 * (c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
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