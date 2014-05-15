package com.hp.hpl.jena.eyeball.web.statistics;

import com.hp.hpl.jena.eyeball.Report;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.eyeball.web.WebEyeballer;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;
import java.io.StringReader;
import java.util.*;

/**
 *
 * @author peter
 */
public class EyeballingStatistician extends StatisticsUtil implements Statistician {
    
    public EyeballingStatistician() {  }
    
    public Model gather( WebEyeballer e, Report r, Resource hook ) {
        Model output = ModelFactory.createDefaultModel();
        output.add( hook, RDF.type, EYE.statistic );
        Model input = ModelFactory.createDefaultModel().read( new StringReader( e.getPlainRdfModel() ), EYE.getURI(), e.getModelType() );
        Model repair = ModelFactory.createDefaultModel().read( new StringReader( e.getPlainRepairedModel() ), EYE.getURI(), e.getModelType() );
        Model report = r.model();
        StmtIterator it = report.listStatements();
        List<RDFNode> faults = StatisticalAnalyser.getConfig().listObjectsOfProperty( EYE.statisticsConfig, EYE.fault ).toList();
        while ( it.hasNext() ) {
            Statement currstat = it.nextStatement();
            Property curr = currstat.getPredicate();
            if ( faults.contains( curr ) ) {
                if ( output.contains( null, EYE.fault, curr ) ) {
                    Resource bn = output.listSubjectsWithProperty( EYE.fault, curr ).nextResource();
                    int number = Integer.parseInt( output.listObjectsOfProperty( bn, EYE.number ).nextNode().asNode().getLiteralLexicalForm() );
                    output.remove( output.listStatements( bn, EYE.number, (RDFNode)null ) ) ;
                    output.add( bn, EYE.number, output.createTypedLiteral( String.valueOf( number + 1 ), XSD.integer.getURI() ) );
                } else {
                    Resource bn = output.createResource();
                    output.add( hook, EYE.failure, bn );
                    output.add( bn, EYE.fault, curr );
                    output.add( bn, EYE.number, output.createTypedLiteral( String.valueOf( 1 ), XSD.integer.getURI() ) );
                }
            }
        }
        Model intersect = input.intersection( repair );
        double change = 100;
        double intS = intersect.size();
        double inpS = input.size();
        if ( inpS != 0 )
            change = ( 1 - ( intS / inpS ) )  * 100;        
        output.add( hook, EYE.percentageChanged, output.createTypedLiteral( String.valueOf( formatDouble( change ) ), XSD.decimal.getURI() ) );
        return output;
    }
    
    public String getReport( Model stats ) {
        ResIterator it = stats.listSubjectsWithProperty( RDF.type, EYE.statistic );
        double numStats = stats.listSubjectsWithProperty( RDF.type, EYE.statistic ).toList().size();
        double totalPercentageChange = 0;
        List<String> faults = new ArrayList<String>(), faultCount = new ArrayList<String>();
        while ( it.hasNext() ) {
            Resource stat = it.nextResource();
            NodeIterator it2 = stats.listObjectsOfProperty( stat, EYE.failure );
            while ( it2.hasNext() ) {
                Resource curr = stats.createResource( it2.nextNode().asNode().getBlankNodeId() );
                inc( faults, faultCount, stats.shortForm( curr.getProperty( EYE.fault ).getObject().asNode().getURI() ), Integer.parseInt( curr.getProperty( EYE.number ).getLiteral().getLexicalForm() ) );
            }
            totalPercentageChange += Double.parseDouble( stat.getProperty( EYE.percentageChanged ).getLiteral().getLexicalForm() );
        }
        String out = "EyeballingStatistician has analysed " + (int)numStats + " sets of statistics and found the following failures;\n";
        ListIterator<String> li = faults.listIterator();
        while ( li.hasNext() ) {
              int curr = li.nextIndex();
              out += "    " + stats.shortForm( li.next() ) + " occurred on average " + formatDouble( Double.parseDouble( faultCount.get( curr ) ) / numStats  ) + " times per eyeballing.\n";
        }
        out += "On average, models were changed by " + formatDouble( totalPercentageChange / numStats ) + "% during eyeballing (note: this includes times the model was not repaired!)";
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