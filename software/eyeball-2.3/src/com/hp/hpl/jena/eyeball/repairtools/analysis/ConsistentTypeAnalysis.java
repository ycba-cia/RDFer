package com.hp.hpl.jena.eyeball.repairtools.analysis;

import java.util.ArrayList;
import java.util.Iterator;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

public class ConsistentTypeAnalysis extends BaseAnalysis implements Analysis {
	
	public ConsistentTypeAnalysis() { }
	
	public void analyse( Report r, Model m, Model config ) 
	{
	this.input = m;
	this.repair = r.model();
	
	StmtIterator it = ((OntModel)input).getBaseModel().listStatements( null, RDF.type, (RDFNode)null );
	int[] typeCounts = new int[ ((OntModel)input).getBaseModel().listObjectsOfProperty( RDF.type ).toList().size() ];
	for ( int i = 0; i < typeCounts.length; i++ )
		typeCounts[i] = 0;
	ArrayList<String> types = new ArrayList<String>();
	while ( it.hasNext() )
		{
		String currType = it.nextStatement().getObject().asNode().getURI();
		if ( !types.contains( currType ) )
			types.add( currType );
		typeCounts[ types.indexOf( currType ) ]++;
		}
	
	ResIterator it2 = repair.listSubjectsWithProperty( EYE.noConsistentTypeFor );
	while( it2.hasNext() ) 
		{
		Resource curr = it2.nextResource();
		// Try and compare the number of usages of clashing classes; instruct to delete if one outweighs the other
		Resource from = input.createResource( repair.listObjectsOfProperty( curr, EYE.noConsistentTypeFor ).nextNode().asNode().getURI() );
		
		NodeIterator it3 = ((OntModel)input).getBaseModel().listObjectsOfProperty( from, RDF.type );
		ArrayList<String> l = new ArrayList<String>();
		String popular = null;
		int popCount = 0;
		while ( it3.hasNext() )
			{
			String test = it3.nextNode().asNode().getURI();
			int testCount = typeCounts[ types.indexOf( test ) ];
			if ( testCount > popCount )
				{
				popCount = testCount;
				if ( popular != null )
					l.add( popular );
				popular = test;
				}
			else
				l.add( test );
			}
		if ( ( popCount > 1 ) && ( l.size() > 0 ) ) // This is probably a moderately OK guess - is it worth deciding on any more strenuous properties?
			{
			repair.add( curr, EYE.repairConfidence, EYE.good )
				  .add( curr, EYE.repairType, EYE.removeType )
				  .add( curr, EYE.checkFix, RDF.Statement );
			Iterator<String> it4 = l.iterator();
			while ( it4.hasNext() ) // Remove all of the others!
				{
				Resource bn = repair.createResource();
				repair.add( curr, EYE.statementRemoved, bn )
				  	  .add( bn, RDF.type, RDF.Statement )
				  	  .add( bn, RDF.subject, from )
				  	  .add( bn, RDF.predicate, RDF.type )
				  	  .add( bn, RDF.object, input.createResource( it4.next() ) );
				}
			}
		else
			repair.add( curr, EYE.repairConfidence, EYE.good )
			  	  .add( curr, EYE.repairType, EYE.defineClass )
			  	  .add( curr, EYE.checkFix, RDF.Statement );
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