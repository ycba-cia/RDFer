/*
    (c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    All rights reserved - see end of file.
    $Id: EyeballStatistics.java,v 1.4 2008/11/04 09:40:55 chris-dollin Exp $
*/
package com.hp.hpl.jena.eyeball.vocabulary;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Property;

public interface EyeballStatistics extends EyeballVocabularyBase
    {
	// Configuration properties    
    public static final Resource statisticsConfig = R.r( "statisticsConfig" );
    
    public static final Resource statistic = R.r( "statistic" );
    
    public static final Property dbURI = R.p( "dbURI" );
    
    public static final Property dbUser = R.p( "dbUser" );
    
    public static final Property dbPass = R.p( "dbPass" );
    
    public static final Property dbType = R.p( "dbType" );
    
    public static final Property dbDriver = R.p( "dbDriver" );
    
    public static final Property statisticsModel = R.p( "statisticsModel" );
    
    public static final Property eyeballerState = R.p( "eyeballerState" );
    
    public static final Property requestDate = R.p( "requestDate" );
    
    public static final Property requestTime = R.p( "requestTime" );
    
    public static final Property modelSize = R.p( "modelSize" );
    
    public static final Property usedOntology = R.p( "usedOntology" );
    
    public static final Property ontURI = R.p( "ontURI" );
    
    public static final Property fault = R.p( "fault" );
    
    public static final Property number = R.p( "number" );
    
    public static final Property failure = R.p( "failure" );
    
    public static final Property percentageChanged = R.p( "percentageChanged" );
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