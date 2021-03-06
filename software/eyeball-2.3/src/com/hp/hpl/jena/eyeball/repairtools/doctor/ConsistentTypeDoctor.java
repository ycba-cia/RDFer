package com.hp.hpl.jena.eyeball.repairtools.doctor;

import com.hp.hpl.jena.eyeball.Doctor;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ConsistentTypeDoctor extends BaseDoctor implements Doctor {
	
	public ConsistentTypeDoctor() { }

	public void doctorModel( Model report, Model m, Model config ) 
		{
		this.input = m;
		this.repair = report;
		this.output = this.input;

    	ResIterator it = report.listSubjectsWithProperty( EYE.noConsistentTypeFor );
    	while( it.hasNext() ) 
    		{
    		Resource curr = it.nextResource();
    		RDFNode fix = report.listObjectsOfProperty( curr, EYE.repairType ).nextNode();
    		if ( fix.equals( EYE.removeType ) )
    			{
    			NodeIterator it2 = repair.listObjectsOfProperty( curr, EYE.statementRemoved );
    			while ( it2.hasNext() )
    				{
    				Resource bn = repair.createResource( it2.nextNode().asNode().getBlankNodeId() );
    				Resource subj = (Resource)repair.listObjectsOfProperty( bn, RDF.subject ).nextNode();
    				Property pred = input.createProperty( repair.listObjectsOfProperty( bn, RDF.predicate ).nextNode().asNode().getURI() );
    				RDFNode obj = repair.listObjectsOfProperty( bn, RDF.object ).nextNode();
    				output.remove( subj, pred, obj );
    				}
    			}
    		else
    			{
	    		RDFNode res = report.listObjectsOfProperty( curr, EYE.noConsistentTypeFor ).nextNode();
	    		Resource newClass = output.createResource();
	    		NodeIterator itb = ((OntModel)output).getBaseModel().listObjectsOfProperty( output.createResource( res.asNode().getURI() ), RDF.type );
	    		output.add( newClass, RDF.type, RDFS.Class );
	    		output.add( output.createResource( res.asNode().getURI() ), RDF.type, newClass );
	    		while ( itb.hasNext() )
	    			{
	    			RDFNode thisType = itb.nextNode();
	    			output.add( newClass, RDFS.subClassOf, thisType );
	    			output.remove( output.createStatement( output.createResource( res.asNode().getURI() ), RDF.type, thisType ) );
	    			}
    			}
    		}
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