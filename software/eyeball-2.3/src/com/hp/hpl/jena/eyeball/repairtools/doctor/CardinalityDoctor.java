package com.hp.hpl.jena.eyeball.repairtools.doctor;

import com.hp.hpl.jena.eyeball.Doctor;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class CardinalityDoctor extends BaseDoctor implements Doctor 
	{
	
	public CardinalityDoctor() { }

	public void doctorModel( Model report, Model m, Model config ) 
		{
		this.input = m;
		this.repair = report;
		this.output = this.input;
		
		StmtIterator it = repair.listStatements(null, EYE.cardinalityFailure, (RDFNode)null);
		
		while ( it.hasNext() )
			{
			Statement currFailure = it.nextStatement();
			Resource subj = currFailure.getSubject();
			NodeIterator it2 = repair.listObjectsOfProperty( subj, EYE.repairType );
			while ( it2.hasNext() )
				{
				RDFNode curr = it2.nextNode();
				if ( curr.equals( EYE.decreaseNumProperties ) )
					{
					NodeIterator it3 = repair.listObjectsOfProperty( subj, EYE.statementRemoved );
					while ( it3.hasNext() )
						{
						Resource remStat = (Resource)it3.nextNode();
						Resource remS = (Resource)repair.listObjectsOfProperty( remStat, RDF.subject ).nextNode();
						Property remP = output.createProperty( repair.listObjectsOfProperty( remStat, RDF.predicate ).nextNode().asNode().getURI() );
						RDFNode remO = repair.listObjectsOfProperty( remStat, RDF.object ).nextNode();
						output.remove( remS, remP, remO );
						}
					}
				else if ( curr.equals( EYE.increaseNumProperties ) )
					{
					Resource addStat = (Resource)repair.listObjectsOfProperty( subj, EYE.statementAdded ).nextNode();
					Resource addS = (Resource)repair.listObjectsOfProperty( addStat, RDF.subject ).nextNode();
					Property addP = output.createProperty( repair.listObjectsOfProperty( addStat, RDF.predicate ).nextNode().asNode().getURI() );
					int target = Integer.parseInt( repair.listObjectsOfProperty( repair.createResource( repair.listObjectsOfProperty( subj, EYE.cardinality ).nextNode().asNode().getBlankNodeId() ), EYE.min ).nextNode().asNode().getLiteralLexicalForm() );
					int start = Integer.parseInt( repair.listObjectsOfProperty( subj, EYE.numValues ).nextNode().asNode().getLiteralLexicalForm() );
					for ( int i = start; i < target; i++ )
						output.add( addS, addP, output.createResource() );
					}
				else if ( curr.equals( EYE.increaseCardinality ) )
					{
					int target = Integer.parseInt( repair.listObjectsOfProperty( subj, EYE.max ).nextNode().asNode().getLiteralLexicalForm() );
					int minCard = Integer.parseInt( repair.listObjectsOfProperty( repair.createResource( repair.listObjectsOfProperty( subj, EYE.cardinality ).nextNode().asNode().getBlankNodeId() ), EYE.min ).nextNode().asNode().getLiteralLexicalForm() );
					Resource cName = output.createResource( ((OntModel)output).getBaseModel().listObjectsOfProperty( output.createResource( repair.listObjectsOfProperty( subj, EYE.onType).nextNode().asNode().getURI() ), RDFS.subClassOf ).nextNode().asNode().getBlankNodeId() );
					StmtIterator it3 = output.listStatements( cName, OWL.cardinality, (RDFNode)null );
					if ( it3.hasNext() )
						{
						output.remove( it3 );
						output.add( cName, OWL.minCardinality, output.createTypedLiteral( "" + minCard, "http://www.w3.org/2001/XMLSchema#integer" ) );
						}
					else
						output.remove( output.listStatements( cName, OWL.maxCardinality, (RDFNode)null ) );
					output.add( cName, OWL.maxCardinality, output.createTypedLiteral( "" + target, "http://www.w3.org/2001/XMLSchema#integer" ) );
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