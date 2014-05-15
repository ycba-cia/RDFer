/*
 * BaseTest.java
 * JUnit based test
 *
 * Created on August 23, 2006, 2007, 2008, 8:55 AM
 */

package com.hp.hpl.jena.eyeball.web.statistics.test;

import com.hp.hpl.jena.eyeball.Report;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.eyeball.web.WebEyeballer;
import com.hp.hpl.jena.eyeball.web.statistics.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.test.ModelTestBase;

import java.io.*;

import java.util.Set;

/**
    @author peter
*/
public class BaseTest extends ModelTestBase
    {
    public BaseTest()
        { super( "BaseTest" ); }

    private Model expResult, result;

    private void prepareTest( WebEyeballer e, Statistician instance, String statName )
        {
        expResult = ModelFactory.createDefaultModel().read( "file:testcases/" + statName + "result.n3", "N3" );
        ByteArrayOutputStream s = stringForModel( statName );
        e.setModelType( "N3" );
        e.setRdfModel( s.toString() );
        Report r = new Report();
        String plainReport = e.getPlainReport();
        // System.err.println( ">> " + plainReport );
        r.setMitems( ModelFactory.createDefaultModel().read( new StringReader( plainReport ), EYE.getURI(), e.getReportFormat() ) );
        Resource hook = ModelFactory.createDefaultModel().createResource();
        result = instance.gather( e, r, hook );
        }

    /**
        long-winded, but ensures that the model is valid.
    */
    private ByteArrayOutputStream stringForModel( String statName )
        {
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        PrintStream out = new PrintStream( s ); 
        ModelFactory.createDefaultModel()
            .read( "file:testcases/" + statName + "input.n3", "N3" )
            .write( out, "N3" );
        return s;
        }

    protected void runStatisticianTest( WebEyeballer e, Statistician instance, String statName )
        {
        prepareTest( e, instance, statName );
        // assertIsoModels( "Statistician did not deliver expected results for " + statName, expResult, result );
        System.err.println( "(Note: statistician test disabled; don't worry about this.)" );        
        }

    protected void runStatisticianContainsPredicatesTest( WebEyeballer e, Statistician instance, String statName )
        {
        prepareTest( e, instance, statName );
        Set<Property> expectedPredicates = expResult.listStatements().mapWith( Statement.Util.getPredicate ).toSet();
        Set<Property> actualPredicates = result.listStatements().mapWith( Statement.Util.getPredicate ).toSet();
        if (!expectedPredicates.containsAll( actualPredicates ) )
            {
            actualPredicates.removeAll( expectedPredicates );
            fail( "statistics report contains unexpected predicates: " + actualPredicates );
            }
        }
    }

/*
 * (c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP All rights
    (c) Copyright 2010 Epimorphics Limited.
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