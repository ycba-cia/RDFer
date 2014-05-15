/*
 	(c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved.
 	[See end of file]
    $Id: PredicateRegister.java,v 1.6 2009/01/20 09:23:08 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Property;

/**
    @author kers
*/
public interface PredicateRegister
    {
    /**
        A trivial (incomplete) predicate register that doesn't do anything.
    */
    public static final PredicateRegister triv = new PredicateRegister()
        {
        public void register( Property property ) {}
        public List<Property> getRegisteredPredicates() { return new ArrayList<Property>(); }
        public void order( Property earlier, Property later ) {}
        };

    /**
         Register property <code>p</code> with this register.
    */
    void register( Property p );

    /**
        Register properties <code>p1</code> and <code>p2</code> with this
        register. Furthermore, p1 is deemed more "significant" than p2 and
        a renderer may wish to output p1 before p2 in an item report.
    */
    void order( Property p1, Property p2 );

    /**
        Answer a list containing all the registered properties, ordered
        according to the ordering pairs given by <code>order</code>.
    */
    List<Property> getRegisteredPredicates();
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
