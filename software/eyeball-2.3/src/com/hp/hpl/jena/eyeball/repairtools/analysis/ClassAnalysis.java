package com.hp.hpl.jena.eyeball.repairtools.analysis;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class ClassAnalysis extends BaseAnalysis implements Analysis {
	
	public ClassAnalysis() { }
	
	public void analyse( Report r, Model m, Model config ) 
	{
	this.input = m;
	this.repair = r.model();
	
	Resource classType = ((OntModel)input).getProfile().CLASS();
	
	NodeIterator it = repair.listObjectsOfProperty( EYE.unknownClass );
	while( it.hasNext() ) 
		{
		RDFNode curr = it.nextNode();
		Resource subject = repair.listSubjectsWithProperty( EYE.unknownClass, curr ).nextResource();
    	Resource stmtBnode = repair.createResource();
		repair.add( subject, EYE.repairConfidence, EYE.moderate )
  			  .add( subject, EYE.repairType, EYE.defineClass )
  			  .add( subject, EYE.checkFix, RDF.Statement )
  			  .add( subject, EYE.statementAdded, stmtBnode )
			  .add( stmtBnode, RDF.type, RDF.Statement )
			  .add( stmtBnode, RDF.object, classType )
			  .add( stmtBnode, RDF.predicate, RDF.type )
			  .add( stmtBnode, RDF.subject, m.createResource( curr.asNode().getURI() ) );
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