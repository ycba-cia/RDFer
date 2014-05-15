/*
 	(c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved - see end of file.
    $Id: EYE.java,v 1.45 2009/01/15 14:20:28 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.vocabulary;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;

/**
     @author kers
*/
public class EYE implements EyeballReporting, EyeballAssembling, EyeballRepair, EyeballStatistics
    {
    public static final String getURI()
        { return uri; }
    
    public static final Resource Set = R.r( "Set" );

    public static final Property min = R.p( "min" );

    public static final Property max = R.p( "max" );

    public static final Property assumed = R.p( "assumed" );
    
    public static final Property checked = R.p( "checked" );

    public static final Property dated = R.p( "dated" );

    public static final Property version = R.p( "version" );

    public static final Property comment = R.p(  "comment" );

    public static final Property signature = R.p( "signature" );
 
    public static final Property signatureVersion = R.p( "signatureVersion" );
    
    private static Model schema = null;

    public static Model getSchema()
        { 
        if (schema == null) schema = FileManager.get().loadModel( "etc/eyeball-schema.n3" );
        return schema;
        }
    }


/*
 * (c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
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
