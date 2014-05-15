package com.hp.hpl.jena.eyeball.repairtools.doctor;

import com.hp.hpl.jena.eyeball.Doctor;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class PropertyDoctor extends BaseDoctor implements Doctor {
	
	public PropertyDoctor() { }

	public void doctorModel( Model report, Model m, Model config ) 
		{
		this.input = m;
		this.repair = report;
		this.output = this.input;
		
		
    	ResIterator it = repair.listSubjectsWithProperty( EYE.unknownPredicate );
    	while ( it.hasNext() )
    		{
    		Resource curr = it.nextResource();
    		NodeIterator it2 = repair.listObjectsOfProperty( curr, EYE.newValue );
    		
    		if ( it2.hasNext() )
    			{
	    		// Replace all instances of statement predicate curr in input with replacement
	    		StmtIterator it3 = input.listStatements((Resource)null, input.createProperty( repair.listObjectsOfProperty( curr, EYE.unknownPredicate ).nextNode().asNode().getURI() ), (RDFNode)null);
	    		Model toDel = ModelFactory.createDefaultModel();
	    		Model toAdd = ModelFactory.createDefaultModel();
	    		String replacement = it2.nextNode().asNode().getLiteralLexicalForm();
	    		
	    		while( it3.hasNext() )
	    			{
	    			Statement torem = it3.nextStatement();
	    			toDel.add( torem );
	    			toAdd.add( torem.getSubject(), toAdd.createProperty( replacement ), torem.getObject() );
	    			}
	    		
	    		input.remove( toDel );
	    		input.add( toAdd );
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