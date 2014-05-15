/*
 * serviceEyeballer.java
 *
 * Created on August 14, 2006, 2007, 2008, 9:02 AM
 */

package com.hp.hpl.jena.eyeball.web;

import java.io.Serializable;
import com.hp.hpl.jena.eyeball.vocabulary.EYESVC;
import com.hp.hpl.jena.vocabulary.*;
import com.hp.hpl.jena.rdf.model.*;
import java.io.*;

/**
 * @author peter
 */
public class RDFServiceEyeballer extends Object implements Serializable {
    
    private String sep = ",";
    private WebEyeballer eye;
    private WebEyeballConfig conf;
    private Model output;
    
    public RDFServiceEyeballer() {
        eye = new WebEyeballer();
        conf = new WebEyeballConfig();
        conf.setSuffix( sep );
        output = ModelFactory.createDefaultModel();
        output.setNsPrefix( "eyesvc", EYESVC.getBaseUri() );
    }
    
    private Model getOwnedModel( Model in, Resource belongsTo ) {
        Model out = ModelFactory.createDefaultModel();
        StmtIterator it = in.listStatements();        
        while ( it.hasNext() ) {
            com.hp.hpl.jena.rdf.model.Statement s = it.nextStatement();
            out.add( s );
            out.add( s.getSubject(), EYESVC.belongsTo, belongsTo );
        }
        return out;
    }
    
    public String getOutput() {
        return modelToString( output );
    }
    
    public String getReport() throws Exception {
        try {
            String rep = eye.getPlainReport();
            Model m = ModelFactory.createDefaultModel();
            if ( eye.getReportFormat().equalsIgnoreCase( "text" ) )
                m.add( EYESVC.textReportOutput, RDF.value, rep );
            else
                m.read( new StringReader( rep ), eye.getBaseURL(), eye.getReportFormat() );
            output.add( getOwnedModel( m, EYESVC.report ) );
            output.setNsPrefixes( m.getNsPrefixMap() );        
            return "<!-- report generation successful -->";
        } catch (Exception e) {
            eye = null;
            System.err.println( ">> Error ocurred generating report!" );
            throw e;   
        }
    }
    
    public String getRepairedModel(){
        String rep = eye.getPlainRepairedModel();
        StringReader reader = new StringReader( rep );
        Model m = ModelFactory.createDefaultModel().read( reader, eye.getBaseURL(), eye.getModelType() );
        output.add( getOwnedModel( m, EYESVC.outputModel ) );
        output.setNsPrefixes( m.getNsPrefixMap() );
        return "<!-- repaired model generation successful -->";
    }
    
    public String getValidInspectors() {
        String[] l = conf.getInspectors().split( sep );
        String[] d = conf.getLabelsFromLastResult().split( sep );
        Model m = ModelFactory.createDefaultModel();
        m.setNsPrefix( "eyesvc", EYESVC.getBaseUri() );
        Resource r = m.createResource( EYESVC.getBaseUri() );
        for ( int i = 0; i < l.length; i++ ) {
            Resource bn = m.createResource();
            m.add( r, EYESVC.hasInspector, bn );
            m.add( bn, RDF.value, l[i] );
            if ( d.length > i )
                m.add( bn, RDFS.label, d[i].replaceAll( "&nbsp;", " " ) );
        }
        output.add( getOwnedModel( m, EYESVC.inspectors ) );
        output.setNsPrefixes( m.getNsPrefixMap() );
        return "<!-- inspector list creation successful -->";
    }
    
    public String getBuiltInAssumes() {
        String[] l = conf.getAssumes().split( sep );
        Model m = ModelFactory.createDefaultModel();
        m.setNsPrefix( "eyesvc", EYESVC.getBaseUri() );
        Resource r = m.createResource( EYESVC.getBaseUri() );
        for ( int i = 0; i < l.length; i++ ) {
            Resource bn = m.createResource();
            m.add( r, EYESVC.hasAssume, bn );
            m.add( bn, RDF.value, l[i] );
        }
        output.add( getOwnedModel( m, EYESVC.assumes ) );
        output.setNsPrefixes( m.getNsPrefixMap() );
        return "<!-- built in assume list creation successful -->";
    }
    
    public void setConfigOptions( String inputModel ) {
        StringReader reader = new StringReader( inputModel );
        Model m = ModelFactory.createDefaultModel().read( reader, null );
        
        // Repair functionality
        if ( m.listStatements( (Resource)null, EYESVC.doRepair, (RDFNode)null ).hasNext() )
            eye.setRepairFuncs( "repair" );
        else if ( m.listStatements( (Resource)null, EYESVC.doAnalysis, (RDFNode)null ).hasNext() )
            eye.setRepairFuncs( "analyse" );
        // Assumes
        NodeIterator it = m.listObjectsOfProperty( EYESVC.addAssume );
        eye.setAssumes( iterToStringArray( it ) );
        // Inspectors
        it = m.listObjectsOfProperty( EYESVC.useInspector );
        eye.setInspectors( iterToStringArray( it ) );
        // Base URL
        it = m.listObjectsOfProperty( EYESVC.withBaseUrl );
        if ( it.hasNext() )
            eye.setBaseURL( it.nextNode().asNode().getURI() );
        // Input model type
        it = m.listObjectsOfProperty( EYESVC.inputModelType );
        if ( it.hasNext() )
            eye.setModelType( it.nextNode().asNode().getLiteralLexicalForm() );
        // Report format
        it = m.listObjectsOfProperty( EYESVC.reportFormat );
        if ( it.hasNext() )
            eye.setReportFormat( it.nextNode().asNode().getLiteralLexicalForm() );
        // Statistics
        it = m.listObjectsOfProperty( EYESVC.collectStatistics );
        if ( it.hasNext() )
            eye.setCollectStatistics( it.nextNode().asNode().getLiteralLexicalForm() );
    }
    
    private String iterToStringArray( NodeIterator it ) {
        String input = "[";
        while ( it.hasNext() ) {
            RDFNode curr = it.nextNode();
            if ( curr.isLiteral() )
                input += curr.asNode().getLiteralLexicalForm();
            else if ( curr.isURIResource() )
                input += curr.asNode().getURI();
            input += ",";
        }
        if ( input.length() > 1)
            input = input.substring( 0, input.length() - 1 );
        input += "]";
        return input;
    }
    
    private String modelToString( Model m ) {
        StringWriter w = new StringWriter();
        m.write( w, "RDF/XML-ABBREV" );
        return w.toString();
    }
    
    public String getCleanUpTask() {
        eye = null;
        conf = null;
        output = null;
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