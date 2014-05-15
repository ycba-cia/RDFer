/*
 * Statistician.java
 *
 * Created on August 22, 2006, 2007, 2008, 10:50 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.hp.hpl.jena.eyeball.web.statistics;

import com.hp.hpl.jena.eyeball.Report;
import com.hp.hpl.jena.eyeball.web.WebEyeballer;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 * @author peter
 */
public interface Statistician {
    	
    public Model gather( WebEyeballer e, Report r, Resource hook );
    
    public String getReport( Model stats );
    
    public static final String STATBREAK = "___________________________________________________________________________________\n";

    public static class Operations {    
	
        public static Statistician create( List<Statistician> statisticianList ) {
            int size = statisticianList.size();
            return
                  size == 0 ? new NullStatistician()
                : size == 1 ? statisticianList.get(0)
                : createStatisticianList( statisticianList, size )
                ;
        }

        private static Statistician createStatisticianList( List<Statistician> statisticianList, final int size ) {
            final Statistician [] stat = statisticianList.toArray( new Statistician[statisticianList.size()] );
            return new Statistician() {
                
                public Model gather( WebEyeballer e, Report r, Resource hook ) {
                    Model output = ModelFactory.createDefaultModel();
                    for ( int i = 0; i < size; i ++ ) output.add( stat[i].gather( e, r, hook ) );
                    return output;
                }
                
                public String getReport( Model stats ) {
                    String output = "Report generated on " + new SimpleDateFormat( "dd/MM/yyyy" ).format( new Date() ) + " at " + new SimpleDateFormat( "HH:mm:ss" ).format( new Date() ) + ".\n\n";
                    for ( int i = 0; i < size; i++ ) {
                        output += STATBREAK;
                        output += stat[i].getReport( stats ) + "\n";
                    }                    
                    return output;
                }
            };
        }
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