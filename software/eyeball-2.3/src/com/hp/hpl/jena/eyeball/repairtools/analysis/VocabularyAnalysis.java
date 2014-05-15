package com.hp.hpl.jena.eyeball.repairtools.analysis;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Iterator;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.repairtools.*;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.swabunga.spell.engine.Word;

public class VocabularyAnalysis extends BaseAnalysis implements Analysis {
	
	public VocabularyAnalysis() { }
	
	private RDFNode curr;
    private Resource subject;
	private String nsBase = "mirror"; // Use default shipped set (if the file listing fails)
	private String[] namespaces = {"dc-dcmitype.rdf", "dc.rdf", "dc-terms.rdf", "owl.rdf", "rdf.rdf", "rdfs.rdf"};
	private String[] looseMappingArr, fullMappingArr;
	
	public void analyse( Report r, Model m, Model config ) 
		{
		this.input = m;
		this.repair = r.model();
		
	//	 Build list of mirrored namespaces
		File dir = new File( nsBase );
	    
		FilenameFilter rdfxml = new FilenameFilter() 
			{
	        public boolean accept( File dir, String name ) 
	        	{
	            return name.matches( ".*\\.rdf" );
	        	}
			};
	    String[] children = dir.list( rdfxml );
	    if ( children != null ) 
	    	namespaces = children.clone();
		
		StmtIterator it = repair.listStatements( (Resource)null, EYE.notFromSchema, (RDFNode)null );
		while ( it.hasNext() )
			{
			subject = it.nextStatement().getSubject();							   
			curr = repair.listObjectsOfProperty( subject, EYE.onResource ).nextNode();
			
			boolean fixed = false;
			
			for ( int j = 0; j < namespaces.length; j++ )
				{
				Model currNS = ModelFactory.createDefaultModel().read( "file:" + nsBase + "/" + namespaces[j] );
				StmtIterator iter = currNS.listStatements( (Resource)null, RDF.type, (RDFNode)null );
				while( iter.hasNext() )
					{
					Resource thisSubj = iter.nextStatement().getSubject();
					if ( thisSubj.getLocalName() != null )
						if ( thisSubj.getLocalName().equals( curr.asNode().getLocalName() ) )
	    				{
	    					fixURI( thisSubj.getURI() );
	    					fixed = true;
	    					break;
	    				}
					}
				if( fixed )
					break;
				}
			
			if ( !fixed )  // Fall back to spellcheck if we can't find it in a known NS.
				{
				Model wordModel = ModelFactory.createDefaultModel();
				for ( int a = 0; a < namespaces.length; a++ )
					wordModel.read("file:" + nsBase + "/" + namespaces[a]);
	
				ResIterator wordIt = wordModel.listSubjectsWithProperty( RDF.type );
				String fullwords = "";
				String loosewords = "";
				int size = wordModel.listSubjectsWithProperty( RDF.type ).toList().size();
				looseMappingArr = new String[size];
				fullMappingArr = new String[size];
				int i = 0;
				
				while ( wordIt.hasNext() )
					{
					Resource currWord = wordIt.nextResource();
					if ( currWord.isURIResource() )
						{
						fullwords += currWord.getURI() + '\n';
						loosewords += currWord.getLocalName() + '\n';
						looseMappingArr[i] = currWord.getLocalName();
						fullMappingArr[i] = currWord.getURI();
						i++;
						}
					}
				GenericWordChecker full = new WordChecker( fullwords );
				Iterator<Word> checkres = full.spellCheck( curr.asNode().getURI() );
				if ( checkres != null )
					fixURI( checkres.next().toString() );
				else
					{
					GenericWordChecker loose = new WordChecker( loosewords );
					checkres = loose.spellCheck( curr.asNode().getLocalName() );
					if ( checkres != null )
						{
						String replacement = checkres.next().toString();
						String fullreplacement = looseToFull( replacement );
						if ( fullreplacement.equals( replacement ) )
							replacement = curr.asNode().getNameSpace() + fullreplacement;
						else
							replacement = fullreplacement;
						fixURI( replacement );
						}
					else // Fail repair analysis
						repair.add( subject, EYE.repairConfidence, EYE.fail );
					}
				}
			fixed = false;
			}
		r.setMitems( repair );
		}
	
	private void fixURI( String replacement )
		{
		repair.add( subject, EYE.repairConfidence, EYE.moderate )
		  	  .add( subject, EYE.repairType, EYE.replaceURI )
		      .add( subject, EYE.checkFix, RDF.Statement )
		      .add( subject, EYE.newValue, replacement);
		}
	
	private String looseToFull(String looseSearch)
		{
		for ( int i = 0; i < looseMappingArr.length; i++ )
			{
			if ( looseMappingArr[i] != null )
				if ( looseMappingArr[i].equals( looseSearch ) )
					return fullMappingArr[i];
			}
		return looseSearch; // default to original looseSearch if it wasn't found
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