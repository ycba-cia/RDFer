/*
 	(c) Copyright 2008 Hewlett-Packard Development Company, LP
 	All rights reserved.
 	$Id: SparqlDrivenInspector.java,v 1.8 2009/01/29 13:39:20 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.inspectors;

import java.util.*;

import com.hp.hpl.jena.eyeball.Report;
import com.hp.hpl.jena.eyeball.util.FileInsertion;
import com.hp.hpl.jena.eyeball.vocabulary.*;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.iterator.Map1;

/**
    Draft SPARQL-driven inspector. Configures when constructed based on
    SPARQL strings attached to root.
    
 	@author kers
*/
public class SparqlDrivenInspector extends InspectorBase
    {
    public SparqlDrivenInspector()
        {}
    
    public SparqlDrivenInspector( Resource root )
        {
        allRequired.addAll( root.listProperties( EyeballAssembling.require ).mapWith( objectAsStrings ).toList() );
        allProhibited.addAll( root.listProperties( EyeballAssembling.prohibit ).mapWith( objectAsStrings ).toList() );
        generalForm( root.listProperties( EyeballAssembling.sparql ).mapWith( statementToResource ).toList() );
        }
    
    private void generalForm( List<Resource> listOfSparqls )
        {
        for (int i = 0; i < listOfSparqls.size(); i += 1)
            {
            Resource rootlet = listOfSparqls.get( i );
            String message = rootlet.getProperty( EyeballAssembling.message ).getString();
            Statement require = rootlet.getProperty( EyeballAssembling.require );
            Statement prohibit = rootlet.getProperty( EyeballAssembling.prohibit );
            if (prohibit != null) allProhibited.add( new KV( prohibit.getString(), message ) );
            if (require != null) allRequired.add( new KV( require.getString(), message ) );
            }
        }

    static class KV
        {
        String query;
        String message;
        
        KV( String query, String message)
            { this.query = query; this.message = message; }
        }
    
    static final Map1<Statement, KV> objectAsStrings = new Map1<Statement, KV>() 
        {
        public KV map1( Statement s )
            { 
            String query = s.getObject().asNode().getLiteralLexicalForm();
            return new KV( query, query );
            }
        };
        
    static final Map1<Statement, Resource> statementToResource = new Map1<Statement, Resource>() 
        {
        public Resource map1( Statement s ) { return s.getResource(); }
        };
        
    protected final List<KV> allRequired = new ArrayList<KV>();
    
    protected final List<KV> allProhibited = new ArrayList<KV>();
    
    public void begin( Report r, OntModel assume )
        {
        r.declareProperty( EYE.sparqlRequireFailed );
        r.declareProperty( EYE.sparqlProhibitFailed );
        }

    public void inspectModel( Report r, OntModel m )
        { 
        check( r, m, allRequired, true, EYE.sparqlRequireFailed );
        check( r, m, allProhibited, false, EYE.sparqlProhibitFailed );
        }

    private void check( Report r, OntModel m, List<KV> demands, boolean present, Property problem )
        {
        for (int i = 0; i < demands.size(); i += 1)
            {
            KV demand = demands.get( i );
            if (hasSolutions( demand.query, m ) != present)
                r.createItem().addMainProperty( problem, demand.message );
            }
        }

    private boolean hasSolutions( String queryTemplate, OntModel m )
        {
        Query q = QueryFactory.create( getFileInsertion().insert( queryTemplate ) );
        QueryExecution qe = QueryExecutionFactory.create( q, m.getBaseModel() );
        ResultSet results = qe.execSelect();
        boolean result = results.hasNext();
        qe.close();
        return result;
        }
    
    /**
        Subclasses over-ride if they want a different file insertion (eg, for testing).
        (You don't need a new one on every call; it's just convenient in this default
        case.)
    */
    protected FileInsertion getFileInsertion()
        { return new FileInsertion(); }
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