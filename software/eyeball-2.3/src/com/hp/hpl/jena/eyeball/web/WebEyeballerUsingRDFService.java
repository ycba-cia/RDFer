/*
 * eyeballerUsingService.java
 *
 * Created on August 7, 2006, 2007, 2008, 8:31 AM
 */

package com.hp.hpl.jena.eyeball.web;

import com.hp.hpl.jena.rdf.model.*;
import java.io.*;
import java.net.*;
import com.hp.hpl.jena.eyeball.vocabulary.EYESVC;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.vocabulary.RDF;


/**
 * @author peter
 */
public class WebEyeballerUsingRDFService extends Object implements Serializable {
    
    private String serviceLocation = "http://localhost/";
    
    private String rdfModel = "";
    
    private String modelType = "N3";
    
    private String reportFormat = "text";
    
    private String baseURL = "http://jena.sourceforge.net/";
    
    private boolean repair = false;
    
    private boolean analyse = false;
    
    private String[] inspectors = {};
    
    private String[] assumes = {};
    
    private String response = "";
    
    private Model serverResponse = null;
    
    private boolean collectStatistics = true;
    
    public WebEyeballerUsingRDFService() {
    }
    
    public void setServiceLocation( String serviceLocation ) {
        this.serviceLocation = serviceLocation;
    }
    
    public void setCollectStatistics( boolean collectStatistics ) {
        this.collectStatistics = collectStatistics;
    }
    
    public void setCollectStatistics( String collectStatistics ) {
        if ( collectStatistics.equalsIgnoreCase( "on" ) || collectStatistics.equalsIgnoreCase( "true" ) || collectStatistics.equalsIgnoreCase( "yes" ) )
            this.collectStatistics = true ;
        else
            this.collectStatistics = false ;
    }
    
    public boolean getCollectStatistics(){
        return collectStatistics;
    }
    
    public void setRdfModel( String rdfModel ) {
        this.rdfModel = rdfModel;
    }
    
    public String getRdfModel() {
        return sanitiseForHtml( rdfModel );
    }
    
    public void setModelType( String modelType ) {
        this.modelType = modelType;
    }
    
    public String getModelType() {
        return modelType;
    }
    
    public void setReportFormat( String reportFormat ) {
        this.reportFormat = reportFormat;
    }
    
    public String getReportFormat() {
        return reportFormat;
    }
    
    public void setBaseURL( String baseURL ) {
        this.baseURL = baseURL;
    }
    
    public String getBaseURL() {
        return baseURL;
    }
    
    public void setInspectors( String inspectors ) {
        if ( inspectors.charAt( 0 ) == '[' )
            this.inspectors = inspectors.replaceAll( "[^-,_\\d\\w]", "" ).split( "," );
    }
    
    public void setAssumes( String assumes ) {
        if ( assumes.charAt( 0 ) == '[' )
            this.assumes = assumes.replaceAll( "[^-,_\\d\\w]", "" ).split( "," );
    }
    
    public void setRepairFuncs( String input ) {
        if ( input.equalsIgnoreCase( "analyse" ) )
            analyse = true;
        else if ( input.equalsIgnoreCase( "repair" ) )
            repair = true;
    }
    
    public String getRepairFuncs() {
        if ( repair )
            return "repair";
        else if ( analyse )
            return "analyse";
        else
            return "none";
    }
    
    public String getRepairedModel() {
        if ( response == "" || repair == false ) {
            repair = true;
            getReport();
            repair = false;
        }
        
        if ( !response.startsWith( "ERROR" ) )
            if ( modelType.equalsIgnoreCase( "Best Guess" ) ) {
                return sanitiseForHtml( modelToString( extractModelFromServerResponse( "outputModel" ), "N3" ) );
            } else {
                return sanitiseForHtml( modelToString( extractModelFromServerResponse( "outputModel" ), modelType ) );            
            }
        return response;
    }
    
    public String getPlainReport() {
        return getReport();
    }
    
    public String getHtmlReport() {
        if ( reportFormat.equalsIgnoreCase( "Text" ) )
            return sanitiseForHtml( getReport() );
        else
            return "<code>" + sanitiseForHtml( getReport() ) + "</code>";
    }
    
    private String getReport() {
        String[] key = new String[4];
        String[] value = new String[2];
        key[0] = "submitRDF";
        value[0] = rdfModel;
        key[1] = "setupConfig";
        value[1] = constructConfigRDF();
        key[2] = "getReport";
        if ( repair )
            key[3] = "getRepairedModel";
        getRequestResponse( key, value );
        
        if ( !response.startsWith( "ERROR" ) )
            if ( reportFormat.equalsIgnoreCase( "Text" ) ) {
                    Model report = extractModelFromServerResponse( "report" );
                    return report.listObjectsOfProperty( RDF.value ).nextNode().asNode().getLiteralLexicalForm();
            } else {
                if ( !response.startsWith( "ERROR" ) )
                    return modelToString( extractModelFromServerResponse( "report" ), reportFormat );            
            }
        return response;
    }
    
    private String sanitiseForHtml( String input ) {
        return input.replaceAll( "\t", "&nbsp;&nbsp;&nbsp;&nbsp;" )
                    .replaceAll( " ", "&nbsp;" )
                    .replaceAll( "<", "&lt;" )
                    .replaceAll( ">", "&gt;" )
                    .replaceAll( "\n", "\n<br />" );
    }
    
