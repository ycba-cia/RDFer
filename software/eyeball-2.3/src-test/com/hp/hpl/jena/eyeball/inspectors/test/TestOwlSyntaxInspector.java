/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 	All rights reserved.
 	$Id: TestOwlSyntaxInspector.java,v 1.4 2010/03/26 14:50:31 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.inspectors.test;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.inspectors.*;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.*;

@RunWith(JUnit4.class) public class TestOwlSyntaxInspector extends InspectorTestBase
    {
    public TestOwlSyntaxInspector()
        {}

    protected Class<? extends Inspector> getInspectorClass()
        { return OwlSyntaxInspector.class; }
    
    @Test public void testInspectorDeclaresPredicates()
        {
        Report r = new Report();
        new OwlSyntaxInspector().begin( r, ontModel() );
        List<Property> predicates = r.getPredicateRegister().getRegisteredPredicates();
        String predicateString = "suspiciousRestriction forReason equivalentClass subClassOf";
        assertEquals( eyeResourceSet( predicateString ), new HashSet<Property>( predicates ) );
        }
    
    @Test public void testHappyWithCompleteRestrictions()
        {
        testHappy( "[owl:onProperty p & owl:allValuesFrom C]" );
        testHappy( "[owl:onProperty p & owl:someValuesFrom C]" );
        testHappy( "[owl:onProperty p & owl:hasValue C]" );
        testHappy( "[owl:onProperty p & owl:minCardinality 2]" );
        testHappy( "[owl:onProperty p & owl:maxCardinality 2]" );
        testHappy( "[owl:onProperty p & owl:cardinality 2]" );
        }

    private void testHappy( String description )
        {
        OntModel x = ontModel( description );
        Report r = inspect( x );
        assertIsoModels( model(), r.model() );
        }
    
    static final OntModel emptyOntModel = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
    
    protected static Report inspect( OntModel x )
        {
        OwlSyntaxInspector ins = new OwlSyntaxInspector();
        Report r = new Report();
        ins.begin( r, emptyOntModel );
        ins.inspectModel( r, x );
        for (StmtIterator st = x.listStatements(); st.hasNext();) ins.inspectStatement( r, st.nextStatement() );
        ins.end( r );
        return r;
        }
    }

/*
 * (c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
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
