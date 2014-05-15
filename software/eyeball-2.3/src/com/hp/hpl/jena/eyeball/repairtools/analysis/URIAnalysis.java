package com.hp.hpl.jena.eyeball.repairtools.analysis;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.repairtools.*;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.swabunga.spell.engine.Word;

public class URIAnalysis extends BaseAnalysis implements Analysis {
	
	public URIAnalysis() { }
	
	public void analyse( Report r, Model m, Model config ) 
    	{
    	this.input = m;
    	this.repair = r.model();
    	
    	ResIterator it1 = repair.listSubjectsWithProperty( EYE.badURI );
    	while( it1.hasNext() ) 
    		{
    		Resource curr = it1.nextResource();
    		NodeIterator it2 = repair.listObjectsOfProperty( curr, EYE.forReason );
    		RDFNode uri = repair.listObjectsOfProperty( curr, EYE.badURI ).nextNode();
    		String work = uri.asNode().getLiteralLexicalForm();
    		while ( it2.hasNext() )
    			{
    			String fault = it2.nextNode().asNode().getURI();
    			repair.add( curr, EYE.checkFix, RDF.subject ); 
    			repair.remove( repair.listStatements( curr, EYE.newValue, work ) );
    			if ( fault.contains( "unrecognisedScheme" ) )
        			{ String fixed = analyseUnrecognisedScheme( config, curr, work );
        			}
    			else if ( fault.contains( "uriContainsSpaces" ) )
    				{
    				analyseHasSpaces( curr, work );
    				}
    			else if ( fault.contains( "uriNoHttpAuthority" ) )
    				{
    				analyseHasNoAuthority( curr, work );
    				}
    			else if ( fault.contains( "uriHasNoLocalname" ) )
        			{
    				analyseHasNoLocalName( curr, work );
        			}
    			else if ( fault.contains( "schemeShouldBeLowercase" ) )
    				{
    				analyseShouldBeLowerCase( curr, work );
    				}
    			else 
    				System.err.println( ">> Unknown badURI fault found - " + fault + ". No repair possible.");
    			}
    		}
    	r.setMitems( repair );
    	}

    private String analyseUnrecognisedScheme( Model config, Resource curr,  String work )
        {
        // Default scheme added
        String fixed;
        try
        	{ fixed = config.listObjectsOfProperty( EYE.defaultScheme ).nextNode().asNode().getLiteralLexicalForm(); } 
        catch ( NoSuchElementException e )
        	{ fixed = "default:///"; }
        if ( work.contains( ":" ) )
        	{ // Attempt to spellcheck the current scheme
        	String scheme = "";
        	for ( int i = 0 ; i < work.length(); i++)
        		if ( work.charAt( i ) == ':' )
        			break;
        		else
        			scheme += work.charAt( i );
        	NodeIterator it2b = config.listObjectsOfProperty( EYE.validScheme );
        	String wordList = "";
        	while ( it2b.hasNext() )
        		wordList += it2b.nextNode().asNode().getLiteralLexicalForm() + '\n';
        	GenericWordChecker check = new WordChecker( wordList );
        	Iterator<Word> sugg = check.spellCheck( scheme );
        	if ( sugg != null )
        		fixed = sugg.next().toString();
        	}
        
        if ( work.contains( ":" ) )
        	{
        	boolean trip = false;
        	for ( int i = 0; i < work.length(); i++ )
        		if (trip)
        			fixed += work.charAt( i );
        		else
        			if ( work.charAt( i ) == ':' )
        				trip = true;
        	}
        else
        	{
        	fixed += work;
        	}
        work = fixed;
        repair.add( curr, EYE.repairType, EYE.addDefaultScheme )
              .add( curr, EYE.repairConfidence, EYE.low )
              .add( curr, EYE.newValue, fixed );
        return fixed;
        }

    private void analyseHasSpaces( Resource curr, String work )
        {
        String domainpart = "";
        String localpart = "";
        boolean flag = false;
        for ( int i = 0; i < work.length(); i++ )
        	{ // Separate the domain and local parts of the URI if possible
        	if (!flag)
        		if ( work.charAt( i ) == '/' )
        			{
        			if ( i + 1 < work.length() )
        				if ( ( work.charAt( i+1 ) != '/' ) && ( work.charAt( i-1 ) != '/' ) )
        					{ // We are at a "/" and not a "//"
        					flag = true;
        					localpart += work.charAt(i);
        					}
        				else
        					domainpart += work.charAt(i);
        			}
        		else
        			domainpart += work.charAt(i);
        	else
        		localpart += work.charAt( i );
        	}
        String fixed = domainpart.replace( " ", "" ) + localpart.replace( " ", "%20" );
        work = fixed;
        repair.add( curr, EYE.repairType, EYE.removeSpaces )
        	  .add( curr, EYE.repairConfidence, EYE.moderate )
              .add( curr, EYE.newValue, fixed );
        }

    private void analyseHasNoAuthority( Resource curr, String work )
        {
        String fixed = work; 
        if ( !fixed.startsWith( "http://" ) )
        	fixed = "http://" + fixed.replaceFirst( "http:", "" );
        work = fixed;
        repair.add( curr, EYE.repairType, EYE.formHttpAuthority )
        	  .add( curr, EYE.repairConfidence, EYE.low )
              .add( curr, EYE.newValue, fixed );
        }

    private void analyseShouldBeLowerCase( Resource curr, String work )
        {
        work = work.toLowerCase();
        repair.add( curr, EYE.repairType, EYE.schemeToLowercase )
        	  .add( curr, EYE.repairConfidence, EYE.good )
              .add( curr, EYE.newValue, work );
        }

    private void analyseHasNoLocalName( Resource curr, String work )
        {
        // Strip off trailing '/', '\', ' ', '.', ',', '!' -- common typo chars that /should/ be invalid
        String fixed = "";
        char charAt = work.charAt( work.length() - 1 );
        if (charAt == '/' || charAt == '\\' || charAt == ' ' || charAt == '.' || charAt == ',' || charAt == '!')
        	for (int i = 0; i <= work.length() - 2; i += 1)	fixed += work.charAt( i );
        work = fixed;
        repair.add( curr, EYE.repairType, EYE.removeIllegalChars )
        	  .add( curr, EYE.repairConfidence, EYE.moderate )
              .add( curr, EYE.newValue, fixed );
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