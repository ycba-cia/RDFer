/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved - see end of file.
 	$Id: JDBCLoader.java,v 1.4 2008/11/04 09:40:54 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.loaders;

import java.util.regex.*;

import com.hp.hpl.jena.db.IDBConnection;
import com.hp.hpl.jena.rdf.model.*;

/**
    Answer the RDB model which has the given name in the given database
    specified by a jdbc URL.
*/
public class JDBCLoader implements Loader
    {
    static final Pattern p = Pattern.compile( "(jdbc:([^:]+):[^:]+):(.*)" );

    /**
        Answer a Jena database model specified by the extended database
        URL <code>s</code>.
    <p>
        <code>s</code> has the form <code>jdbc:DB:head:model</code>.
        The prefix <code>jdbc:DB:head</code> is taken to be the URL of the
        database connection, and <code>model</code> is the name of a
        Jena model  in that database. The user and password are taken from the
        usual Jena system properties.
    */
    public Model load( String s )
        {
        Matcher m = p.matcher( s );
        m.find();
        String dbServer = m.group(1);
        String type = m.group(2);
        String modelName = m.group(3);
        String user = System.getProperty( "jena.db.user" );
        String password = System.getProperty( "jena.db.password" );
        IDBConnection con = ModelFactory.createSimpleRDBConnection( dbServer, user, password, type );
        return ModelFactory.createModelRDBMaker( con ).createModel( modelName );
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