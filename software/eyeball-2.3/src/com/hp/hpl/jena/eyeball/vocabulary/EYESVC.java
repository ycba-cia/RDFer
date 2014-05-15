/*
 * EYESVC.java
 *
 * Created on August 14, 2006, 2007, 2008, 9:45 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.hp.hpl.jena.eyeball.vocabulary;

import com.hp.hpl.jena.rdf.model.*;

/**
 *
 * @author peter
 */
public class EYESVC {
    
    public static String getBaseUri(){
        return "http://jena.hpl.hp.com/EyeballService#";
    }
    
    public static final Property hasInspector = p( "hasInspector" );

    public static final Property hasAssume = p( "hasAssume" );
    
// Configuration options
    
    public static final Property doRepair = p( "doRepair" );
    
    public static final Property doAnalysis = p( "doAnalysis" );
    
    public static final Property useInspector = p( "useInspector" );
    
    public static final Property addAssume = p( "addAssume" );
    
    public static final Property withBaseUrl = p( "withBaseUrl" );
    
    public static final Property inputModelType = p( "inputModelType" );
    
    public static final Property reportFormat = p( "reportFormat" );
    
    public static final Property belongsTo = p( "belongsTo" );
    
    public static final Property collectStatistics = p( "collectStatistics" );
    
    public static final Resource textReportOutput = r( "textReportOutput" );
    
    public static final Resource report = r( "report" );
    
    public static final Resource outputModel = r( "outputModel" );
    
    public static final Resource inspectors = r( "inspectors" );
    
    public static final Resource assumes = r( "assumes" );
    
    
    private static Property p( String prop ) {
        return ResourceFactory.createProperty( getBaseUri(), prop );
    }
    
    private static Resource r( String res ) {
        return ResourceFactory.createResource( getBaseUri() + res );
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
