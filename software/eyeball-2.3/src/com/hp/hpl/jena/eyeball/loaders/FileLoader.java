/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved - see end of file.
 	$Id: FileLoader.java,v 1.5 2008/11/05 14:42:27 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.loaders;

import com.hp.hpl.jena.assembler.Assembler;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.NotFoundException;
import com.hp.hpl.jena.util.FileManager;

/**
    A FileLoader wraps a FileManager, providing only the load(String) method,
    and intercepting names of the form "via:ROOT@FILE" to load the model
    described by ROOT in the assembly file FILE.
*/
public class FileLoader implements Loader
    {
    protected final FileManager fm;
    
    /**
        Initialise this FileLoader with the specified FileManager.
    */
    public FileLoader( FileManager fm )
        { this.fm = fm; }
    
    /**
        Initialise this FileLoader with the default file manager, FileManager.get().
    */
    public FileLoader()
        { this( FileManager.get() ); }
    
    /**
        Answer the model returned by our FileManager.
    */
    public Model load( String s )
        { return s.startsWith( "ja:" ) ? loadVia( s.substring( 3 ) ) : fm.loadModel( s ); }

    private Model loadVia( String rootAtFile )
        {
        int at = rootAtFile.indexOf( '@' );
        String rootString = rootAtFile.substring( 0, at ), fileName = rootAtFile.substring( at + 1 );
        Model assembly = load( fileName );
        if (assembly == null) throw new NotFoundException( fileName );
        Resource root = assembly.createResource( assembly.expandPrefix( rootString ) );
        return Assembler.general.openModel( root );
        }

    /**
        Answer the FileManager this FileLoader uses to locate and load files.
    */
    public FileManager getFileManager()
        { return fm; }
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