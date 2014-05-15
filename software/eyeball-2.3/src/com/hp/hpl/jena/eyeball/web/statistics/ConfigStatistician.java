package com.hp.hpl.jena.eyeball.web.statistics;

import com.hp.hpl.jena.eyeball.Report;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.eyeball.vocabulary.EYESVC;
import com.hp.hpl.jena.eyeball.web.WebEyeballer;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author peter
 */
public class ConfigStatistician extends StatisticsUtil implements Statistician {
    
    public ConfigStatistician() {  }
    
    public Model gather( WebEyeballer e, Report r, Resource hook ) {
        Model output = ModelFactory.createDefaultModel();
        output.add( hook, RDF.type, EYE.statistic );
        Resource bn = output.createResource();
        output.add( hook, EYE.eyeballerState, bn );
        output.add( bn, EYESVC.doAnalysis, output.createTypedLiteral( e.getRepairFuncs().contains( "analyse" ) ) );
        output.add( bn, EYESVC.doRepair, output.createTypedLiteral( e.getRepairFuncs().contains( "repair" ) ) ) ;
        output.add( bn, EYESVC.inputModelType, e.getModelType() );
        output.add( bn, EYESVC.reportFormat, e.getReportFormat() );
        String[] tmp = e.getInspectors();
        for ( int i = 0; i < tmp.length; i++ )
            output.add( bn, EYESVC.hasInspector, tmp[i] );
        tmp = e.getAssumes();
        for ( int i = 0; i < tmp.length; i++ )
            output.add( bn, EYESVC.hasAssume, tmp[i] );
        return output;
    }
    
    public String getReport( Model stats ) {
        ResIterator it = stats.listSubjectsWithProperty( RDF.type, EYE.statistic );
        double numStats = stats.listSubjectsWithProperty( RDF.type, EYE.statistic ).toList().size();
        double analysis = 0;
        double repair = 0;
        List<String> models = new ArrayList<String>(), 
             modelCount = new ArrayList<String>(), 
             reports = new ArrayList<String>(), 
             reportCount = new ArrayList<String>(), 
             inspectors = new ArrayList<String>(), 
             inspectorCount = new ArrayList<String>(), 
             assumes = new ArrayList<String>(), 
             assumeCount = new ArrayList<String>();
        while ( it.hasNext() ) {
            Resource state = stats.createResource( stats.listObjectsOfProperty( it.nextResource(), EYE.eyeballerState ).nextNode().asNode().getBlankNodeId() );
            if ( state.getProperty( EYESVC.doAnalysis ).getObject().asNode().getLiteralLexicalForm().equalsIgnoreCase( "true" ) )
                analysis++;
            if ( state.getProperty( EYESVC.doRepair ).getObject().asNode().getLiteralLexicalForm().equalsIgnoreCase( "true" ) )
                repair++;
            String model = state.getProperty( EYESVC.inputModelType ).getObject().asNode().getLiteralLexicalForm();
            String report = state.getProperty( EYESVC.reportFormat ).getObject().asNode().getLiteralLexicalForm();
            inc( models, modelCount, model );
            inc( reports, reportCount, report );
            NodeIterator it2 = stats.listObjectsOfProperty( state, EYESVC.hasInspector );
            while ( it2.hasNext() )
                inc( inspectors, inspectorCount, it2.nextNode().asNode().getLiteralLexicalForm() );
            it2 = stats.listObjectsOfProperty( state, EYESVC.hasAssume );
            while ( it2.hasNext() )
                inc( assumes, assumeCount, it2.nextNode().asNode().getLiteralLexicalForm() );
        }
        String out = "";
        out += "ConfigStatistician has analysed " + (int)numStats + " sets of statistics and found that;\n"
             + "The eyeballer was requested to analyse the model " + formatDouble( ((analysis / numStats) * 100) + ((repair / numStats) * 100) ) + "% of the time.\n"
             + "It was also asked to repair the model " + formatDouble( (repair / numStats) * 100 ) + "% of the time.\n"
             + "The following input model types were used:\n";
        ListIterator<String> li = models.listIterator();
        while ( li.hasNext() ) {
              int curr = li.nextIndex();
              out += "    " + li.next() + ", used in " + formatDouble( (Double.parseDouble( modelCount.get( curr ) ) / numStats) * 100 ) + "% of models.\n";
        }
        out += "The following report formats were used:\n";
        li = reports.listIterator();
        while ( li.hasNext() ) {
              int curr = li.nextIndex();
              out += "    " + li.next() + ", used in " + formatDouble( (Double.parseDouble( reportCount.get( curr ) ) / numStats) * 100 ) + "% of requests.\n";
        }
        out += "The popularity of Inspectors can be seen below:\n";
        li = inspectors.listIterator();
        while ( li.hasNext() ) {
              int curr = li.nextIndex();
              out += "    " + li.next() + " was requested in " + formatDouble( (Double.parseDouble( inspectorCount.get( curr ) ) / numStats) * 100 ) + "% of eyeballings.\n";
        }
        out += "Finally, the popularity of the assumes:\n";
        li = assumes.listIterator();
        while ( li.hasNext() ) {
              int curr = li.nextIndex();
              out += "    " + li.next() + " was assumed in " + formatDouble( (Double.parseDouble( assumeCount.get( curr ) ) / numStats) * 100 ) + "% of eyeballings.\n";
        }
        return out;
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