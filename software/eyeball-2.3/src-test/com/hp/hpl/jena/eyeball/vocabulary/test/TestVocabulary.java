/*
 	(c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
    All rights reserved - see end of file.
    $Id: TestVocabulary.java,v 1.6 2010/03/26 14:50:00 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.vocabulary.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.hp.hpl.jena.eyeball.vocabulary.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.test.ModelTestBase;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
    @author kers
*/
@RunWith(JUnit4.class) public class TestVocabulary extends ModelTestBase
    {
    public TestVocabulary()
        { super( "TestVocabulary" ); }
    
    @Test public void testConfigLayoutVocabulary()
        {
        assertEyeballURIEquals( "layout", EyeballAssembling.layout );
        assertEyeballURIEquals( "formats", EyeballAssembling.formats );
        assertEyeballURIEquals( "format", EyeballAssembling.format );
        assertEyeballURIEquals( "forPredicate", EyeballAssembling.forPredicate );
        assertEyeballURIEquals( "useFormat", EyeballAssembling.useFormat );
        }
    
    @Test public void testSparqAndPatternlConfigVocabulary()
        {
        assertEyeballURIEquals( "prefix", EyeballAssembling.prefix );
        assertEyeballURIEquals( "check", EyeballAssembling.check );
        assertEyeballURIEquals( "require", EyeballAssembling.require );
        assertEyeballURIEquals( "prohibit", EyeballAssembling.prohibit );
        assertEyeballURIEquals( "sparql", EyeballAssembling.sparql );
        assertEyeballURIEquals( "message", EyeballAssembling.message );
        }
    
    private void assertEyeballURIEquals( String local, Resource r )
        { assertEquals( EYE.getURI() + local, r.getURI() ); }
    
    @Test public void testSparqlFailures()
        {
        assertEyeballURIEquals( "sparqlRequireFailed", EYE.sparqlRequireFailed );
        assertEyeballURIEquals( "sparqlProhibitFailed", EYE.sparqlProhibitFailed );
        }
    
    @Test public void testReportEmptyLocalNames()
        {
        assertEquals( EYE.uri + "reportEmptyLocalNames", EYE.reportEmptyLocalNames.getURI() );
        }

    @Test public void testEyeballURI()
        { assertEquals( "http://jena.hpl.hp.com/Eyeball#", EYE.getURI() ); }

    @Test public void testOnLiteral()
        { assertEquals( EYE.uri + "onLiteral", EYE.onLiteral.getURI() ); }

    @Test public void testOnStatement()
        { assertEquals( EYE.uri + "onStatement", EYE.onStatement.getURI() ); }

    @Test public void testOnResource()
        { assertEquals( EYE.uri + "onResource", EYE.onResource.getURI() ); }

    @Test public void testBadLanguage()
        { assertEquals( EYE.uri + "badLanguage", EYE.badLanguage.getURI() ); }

    @Test public void testForReason()
        { assertEquals( EYE.uri + "forReason", EYE.forReason.getURI() ); }

    @Test public void testOnPrefix()
        { assertEquals( EYE.uri + "onPrefix", EYE.onPrefix.getURI() ); }

    @Test public void testInspector()
        { assertEquals( EYE.uri + "inspector", EYE.inspector.getURI() );  }

    @Test public void testNotFromNamespace()
        { assertEquals( EYE.uri + "notFromSchema", EYE.notFromSchema.getURI() ); }

    @Test public void testJenaPrefixFound()
        { assertEquals( EYE.uri + "jenaPrefixFound", EYE.jenaPrefixFound.getURI() ); }

    @Test public void testForNamespace()
        { 
        assertEquals( EYE.uri + "forNamespace", EYE.forNamespace.getURI() ); 
        }

    @Test public void testNamespaceEndsWithNameCharacter()
        { 
        assertEquals( EYE.uri + "namespaceEndsWithNameCharacter", EYE.namespaceEndsWithNameCharacter.getURI() ); 
        }

    @Test public void testloadConfig()
        { assertEquals( EYE.uri + "loadConfig", EYE.loadConfig.getURI() ); }

    @Test public void testUnknownClass()
        { assertEquals( EYE.uri + "unknownClass", EYE.unknownClass.getURI() ); }

    @Test public void testInferSubclass()
        { assertEquals( EYE.uri + "inferSubclass", EYE.inferSubclass.getURI() ); }

    @Test public void testInferSuperclass()
        { assertEquals( EYE.uri + "inferSuperclass", EYE.inferSuperclass.getURI() ); }

    @Test public void testHasNoType()
        { assertEquals( EYE.uri + "hasNoType", EYE.hasNoType.getURI() ); }

    @Test public void testCardinalityFailure()
        { assertEquals( EYE.uri + "cardinalityFailure", EYE.cardinalityFailure.getURI() ); }

    @Test public void testResourceRequired()
        { assertEquals( EYE.uri + "resourceRequired", EYE.resourceRequired.getURI() ); }

    @Test public void testOnProperty()
        { assertEquals( EYE.uri + "onProperty", EYE.onProperty.getURI() ); }

    @Test public void testOnType()
        { assertEquals( EYE.uri + "onType", EYE.onType.getURI() ); }

    @Test public void testNumValues()
        { assertEquals( EYE.uri + "numValues", EYE.numValues.getURI() ); }

    @Test public void testValues()
        { assertEquals( EYE.uri + "values", EYE.values.getURI() ); }

    @Test public void testCardinality()
        { assertEquals( EYE.uri + "cardinality", EYE.cardinality.getURI() ); }

    @Test public void testMin()
        { assertEquals( EYE.uri + "min", EYE.min.getURI() ); }

    @Test public void testMax()
        { assertEquals( EYE.uri + "max", EYE.max.getURI() ); }
    
    @Test public void testSignatureURIs()
        {
        assertEquals( EYE.uri + "signatureInclusionFails", EYE.signatureInclusionFails.getURI() );
        assertEquals( EYE.uri + "requiredItems", EYE.requiredItems.getURI() );
        assertEquals( EYE.uri + "signedItems", EYE.signedItems.getURI() );
        assertEquals( EYE.uri + "missingItems", EYE.missingItems.getURI() );
        }
    
    @Test public void testSignatureLabels()
        {
        assertEquals( "signature fails: missing configuration elements", getLabel( EYE.signatureInclusionFails ) );
        assertEquals( "required items (from current check)", getLabel( EYE.requiredItems ) );
        assertEquals( "signed items (from signed check)", getLabel( EYE.signedItems ) );
        assertEquals( "missing items", getLabel( EYE.missingItems ) );
        }
    
    @Test public void testIllFormedListURIs()
        { 
        assertEquals( EYE.uri + "illFormedList", EYE.illFormedList.getURI() ); 
        assertEquals( EYE.uri + "hasMultipleFirsts", EYE.hasMultipleFirsts.getURI() ); 
        assertEquals( EYE.uri + "hasNoFirst", EYE.hasNoFirst.getURI() );    
        assertEquals( EYE.uri + "hasNoRest", EYE.hasNoRest.getURI() ); 
        assertEquals( EYE.uri + "hasMultipleRests", EYE.hasMultipleRests.getURI() ); 
        assertEquals( EYE.uri + "element", EYE.element.getURI() ); 
        assertEquals( EYE.uri + "suspectListIdiom", EYE.suspectListIdiom.getURI() ); 
        assertEquals( EYE.uri + "illTypedListElement", EYE.illTypedListElement.getURI() ); 
        assertEquals( EYE.uri + "shouldHaveType", EYE.shouldHaveType.getURI() ); 
        }

    @Test public void testIllFormedListLabels()
        {
        assertEquals( "ill-formed list", getLabel( EYE.illFormedList ) );
        assertEquals( "has no rdf:first property", getLabel( EYE.hasNoFirst ) );
        assertEquals( "has multiple rdf:first properties", getLabel( EYE.hasMultipleFirsts ) );
        assertEquals( "has multiple rdf:rest properties", getLabel( EYE.hasMultipleRests ) );
        assertEquals( "has no rdf:rest property", getLabel( EYE.hasNoRest ) );
        assertEquals( "suspect list idiom", getLabel( EYE.suspectListIdiom ) );
        assertEquals( "should have type", getLabel( EYE.shouldHaveType ) );
        assertEquals( "ill-typed list element", getLabel( EYE.illTypedListElement ) );
        }
    
    @Test public void testSuspiciousRestrictionURIs()
        {
        assertEquals( EYE.uri + "suspiciousRestriction", EYE.suspiciousRestriction.getURI() ); 
        assertEquals( EYE.uri + "missingOnProperty", EYE.missingOnProperty.getURI() );
        assertEquals( EYE.uri + "multipleOnProperty", EYE.multipleOnProperty.getURI() );
        assertEquals( EYE.uri + "equivalentClass", EYE.equivalentClass.getURI() ); 
        assertEquals( EYE.uri + "subClassOf", EYE.subClassOf.getURI() ); 
        assertEquals( EYE.uri + "unknownPredicate", EYE.unknownPredicate.getURI() ); 
        assertEquals( EYE.uri + "missingConstraint", EYE.missingConstraint.getURI() ); 
        assertEquals( EYE.uri + "multipleConstraint", EYE.multipleConstraint.getURI() ); 
        }
    
    @Test public void testSuspiciousRestrictionLabels()
        {
        assertEquals( "subclass of", getLabel( EYE.subClassOf ) );
        assertEquals( "equivalent to", getLabel( EYE.equivalentClass ) );
        assertEquals( "suspicious restriction", getLabel( EYE.suspiciousRestriction ) );
        assertEquals( "missing owl:onProperty property", getLabel( EYE.missingOnProperty ) );
        assertEquals( "multiple owl:onProperty properties", getLabel( EYE.multipleOnProperty ) );
        assertEquals( "multiple cardinality or value constraints", getLabel( EYE.multipleConstraint ) );
        assertEquals( "missing cardinality or value constraints", getLabel( EYE.missingConstraint ) );
        }

    @Test public void testURIVocabularyURIs()
        {
        assertEquals( EYE.uri + "badURI", EYE.badURI.getURI() );
        assertEquals( EYE.uri + "badNamespaceURI", EYE.badNamespaceURI.getURI() );
        assertEquals( EYE.uri + "uriFileInappropriate", EYE.uriFileInappropriate.getURI() ); 
        assertEquals( EYE.uri + "uriContainsSpaces", EYE.uriContainsSpaces.getURI() );
        assertEquals( EYE.uri + "uriHasNoScheme", EYE.uriHasNoScheme.getURI() ); 
        assertEquals( EYE.uri + "uriSyntaxFailure", EYE.uriSyntaxFailure.getURI() ); 
        assertEquals( EYE.uri + "uriNoHttpAuthority", EYE.uriNoHttpAuthority.getURI() ); 
        assertEquals( EYE.uri + "unrecognisedScheme", EYE.unrecognisedScheme.getURI() ); 
        assertEquals( EYE.uri + "uriFailsPattern", EYE.uriFailsPattern.getURI() ); 
        assertEquals( EYE.uri + "schemePattern", EYE.schemePattern.getURI() ); 
        assertEquals( EYE.uri + "schemeShouldBeLowercase", EYE.schemeShouldBeLowercase.getURI() ); 
        }
    
    @Test public void testURIVocabularyLabels()
        {
        assertEquals( "URI contains spaces", getLabel( EYE.uriContainsSpaces ) );
        assertEquals( "file URI inappropriate for namespace", getLabel( EYE.uriFileInappropriate ) );
        assertEquals( "URI has an unrecognised scheme", getLabel( EYE.unrecognisedScheme ) );
        assertEquals( "URI has no scheme", getLabel( EYE.uriHasNoScheme ) );
        assertEquals( "http: URI has no authority component", getLabel( EYE.uriNoHttpAuthority ) );
        assertEquals( "URI syntax error", getLabel( EYE.uriSyntaxFailure ) );
        assertEquals( "namespace URI ends with name character", getLabel( EYE.namespaceEndsWithNameCharacter ) );
        assertEquals( "URI doesn't fit pattern", getLabel( EYE.uriFailsPattern ) );
        assertEquals( "bad namespace URI", getLabel( EYE.badNamespaceURI ) );
        assertEquals( "scheme should be lower case", getLabel( EYE.schemeShouldBeLowercase ) );
        }
    
    @Test public void testVocabulary()
        {
        assertEquals( "predicate not declared in any schema", getLabel( EYE.unknownPredicate ) );
        assertEquals( "class not declared in any schema", getLabel( EYE.unknownClass ) );
        assertEquals( "on literal", getLabel( EYE.onLiteral ) );
        assertEquals( "bad language", getLabel( EYE.badLanguage ) );
        assertEquals( "not from schema", getLabel( EYE.notFromSchema ) );
        assertEquals( "on resource", getLabel( EYE.onResource ) );
        assertEquals( "for reason", getLabel( EYE.forReason ) );
        assertEquals( "on prefix", getLabel( EYE.onPrefix ) );
        assertEquals( "bad datatype URI", getLabel( EYE.badDatatypeURI ) );
        assertEquals( "Jena generated prefix found", getLabel( EYE.jenaPrefixFound ) );
        assertEquals( "resource has no rdf:type", getLabel( EYE.hasNoType ) );
        assertEquals( "for namespace", getLabel( EYE.forNamespace ) );
        assertEquals( "resource [not literal] required", getLabel( EYE.resourceRequired ) );
        assertEquals( "cardinality failure for", getLabel( EYE.cardinalityFailure ) );
        assertEquals( "on property", getLabel( EYE.onProperty ) );
        assertEquals( "on type", getLabel( EYE.onType ) );
        assertEquals( "number of values", getLabel( EYE.numValues ) );
        assertEquals( "values", getLabel( EYE.values ) );
        assertEquals( "min:", getLabel( EYE.min ) );
        assertEquals( "max:", getLabel( EYE.max ) );
        assertEquals( "because", getLabel( EYE.because ) );
        assertEquals( "element", getLabel( EYE.element ) );
        assertEquals( "cardinality range", getLabel( EYE.cardinality ) );
        assertEquals( "SPARQL require failed", getLabel( EYE.sparqlRequireFailed ) );
        assertEquals( "SPARQL prohibit failed", getLabel( EYE.sparqlProhibitFailed ) );
        }

    protected String getLabel( Resource r )
        {
        StmtIterator labels = EYE.getSchema().listStatements( r, RDFS.label, (RDFNode) null );
        if (labels.hasNext()) 
            return labels.nextStatement().getString();
        else
            throw new JenaException( "no label found for resource " + r );
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
