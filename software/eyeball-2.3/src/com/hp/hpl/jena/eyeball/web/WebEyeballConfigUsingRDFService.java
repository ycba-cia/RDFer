/*
 * eyeConfig.java
 *
 * Created on August 8, 2006, 2007, 2008, 11:26 AM
 */

package com.hp.hpl.jena.eyeball.web;

import java.io.*;
import java.net.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.eyeball.vocabulary.EYESVC;
import com.hp.hpl.jena.query.*;

/**
 * @author peter
 */
public class WebEyeballConfigUsingRDFService extends Object implements Serializable {
    
    private String prefix, suffix;
    private Model lastResult = null;
    
    private String serviceLocation = "http://localhost/";
    
    public WebEyeballConfigUsingRDFService() {
        prefix = "";
        suffix = "";
    }
    
    public void setServiceLocation( String serviceLocation ) {
        this.serviceLocation = serviceLocation;
    }
    
    public void setPrefix( String prefix ) {
        this.prefix = prefix;
    }
    
    public void setSuffix( String suffix ) {
        this.suffix = suffix;
    }
    
    private String getString( ResultSet it, String target ) {
        if ( !it.hasNext() )
            return prefix + suffix;
        else {
            String value = "";
            while ( it.hasNext() ) {
                RDFNode curr = it.nextSolution().get( target );
                value += prefix;
                if ( curr.isAnon() )
                    value += curr.asNode().getBlankNodeLabel();
                else if ( curr.isLiteral() )
                    value += curr.asNode().getLiteralLexicalForm();
                else if ( curr.isURIResource() )
                    value += curr.asNode().getURI();
                value += suffix;
            }
            return value.substring( 0, value.length() - 1 );
        }
    }
    
    private String getRequestResponse( String key, String value ) {
        try {
            // Construct data
            String data = "";
            if ( value == null )
                data = URLEncoder.encode( key, "UTF-8" );
            else
                data = URLEncoder.encode( key, "UTF-8" ) + "=" + URLEncoder.encode( value, "UTF-8" );

            // Send data
            URL url = new URL( serviceLocation );
            URLConnection conn = url.openConnection();
            conn.setDoOutput( true );
            OutputStreamWriter wr = new OutputStreamWriter( conn.getOutputStream() );
            wr.write( data );
            wr.flush();

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String response = "";
            while ( (line = rd.readLine()) != null ) {
                response += line + "\n";
            }
            wr.close();
            rd.close();
            return response;
        } catch (IOException e) {
            return "ERROR: " + e.getMessage();
        }
    }
    
    public String getInspectors() {
        String response = getRequestResponse( "getValidInspectors", null );
        try {
            StringReader reader = new StringReader( response );
            Model m = ModelFactory.createDefaultModel().read( reader, null );
            lastResult = m;
            String queryString = "PREFIX eyesvc:  <" + EYESVC.getBaseUri() + "> \n" +
                                 "PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                                 "SELECT ?val \n" +
                                 "WHERE \n" +
                                 "  { <" + EYESVC.getBaseUri() + "> eyesvc:hasInspector  ?bn  . \n" +
                                 "    ?bn                           rdf:value            ?val . }";
        
            Query query = QueryFactory.create( queryString );
            QueryExecution qe = QueryExecutionFactory.create( query, m );
            ResultSet results = qe.execSelect();
            String output = getString( results, "?val" );
            qe.close();
            return output;
        } catch ( Exception e ) {
            return "Error " + e.getMessage() + " was caused by " + response;
        }
    }
    
    public String getAssumes() {
        String response = getRequestResponse( "getBuiltInAssumes", null );
        try {
            StringReader reader = new StringReader( response );
            Model m = ModelFactory.createDefaultModel().read( reader, null );
            lastResult = m;
            String queryString = "PREFIX eyesvc:  <" + EYESVC.getBaseUri() + "> \n" +
                                 "PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                                 "SELECT ?val \n" +
                                 "WHERE \n" +
                                 "  { <" + EYESVC.getBaseUri() + "> eyesvc:hasAssume ?bn  . \n" +
                                 "    ?bn                           rdf:value        ?val . }";
        
            Query query = QueryFactory.create( queryString );
            QueryExecution qe = QueryExecutionFactory.create( query, m );
            ResultSet results = qe.execSelect();
            String output = getString( results, "?val" );
            qe.close();
            return output;
        } catch ( Exception e ) {
            return "Error " + e.getMessage() + " was caused by " + response;
        }
    }
    
    public String getLabelsForInspectors() {
        if ( lastResult == null)
            getInspectors();
        
        String queryString = "PREFIX eyesvc:  <" + EYESVC.getBaseUri() + "> \n" +
                             "PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n" +
                             "SELECT ?lbl \n" +
                             "WHERE \n" +
                             "  { <" + EYESVC.getBaseUri() + "> eyesvc:hasInspector  ?bn  . \n" +
                             "    ?bn                           rdfs:label           ?lbl . }";

        Query query = QueryFactory.create( queryString );
        QueryExecution qe = QueryExecutionFactory.create( query, lastResult );
        ResultSet results = qe.execSelect();
        String output = getString( results, "?lbl" );
        qe.close();
        output = output.substring( 0, output.length() - 1 );
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