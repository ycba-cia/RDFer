/*
 * eyeConfig.java
 *
 * Created on August 8, 2006, 2007, 2008, 11:26 AM
 */

package com.hp.hpl.jena.eyeball.web;

import java.io.Serializable;
import java.util.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.query.*;

/**
 * @author peter
 */
public class WebEyeballConfig extends Object implements Serializable {
    
    Model config;
    
    String prefix, suffix;
    
    List<RDFNode> lastResult;
    
    public WebEyeballConfig() {
        config = ModelFactory.createDefaultModel().read( "file:etc/eyeball-config.n3", "N3" );
        prefix = "";
        suffix = "";
    }
    
    private String getString( ResultSet it, String target ) {
        lastResult = new ArrayList<RDFNode>();
        if ( !it.hasNext() )
            return prefix + suffix;
        else {
            String value = "";
            while ( it.hasNext() ) {
                RDFNode curr = it.nextSolution().get( target );
                lastResult.add( curr );
                value += prefix;
                if ( curr.isAnon() )
                    value += curr.asNode().getBlankNodeLabel();
                else if ( curr.isLiteral() )
                    value += curr.asNode().getLiteralLexicalForm();
                else if ( curr.isURIResource() )
                    value += curr.asNode().getURI();
                value += suffix;
            }
            return value;
        }
    }
    
    public void setPrefix( String prefix ) {
        this.prefix = prefix;
    }
    
    public void setSuffix( String suffix ) {
        this.suffix = suffix;
    }
    
    public String getInspectors() {
        String queryString = "PREFIX eye:     <http://jena.hpl.hp.com/Eyeball#> \n" +
                             "PREFIX rdf:        <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                             "SELECT ?sn \n" +
                             "WHERE \n" +
                             "  { ?inspector rdf:type      eye:Inspector ; \n" +
                             "               eye:shortName ?sn           . }";
        
        Query query = QueryFactory.create( queryString );
        QueryExecution qe = QueryExecutionFactory.create( query, config );
        ResultSet results = qe.execSelect();
        String output = getString( results, "?sn" );
        qe.close();
        output = output.substring( 0, output.length() - 1 );
        return output;
    }
    
    public String getAssumes() {
        String queryString = "PREFIX eye:     <http://jena.hpl.hp.com/Eyeball#> \n" +
                             "PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                             "SELECT ?name ?path \n" +
                             "WHERE \n" +
                             "  { ?target rdf:type      eye:mirror ; \n" +
                             "            eye:shortName ?name      . }";
        
        Query query = QueryFactory.create( queryString );
        QueryExecution qe = QueryExecutionFactory.create( query, config );
        ResultSet results = qe.execSelect();
        String output = getString( results, "?name" );
        qe.close();
        output = output.substring( 0, output.length() - 1 );
        return output;
    }
    
    public String getLabelsFromLastResult() {
        String output = "";
        for ( int i = 0; i < lastResult.size(); i++ ) {
            String curr = "none";
            if ( lastResult.get( i ).isAnon() )
                    curr = "[]";
                else if ( lastResult.get( i ).isLiteral() )
                    curr = "\"" + lastResult.get( i ).asNode().getLiteralLexicalForm() + "\"";
                else if ( lastResult.get( i ).isURIResource() )
                    curr = "<" + lastResult.get( i ).asNode().getURI() + ">";
            String queryString = "PREFIX eye:     <http://jena.hpl.hp.com/Eyeball#> \n" +
                                 "PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n" +
                                 "SELECT ?label \n" +
                                 "WHERE \n" +
                                   "  { ?target eye:shortName " + curr + " ; \n" +
                                   "            rdfs:label    ?label       . }";        
            Query query = QueryFactory.create( queryString );
            QueryExecution qe = QueryExecutionFactory.create( query, config );
            ResultSet results = qe.execSelect();
            
            if ( results.hasNext() )
                output += prefix + curr + "&nbsp;:&nbsp;" + results.nextSolution().get( "?label" ).asNode().getLiteralLexicalForm() + suffix;
            qe.close();
        }
        return output;
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