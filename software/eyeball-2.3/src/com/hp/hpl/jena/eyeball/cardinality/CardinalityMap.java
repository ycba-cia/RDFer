/*
 (c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 All rights reserved - see end of file.
 $Id: CardinalityMap.java,v 1.10 2009/01/29 14:12:05 chris-dollin Exp $
 */

package com.hp.hpl.jena.eyeball.cardinality;

import java.util.*;

import com.hp.hpl.jena.extras.tools.query.*;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.*;

public class CardinalityMap
    {
    public static CardinalityMap create( Model m )
        {
        Map<RDFNode, ClassProperties> cards = new HashMap<RDFNode, ClassProperties>();
        extractCardinality( m, cards, OWL.cardinality, accept, accept );
        extractCardinality( m, cards,OWL.minCardinality, accept, constant(Integer.MAX_VALUE) );
        extractCardinality( m, cards, OWL.maxCardinality, constant(0), accept );
        return new CardinalityMap( cards );
        }

    protected Map<RDFNode, ClassProperties> cards;

    protected CardinalityMap( Map<RDFNode, ClassProperties> cards )
        { this.cards = cards; }

    static Choice constant( final int n )
        { return new Choice() { public int choose( int ignored ) { return n; } }; }

    static final Choice accept =
        new Choice() { public int choose( int result ) { return result; } };

    private interface Choice
        {
        int choose( int cardinality );
        }

    private static void extractCardinality( Model m, Map<RDFNode, ClassProperties> cards, Property p, Choice forMin, Choice forMax )
        {
        ModelQuery Q = ModelQuery.create();
        QueryVariable
            aClass = Q.createVariable(),
            aRestriction = Q.createVariable(),
            aProperty = Q.createVariable(),
            theCardinality = Q.createVariable();
        Q
            .add( aClass, RDFS.subClassOf, aRestriction )
            .add( aRestriction, OWL.onProperty, aProperty )
            .add( aRestriction, p, theCardinality );
        ExtendedIterator<? extends List<Node>> results = Q.run( m );
    //
        while (results.hasNext())
            {
            List<Node> L = results.next();
            RDFNode c = aClass.value( L );
            int cardinality = theCardinality.value( L ).as( Literal.class ).getInt();
            getClassProperties( cards, c )
                .addProperty
                    ( (Resource) aProperty.value( L ), forMin.choose( cardinality ), forMax.choose( cardinality ) );
            }
        }

    private static ClassProperties getClassProperties( Map<RDFNode, ClassProperties> cards, RDFNode c )
        {
        ClassProperties cp = cards.get( c );
        if (cp == null) cards.put( c, cp = new ClassProperties() );
        return cp;
        }

    public boolean isEmpty()
        { return cards.isEmpty(); }

    public ClassProperties cardinalities( Resource resource )
        { return cards.get( resource ); }
    }

/*
 * (c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The name of the author may not
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
