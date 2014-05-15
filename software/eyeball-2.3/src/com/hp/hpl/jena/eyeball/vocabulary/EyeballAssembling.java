/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved - see end of file.
 	$Id: EyeballAssembling.java,v 1.13 2008/12/04 16:03:23 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.vocabulary;

import com.hp.hpl.jena.rdf.model.*;

public interface EyeballAssembling extends EyeballVocabularyBase
    {
    public static final Property inspector = R.p( "inspector" );

    public static final Property analysis = R.p( "analysis" );

    public static final Property doctor = R.p( "doctor" );

    public static final Property statistician = R.p( "statistician" );

    public static final Property shortName = R.p( "shortName" );

    public static final Property include = R.p( "include" );

    public static final Property includeByName = R.p( "includeByName" );

    public static final Property className = R.p( "className" );

    public static final Property schemePattern = R.p( "schemePattern" );

    public static final Property loadConfig = R.p( "loadConfig" );
    
    public static final Property require = R.p( "require" );
    public static final Property prohibit = R.p( "prohibit" );
    
    public static final Property check = R.p(  "check"  );
    public static final Property prefix = R.p(  "prefix"  );
    public static final Property sparql = R.p( "sparql" );
    public static final Property message = R.p( "message" );

    public static final Resource True = R.r( "true" );

    public static final Property checkLiteralTypes = R.p( "checkLiteralTypes" );
    
    public static final Property inspectors = R.p( "inspectors" );

    public static final Property renderer = R.p( "renderer" );

    public static final Resource Eyeball = R.r( "Eyeball" );

    public static final Resource Inspector = R.r( "Inspector" );
    
    public static final Property reportEmptyLocalNames = R.p( "reportEmptyLocalNames" );

    public static final Property assume = R.p( "assume" );

    public static final Resource Renderer = R.r( "Renderer" );

    public static final Resource Analysis = R.r( "Analysis" );

    public static final Resource Doctor = R.r( "Doctor" );

    public static final Resource Statistician = R.r( "Statistician" );

    public static final Resource statisticianAssembly = R.r( "statisticianAssembly" );

    public static final Property checkObjectTypes = R.p( "checkObjectTypes" );

    public static final Property openNamespace = R.p( "openNamespace" );

    public static final Resource mirror = R.r( "mirror" );

    public static final Property path = R.p( "path" );

    public static final Property layout = R.p( "layout" );
    
    public static final Property formats = R.p( "formats" );
    
    public static final Property format = R.p( "format" );
    
    public static final Property forPredicate = R.p( "forPredicate" );
    
    public static final Property useFormat = R.p( "useFormat" );

    public static final Property labels = R.p( "labels" );
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