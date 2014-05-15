/*
 	(c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved - see end of file.
 	$Id: ReportItem.java,v 1.15 2009/01/29 12:20:13 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball;

import java.util.*;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;

public class ReportItem
    {
    protected final Model mitems;

    protected final Resource self;

    public static ReportItem create( Model mitems )
        { return new ReportItem( mitems ); }

    public static ReportItem create( Model mitems, ReifiedStatement s )
        { return new ReportItem( mitems, s ); }

    protected ReportItem( Model mitems )
        { this.mitems = mitems; self = mitems.createResource(); }

    protected void ensureTyped()
        {
        self.addProperty( RDF.type, EYE.Item );
        }

    protected ReportItem( Model mitems, ReifiedStatement s )
        {
        this( mitems );
        self.addProperty( EYE.onStatement, s );
        }

    /**
        Add (property, resource) to the properties of this report item. Add
        (eye:mainProperty, property) as a metaproperty declaring that this
        is the "main" property of the item, ie the one to use as a key when
        rendering.
    */
    public ReportItem addMainProperty( Property property, RDFNode resource )
        {
        return addProperty( property, resource ).addProperty( EYE.mainProperty, property );
        }    

    /**
        Add (property, spelling) to the properties of this report item. Add
        (eye:mainProperty, property) as a metaproperty declaring that this
        is the "main" property of the item, ie the one to use as a key when
        rendering.
    */
    public ReportItem addMainProperty( Property property, String spelling )
        {
        return addProperty( property, spelling ).addProperty( EYE.mainProperty, property );
        }

    public ReportItem addProperty( Property p, String literal )
        {
        ensureTyped();
        self.addProperty( p, literal );
        return this;
        }
    
    public ReportItem addProperty( Property p, RDFNode r )
        {
        ensureTyped();
        self.addProperty( p, r );
        return this;
        }

    public ReportItem addType( Property hat, Resource type )
        {
        ensureTyped();
        Resource tim = (Resource) type.inModel( self.getModel() );
        self.addProperty( hat, tim );
        return this;
        }
    
    public ReportItem addSetProperty( Property p, List<? extends RDFNode> list )
        {
        ensureTyped();
        Model m = self.getModel();
        Resource x = m.createResource();
        self.addProperty( p, x );
        x.addProperty( RDF.type, EYE.Set );
        for (int i = 0; i < list.size(); i += 1) x.addProperty( RDFS.member, list.get(i) );
        return this;
        }

    public ReportItem addCardinality( int min, int max )
        {
        ensureTyped();
        Model m = self.getModel();
        Resource x = m.createResource();
        self.addProperty( EYE.cardinality, x );
        if (min > 0) x.addProperty( EYE.min, integer( min ) );
        if (max < Integer.MAX_VALUE) x.addProperty( EYE.max, integer( max ) );
        return this;
        }
    
    /**
        Create a new Resource hanging from this item with the
        <code>link</code> property.
    */
    public Resource createSub( Property link )
        {
        Resource sub = self.getModel().createResource();
        addProperty( link, sub );
        return sub;
        }
    
    public Literal integer( int n )
        { return ResourceFactory.createTypedLiteral( "" + n, XSDDatatype.XSDinteger ); }

    public Resource getStatement()
        { return self.getRequiredProperty( EYE.onStatement ).getResource(); }

    public RDFNode getRequiredProperty( Property p )
        { return self.getRequiredProperty( p ).getObject(); }

    public Set<RDFNode> getPropertyValues( Property p )
        { return self.listProperties( p ).mapWith( Statement.Util.getObject ).toSet(); }
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
