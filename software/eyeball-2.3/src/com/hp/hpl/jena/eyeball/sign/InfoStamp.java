/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved.
 	$Id: InfoStamp.java,v 1.6 2008/11/04 09:40:57 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.sign;

import java.util.Calendar;

import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.rdf.model.*;

/**
    Class to generate "stamping" properties
    @author kers
 */
public class InfoStamp
    {    
//    public InfoStamp( String assumed, String checked, String version, String comment )
//        {
//        this( transientSpoo( assumed, checked, version, comment ) );
//        }
//    
//    public static Resource transientSpoo( String assumed, String checked, String version, String comment )
//        {
//        Model m = ModelFactory.createDefaultModel();
//        Resource x = m.createResource();
//        x
//            .addProperty( EYE.assumed, m.createResource( assumed ) )
//            .addProperty( EYE.checked, m.createResource( checked ) )
//            .addProperty( EYE.version, version )
//            .addProperty( EYE.comment, comment );
//        return x;
//        }
    
    protected final Resource root = 
        ModelFactory.createDefaultModel().createResource();
    
    public InfoStamp( Resource x )
        {
        for (StmtIterator it = x.listProperties(); it.hasNext();)
            {
            Statement s = it.nextStatement();
            root.addProperty(  s.getPredicate(), s.getObject() );
            }
        }
    
    public Resource stamp( Calendar when )
        {
        Model stamped = ModelFactory.createDefaultModel();
        Resource x = stamped.createResource();
        for (StmtIterator it = root.listProperties(); it.hasNext();)
            {
            Statement s = it.nextStatement();
            x.addProperty(  s.getPredicate(), s.getObject() );
            }
        return x
//            .addProperty( EYE.assumed, stamped.createLiteral( assumed ) )
//            .addProperty( EYE.checked, stamped.createLiteral( checked ) )
//            .addProperty( EYE.version, version )
//            .addProperty( EYE.comment, comment )
            .addProperty( EYE.dated, stamped.createTypedLiteral( when ) );
        }
    }

/*
 * (c) Copyright 2007 Hewlett-Packard Development Company, LP
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
