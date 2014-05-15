/*
 (c) Copyright 2005 Hewlett-Packard Development Company, LP
 All rights reserved - see end of file.
 $Id: ModelQuery.java,v 1.1 2010/03/19 14:34:04 chris-dollin Exp $
 */

package com.hp.hpl.jena.extras.tools.query;

import java.util.*;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.query.Query;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
    A ModelQuery object holds a collection of (S P O) query triples. Each of the
    S P O nodes can be an ordinary Model RDFNode or a QueryVariable.
    When the ModelQuery is run over some Model, it returns a sequence of
    results; each result (a List) can be supplied to a Queryariable to extract
    the underlying result binding.
    
    @author kers
*/
public class ModelQuery
    {
    /**
        Factory method to create new ModelQuery objects.
    */
    public static ModelQuery create()
        { return create( QueryFactory.usual ); }
    
    /**
        Factory method to create a new, distinct QueryVariable for this ModelQuery.
        Note that QueryVariables are not exchangable between ModelQueries.
    */
    public QueryVariable createVariable()
        { return createVariable( "_v" + index ); }
    
    /**
        Factory method to create a new QueryVariable for this ModelQuery and
        give it a user-specified name.
    */
    public QueryVariable createVariable( String name )
        {
        QueryVariable result = already.get( name );
        if (result == null)
            {
            result  = new QueryVariable( this, name, index++ );
            variables.add( translate( result ) );
            already.put( name, result );
            }
        return result;
        }
    
    protected Map<String, QueryVariable> already = new HashMap<String, QueryVariable>();
    
    /**
        Add a new query triple (S P O) to this ModelQuery and return it.
    */
    public ModelQuery add( RDFNode S, RDFNode P, RDFNode O )
        {
        query.addMatch( translate( S ), translate( P ), translate( O ) );
        return this;
        }

    /**
        Factory method to create new ModelQuery objects with specialised
        embedded Query objects - exposed for testing purposes only.
    */
    public static ModelQuery create( QueryFactory qf )
        { return new ModelQuery( qf ); }

    /**
        Constructor for ModelQuery objects: initialise with the QueryFactory.
    */
    protected ModelQuery( QueryFactory qf )
        { query = qf.create(); }

    /**
         Add a new (S P O) query triple pattern specified by <code>s</code>. Really
         for testing purposes: note that it is not possible to beuild a statement with
         a QueryVariable as a property (a type inconvenience).
    */
    public ModelQuery add( Statement s )
        { return add( s.getSubject(), s.getPredicate(), s.getObject() ); }

    /**
        Answer the SPI node associated with the RDFNode <code>s</code>. This
        will be <code>s.asNode()</code> unless <code>s</code> is a QueryVariable,
        when it will be the associated variable node.
    */
    public Node translate( RDFNode s )
        {
        if (s instanceof QueryVariable)
            {
            QueryVariable qv = ((QueryVariable) s);
            if (qv.parent() != this) 
                throw new JenaException( "this query variable doesn't belong in this ModelQuery" );
            return Node.createVariable( qv.getId().toString() );
            }
        else
            return s.asNode();
        }

    /**
        Answer an iterator over the variable bindings which satisfy this query over
        the model <code>data</code>.
    */
    public ExtendedIterator<? extends List<Node>> run( Model data )
        {
        Node [] vars = variables.toArray( new Node[variables.size()] );
        return query.executeBindings( data.getGraph(),  vars );
        }

    /**
        Public for testing purposes only. This bit might go away.
    */
    public final Query query;

    private List<Node> variables = new ArrayList<Node>();
    
    private int index = 0;

    public interface QueryFactory
        {
        public Query create();
        
        public static final QueryFactory usual = new QueryFactory()
            { public Query create() { return new Query(); } };
        }
    }

/*
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP All rights
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