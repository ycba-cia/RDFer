/*
 	(c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved - see end of file.
 	$Id: SimplePredicateRegister.java,v 1.11 2010/01/11 14:08:26 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball;

import java.util.*;

import com.hp.hpl.jena.rdf.model.*;

public class SimplePredicateRegister implements PredicateRegister
    {
    protected Model registered = ModelFactory.createDefaultModel();

    protected final Property before = registered.createProperty( "eye:before" );
    protected final Property terminal = registered.createProperty( "eye:terminal" );

    public void register( Property p )
        { order( p, terminal ); }

    public void order( Property earlier, Property later )
        { registered.add( earlier, before, later ); }

    public List<Property> getRegisteredPredicates()
        {
        Model copyOfRegistered = ModelFactory.createDefaultModel().add( registered );
        List<Property> result = crudeTopologicalSort( copyOfRegistered );
        result.remove( terminal );
        return result;
        }

    protected List<Property> crudeTopologicalSort( Model m )
        {
        List<Property> result = new ArrayList<Property>();
        Set<Property> all = allNodes( m );
        while (!m.isEmpty())
            {
            Set<Resource> subjects = m.listSubjects().toSet();
            Set<RDFNode> objects = m.listObjects().toSet();
            subjects.removeAll( objects );
            Resource x = subjects.iterator().next();
            result.add( x.as( Property.class ) );
            all.remove( x );
            x.removeProperties();
            }
        result.addAll( all );
        return result;
        }

    protected Set<Property> allNodes( Model m )
        {
        Set<Property> result = new HashSet<Property>();
        for (Resource r: m.listSubjects().toSet()) result.add( r.as(  Property.class ) );
        for (RDFNode r: m.listObjects().toSet()) result.add( r.as(  Property.class ) );
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
