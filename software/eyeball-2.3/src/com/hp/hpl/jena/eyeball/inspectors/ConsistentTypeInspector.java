/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved - see end of file.
 	$Id: ConsistentTypeInspector.java,v 1.13 2009/01/29 12:20:00 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.inspectors;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.*;
import com.hp.hpl.jena.util.iterator.*;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;

/**
    The TypeInspector checks that subjects have consistent types, subject to
    the assumption that types not declared or inferred to be subtypes are
    in fact not subtypes.

    @author kers
*/
public class ConsistentTypeInspector extends InspectorBase
    {
    public ConsistentTypeInspector()
        {}
    
    public ConsistentTypeInspector( Resource root )
        {}

    protected OntModel om;
    protected Model assume;

    protected Set<Resource> seen = new HashSet<Resource>();
    protected Set<Set<Resource>> seenTypes = new HashSet<Set<Resource>>();

    protected final String ruleSet = "etc/owl-like.rules";
    
    public void begin( Report r, OntModel assume )
        { 
        r.declareOrder( EYE.noConsistentTypeFor, EYE.hasAttachedType );
        this.assume = assume;
        }
    
    public void inspectModel( Report r, OntModel toTest )
        {
        InfModel inf = createInferenceModel( toTest );
        om = ModelFactory.createOntologyModel( OntModelSpec.RDFS_MEM, inf );
        }
    
    protected InfModel createInferenceModel( Model toTest )
        {
        return ModelFactory.createInfModel
            ( createReasoner(), ModelFactory.createUnion( assume, toTest ) );
        }

    protected Reasoner createReasoner()
        {
        Literal True = ResourceFactory.createTypedLiteral( Boolean.TRUE );
        Resource configuration =
            ModelFactory.createDefaultModel()
            .createResource()
            .addProperty( ReasonerVocabulary.PROPenableTGCCaching, True )
            .addProperty( ReasonerVocabulary.PROPruleSet, ruleSet )
            ;
        return  GenericRuleReasonerFactory.theInstance().create( configuration );
        }

    // protected int count = 0;

    public void inspectStatement( Report r, Statement toTest )
        {
        Resource subject = toTest.getSubject();
        if (seen.add( subject ))
            {
            // System.err.println( ">> #" + ++count + ": " + subject );
            long base = System.currentTimeMillis();
            Set<Resource> types = getTypesFor( subject );
            long t1 = System.currentTimeMillis();
            // System.err.println( "]]  #types: " + types );
            if (seenTypes.contains( types ))
                {
                // System.err.println( "]]  seen: " + (t1 - base) + "ms" );
                }
            else
                {
                seenTypes.add( types );
                if (types.size() > 0 && noConsistentSubtype( types ))
                    {
                    long t2 = System.currentTimeMillis();
                    // System.err.println( "]] unseen, fail: " + (t1 - base) + "ms, " + (t2 - t1) + "ms" );
                    createReportItem( r, subject, types );
                    }
                else
                    {
                    long t2 = System.currentTimeMillis();
                    // System.err.println( "]]  unseen, OK: " + (t1 - base) + "ms, " + (t2 - t1) + "ms" );
                    }
                }
            }
        }

    protected Set<Resource> getTypesFor( Resource subject )
        { 
        return asResource( subject )
            .listProperties( RDF.type )
            .mapWith( Statement.Util.getObject )
            .filterDrop( isAnon)
            .mapWith( ensureResource )
            .toSet(); 
        }
    
    private static final Map1<RDFNode, Resource> ensureResource = new Map1<RDFNode, Resource>()
        {
        public Resource map1( RDFNode r ) { return (Resource) r; }   
        };

    private static final Filter<RDFNode> isAnon = new Filter<RDFNode>()
        {
        public boolean accept( RDFNode o ) { return o.isAnon(); }
        };

    protected boolean noConsistentSubtype( Set<Resource> types )
        { return consistentSubtypesOf( types ).isEmpty(); }

    public Set<? extends Resource> consistentSubtypesOf( Resource r )
        { Set<Resource> types = getTypesFor( r );
        return types.size() < 2 ? types : consistentSubtypesOf( types ); }

    /**
        Precondition: types.size > 0.
    */
    protected Set<OntClass> consistentSubtypesOf( Set<? extends Resource> types )
        {
        // System.err.println( ">> cSO of " + types );
        Iterator<? extends Resource> it = types.iterator();
        Set<OntClass> working = nextTypes( it );
        while (working.size() > 0 && it.hasNext())
            {
            Set<OntClass> nextTypes = nextTypes( it );
            working.retainAll( nextTypes );
            // System.err.println( ">> working := " + working );
            }
        return working;
        }

    protected Set<OntClass> nextTypes( Iterator <? extends Resource>it )
        { 
        OntClass c = it.next().as( OntClass.class );
        Set<OntClass> result = c.listSubClasses().toSet();
        result.add( c );
        // System.err.println( ">> type: " + next + " yeilds " + result );
        return result; 
        }

    protected void createReportItem( Report r, Resource subject, Set<? extends Resource> types )
        {
        ReportItem item = r.createItem().addMainProperty( EYE.noConsistentTypeFor, subject );
        Iterator<? extends Resource> it = types.iterator();
        while (it.hasNext()) item.addType( EYE.hasAttachedType, it.next() );
        }

    protected OntResource asResource( Resource r )
        { return r .inModel( om ) .as( OntResource.class ); }
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