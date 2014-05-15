/*
 * statisticalAnalyser.java
 *
 * Created on August 22, 2006, 2007, 2008, 9:20 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.hp.hpl.jena.eyeball.web.statistics;

import com.hp.hpl.jena.assembler.*;
import com.hp.hpl.jena.db.ModelRDB;
import com.hp.hpl.jena.eyeball.Report;
import com.hp.hpl.jena.eyeball.assemblers.StatisticianAssembler;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.eyeball.web.WebEyeballer;
import com.hp.hpl.jena.rdf.model.*;

/**
 *
 * @author peter
 */
public class StatisticalAnalyser {
    
    protected WebEyeballer request;
    
    protected Report report;
    
    private Statistician stat;
    
    private Model config = getConfig();
    
    private Model statistics;
    
    /**
     * create the statisticalAnalyser;
     *    o assemble the statisticians
     *    o store the eyeballer
     */
    public StatisticalAnalyser( WebEyeballer request, Report report ) {
        this.request = request;
        this.report = report;
        setupStatistician();
        setupModel();
    }
    
    public StatisticalAnalyser() {
        setupStatistician();
        setupModel();
    }
    
    private void setupStatistician() {
        Assembler.general.implementWith( EYE.Statistician, new StatisticianAssembler() );
        stat = StatisticianAssembler.getStatistician( Assembler.general, EYE.statisticianAssembly, config );
    }
    
    private void setupModel() {
        try { Class.forName( c( EYE.dbDriver ) ); } 
        catch( ClassNotFoundException e) { e.printStackTrace(); }
        statistics = ModelFactory.createModelRDBMaker( ModelFactory.createSimpleRDBConnection( c( EYE.dbURI ), c( EYE.dbUser ), c( EYE.dbPass ), c( EYE.dbType ) ) )
                                 .openModel( c( EYE.statisticsModel ) );
    }
    
    private String c( Property target ) {
        NodeIterator it = config.listObjectsOfProperty( EYE.statisticsConfig, target );
        String output = null;
        if ( it.hasNext() ) {
            output = "";
            while ( it.hasNext() ) {
                RDFNode curr = it.nextNode();
                if ( curr.isAnon() )
                    output += curr.asNode().getBlankNodeLabel();
                else if ( curr.isLiteral() )
                    output += curr.asNode().getLiteralLexicalForm();
                else if ( curr.isResource() )
                    output += curr.asNode().getURI();
                output += ",";
            } // now trim off the last comma
            output = output.substring( 0, output.length() - 1 );
        }
        return output;
    }
    
    public Model getStatistics() {
        return statistics;
    }
    
    /**
     * statistically analyse the requested eyeballer
     */
    public void gatherStatistics() {
        if ( statistics == null )
            setupModel();
        if ( stat == null )
            setupStatistician();
        statistics.add( stat.gather( request, report, statistics.createResource() ) );
        cleanUp();
    }
    
    public String getStatisticalReport() {
        if ( statistics == null )
            setupModel();
        if ( stat == null )
            setupStatistician();
        String report = stat.getReport( statistics );
        cleanUp();
        return report;
    }
    
    public String getHtmlStatistics() {
        return getStatisticalReport().replaceAll( "\t", "&nbsp;&nbsp;&nbsp;&nbsp;" )
                                     .replaceAll( " ", "&nbsp;" )
                                     .replaceAll( "<", "&lt;" )
                                     .replaceAll( ">", "&gt;" )
                                     .replaceAll( Statistician.STATBREAK, "<hr />" )
                                     .replaceAll( "\n", "\n<br />" )
                                     .replaceAll( "<hr />", "\n<hr />\n" );
    }
    
    public static Model getConfig () {
        return ModelFactory.createDefaultModel().read( "file:etc/eyeball-config.n3", "N3" );
    }
    
    public String getSetUpPrefixTask() {
        statistics.setNsPrefix( "eye", "http://jena.hpl.hp.com/Eyeball#" );
        return "Set up prefix.";
    }
    
    private void cleanUp() {
        try {
            ((ModelRDB)statistics).close();
        } finally {
            stat = null;
            statistics = null;
            config = null;
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