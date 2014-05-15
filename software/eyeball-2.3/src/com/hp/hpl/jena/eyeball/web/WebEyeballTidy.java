/*
 * tidy.java
 *
 * Created on August 31, 2006, 2007, 2008, 12:18 PM
 */

package com.hp.hpl.jena.eyeball.web;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.StringReader;

/**
 * @author peter
 */
public class WebEyeballTidy extends Object implements Serializable {
    
    private String modelType = "Best Guess";
    
    private String baseURL = "http://jena.sourceforge.net/Eyeball";
    
    private String rdfModel = "";
    
    private String outLang = "N3";
    
    public WebEyeballTidy() {
    }
    
    private Model readModel() throws Exception {
        Model m = ModelFactory.createDefaultModel();
        StringReader reader = new StringReader( rdfModel );
        if ( modelType.equalsIgnoreCase( "Best Guess" ) )
            if ( rdfModel.startsWith( "<rdf:RDF" ) || rdfModel.startsWith( "<!" ) || rdfModel.startsWith( "<?" ) ) {
                modelType = "RDF/XML";
                m.read( reader, baseURL, modelType );
            } else {
                try {
                    modelType = "N3";
                    m.read( reader, baseURL, modelType );
                } catch ( Exception e1 ) {
                    try {
                        modelType = "N-TRIPLE";
                        m.read( reader, baseURL, modelType );
                    } catch ( Exception e2 ) { throw e1; }
                }
            }
        else
            m.read( reader, baseURL, modelType );
        return m;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType( String modelType)  {
        this.modelType = modelType;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL( String baseURL ) {
        this.baseURL = baseURL;
    }
    
    public void setRdfModel( String rdfModel ) {
        this.rdfModel = rdfModel;
    }
    
    public String getInputModel() {
        return sanitiseForHtml( this.rdfModel );
    }
    
    public String getTidyModel() throws Exception {
        ByteArrayOutputStream s = new ByteArrayOutputStream();
	PrintStream out = new PrintStream( s );
        readModel().write( out, outLang );
        return sanitiseForHtml( s.toString() );
    }
    
    private String sanitiseForHtml( String input ) {
        return input.replaceAll( "\t", "&nbsp;&nbsp;&nbsp;&nbsp;" )
                    .replaceAll( " ", "&nbsp;" )
                    .replaceAll( "<", "&lt;" )
                    .replaceAll( ">", "&gt;" )
                    .replaceAll( "\n", "\n<br />" );
    }

    public String getOutLang() {
        return outLang;
    }

    public void setOutLang(String outLang) {
        this.outLang = outLang;
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