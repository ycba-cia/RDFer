/*
 * eyeballer.java
 *
 * Created on August 7, 2006, 2007, 2008, 8:31 AM
 */

package com.hp.hpl.jena.eyeball.web;

import java.io.Serializable;
import jena.eyeball;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.ontology.OntModel;
import java.util.*;
import java.io.*;
import com.hp.hpl.jena.assembler.Assembler;
import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.assemblers.*;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.eyeball.web.statistics.StatisticalAnalyser;

/**
 * @author peter
 */
public class WebEyeballer implements Serializable
    {
    public WebEyeballer()
        {}

    private Eyeball eye = null;

    private String rdfModel = "";

    private String modelType = "N3";

    private String reportFormat = "text";

    private String baseURL = "http://jena.sourceforge.net/";

    private boolean repair = false;

    private boolean analyse = false;

    private String[] inspectors =
        {};

    private String[] assumes =
        {};

    private OntModel m = null;

    private boolean collectStatistics = true;

    public void setCollectStatistics( boolean collectStatistics )
        {
        this.collectStatistics = collectStatistics;
        }

    public void setCollectStatistics( String collectStatistics )
        {
        if (collectStatistics.equalsIgnoreCase( "on" )
                || collectStatistics.equalsIgnoreCase( "true" )
                || collectStatistics.equalsIgnoreCase( "yes" )) setCollectStatistics( true );
        else
            setCollectStatistics( false );
        }

    public boolean getCollectStatistics()
        {
        return collectStatistics;
        }

    public void setRdfModel( String rdfModel )
        {
        this.rdfModel = rdfModel;
        }

    public String getRdfModel()
        {
        return sanitiseForHtml( rdfModel );
        }

    public String getPlainRdfModel()
        {
        return rdfModel;
        }

    public void setModelType( String modelType )
        {
        this.modelType = modelType;
        }

    public String getModelType()
        {
        return modelType;
        }

    public String[] getInspectors()
        {
        return inspectors;
        }

    public String[] getAssumes()
        {
        return assumes;
        }

    public void setReportFormat( String reportFormat )
        {
        this.reportFormat = reportFormat;
        }

    public String getReportFormat()
        {
        return reportFormat;
        }

    public void setBaseURL( String baseURL )
        {
        this.baseURL = baseURL;
        }

    public String getBaseURL()
        {
        return baseURL;
        }

    public void setInspectors( String inspectors )
        {
        if (inspectors.charAt( 0 ) == '[') this.inspectors = inspectors
                .replaceAll( "[^-,_\\d\\w]", "" ).split( "," );
        if (this.inspectors[0].equalsIgnoreCase( "" )) this.inspectors = new String[0];
        }

    public void setAssumes( String assumes )
        {
        if (assumes.charAt( 0 ) == '[') this.assumes = assumes.replaceAll(
                "[^-,_\\d\\w]", "" ).split( "," );
        if (this.assumes[0].equalsIgnoreCase( "" )) this.assumes = new String[0];
        }

    public void setRepairFuncs( String input )
        {
        if (input.equalsIgnoreCase( "analyse" )) analyse = true;
        else if (input.equalsIgnoreCase( "repair" )) repair = true;
        }

    public String getRepairFuncs()
        {
        if (repair) return "repair";
        else if (analyse) return "analyse";
        else
            return "none";
        }

    public String getPlainRepairedModel()
        {
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        PrintStream out = new PrintStream( s );
        m.write( out, modelType );
        return s.toString();
        }

    public String getRepairedModel()
        {
        return sanitiseForHtml( getPlainRepairedModel() );
        }

    private Report getReport() 
        {
        m = ModelFactory.createOntologyModel();
        StringReader reader = new StringReader( rdfModel );
        if (modelType.equalsIgnoreCase( "Best Guess" )) 
            if (rdfModel.startsWith( "<rdf:RDF" )  || rdfModel.startsWith( "<!" ) || rdfModel.startsWith( "<?" ))
            {
            modelType = "RDF/XML";
            m.read( reader, baseURL, modelType );
            }
        else
            {
            try
                {
                modelType = "N3";
                m.read( reader, baseURL, modelType );
                }
            catch (Exception e1)
                {
                try
                    {
                    modelType = "N-TRIPLE";
                    m.read( reader, baseURL, modelType );
                    }
                catch (JenaException e)
                    { throw e; }
                catch (Exception e2)
                    {
                    System.err.println( rdfModel );
                    throw new JenaException( e1 );
                    }
                }
            }
        else
            m.read( reader, baseURL, modelType );
        if (eye == null) createEyeball();
        Report r = eye.inspect( new Report(), m );
        if (repair || analyse) eye.analyse( r, m );
        if (repair) eye.doctorModel( r, m );
        if (collectStatistics) // analyse this request for statistical purposes
        new StatisticalAnalyser( this, r ).gatherStatistics();
        return r;
        }

    public String getPlainReport()
        {
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        PrintStream out = new PrintStream( s );
        Report report = getReport();
//        report.model().write( System.err, "N-TRIPLES" );
        eye.render( report, out );
//        System.err.println( ">>> " + s );
        return s.toString();
        }

    public String getHtmlReport()
        { return formatReport( getReport() ); }

    public void printDiagnostics()
        {
        System.out
                .println( "==========================================================================================" );
        try
            {
            System.out.println( ">> eyeballer executing from: "
                    + new java.io.File( "." ).getCanonicalPath() );
            }
        catch (java.io.IOException e)
            {
            System.err.println( ">> error getting execution directory:" );
            e.printStackTrace();
            }
        System.out.println( ">> eyeball config seems to be:" );
        ModelFactory.createDefaultModel().read( "file:etc/eyeball-config.n3",
                "N3" ).write( System.out, "N3" );
        System.out
                .println( "==========================================================================================" );
        }

    private void createEyeball()
        {
        Assembler.general
            .implementWith( EYE.Inspector, new InspectorAssembler() )
            .implementWith( EYE.Analysis, new AnalysisAssembler() )
            .implementWith( EYE.Doctor, new DoctorAssembler() )
            .implementWith( EYE.Eyeball, new EyeballAssembler() )
            .implementWith( EYE.Renderer, new RendererAssembler() )
            ;
        eye = new eyeball( constructArgs() ).getEyeball();
        }

    private String formatReport( Report r )
        {
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        PrintStream out = new PrintStream( s );
        eye.render( r, out );
        if (reportFormat.equalsIgnoreCase( "text" )) 
            return "<p>" + sanitiseForHtml( s.toString() ) + "</p>";
        else
            return "<code>" + sanitiseForHtml( s.toString() ) + "</code>";
        }

    private String sanitiseForHtml( String input )
        {
        return input.replaceAll( "\t", "&nbsp;&nbsp;&nbsp;&nbsp;" ).replaceAll(
                " ", "&nbsp;" ).replaceAll( "<", "&lt;" ).replaceAll( ">",
                "&gt;" ).replaceAll( "\n", "\n<br />" );
        }

    private String[] constructArgs()
        {
        WebEyeballConfig conf = new WebEyeballConfig();
        conf.setSuffix( "," );
        String[] allInspectors = conf.getInspectors().split( "," );
        List<String> rqInspectors = Arrays.asList( inspectors );

        List<String> args = new ArrayList<String>();
        args.add( "-check" );
        args.add( "null" );
        args.add( "-exclude" );
        for (int i = 0; i < allInspectors.length; i++)
            if (!rqInspectors.contains( allInspectors[i] )) args
                    .add( allInspectors[i] );
        if (args.get( args.size() - 1 )
                .equalsIgnoreCase( "-exclude" )) args.remove( "-exclude" );
        if (rqInspectors.size() > 0) args.add( "-include" );
        args.addAll( rqInspectors );
        args.add( "-render" );
        args.add( reportFormat.toLowerCase() );
        if (assumes.length > 0)
            {
            args.add( "-assume" );
            args.addAll( Arrays.asList( assumes ) );
            }
        return args.toArray( new String[args.size()] );
        }

    }

/*
 * (c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP All rights
 * reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The name of the author may not
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */