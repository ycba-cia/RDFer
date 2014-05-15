package com.hp.hpl.jena.eyeball.repairtools.doctor;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.inspectors.LiteralInspector;
import com.hp.hpl.jena.eyeball.repairtools.analysis.LiteralAnalysis;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

public class LiteralDoctor extends BaseDoctor implements Doctor 
	{
	
	public LiteralDoctor() { }
	
	public void doctorModel( Model report, Model m, Model config ) 
		{
		this.input = m;
		this.repair = report;
		this.output = this.input;
		boolean didRepair = false;
		
		ResIterator it = repair.listSubjectsWithProperty( EYE.repairType, EYE.setDatatype );
		while ( it.hasNext() )
			{
			Resource curr = it.nextResource();
    		String literal = repair.listObjectsOfProperty( curr, EYE.onLiteral ).nextNode().asNode().getLiteralLexicalForm();

    		Resource b = output.createResource( repair.listObjectsOfProperty( curr, EYE.onStatement ).nextNode().asNode().getBlankNodeId() );
    		Resource subject = output.createResource( repair.listObjectsOfProperty( b, RDF.subject ).nextNode().asNode().getURI() );
    		Property predicate = output.createProperty( repair.listObjectsOfProperty( b, RDF.predicate ).nextNode().asNode().getURI() );
    		String oldType = repair.listObjectsOfProperty( curr, EYE.badDatatypeURI ).nextNode().asNode().getLiteralLexicalForm();
    		String datatype = repair.listObjectsOfProperty( curr, EYE.newValue ).nextNode().asNode().getLiteralLexicalForm();
    		
    		output.remove( subject, predicate, output.createTypedLiteral( literal, oldType ) );
    		output.add( subject, predicate, output.createTypedLiteral( literal, datatype ) );
    		didRepair = true;
			}
		
		it = repair.listSubjectsWithProperty( EYE.repairType, EYE.setLanguage );
		while ( it.hasNext() )
			{
			Resource curr = it.nextResource();
    		String literal = repair.listObjectsOfProperty( curr, EYE.onLiteral ).nextNode().asNode().getLiteralLexicalForm();

    		Resource b = output.createResource( repair.listObjectsOfProperty( curr, EYE.onStatement ).nextNode().asNode().getBlankNodeId() );
    		Resource subject = output.createResource( repair.listObjectsOfProperty( b, RDF.subject ).nextNode().asNode().getURI() );
    		Property predicate = output.createProperty( repair.listObjectsOfProperty( b, RDF.predicate ).nextNode().asNode().getURI() );
    		String oldType = repair.listObjectsOfProperty( curr, EYE.badDatatypeURI ).nextNode().asNode().getLiteralLexicalForm();
    		String lang = repair.listObjectsOfProperty( curr, EYE.newValue ).nextNode().asNode().getLiteralLexicalForm();
    		
    		output.remove( subject, predicate, output.createTypedLiteral( literal, oldType ) );
    		output.add( subject, predicate, output.createLiteral( literal, lang ) );
    		didRepair = true;
			}
		
		if ( didRepair )
			{
			// Check the rest of the model for other instances of this issue and repair (LiteralInspector only reports one usage at a time)
			Report r = new Report();
	        LiteralInspector inspector = new LiteralInspector();
	        inspector.inspectModel( r, (OntModel)output );
	        for (StmtIterator itb = ((OntModel)output).getBaseModel().listStatements(); itb.hasNext();)
	            inspector.inspectStatement( r, itb.nextStatement() );
	        inspector.end( r );
	        
	        if( r.model().size() > 0 )
	        	{ // New items were found to report; analyse and doctor them
	        	LiteralAnalysis litA = new LiteralAnalysis();
	        	litA.analyse( r, output, config );
	        	LiteralDoctor litD = new LiteralDoctor();
	        	litD.doctorModel( r.model(), output, config );
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