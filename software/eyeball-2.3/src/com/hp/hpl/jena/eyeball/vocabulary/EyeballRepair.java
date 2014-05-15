/*
    (c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    All rights reserved - see end of file.
    $Id: EyeballRepair.java,v 1.5 2008/11/06 11:26:27 chris-dollin Exp $
*/
package com.hp.hpl.jena.eyeball.vocabulary;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Property;

public interface EyeballRepair extends EyeballVocabularyBase
    {
	// Configuration properties    
    public static final Property runPosition = R.p( "runPosition" );
    
    public static final Resource repairConfig = R.r( "repairConfig" );

    public static final Property defaultScheme = R.p( "defaultScheme" );
    
    public static final Property validScheme = R.p( "validScheme" );
    
    public static final Property nsPrefix = R.p( "nsPrefix" );
    
//    public static final Property prefix = R.p( "prefix" );
    
    public static final Property nsURI = R.p( "nsURI" );
    
    public static final Property defaultLiteralFix = R.p( "defaultLiteralFix" );
    
    public static final Property defaultDatatype = R.p( "defaultDatatype" );
    
    public static final Property defaultLanguage = R.p( "defaultLanguage" );
    
    // Reporting properties
    public static final Property repairConfidence = R.p( "repairConfidence" );
    
    public static final Property repairType = R.p( "repairType" );
    
    public static final Property newValue = R.p( "newValue" );
    
    public static final Property statementAdded = R.p( "statementAdded" );
    
    public static final Property statementRemoved = R.p( "statementRemoved" );
    
    public static final Property checkFix = R.p( "checkFix" );
    
    public static final Resource uriPrefix = R.r( "uriPrefix" );
    
    public static final Resource namespacePrefix = R.r( "namespacePrefix" );
    
    // --- Repair Types
    public static final Resource addDefaultType = R.r( "addDefaultType" );
    
    public static final Resource increaseCardinality = R.r( "increaseCardinality" );
    
    public static final Resource increaseNumProperties = R.r( "increaseNumProperties" );
    
    public static final Resource decreaseNumProperties = R.r( "decreaseNumProperties" );
    
    public static final Resource defineClass = R.r( "defineClass" );
    
    public static final Resource removeType = R.r( "removeType" );
    
    public static final Resource setDatatype = R.r( "setDatatype" );
    
    public static final Resource setLanguage = R.r( "setLanguage" );
    
    public static final Resource replaceNamespace = R.r( "replaceNamespace" );
    
    public static final Resource removeDuplicatePrefixes = R.r( "removeDuplicatePrefixes" );
    
    public static final Resource addDefaultScheme = R.r( "addDefaultScheme" );
    
    public static final Resource removeSpaces = R.r( "removeSpaces" );
    
    public static final Resource formHttpAuthority = R.r( "formHttpAuthority" );
    
    public static final Resource removeIllegalChars = R.r( "removeIllegalChars" );
    
    public static final Resource schemeToLowercase = R.r( "schemeToLowercase" );
    
    public static final Resource replacePredicate = R.r( "replacePredicate" );
    
    public static final Resource replaceURI = R.r( "replaceURI" );
    
    // --- Repair Confidence
    public static final Resource fail = R.r( "fail" );
    
    public static final Resource low = R.r( "low" );
    
    public static final Resource moderate = R.r( "moderate" );
    
    public static final Resource good = R.r( "good" );
    
    public static final Resource high = R.r( "high" );
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