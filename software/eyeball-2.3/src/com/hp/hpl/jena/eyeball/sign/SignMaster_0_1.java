/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved.
 	$Id: SignMaster_0_1.java,v 1.7 2009/01/29 12:20:13 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.sign;

import java.util.*;

import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.graph.*;
import com.hp.hpl.jena.rdf.model.Model;

public class SignMaster_0_1 implements SignMaster
    { 
    public static final long LITERAL_MULTIPLIER = 19853;
    
    public static final long SUBJECT_MULTIPLIER = 39709;
    public static final long PREDICATE_MULTIPLIER = 307;
    public static final long OBJECT_MULTIPLIER = 1;
    
    static class Numbers
        {
        long inBound = 1;
        long outBound = 1;
        }

    protected static final Node EYE_signature = EYE.signature.asNode();

    public long signature( Model m )
        {  
        Graph g = m.getGraph();
        Map<Node, Numbers> map = new HashMap<Node, Numbers>();
        long result = 0;
        for (Iterator<Triple> it = GraphUtil.findAll( g ); it.hasNext();) 
            {
            Triple t = it.next();
            if (!t.getPredicate().equals( EYE_signature ))
                {
                Node subject = t.getSubject(), object = t.getObject();
                long sig = tripleSignature( t );
                result += sig;
                getNumbers( map, subject ).outBound += sig;
                getNumbers( map, object ).inBound += sig;
                }
            }
        for (Iterator<Numbers> it = map.values().iterator(); it.hasNext();)
            {
            SignMaster_0_1.Numbers n = it.next();
            result += n.inBound * (n.outBound ^ 0x0f0f0f0f0f0f0f0fL);
            }
        return result;
        }
    
    private SignMaster_0_1.Numbers getNumbers( Map<Node, Numbers> map, Node node )
        {
        SignMaster_0_1.Numbers n = map.get( node );
        if (n == null) map.put( node, n = new Numbers() );
        return n;
        }
    
    public long signature( Node node )
        {
        if (node.isLiteral()) return node.hashCode() * LITERAL_MULTIPLIER;
        return node.isBlank() ? 0 : node.hashCode();
        }
    
    public long tripleSignature( Triple t )
        {
        return
            signature( t.getSubject() ) * SUBJECT_MULTIPLIER
            + signature( t.getPredicate() ) * PREDICATE_MULTIPLIER
            + signature( t.getObject() ) * OBJECT_MULTIPLIER
            ;
        }
    }

/*
 * (c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
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
