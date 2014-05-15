/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved - see end of file.
 	$Id: EyeballReporting.java,v 1.16 2008/11/04 09:40:55 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.vocabulary;

import com.hp.hpl.jena.rdf.model.*;

public interface EyeballReporting extends EyeballVocabularyBase
    {
    public static final Resource Item = R.r( "Item" );

    public static final Property mainProperty = R.p(  "mainProperty"  );
    
    public static final Property suspiciousRestriction = R.p( "suspiciousRestriction" );
    public static final Property equivalentClass = R.p( "equivalentClass" );
    public static final Property subClassOf = R.p( "subClassOf" );

    public static final Resource missingOnProperty = R.p( "missingOnProperty" );
    public static final Resource multipleOnProperty = R.p( "multipleOnProperty" );
    public static final Resource multipleConstraint = R.p( "multipleConstraint" );
    public static final Resource missingConstraint = R.p( "missingConstraint" );
    
    public static final Property sparqlRequireFailed = R.p(  "sparqlRequireFailed" );
    
    public static final Property sparqlProhibitFailed = R.p(  "sparqlProhibitFailed" );
    
    public static final Property resourceRequired = R.p( "resourceRequired" );

    public static final Property cardinalityFailure = R.p( "cardinalityFailure" );

    public static final Property onProperty = R.p( "onProperty" );

    public static final Property onType = R.p( "onType" );

    public static final Property numValues = R.p( "numValues" );

    public static final Property values = R.p( "values" );

    public static final Property inferSubclass = R.p( "inferSubclass" );

    public static final Property inferSuperclass = R.p( "inferSuperclass" );

    public static final Property unknownPredicate = R.p( "unknownPredicate" );

    public static final Property hasNoType = R.p( "hasNoType" );

    public static final Property unknownClass = R.p( "unknownClass" );

    public static final Property onLiteral = R.p( "onLiteral" );

    public static final Property badLanguage = R.p( "badLanguage" );

    public static final Property badURI = R.p( "badURI" );

    public static final Property notFromSchema = R.p( "notFromSchema" );

    public static final Property onResource = R.p( "onResource" );

    public static final Property onStatement = R.p( "onStatement" );

    public static final Property noConsistentTypeFor = R.p( "noConsistentTypeFor" );

    public static final Property forReason = R.p( "forReason" );

    public static final Property badNamespaceURI = R.p( "badNamespaceURI" );

    public static final Property onPrefix = R.p( "onPrefix" );

    public static final Property badDatatypeURI = R.p( "badDatatypeURI" );

    public static final Property hasAttachedType = R.p( "hasAttachedType" );

    public static final Property jenaPrefixFound = R.p( "jenaPrefixFound" );

    public static final Property expected = R.p( "expected" );

    public static final Property badLexicalForm = R.p( "badLexicalForm" );

    public static final Property forDatatype = R.p( "forDatatype" );

    public static final Property uriHasNoLocalname = R.p( "uriHasNoLocalname" );

    public static final Property multiplePrefixesForNamespace = R.p( "multiplePrefixesForNamespace" );

    public static final Property cardinality = R.p( "cardinality" );
    
    public static final Property signatureInclusionFails = R.p( "signatureInclusionFails" );
    
    public static final Property requiredItems = R.p( "requiredItems" );
    
    public static final Property signedItems = R.p( "signedItems" );
    
    public static final Property missingItems = R.p( "missingItems" );
    
    //
    // URI problem messages
    //

    public static final Property forNamespace = R.p( "forNamespace" );

    public static final Property uriFailsPattern = R.p( "uriFailsPattern" );

    public static final Resource uriContainsSpaces = R.r( "uriContainsSpaces" );

    public static final Resource uriHasNoScheme = R.r( "uriHasNoScheme" );

    public static final Resource uriNoHttpAuthority = R.r( "uriNoHttpAuthority" );

    public static final Resource uriFileInappropriate = R.r( "uriFileInappropriate" );

    public static final Resource uriSyntaxFailure = R.r( "uriSyntaxFailure" );

    public static final Resource namespaceEndsWithNameCharacter = R.r( "namespaceEndsWithNameCharacter" );

    public static final Resource unrecognisedScheme = R.r( "unrecognisedScheme" );

    public static final Resource schemeShouldBeLowercase = R.p( "schemeShouldBeLowercase");
    
    // list problems

    public static final Property illFormedList = R.p( "illFormedList" );

    public static final Property because = R.p( "because" );

    public static final Property element = R.p( "element" );

    public static final Property hasNoFirst = R.p(  "hasNoFirst" );

    public static final Property hasNoRest = R.p( "hasNoRest" );

    public static final Property hasMultipleFirsts = R.p( "hasMultipleFirsts" );

    public static final Property hasMultipleRests = R.p(  "hasMultipleRests" );

    public static final Property suspectListIdiom = R.p( "suspectListIdiom" );

    public static final Property illTypedListElement = R.p( "illTypedListElement" );

    public static final Property shouldHaveType = R.p(  "shouldHaveType" );
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