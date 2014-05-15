/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved - see end of file.
 	$Id: PropertyInspector.java,v 1.7 2009/03/20 15:42:05 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.inspectors;

import java.util.*;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.*;

public class PropertyInspector extends InspectorBase implements Inspector
    {
    public PropertyInspector()
        { addBuiltinProperties(); }
    
    public PropertyInspector( Resource root )
        { this(); }

    public void begin( Report r, OntModel assume )
        {
        r.declareProperty( EYE.unknownPredicate );
        addProperties( assume ); 
        }

    public void inspectModel( Report r, OntModel model )
        {
        addProperties( model );
//        for (Iterator<OntModel> models = model.listSubModels(); models.hasNext();)
//            addProperties( models.next() );
        }
    
    private void addBuiltinProperties()
        {
        FileManager fm = FileManager.get();
        OntModel builtin = ModelFactory.createOntologyModel( OntModelSpec.RDFS_MEM_RDFS_INF );
        builtin.addSubModel( fm.loadModel( RDF.getURI() ) );
        builtin.addSubModel( fm.loadModel( RDFS.getURI() ) );
        addProperties( builtin );
        }
    
    private void addProperties( OntModel model )
        {
        for (Iterator<OntProperty> it = model.listOntProperties(); it.hasNext();) 
            addProperty( it.next() ); 
        }

    private void addProperty( OntProperty property )
        { knownProperties.add( property ); }
    
    protected Set<OntProperty> knownProperties = new HashSet<OntProperty>();
    
    public boolean knownProperty( Property p )
        { return knownProperties.contains( p ); }
    
    protected Set<Property> seen = new HashSet<Property>();
    
    public void inspectStatement( Report r, Statement s )
        {
        Property predicate = s.getPredicate();
        if (seen.add( predicate ) && !knownProperty( predicate ))
            r.createItem( s ).addMainProperty( EYE.unknownPredicate, predicate );
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