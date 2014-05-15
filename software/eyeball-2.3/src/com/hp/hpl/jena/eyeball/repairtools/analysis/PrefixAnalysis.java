package com.hp.hpl.jena.eyeball.repairtools.analysis;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;

public class PrefixAnalysis extends BaseAnalysis implements Analysis {
	
	public PrefixAnalysis() { }
	
	public void analyse( Report r, Model m, Model config ) 
	{
	this.input = m;
	this.repair = r.model();
	
	ResIterator it1 = repair.listSubjectsWithProperty( EYE.badNamespaceURI );
	while( it1.hasNext() ) 
		{
		Resource curr = it1.nextResource();
		repair.add( curr, EYE.repairConfidence, EYE.low )
		  	  .add( curr, EYE.repairType, EYE.replaceNamespace )
		  	  .add( curr, EYE.checkFix, EYE.namespacePrefix );
		}
	
	ResIterator it2 = repair.listSubjectsWithProperty( EYE.multiplePrefixesForNamespace );
	while ( it2.hasNext() )
    	{
		Resource curr = it2.nextResource();
		repair.add( curr, EYE.repairConfidence, EYE.good )
	  	  	  .add( curr, EYE.repairType, EYE.removeDuplicatePrefixes )
	  	  	  .add( curr, EYE.checkFix, EYE.namespacePrefix );
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