/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 	All rights reserved - see end of file.
 	$Id: TestVocabularyInspector.java,v 1.4 2010/03/26 14:50:31 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.inspectors.test;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.inspectors.VocabularyInspector;
import com.hp.hpl.jena.eyeball.vocabulary.EyeballVocabularyBase;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;

@RunWith(JUnit4.class) public class TestVocabularyInspector extends InspectorTestBase
    {
    public TestVocabularyInspector()
        {}
    
    protected final Report r = new Report();
    
    protected final VocabularyInspector ins = new VocabularyInspector();

    protected Class<? extends Inspector> getInspectorClass()
        { return VocabularyInspector.class; }

    @Test public void testVocabularyInspectorDeclaresPredicates()
        {
        ins.begin( r, ontModel() );
        List<Property> predicates = r.getPredicateRegister().getRegisteredPredicates();
        assertEquals( eyeResourceSet( "notFromSchema onResource" ), new HashSet<Property>( predicates ) );
        assertOrder( "notFromSchema onResource", predicates );
        }
    
    @Test public void testInspectStatementCallsInspectURI()
        {
        final List<String> uris = new ArrayList<String>();
        Inspector i = new VocabularyInspector() 
            {
            public void inspectURI( Report ignored, Statement s, Resource res )
                { uris.add( res.getURI() ); }
            };
        i.inspectStatement( new Report(), statement( "S P O" ) );
        assertEquals( listOfStrings( "eh:/S eh:/P eh:/O" ), uris );
        }
    
    @Test public void testNoAssumptionsCreatesBuiltinNamespaceTable()
        {
        OntModel assume = ontModel();
        ins.begin( new Report(), assume );
        assertEquals( builtinNamespaces(), ins.map.keySet() );
        }

    private Set<String> builtinNamespaces()
        {
        Set<String> result = new HashSet<String>();
        result.add( RDF.getURI() );
        result.add( RDFS.getURI() );
        result.add( "http://www.w3.org/2000/01/" );
        result.add( EyeballVocabularyBase.XSD_URI );
        result.add( DC.getURI() );
        result.add( OWL.getURI() );
        return result;
        }

    @Test public void testAssumptionsFillNamespaceTable()
        {
        OntModel assume = ontModel( "ex:/A/x ex:/A/p ex:/B/y" );
        ins.begin( new Report(), assume );
        assertEquals( setOfStrings( "x p" ), ins.map.get( "ex:/A/" ) );
        assertEquals( setOfStrings( "y" ), ins.map.get( "ex:/B/" ) );
        }
    
    @Test public void testSubmodelsFillNamespaceTable()
        {
        OntModel model = ontModel();
        Model subModel = model( "ex:/A/x ex:/A/p ex:/B/y" );
        model.addSubModel( subModel );
        ins.inspectModel( new Report(), model );
        assertEquals( setOfStrings( "x p" ), ins.map.get( "ex:/A/" ) );
        assertEquals( setOfStrings( "y" ), ins.map.get( "ex:/B/" ) );
        }
    
    @Test public void testBothAssumptionsAndModelsFillNamespaceTable()
        {
        OntModel model = ontModel();
        model.addSubModel( model( "ex:/A/x ex:/A/p ex:/B/y" ) );
        ins.begin( new Report(), ontModel( "ex:/C/a ex:/D/q ex:/C/b" ) );
        ins.inspectModel( new Report(), model );
        assertEquals( setOfStrings( "x p" ), ins.map.get( "ex:/A/" ) );
        assertEquals( setOfStrings( "y" ), ins.map.get( "ex:/B/" ) );
        assertEquals( setOfStrings( "a b" ), ins.map.get( "ex:/C/" ) );
        assertEquals( setOfStrings( "q" ), ins.map.get( "ex:/D/" ) );
        }

    protected final Statement S = statement( "not in schema" );
    
    @Test public void testURIWithUnknownNamespaceGeneratesNoReport()
        {
        ins.inspectURI( r, S, resource( "X" ) );
        assertIsoModels( model( "" ), r.model() );
        }
    
    @Test public void testKnownURIGeneratesNoReport()
        {
        ins.begin( r, ontModel( "X P Y" ) );
        ins.inspectURI( r, S, resource( "X" ) );
        assertIsoModels( model( "" ), r.model() );
        }
    
    @Test public void testURIInKnownNamespaceButNoDeclaredGeneratesReport()
        {
        ins.begin( r, ontModel( "X P Y" ) );
        ins.inspectURI( r, S, resource( "Z" ) );
        Model wanted = wanted( "[eye:mainProperty eye:notFromSchema & eye:notFromSchema 'eh:/' & eye:onResource eh:/Z]" );
        assertIsoModels( wanted, r.model() );
        }
    
    @Test public void testRDFNamespace()
        {
        ins.inspectURI( r, S, resource( "rdf:noSuch" ) );
        Model wanted = wanted( "[eye:mainProperty eye:notFromSchema & eye:notFromSchema '<a>' & eye:onResource rdf:noSuch]".replaceAll( "<a>", RDF.getURI() ) );
        assertIsoModels( wanted, r.model() );
        }
    
    private Model wanted( String string )
        { return itemModel( string, S ); }

    @Test public void testRDFSNamespace()
        {
        ins.inspectURI( r, S, resource( "rdfs:noSuch" ) );
        Model wanted = wanted( "[eye:mainProperty eye:notFromSchema & eye:notFromSchema '<a>' & eye:onResource rdfs:noSuch]".replaceAll( "<a>", RDFS.getURI() ) );
        assertIsoModels( wanted, r.model() );
        }

    @Test public void testXSDNamespace()
        {
        ins.inspectURI( r, S, resource( "xsd:noSuch" ) );
        Model wanted = wanted( "[eye:mainProperty eye:notFromSchema & eye:notFromSchema '<a>' & eye:onResource xsd:noSuch]".replaceAll( "<a>", EyeballVocabularyBase.XSD_URI ) );
        assertIsoModels( wanted, r.model() );
        }
    
    @Test public void testDuplicatedURIGeneratesOnlyOneReport()
        {
        OntModel assume = ontModel( "X P Y" );
        ins.begin( r, assume );
        ins.inspectURI( r, S, resource( "Z" ) );
        ins.inspectURI( r, S, resource( "Z" ) );
        Model wanted = wanted( "[eye:mainProperty eye:notFromSchema & eye:notFromSchema 'eh:/' & eye:onResource eh:/Z]" );
        assertIsoModels( wanted, r.model() );
        }
    
    @Test public void testDoesNotReportOpenNamespace()
        {
        Resource root = resourceInModel( "root eye:openNamespace open:/" );
        OntModel assume = ontModel( "open:/cabbage P Y" );
        VocabularyInspector ins = new VocabularyInspector( root );
        ins.begin( r, assume );
        ins.inspectURI( r, S, resource( "open:/rhubarb" ) );
        assertIsoModels( model(), r.model() );
        }
    }


/*
 * (c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
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