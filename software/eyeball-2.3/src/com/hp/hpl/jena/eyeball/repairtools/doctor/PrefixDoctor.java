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

public class PrefixDoctor extends BaseDoctor implements Doctor {
	
	public PrefixDoctor() { }
	
	private Model config;

	private String getConfigsPrefixFromUri ( String uri )
		{
		ResIterator subjects = config.listSubjectsWithProperty( EYE.nsURI, config.createLiteral( uri ) );
		if ( subjects.hasNext() )
			{
			StmtIterator prefixes = config.listStatements( subjects.nextResource(), EYE.prefix, (RDFNode)null );
			if ( prefixes.hasNext() )
				return prefixes.nextStatement().getObject().asNode().getLiteralLexicalForm();
			}
		return null; // Return null otherwise
		}

	private String getConfigsUriFromPrefix ( String pre )
		{
		ResIterator subjects = config.listSubjectsWithProperty( EYE.prefix, config.createLiteral( pre ) );
		if ( subjects.hasNext() )
			{
			StmtIterator uris = config.listStatements( subjects.nextResource(), EYE.nsURI, (RDFNode)null );
			if ( uris.hasNext() )
				return uris.nextStatement().getObject().asNode().getLiteralLexicalForm();
			}
		return null; // Return null otherwise
		}
	
	private void changeNsUri( String from, String to )
		{
		/*
		 *   o Change all usages of URI 'from' to 'to'
		 *   o Set the prefix of 'from' to be that of 'to' 
		 */
		Model toAdd = ModelFactory.createDefaultModel();
		Model toDel = ModelFactory.createDefaultModel();
		Resource newUri = output.createResource( to );
		StmtIterator it = output.listStatements( output.createResource( from ), null, (RDFNode)null );
		while ( it.hasNext() )
			{
			Statement curr = it.nextStatement();
			toDel.add( curr );
			toAdd.add( newUri, curr.getPredicate(), curr.getObject() );
			}
		
		it = output.listStatements( null, null, output.createResource( from ) );
		while ( it.hasNext() )
			{
			Statement curr = it.nextStatement();
			toDel.add( curr );
			toAdd.add( curr.getSubject(), curr.getPredicate(), newUri );
			}
		
		output.remove( toDel );
		output.add( toAdd );
		
		String prefix = output.getNsURIPrefix( from );
		output.setNsPrefix( prefix, to );
		}
	
	private void changeNsPrefix( String from, String to )
		{
		String uri = output.getNsPrefixURI( from );
		output.setNsPrefix( to, uri );
		}
	
	public void doctorModel( Model report, Model m, Model config ) 
		{
		this.input = m;
		this.repair = report;
		this.output = this.input;
		this.config = config;
		
		ResIterator it = repair.listSubjectsWithProperty( EYE.repairType, EYE.replaceNamespace );
		while ( it.hasNext() )
			{
			/*
			 * We are able to make one or more of the following assumptions;
			 * (1)	o The Prefix is probably desired                                       \\
			 * (3)	o The URI is likely misspelt                                          ===>  Work these with the precedence specified
			 * (2)	o The URI is right, but the user mistakenly used a 'reserved' prefix   //
			 */
			Resource curr = it.nextResource();
			String prefix = repair.listObjectsOfProperty( curr, EYE.onPrefix ).nextNode().asNode().getLiteralLexicalForm();
			String currUri = output.getNsPrefixURI( prefix );
			String configsUri = getConfigsUriFromPrefix( prefix );
			String configsPre = null;
			if ( currUri != null )
				configsPre = getConfigsPrefixFromUri( currUri );
			if ( configsUri != null ) // Change the URI to a preferred one
				changeNsUri( currUri, configsUri );
			else if ( configsPre != null ) // Use that URI, but set the prefix according to config
				changeNsPrefix( prefix, configsPre );
			else // Use the 'expected' URI
				output.setNsPrefix( prefix, repair.listObjectsOfProperty( curr, EYE.expected ).nextNode().asNode().getLiteralLexicalForm() );
			}
		
		it = repair.listSubjectsWithProperty( EYE.repairType, EYE.removeDuplicatePrefixes );
		while ( it.hasNext() )
			{
			Resource curr = it.nextResource();
			NodeIterator it2 = repair.listObjectsOfProperty( curr, EYE.onPrefix );
    		String uri = repair.listObjectsOfProperty( curr, EYE.multiplePrefixesForNamespace ).nextNode().asNode().getLiteralLexicalForm();
    		if (it2.hasNext())
	    		{
    			boolean oneKept = false;
	    		while ( it2.hasNext() ) 
	    			{
	    			RDFNode curra = it2.nextNode();
	    			if ( m.getNsPrefixURI( curra.asNode().getLiteralLexicalForm() ) != null )
		    			if ( m.getNsPrefixURI( curra.asNode().getLiteralLexicalForm() ).equals( uri ) ) 	// The prefix is still used in the model
		    				{
		    				if ( oneKept )
			    				m.removeNsPrefix( curra.asNode().getLiteralLexicalForm() );
		    				else
			    				oneKept = true;
		    				}
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