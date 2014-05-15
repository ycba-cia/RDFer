package com.hp.hpl.jena.eyeball.repairtools.analysis;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class LiteralAnalysis extends BaseAnalysis implements Analysis {
	
	public LiteralAnalysis() { }
	
	private String findDatatype( String literal )
		{
		String datatype = "http://www.w3.org/2001/XMLSchema#"; // insert desired prefix here (shouldn't need to change - i.e. no config file option needed?!!
		if ( literal.matches( "[\\d]*" ) )
			datatype += "integer";
		else if ( literal.matches( "[+-]?[\\d]*[.]{0,1}[\\d]*" ) )
			datatype += "decimal";
		else if ( literal.matches( "(10|11|12|13|14|15|16|17|18|19|20)[\\d][\\d][-](0[1-9]|1[012])[-](0[1-9]|[12][0-9]|3[01])([-+]([0][\\d]|[1][01234]):[012345][\\d])?" ) )
			datatype += "date";
		else if ( literal.matches( "([01][\\d]|[2][0123]):[012345][\\d]:[012345][\\d]([-+]([0][\\d]|[1][01234]):[012345][\\d])?" ) )
			datatype += "time";
		else if ( literal.matches( "(10|11|12|13|14|15|16|17|18|19|20)[\\d][\\d][-](0[1-9]|1[012])[-](0[1-9]|[12][0-9]|3[01])[T]([01][\\d]|[2][0123]):[012345][\\d]:[012345][\\d]([-+]([0][\\d]|[1][01234]):[012345][\\d])?" ) )
			datatype += "dateTime";
		else if ( literal.matches( "true|false" ) )
			datatype += "boolean";
		else
			datatype = null; // No suitable pattern matched!
		return datatype;
		}
	
	public void analyse( Report r, Model m, Model config ) 
		{
		this.input = m;
		this.repair = r.model();
		
		ResIterator it1 = repair.listSubjectsWithProperty( EYE.badDatatypeURI );
		while( it1.hasNext() ) 
			{
			Resource curr = it1.nextResource();
    		String datatype = findDatatype( repair.listObjectsOfProperty( curr, EYE.onLiteral ).nextNode().asNode().getLiteralLexicalForm() );
    		if ( datatype == null )
    			{ // Find the default action defined in config
    			RDFNode defaultFix = config.listObjectsOfProperty( EYE.repairConfig, EYE.defaultLiteralFix ).nextNode();
    			if ( defaultFix.equals( EYE.defaultLanguage ) )
    				{
    				String language = config.listObjectsOfProperty( EYE.repairConfig, EYE.defaultLanguage ).nextNode().asNode().getLiteralLexicalForm();
    				repair.add( curr, EYE.repairConfidence, EYE.low )
    				  	  .add( curr, EYE.repairType, EYE.setLanguage )
    				  	  .add( curr, EYE.checkFix, RDF.object )
    				  	  .add( curr, EYE.newValue, language );
    				}
    			else
    				{
    				try
    					{
    					datatype = config.listObjectsOfProperty( EYE.repairConfig, EYE.defaultDatatype ).nextNode().asNode().getLiteralLexicalForm();
    					}
    				catch ( Exception e )
    					{
    					datatype = "http://www.w3.org/2001/XMLSchema#string"; // We can fall back to this if we absolutely have to!
    					}
    		        repair.add( curr, EYE.repairConfidence, EYE.moderate )
    				  	  .add( curr, EYE.repairType, EYE.setDatatype )
    				  	  .add( curr, EYE.checkFix, RDF.object )
    				  	  .add( curr, EYE.newValue, datatype );
    				}
    			}
    		else // Fix to this datatype
		        repair.add( curr, EYE.repairConfidence, EYE.moderate )
				  	  .add( curr, EYE.repairType, EYE.setDatatype )
				  	  .add( curr, EYE.checkFix, RDF.object )
				  	  .add( curr, EYE.newValue, datatype );
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