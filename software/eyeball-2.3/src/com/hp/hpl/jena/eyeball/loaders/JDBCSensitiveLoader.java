/*
 (c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 All rights reserved - see end of file.
 $Id: JDBCSensitiveLoader.java,v 1.4 2008/11/05 12:34:36 chris-dollin Exp $
 */

package com.hp.hpl.jena.eyeball.loaders;

import com.hp.hpl.jena.rdf.model.Model;

/**
    A JDBCSensitiveLoader will pick one of two laoders, depending on 
    whether the URI it's offered starts with <b>jdbc:</b> or not.
*/
public class JDBCSensitiveLoader implements Loader
    {
    /**
        The loader to use to load jdbc: URIs.
    */
    protected final Loader db;

    /**
        The loader to use to laod non-jdbc: URIs.
    */
    protected final Loader base;

    /**
        Initialise this loader with <code>db</code> for loading jdbc: URIs and
        <code>base</code> for loading non-jdbc: URIs.
    */
    public JDBCSensitiveLoader( Loader db, Loader base )
        {
        this.db = db;
        this.base = base;
        }

    /**
        Answer the db loaded or plain loaded model, depending on whether
        <code>s</code> starts with jdbc:. 
    */
    public Model load( String s )
        { return s.startsWith( "jdbc:" ) ? db.load( s ) : base.load( s ); }
    }

/*
 * (c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP All rights
 * reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The name of the author may not
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */