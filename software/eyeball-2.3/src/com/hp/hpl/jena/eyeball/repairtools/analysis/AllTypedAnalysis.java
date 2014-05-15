package com.hp.hpl.jena.eyeball.repairtools.analysis;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class AllTypedAnalysis extends BaseAnalysis implements Analysis {
	
	public AllTypedAnalysis() { }
	
	private Resource[] alreadySeen = {};
	
	private void addSeen( Resource in )
		{
		Resource[] out = new Resource[ alreadySeen.length + 1 ];
		for ( int i = 0; i < alreadySeen.length; i++ )
			out[i] = alreadySeen[i];
		out[alreadySeen.length] = in;
		alreadySeen = out;
		}
	
	private boolean hasBeenSeen( Resource in )
		{
		for ( int i = 0; i < alreadySeen.length; i++ )
			if ( alreadySeen[i].asNode().getURI().equals( in.asNode().getURI() ))
				return true;
		return false;
		}
	
	private boolean xSubClassOfY ( Resource x, Resource y )
		{
		NodeIterator it = input.listObjectsOfProperty( x, RDFS.subClassOf );
		if ( x.equals( y ) )
			return true;
		else
			while ( it.hasNext() )
				{
				RDFNode curr = it.nextNode();
				if ( !curr.equals( x ) )
					if ( curr.isURIResource() )
						{
						if ( xSubClassOfY( input.createResource( curr.asNode().getURI() ), y ) )
							return true;
						}
					else
						{
						if ( xSubClassOfY( input.createResource( curr.asNode().getBlankNodeId() ), y ) )
							return true;
						}
				}
		return false;
		}
	
	private Resource getSubbiestClass( Resource parent, Model in )
		{
		Resource result = parent;
		ResIterator it = in.listSubjectsWithProperty( RDFS.subClassOf, parent );
		while ( it.hasNext() )
			{
			Resource curr = it.nextResource();
			if ( !hasBeenSeen( curr ) )
				{
				addSeen( curr );
				Resource midresult = getSubbiestClass( curr, in );
				if ( midresult != null )
					result = midresult;
				}
			}
		
		for ( int i = 0; i < alreadySeen.length; i++ )
			if ( !xSubClassOfY( result, alreadySeen[i] ) )
				return null;
		return result;
		}
	
	private int getDepthOf( Resource node )
		{
		int greatestDepth = 1;
		NodeIterator it = input.listObjectsOfProperty( node, RDFS.subClassOf );
		while ( it.hasNext() )
			{
			RDFNode thisnode = it.nextNode();
			Resource curr = null;
			if ( thisnode.isURIResource() )
				curr = input.createResource( thisnode.asNode().getURI() );
			else
				curr = input.createResource( thisnode.asNode().getBlankNodeId() );
			if ( !curr.equals( node ) )
				{
				int temp = getDepthOf( curr );
				if ( temp > greatestDepth )
					greatestDepth = temp;
				}
			}
		return greatestDepth + 1;
		}
	
	private Model getLeaves( Model in )
		{
		Model out = ModelFactory.createDefaultModel();
		StmtIterator it = in.listStatements( null, RDFS.subClassOf, (RDFNode)null );
		int greatestDepth = 0;
		while ( it.hasNext() )
			{
			Statement curr = it.nextStatement();
			if ( in.listSubjectsWithProperty( RDFS.subClassOf, curr.getSubject() ).toList().size() <= 1 )
				{
				out.add( curr );
				out.add( input.listStatements( curr.getSubject(), null, (RDFNode)null ) );
				int temp = getDepthOf( curr.getSubject() );
				if ( temp > greatestDepth )
					greatestDepth = temp;
				}
			}
		it = out.listStatements();
		Model torem = ModelFactory.createDefaultModel();
		while ( it.hasNext() )
			{
			Statement curr = it.nextStatement();
			if ( getDepthOf( curr.getSubject() ) < greatestDepth )
				torem.add( curr );
			}
		out.remove( torem );
		return out;
		}
	
	private Resource getReallySubbiestClass ( Resource parent, Model in )
		{
		alreadySeen = new Resource[0];
		Resource result = getSubbiestClass( parent, in );
		if ( result == null) // Trim off bottom leaves and try again
			result = getReallySubbiestClass( parent, in.difference( getLeaves( in ) ) );
		return result;
		}
	
	
	public void analyse( Report r, Model m, Model config ) 
		{
		this.input = m;
		this.repair = r.model();
		// Remove the inference engine from the model - we don't want it replacing statements when we remove them!
		Resource subbiest = getReallySubbiestClass( RDFS.Resource, ModelFactory.createModelForGraph( input.getGraph() ) );
		if ( subbiest == null) // Fall back to Resource if worst comes to worst!
			subbiest = RDFS.Resource;
		
		NodeIterator it1 = repair.listObjectsOfProperty( EYE.hasNoType );
		while( it1.hasNext() ) 
			{
			RDFNode curr = it1.nextNode();
			
			Resource thisres = null;
			
			if ( curr.isURIResource() )
				thisres = repair.createResource( curr.asNode().getURI() );
			else
				thisres = repair.createResource( curr.asNode().getBlankNodeId() );
			
			thisres = (Resource)curr;
			
			Resource stmtBnode = repair.createResource();
			Resource subject = repair.listSubjectsWithProperty(EYE.hasNoType, curr).nextResource();
			repair.add( subject, EYE.repairConfidence, EYE.low )
				  .add( subject, EYE.repairType, EYE.addDefaultType )
				  .add( subject, EYE.checkFix, RDF.object )
				  .add( subject, EYE.statementAdded, stmtBnode )
				  .add( stmtBnode, RDF.type, RDF.Statement )
				  .add( stmtBnode, RDF.object, subbiest ) // The subbiest type found above
				  .add( stmtBnode, RDF.predicate, RDF.type )
				  .add( stmtBnode, RDF.subject, thisres );
			}
		r.setMitems( repair );
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