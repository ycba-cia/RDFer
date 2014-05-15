/*
 	(c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved - see end of file.
 	$Id: Finder.java,v 1.5 2009/01/19 15:53:17 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball;

import java.util.*;

import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.NotFoundException;

public class Finder
    {
    protected final Model config;

    protected final Map<Resource, String> doing = new HashMap<Resource, String>();

    public Finder( Model config )
        { this.config = config; }

    public List<String> getClassNames( String shortName )
        {
        List<String> result = new ArrayList<String>();
        boolean missing = true;
        StmtIterator it = config.listStatements( null, EYE.shortName, shortName );
        while (it.hasNext())
            {
            missing = false;
            Resource x = it.nextStatement().getSubject();
            getClassNames( result, x );
            }
        if (missing) throw new NotFoundException( shortName );
        return result;
        }

    protected void getClassNames( List<String> result, Resource x )
        {
        if (doing.containsKey( x ))
            {}
        else
            {
            doing.put( x, "" );
            getDirectClassNames( result, x );
            getIndirectClassNames( result, x );
            }
        }

    protected void getIndirectClassNames( List<String> result, Resource x )
        {
        getIndirectByResource( result, x );
        getIndirectByName( result, x );
        }

    protected void getIndirectByResource( List<String> result, Resource x )
        {
        StmtIterator a = x.listProperties( EYE.include );
        while (a.hasNext())
            getClassNames( result, a.nextStatement().getResource() );
        }

    protected void getIndirectByName( List<String> result, Resource x )
        {
        StmtIterator b = x.listProperties( EYE.includeByName );
        while (b.hasNext())
            result.addAll( getClassNames( b.nextStatement().getString() ) );
        }

    protected void getDirectClassNames( List<String> result, Resource x )
        {
        StmtIterator b = x.listProperties( EYE.inspector );
        while (b.hasNext()) result.add( b.nextStatement().getString() );
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
