/*
 	(c) Copyright 2007 Hewlett-Packard Development Company, LP
 	All rights reserved.
 	$Id: Copy.java,v 1.4 2009/01/29 12:20:14 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.util;

import java.util.*;

import com.hp.hpl.jena.rdf.model.*;

public class Copy
    {
    public static Resource copyStatements( Resource x )
        {
        Model m = ModelFactory.createDefaultModel();
        Resource result = m.createResource();
        for (StmtIterator it = x.listProperties(); it.hasNext();)
            {
            Statement s = it.nextStatement();
            result.addProperty( s.getPredicate(), s.getObject() );
            }
        return result;
        }
    
    public static Resource copyStatements( Resource x, Property p )
        {
        Model m = ModelFactory.createDefaultModel();
        Resource result = m.createResource();
        for (StmtIterator it = x.listProperties( p ); it.hasNext();)
            {
            Statement s = it.nextStatement();
            result.addProperty( s.getPredicate(), s.getObject() );
            }
        return result;
        }

    public static void addStringProperties( Resource subject, Property p, List<String> strings )
        {
        for (Iterator<String> it = strings.iterator(); it.hasNext();)
            subject.addProperty( p, it.next() );
        }

    public static void addProperties( Resource toStamp, Resource subject, Property p )
        {
        for (StmtIterator it = subject.listProperties( p ); it.hasNext();)
            {
            Statement s = it.nextStatement();
            toStamp.addProperty( p, s.getObject() );
            }
        }
    }

/*
    (c) Copyright 2007 Hewlett-Packard Development Company, LP
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions
    are met:

    1. Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.

    2. Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.

    3. The name of the author may not be used to endorse or promote products
       derived from this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
    IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
    OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
    IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
    INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
    NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
    DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
    THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
    THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
