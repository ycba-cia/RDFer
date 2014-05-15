package com.hp.hpl.jena.eyeball.repairtools.doctor;

import com.hp.hpl.jena.eyeball.Doctor;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.*;

public class URIDoctor extends BaseDoctor implements Doctor {
	
	public URIDoctor() { }

	public void doctorModel( Model report, Model m, Model config ) 
		{
		this.input = m;
		this.repair = report;
		this.output = this.input;
		
		ResIterator it1 = repair.listSubjectsWithProperty( EYE.badURI );
		while( it1.hasNext() ) 
			{
			Resource curr = it1.nextResource();
			NodeIterator iter = repair.listObjectsOfProperty( curr, EYE.newValue );
			if ( iter.hasNext() )
				replaceURI( repair.listObjectsOfProperty( curr, EYE.badURI ).nextNode().asNode().getLiteralLexicalForm(), iter.nextNode().asNode().getLiteralLexicalForm(), output );
			}
		}
	
    private void replaceURI( String uri, String fixed, Model m ) 
		{    	
		StmtIterator it = ( (OntModel)m ).getBaseModel().listStatements();
		Model toDel = ModelFactory.createDefaultModel();
		Model toAdd = ModelFactory.createDefaultModel();
		while ( it.hasNext() )
			{
			Statement s = it.nextStatement();
			boolean deleted = false;
			String currtest = null;
			if ( s.getSubject().isURIResource() )
				currtest = s.getSubject().asNode().getURI();
			else
				currtest = s.getSubject().asNode().getBlankNodeLabel();
			if ( currtest.equals( uri ) )
				{
				deleted = true;
				toDel.add( s );
				s = toAdd.createStatement( m.createResource( fixed ), s.getPredicate(), s.getObject() );
				toAdd.add( s );
				}
			if ( s.getObject().isURIResource())
				if ( s.getObject().asNode().getURI().equals( uri ) )
					{
					if ( !deleted )
						toDel.add( s );
					else // s was already addded; we don't want a half repaired version sitting in the model!
						toAdd.remove( s );
					toAdd.add( m.createResource( fixed ), s.getPredicate(), s.getObject() );
					}
			}
		m.remove( toDel );
		m.add( toAdd );
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