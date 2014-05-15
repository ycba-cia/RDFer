/*
	(c) Copyright 2005, 2006, 2007, 2008, Hewlett-Packard Development Company, LP
  	[See end of file]
  	$Id: Report.java,v 1.19 2009/01/20 09:23:08 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball;

import java.util.*;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.*;

/**
    A Report is a collection of ReportItems, each of which is an RDF subgraph
    rooted on a fresh bnode. It also contains a predicate registry, recording
    the predicates that will be used in the reports and their order of
    output preference for the text renderer, and a prefix mapping to be
    used for the report model.

    @author hedgehog
*/
public class Report
    {
    protected Model mitems;
    protected final PrefixMapping prefixes;

    protected final PredicateRegister register = new SimplePredicateRegister();

    /**
         Initialise this Report with an empty configuration.
    */
    public Report()
        { this( ModelFactory.createDefaultModel() ); }

    /**
        Initialise this Report with the specified configuration (currently unused).
    */
    private Report( Model config )
        {
        this.prefixes = PrefixMapping.Factory.create();
        this.mitems = ModelFactory.createDefaultModel(); 
        }
    
    // Added for repair conversion -- peter
    public Report setMitems( Model mitems )
    	{ 
    	this.mitems = mitems;
    	return this;
    	}

    /**
        Answer true iff there are no report items in this report.
    */
    public boolean valid()
        { return mitems.size() == 0; }

    /**
        Answer a model containing all the report items from this report. The
        model will have the prefix mappings that have been set using
        <code>setPrefixes</code>, with additional mappings from
        <code>PrefixMapping.Extended</code> and a binding of
        <b>eye</b> to the Eyeball URI.
    */
    public Model model()
        { 
        Model result = ModelFactory.createDefaultModel();
        result.add( mitems );
        result
            .withDefaultMappings( prefixes )
            .withDefaultMappings( PrefixMapping.Extended )
            .setNsPrefix( "eye", EYE.getURI() )
            ;
        return result;
        }

    /**
        Add the prefixes <code>pm</code> to this Report's prefix mapping.
        Only prefixes not already in the map are added (<i>ie</i>, this uses
        <code>PrefixMapping::withDefaultMappings</code>.
    */
    public void setPrefixes( PrefixMapping pm )
        { prefixes.withDefaultMappings( pm ); }

    /**
        Answer a new ReportItem for this Report. The ReportItem will then
        have report properties and values added to it. The item is not
        associated with any particular statement of the model.
    */
    public ReportItem createItem( )
        { return ReportItem.create( mitems ); }

    /**
        Answer a new ReportItem for this Report, associated with a statement
        <code>s</code> from the model under inspection. (The statement is
        stored as a reified statement in the Report model.)
    */
    public ReportItem createItem( Statement s )
        { return ReportItem.create( mitems, createReifiedStatement( s ) ); }

    /**
        Answer a Literal object from the report model, representing the integer
        value <code>n</code> as an <code>XSDDatatype.XSDinteger</code>.
    */
    public Literal integer( int n )
        { return mitems.createTypedLiteral( "" + n, XSDDatatype.XSDinteger ); }

    protected final Map<Statement, ReifiedStatement> statementMap = new HashMap<Statement, ReifiedStatement>();

    protected ReifiedStatement createReifiedStatement( Statement s )
        {
        ReifiedStatement found = statementMap.get( s );
        if (found == null)
            statementMap.put( s, found = mitems.createReifiedStatement( s ) );
        return found;
        }

    /**
        Answer the predicate registry associated with this Report.
    */
    public PredicateRegister getPredicateRegister()
        { return register; }

    /**
        Declare that the property <code>p</code> is a property used as
        a report property. Only declared properties will be displayed by the
        text renderer. Answer this Report object.
    */
    public Report declareProperty( Property p )
        { register.register(  p  ); return this; }

    /**
        Declare that both <code>p1</code> and <code>p2</code> are
        properties used as report properties. Further, if possible, the text
        renderer should display <code>p1</code> before <code>p2</code>.
        Answer this Report object.
    */
    public Report declareOrder( Property p1, Property p2 )
        { register.order( p1, p2 ); return this; }
    }

/*
    (c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
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