    private String constructConfigRDF() {
        Model conf = ModelFactory.createDefaultModel();
        Resource s = conf.createResource( EYESVC.getBaseUri() );
        if ( repair )
            conf.add( s, EYESVC.doRepair, "true" );
        else if ( analyse )
            conf.add( s, EYESVC.doAnalysis, "true" );
        for ( int i = 0; i < inspectors.length; i++ )
            conf.add( s, EYESVC.useInspector, inspectors[i]);
        for ( int i = 0; i < assumes.length; i++ )
            conf.add( s, EYESVC.addAssume, assumes[i] );
        conf.add( s, EYESVC.withBaseUrl, conf.createResource( baseURL ) );
        conf.add( s, EYESVC.inputModelType, modelType );
        conf.add( s, EYESVC.reportFormat, reportFormat );
        conf.addLiteral( s, EYESVC.collectStatistics, collectStatistics );
        
        StringWriter w = new StringWriter();
        conf.write( w, "RDF/XML-ABBREV" );
        return w.toString();
    }
    
    private String constructRequestData( String[] key, String[] value ) throws UnsupportedEncodingException {
        String data = "";
        if ( value == null )
            data = URLEncoder.encode( key[0], "UTF-8" );
        else {
            for ( int i = 0; i < key.length; i++ ) {
                if ( key[i] != null ) {
                    data += URLEncoder.encode( key[i], "UTF-8" );
                    if ( i < value.length )
                        if ( value[i] != null)
                            data +=  "=" + URLEncoder.encode( value[i], "UTF-8" );
                    data += "&";
                }
            }
        }
        return data;
    }
    
    private String ltrim( String in ) {
        if ( ( in.length() > 0 ) && ( ( in.charAt( 0 ) == '\n' ) || ( in.charAt( 0 ) == ' ' ) ) )
            return ltrim( in.substring( 1, in.length() ) );
        else
            return in;
    }
    
    private String rtrim( String in ) {
        if ( ( in.charAt( in.length() - 1 ) == '\n' ) || ( in.charAt( in.length() - 1 ) == ' ' ) )
            return rtrim( in.substring( 0, in.length() - 1 ) );
        else
            return in;
    }
    
    private void getRequestResponse( String[] key, String[] value ) {
        try {
            // Construct data
            String data = constructRequestData( key, value );

            // Send data
            URL url = new URL( serviceLocation );
            URLConnection conn = url.openConnection();
            conn.setDoOutput( true );
            OutputStreamWriter wr = new OutputStreamWriter( conn.getOutputStream() );
            wr.write( data );
            wr.flush();

            // Get the response
            BufferedReader rd = new BufferedReader( new InputStreamReader( conn.getInputStream() ) );
            String line;
            String response = "";
            while ( (line = rd.readLine() ) != null ) {
                response += line + "\n";
            }
            wr.close();
            rd.close();
            this.response = rtrim( ltrim( response ) );
            serverResponse = ModelFactory.createDefaultModel().read( new StringReader( response ), baseURL );
        } catch (IOException e) {
            e.printStackTrace();
            this.response = "ERROR: " + e.getMessage();            
        }
    }
    
    private Model extractModelFromServerResponse( String belongsTo ) {
        String queryString = "PREFIX eyesvc:  <" + EYESVC.getBaseUri() + "> \n" +
                             "PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                             "SELECT ?subject ?predicate ?object \n" +
                             "WHERE \n" +
                             "  { ?subject ?predicate       ?object       . \n" +
                             "    FILTER ( ?predicate != eyesvc:belongsTo ) \n" +
                             "    ?subject eyesvc:belongsTo eyesvc:" + belongsTo + " . }";
        
        Query query = QueryFactory.create( queryString );
        QueryExecution qe = QueryExecutionFactory.create( query, serverResponse );
        com.hp.hpl.jena.query.ResultSet it = qe.execSelect();
        
        Model m = ModelFactory.createDefaultModel();
        while ( it.hasNext() ) {
            QuerySolution curr = it.nextSolution();
            RDFNode sNode = curr.get( "?subject" );
            Property p = m.createProperty( curr.get( "?predicate" ).asNode().getURI() );
            RDFNode oNode = curr.get( "?object" );
            Resource s, o;
            
            if ( sNode.isAnon() )
                s = m.createResource( sNode.asNode().getBlankNodeId() );
            else if ( sNode.isURIResource() )
                s = m.createResource( sNode.asNode().getURI() );
            else
                s = m.createResource( sNode.asNode().getLiteralLexicalForm() );
            
            if ( oNode.isAnon() )
                o = m.createResource( oNode.asNode().getBlankNodeId() );
            else if ( oNode.isURIResource() )
                o = m.createResource( oNode.asNode().getURI() );
            else if ( oNode.isResource() )
                o = (Resource)oNode.asNode();
            else {
                m.add( s, p, m.createTypedLiteral( oNode.asNode().getLiteralLexicalForm(), oNode.asNode().getLiteralDatatype() ) );
                o = null;                
            }
            if ( o != null )
                m.add( s, p, o );
        }
        qe.close();
        m.setNsPrefixes( serverResponse.getNsPrefixMap() );
        
        return m;
    }
    
    private String modelToString( Model m, String lang ) {
        StringWriter w = new StringWriter();
        m.write( w, lang );
        return w.toString();
    }
    
    public String getCleanUpTask() {
        inspectors = null;
        assumes = null;
        serverResponse = null;
        response = null;
        System.gc();
        return "<!-- cleanUp completed -->";
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