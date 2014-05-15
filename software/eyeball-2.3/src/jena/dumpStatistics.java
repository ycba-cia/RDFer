/*
    (c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    All rights reserved - see end of file.
    $Id: dumpStatistics.java,v 1.3 2008/11/04 09:40:58 chris-dollin Exp $
*/

package jena;

import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.eyeball.vocabulary.EYESVC;
import com.hp.hpl.jena.eyeball.web.statistics.StatisticalAnalyser;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.PrefixMapping;

/**
    @author peter
*/
public class dumpStatistics 
    { 
    public static void main( String[] args ) 
        {
        if ( args.length == 0 ) args = new String[] {"N3"};
        Model statistics = new StatisticalAnalyser().getStatistics();
        statistics.withDefaultMappings( PrefixMapping.Standard );
        statistics.setNsPrefix( "eye", EYE.getURI() );
        statistics.setNsPrefix( "eyesvc", EYESVC.getBaseUri() );
        statistics.write( System.out, args[0] );
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