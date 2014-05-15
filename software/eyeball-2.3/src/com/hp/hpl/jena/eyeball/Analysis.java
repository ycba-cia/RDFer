package com.hp.hpl.jena.eyeball;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.eyeball.repairtools.analysis.NullAnalysis;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;

import java.util.List;

public interface Analysis 
    {
	public void analyse( Report r, Model m, Model config );

    public static class Operations
	    {   
	    public static Analysis create( List<Analysis> analysisList )
	        {
	        int size = analysisList.size();
	        return
	            size == 0 ? new NullAnalysis()
	            : size == 1 ? (Analysis) analysisList.get(0)
	            : createAnalysisList( analysisList, size )
	            ;
	        }
	
	    private static Analysis createAnalysisList( List<Analysis> analysisList, final int size )
	        {
	        final Analysis [] analysis = analysisList.toArray( new Analysis[analysisList.size()] );
	        return new Analysis()
	            {
	            public void analyse( Report r, Model m, Model config )
	                {
	                for (int i = 0; i < size; i += 1) analysis[i].analyse( r, m, config );
	        		r.declareOrder( EYE.onLiteral, EYE.repairConfidence );
	        		r.declareOrder( EYE.onPrefix, EYE.repairConfidence );
	        		r.declareOrder( EYE.onProperty, EYE.repairConfidence );
	        		r.declareOrder( EYE.onResource, EYE.repairConfidence );
	        		r.declareOrder( EYE.onStatement, EYE.repairConfidence );
	        		r.declareOrder( EYE.onType, EYE.repairConfidence );
	        		r.declareOrder( EYE.forNamespace, EYE.repairConfidence );
	        		r.declareOrder( EYE.forDatatype, EYE.repairConfidence );
	        		r.declareOrder( EYE.forReason, EYE.repairConfidence );
	        		r.declareOrder( EYE.values, EYE.repairConfidence );
	        		r.declareOrder( EYE.repairConfidence, EYE.repairType );
	        		r.declareOrder( EYE.repairType, EYE.newValue );
	        		r.declareOrder( EYE.newValue, EYE.checkFix );
	        		r.declareOrder( EYE.checkFix, EYE.statementAdded );
	                }
	            };
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