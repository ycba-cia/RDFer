package com.hp.hpl.jena.eyeball.web.statistics;

import com.hp.hpl.jena.eyeball.Report;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.eyeball.web.WebEyeballer;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.XSD;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author peter
 */
public class MetaDataStatistician extends StatisticsUtil implements Statistician {
    
    public MetaDataStatistician() {  }
    
    public Model gather( WebEyeballer e, Report r, Resource hook ) {
        Model output = ModelFactory.createDefaultModel();
        output.add( hook, RDF.type, EYE.statistic );
        output.add( hook, EYE.requestDate, output.createTypedLiteral( new SimpleDateFormat( "yyyy-MM-dd" ).format( new Date() ), XSD.date.getURI() ) );
        output.add( hook, EYE.requestTime, output.createTypedLiteral( new SimpleDateFormat( "HH:mm:ss" ).format( new Date() ), XSD.time.getURI() ) );
        return output;
    }
    
    public String getReport( Model stats ) {
        int numStats = stats.listSubjectsWithProperty( RDF.type, EYE.statistic ).toList().size();
        String out = "MetaDataStatistician has analysed " + numStats + " sets of statistics and found that requests were made on the following dates:\n";
        List<String> dates = new ArrayList<String>(), dateCount = new ArrayList<String>();
        String queryString = "PREFIX eye:  <" + EYE.getURI() + "> \n" +
                             "PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                             "SELECT ?date \n" +
                             "WHERE \n" +
                             "  { ?s rdf:type        eye:statistic ; \n" +
                             "       eye:requestDate ?date         . }" +
                             "ORDER BY ASC( ?date )";
        
        Query query = QueryFactory.create( queryString );
        QueryExecution qe = QueryExecutionFactory.create( query, stats );
        ResultSet it = qe.execSelect();
        
        while ( it.hasNext() )
            inc( dates, dateCount, it.nextSolution().get( "?date" ).asNode().getLiteralLexicalForm() );
        /*
        ResIterator it = stats.listSubjectsWithProperty( RDF.type, EYE.statistic );
        while ( it.hasNext() ) {
            NodeIterator it2 = stats.listObjectsOfProperty( it.nextResource(), EYE.requestDate );
            while( it2.hasNext() )
                inc( dates, dateCount, it2.nextNode().asNode().getLiteralLexicalForm() );
        }*/
        ListIterator<String> li = dates.listIterator();
        while ( li.hasNext() ) {
            int i = li.nextIndex();
            String curr = li.next();
            String count = dateCount.get( i );
            if ( count.equalsIgnoreCase( "1" ) )
                count = "was 1 request";
            else
                count = "were " + count + " requests";
            out += "    On " + curr + " there " + count + " made of the service.\n";
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