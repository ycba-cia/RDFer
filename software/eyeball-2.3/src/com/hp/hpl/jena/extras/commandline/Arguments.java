/*
 	(c) Copyright 2006, 2007 Hewlett-Packard Development Company, LP
 	All rights reserved.
 	$Id: Arguments.java,v 1.1 2010/03/19 14:34:04 chris-dollin Exp $
*/
package com.hp.hpl.jena.extras.commandline;

import java.util.*;

import com.hp.hpl.jena.shared.DoesNotExistException;

/**
    Command-line argument handling; used by Eyeball and TestFramework
    (well, it will be, soon)
    @author kers
*/
public class Arguments
    {
    protected Map<String, List<String>> map = new HashMap<String, List<String>>();
    protected Set<String> allowed = new HashSet<String>();
    protected String unspecified = "none";

    public Arguments( String something )
        {
        StringTokenizer st = new StringTokenizer( something );
        while (st.hasMoreTokens()) allowed.add( unspecified = st.nextToken() );
        }

    public static Arguments readArguments( String allowed, String[] args )
        {
        return new Arguments( allowed ).processArgs( args );
        }
    
    public String getDefaultKeyword()
        { return unspecified; }

    public Arguments processArgs( String [] args )
        {
        String current = unspecified;
        for (int i = 0; i < args.length; i += 1)
            {
            String arg = args[i];
            if (arg.charAt(0) == '-')
                ensureAllowedAndPresent( current = arg.substring(1) );
            else put( current, arg );
            }
        return this;
        }

    public Arguments validate( String required )
        {
        StringTokenizer st = new StringTokenizer( required );
        while (st.hasMoreTokens())
            {
            String r = st.nextToken();
            if (map.containsKey( r ) == false || map.get( r ).isEmpty())
                throw new RuntimeException( "required option -" + r + " absent or empty" );
            }
        return this;
        }

    protected void ensureAllowedAndPresent( String key )
        {
        if (!allowed.contains( key )) throw new DoesNotExistException( key );
        if (map.get( key ) == null) map.put( key, new ArrayList<String>() );
        }

    public List<String> listFor( String key )
        {
        List<String> L = map.get( key );
        return L == null ? new ArrayList<String>() : L;
        }

    public List<String> listFor( String key, String ifAbsent )
        {
        List<String> L = map.get( key );
        return L == null ? listOfStrings( ifAbsent ) : L;
        }

    protected List<String> listOfStrings( String s )
        {
        List<String> result = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer( s );
        while (st.hasMoreTokens()) result.add( st.nextToken() );
        return result;
        }

    public String valueFor( String key )
        { return valueFor( key, null ); }

    public String valueFor( String key, String ifAbsent )
        {
        List<String> L = map.get( key );
        return L == null || L.size() == 0 ? ifAbsent : (String) L.get(0);
        }

    public void put( String key, String value )
        {
        List<String> L = map.get( key );
        if (L == null) map.put( key, L = new ArrayList<String>() );
        L.add( value );
        }

    public boolean has( String key )
        { return map.get( key ) != null; }
    
    public boolean hasSomeValues( String key )
        {
        List<String> L = map.get( key );
        return L != null && L.size() > 0;
        }
    }

/*
 * (c) Copyright 2005, 2006, 2007 Hewlett-Packard Development Company, LP
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