package com.hp.hpl.jena.eyeball.repairtools.analysis;

import java.util.*;

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
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class CardinalityAnalysis extends BaseAnalysis implements Analysis {
	
	public CardinalityAnalysis() { }
	
	private int strToInt( String s )
		{
		int val = 0;
		int decPlace = 1;
		
		for ( int i = s.length() - 1; i >= 0; i-- )
			{
			val += (s.charAt(i) - 48) * decPlace;
			decPlace *= 10;
			}
		
		return val;
		}
	
	public void analyse( Report r, Model m, Model config ) 
		{
		this.input = m;
		this.repair = r.model();
		
		ResIterator it1 = repair.listSubjectsWithProperty( EYE.cardinalityFailure );
		while( it1.hasNext() ) 
			{
			Resource curr = it1.nextResource();
			RDFNode type = repair.listObjectsOfProperty( curr, EYE.onType ).nextNode();
			RDFNode prop = repair.listObjectsOfProperty( curr, EYE.onProperty ).nextNode();
			RDFNode b = repair.listObjectsOfProperty( curr, EYE.cardinality ).nextNode();
			RDFNode maxCardR = null;
			RDFNode minCardR = null;
			try 
				{
				maxCardR = repair.listObjectsOfProperty( repair.createResource( b.asNode().getBlankNodeId() ), EYE.max ).nextNode();
				}
			catch (NoSuchElementException e) {}
			finally
				{
				try
					{
					minCardR = repair.listObjectsOfProperty( repair.createResource( b.asNode().getBlankNodeId() ), EYE.min ).nextNode();
					}
				catch (NoSuchElementException e) {}
				
				if ( maxCardR == null )		// Set these equal if the Eyeball report didn't specify
					maxCardR = minCardR;	//  (see below - this *shouldn't* cause an issue - send me
				else if ( minCardR == null )//	  a test case if it does ;o) )
					minCardR = maxCardR;
				}
			RDFNode numValsR = repair.listObjectsOfProperty( curr, EYE.numValues ).nextNode();
			
			int maxCard = strToInt( maxCardR.asNode().getLiteralLexicalForm() );
			int numVals = strToInt( numValsR.asNode().getLiteralLexicalForm() );
			
			NodeIterator it1b = m.listObjectsOfProperty( m.createResource( type.asNode().getURI() ), RDFS.subClassOf );
			while (it1b.hasNext() )
				{
				b = it1b.nextNode();
	    		try
	    			{
	    			if ( b.isAnon() )
	    				{
		    			if ( m.listObjectsOfProperty( m.createResource( b.asNode().getBlankNodeId() ), OWL.onProperty ).nextNode().equals( prop ) )
		    				break;
	    				}
	    			else
	    				if ( m.listObjectsOfProperty( m.createResource( b.asNode().getURI() ), OWL.onProperty ).nextNode().equals( prop ) )
		    				break;
	    			} 
	    		catch (java.util.NoSuchElementException e) 
	    			{ // Not all statements returned are going to match the search above;
	    			} //  don't complain / warn about it!
				}
			
			// This looks misleading, but it will not be called if numVals > maxCard and owl:maxCardinality is not specified in the model!
			if ( numVals > maxCard )
				{ // Increase owl:maxCardinality as needed, unless we can remove some invalid type declarations...
				// Get the rdfs:members of this failure's eye:values eye:Set
				List<String> types = new ArrayList<String>();
				List<Integer> typeCount = new ArrayList<Integer>();
				NodeIterator it1c = repair.listObjectsOfProperty( repair.createResource( repair.listObjectsOfProperty( curr, EYE.values ).nextNode().asNode().getBlankNodeId() ), RDFS.member);
				String defaultURI = "http://www.w3.org/2001/XMLSchema#String"; // This will be used later too (config file it??)
				
				while ( it1c.hasNext() )
					{
					RDFNode member = it1c.nextNode();
					String uri = member.asNode().getLiteralDatatypeURI();
					if ( uri == null )
						uri = defaultURI; // Default datatype URI
					if ( types.contains( uri ) )
						{
						int idx = types.lastIndexOf( uri );
						typeCount.set( idx , Integer.parseInt( typeCount.get( idx ).toString() ) + 1 );
						}
					else
						{
						types.add( uri );
						typeCount.add( 1 );
						}
					}
				Iterator<Integer> it1d = typeCount.iterator();
				int largest = 0;
				while ( it1d.hasNext() )
					{
					int tested = Integer.parseInt( it1d.next().toString() );
					if ( largest < tested )
						largest = tested;
					}
				String decided = types.get( typeCount.indexOf( largest ) );
				// Now remove statements where their object is not of this type from this cardinality failure
				StmtIterator it1e = input.listStatements( input.createResource( repair.listObjectsOfProperty( curr, EYE.cardinalityFailure ).nextNode().asNode().getURI() ), input.createProperty( repair.listObjectsOfProperty( curr, EYE.onProperty ).nextNode().asNode().getURI() ), (RDFNode)null );
				Model remStat = ModelFactory.createDefaultModel();
				while ( it1e.hasNext() && numVals > maxCard ) // No sense removing more statements than we absolutely have to
					{
					Statement testing = it1e.nextStatement();
					String datatype = testing.getObject().asNode().getLiteralDatatypeURI();
					if ( datatype == null )
						datatype = defaultURI;
					if ( datatype != decided )
						{
						Resource bn = repair.createResource();
						remStat.add( curr, EYE.statementRemoved, bn )
							    .add( bn, RDF.type, RDF.Statement )
							    .add( bn, RDF.subject, testing.getSubject() )
							    .add( bn, RDF.predicate, testing.getPredicate() )
							    .add( bn, RDF.object, testing.getObject() );
						numVals--;
						}
					}
				
				if ( remStat.size() > 0 )
					// We fixed it above, add repairType and statementRemoveds
					repair.add( curr, EYE.repairConfidence, EYE.moderate )
						  .add( curr, EYE.repairType, EYE.decreaseNumProperties )
			  			  .add( curr, EYE.checkFix, RDF.Statement )
			  			  .add( remStat );
				
				if ( numVals > maxCard )
					// If we still have an un-ok cardinality, tell the doctor to increase the cardinality (as well!)
					repair.add( curr, EYE.repairConfidence, EYE.moderate )
			  			  .add( curr, EYE.repairType, EYE.increaseCardinality )
			  			  .add( curr, EYE.max, repair.createTypedLiteral( numVals + "", "http://www.w3.org/2001/XMLSchema#integer" ) )
			  			  .add( curr, EYE.checkFix, RDF.Statement );
				} 
			else
				{ // Add minimal bNode prop's to class usage up to minCard
				Resource x = m.createResource( repair.listObjectsOfProperty( curr, EYE.cardinalityFailure ).nextNode().asNode().getURI() );			
				Resource stmtBnode = repair.createResource();
				repair.add( curr, EYE.repairConfidence, EYE.low )
		  			  .add( curr, EYE.repairType, EYE.increaseNumProperties )
		  			  .add( curr, EYE.checkFix, RDF.object )
		  			  .add( curr, EYE.statementAdded, stmtBnode )
	    			  .add( stmtBnode, RDF.type, RDF.Statement )
	    			  .add( stmtBnode, RDF.object, m.createResource() )
	    			  .add( stmtBnode, RDF.predicate, m.createProperty( prop.asNode().getURI() ) )
	    			  .add( stmtBnode, RDF.subject, x );
				}
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