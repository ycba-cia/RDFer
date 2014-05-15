/*
 	(c) Copyright 2008 Hewlett-Packard Development Company, LP
 	All rights reserved.
 	$Id: SetPropertiesFromString.java,v 1.2 2009/01/29 12:20:14 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.util;

import com.hp.hpl.jena.graph.*;
import com.hp.hpl.jena.graph.test.NodeCreateUtils;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.PrefixMapping;

/**
    A utility class to add statements to a model (supplied to its constructor)
    from a template string of the form `subject.predicate=object`. The subject,
    predicate, and object are all in NodeCreateUtils syntax (at this time).
    
 	@author kers
*/
public class SetPropertiesFromString
    {
    protected final Model m;
    protected final PrefixMapping pm;
    
    public SetPropertiesFromString( Model m )
        { this.m = m; this.pm = m.getGraph().getPrefixMapping(); }
    
    public void set( String string )
        {
        int dotPos = string.indexOf( '.' );
        int eqPos = string.indexOf( '=', dotPos );
        String S = string.substring( 0, dotPos );
        String P = string.substring( dotPos + 1, eqPos );
        String O = string.substring( eqPos + 1 );
        m.getGraph().add( Triple.create( createNode( S ), createNode( P ), createNode( O ) ) );
        }
    
    private Node createNode( String s )
        { return NodeCreateUtils.create( pm, s ); }
    }

/*
 * (c) Copyright 2005, 2006, 2007 Hewlett-Packard Development Company, LP
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