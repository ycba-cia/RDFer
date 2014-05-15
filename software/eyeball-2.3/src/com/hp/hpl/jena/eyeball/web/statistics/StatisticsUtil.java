/*
 * statUtil.java
 *
 * Created on August 24, 2006, 2007, 2008, 10:46 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.hp.hpl.jena.eyeball.web.statistics;

import java.util.List;

/**
 *
 * @author peter
 */
public class StatisticsUtil
    {
    protected static void inc( List<String> key, List<String> value, String target )
        {
        inc( key, value, target, 1 );
        }

    protected static void inc( List<String> key, List<String> value, String target, int amt )
        {
        int i = key.indexOf( target );
        if (i != -1) value.set( i, String.valueOf( Integer.parseInt( value.get( i ) ) + amt ) );
        else
            {
            key.add( target );
            value.add( String.valueOf( amt ) );
            }
        }

    protected static String formatDouble( double val )
        {
        String out = String.valueOf( val );
        if (out.length() > 5)
            {
            boolean round = false;
            if (round && out.charAt( 5 ) != '.') 
                if (Integer.parseInt( String.valueOf( out.charAt( 5 ) ) ) > 4) 
                    round = true;
            out = out.substring( 0, 5 );
            if (round && out.charAt( 4 ) != '.') 
                out = out.substring( 0, 4 ) + String.valueOf( Integer.parseInt( String.valueOf( out.charAt( 4 ) ) ) + 1 );
            else
                // trim off final decimal point
                out = out.substring( 0, 4 );
            }
        return out;
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