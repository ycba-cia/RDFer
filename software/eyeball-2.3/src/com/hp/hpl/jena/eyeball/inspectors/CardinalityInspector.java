/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved - see end of file.
 	$Id: CardinalityInspector.java,v 1.21 2009/01/29 12:20:00 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.inspectors;

import java.util.*;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.cardinality.*;
import com.hp.hpl.jena.eyeball.vocabulary.*;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDF;

public class CardinalityInspector extends InspectorBase
    {
    public CardinalityInspector()
        {}
    
    public CardinalityInspector( Resource root )
        {}

    protected OntModel assume;
    
    public void begin( Report r, OntModel assume )
        {
        this.assume = assume;
        declareProperties( r );
        }

    public void inspectModel( Report r, OntModel m )
        { 
        map = CardinalityMap.create( ModelFactory.createUnion( assume, m ) );
        }
    
    private void declareProperties( Report r )
        {
        r.declareOrder( EYE.cardinalityFailure, EYE.onProperty );
        r.declareOrder( EYE.cardinalityFailure, EYE.onType );
        r.declareOrder( EYE.cardinalityFailure, EYE.numValues );
        r.declareOrder( EYE.cardinalityFailure, EYE.values );
        r.declareOrder( EYE.cardinalityFailure, EYE.cardinality );
        }
    
    protected final Set<Resource> seen = new HashSet<Resource>();
    
    public void inspectStatement( Report r, Statement toTest )
        {
        Resource subject = toTest.getSubject();
        if (seen.add( subject ))
            {
            StmtIterator typings = subject.listProperties( RDF.type );
            while (typings.hasNext())
                {
                Resource type = typings.nextStatement().getResource();
                ClassProperties cp = map.cardinalities( type );
                if (cp != null)
                    {
                    Set<Resource> properties = cp.onProperties();
                    for (Resource element : properties)
                        {
                        Property property = element.as( Property.class );
                        PropertyCardinality pc = cp.get( property );
                        List<RDFNode>list = subject.listProperties( property ) .mapWith( Statement.Util.getObject ).toList();
                        int count = list.size();
                        int min = pc.minCardinality(), max = pc.maxCardinality();
                        if (count < min || max < count)
                            {
                            r.createItem()
                                .addMainProperty( EYE.cardinalityFailure, subject )
                                .addProperty( EYE.onProperty, property )
                                .addProperty( EYE.onType, type )
                                .addCardinality( min, max )
                                .addProperty( EYE.numValues, r.integer( count ) )
                                .addSetProperty( EYE.values, list )
                                ;
                            }
                        }
                    }
                }
            }
        }

    protected CardinalityMap map;
    
    public CardinalityMap getCardinalityMap()
        { return map; }
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