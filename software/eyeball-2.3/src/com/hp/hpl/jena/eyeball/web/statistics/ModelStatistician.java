package com.hp.hpl.jena.eyeball.web.statistics;

import com.hp.hpl.jena.eyeball.Report;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.eyeball.web.WebEyeballer;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.*;
import java.io.StringReader;
import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author peter
 */
public class ModelStatistician extends StatisticsUtil implements Statistician 
    {
    public ModelStatistician() 
        {}
    
    public Model gather( WebEyeballer e, Report r, Resource hook ) 
        {
        Model output = ModelFactory.createDefaultModel();
        output.add( hook, RDF.type, EYE.statistic );
        Model input = ModelFactory.createDefaultModel().read( new StringReader( e.getPlainRdfModel() ), e.getBaseURL(), e.getModelType() );
        output.add( hook, EYE.modelSize, output.createTypedLiteral( String.valueOf( input.size() ), XSD.integer.getURI() ) );
        Set<Entry<String, String>> entries = input.getNsPrefixMap().entrySet();
        for (Entry<String, String> entry: entries)
            {
            String key = entry.getKey(), value = entry.getValue();
            if ( !( key.equals( "" ) || value.equals( "" ) ) ) 
                {
                Resource bn = output.createResource();
                output.add( hook, EYE.usedOntology, bn );
                bn.addProperty( EYE.prefix, key );
                bn.addProperty( EYE.ontURI, output.createResource( value ) );
                }
            }
//        Object[] mapping = entries.toArray();
//        for (int i = 0; i < mapping.length; i += 1) 
//            {
//            Resource bn = output.createResource();
//            Entry entry = (Entry) mapping[i];
//            if ( !( ( (String) entry.getKey() ).equals( "" ) || ( (String) entry.getValue() ).equals( "" ) ) ) 
//                {
//                output.add( hook, EYE.usedOntology, bn );
//                bn.addProperty( EYE.prefix, entry.getKey().toString() );
//                bn.addProperty( EYE.ontURI, output.createResource( (String) entry.getValue() ) );
//                }
//            }
        return output;
        }
    
    public String getReport( Model stats ) {
        ResIterator it = stats.listSubjectsWithProperty( RDF.type, EYE.statistic );
        double numStats = stats.listSubjectsWithProperty( RDF.type, EYE.statistic ).toList().size();
        double totalModelTriples = 0;
        List<String> onts = new ArrayList<String>(),
             ontCount = new ArrayList<String>(),
             ontPrefixes = new ArrayList<String>(),
             ontPrefixCount = new ArrayList<String>(),
             ontPrefixMapping = new ArrayList<String>();
        while ( it.hasNext() ) {
            Resource curr = it.nextResource();
            totalModelTriples += Double.parseDouble( curr.getProperty( EYE.modelSize ).getLiteral().getLexicalForm() );
            NodeIterator it2 = stats.listObjectsOfProperty( curr, EYE.usedOntology );
            while ( it2.hasNext() ) {
                Resource bn = stats.createResource( it2.nextNode().asNode().getBlankNodeId() );
                String ont = bn.getProperty( EYE.ontURI ).getObject().asNode().getURI();
                String prefix = bn.getProperty( EYE.prefix ).getLiteral().getLexicalForm();
                inc( onts, ontCount, ont );
                if ( !ontPrefixes.contains( prefix ) )
                    ontPrefixMapping.add( ont );
                inc( ontPrefixes, ontPrefixCount, prefix );
            }
        }
        String out = "ModelStatistician has analysed " + (int)numStats + " sets of statistics and acquired the following results.\n"
                   + "The average size of model submitted for eyeballing is " + formatDouble( totalModelTriples / numStats ) + " triples.\n"
                   + "The following information was gathered about ontology usage:\n";
        ListIterator<String> li = onts.listIterator();
        while ( li.hasNext() ) {
              int curr = li.nextIndex();
              String ont = li.next();
              out += "    " + ont + " occurred on average " + formatDouble( Double.parseDouble( ontCount.get( curr ) ) / numStats ) + " times per model. It was used with the following prefixes: ";
              ListIterator<String> li2 = ontPrefixes.listIterator();
              while ( li2.hasNext() ) {
                  int i = li2.nextIndex();
                  String prefix = li2.next();
                  if ( ontPrefixMapping.get( i ).equalsIgnoreCase( ont ) ) {
                      int numTimes = Integer.parseInt( ontPrefixCount.get( i ) );
                      out += prefix + " (" + numTimes + " time" ;
                      if ( numTimes > 1)
                          out += "s";
                      out += "), ";
                  }
              }
              out = out.substring( 0, out.length() - 2 );
              out += ".\n";
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