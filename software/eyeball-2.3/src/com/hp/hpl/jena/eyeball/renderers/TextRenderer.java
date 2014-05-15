/*
    (c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    All rights reserved - see end of file.
    $Id: TextRenderer.java,v 1.6 2008/11/04 09:40:56 chris-dollin Exp $
*/
package com.hp.hpl.jena.eyeball.renderers;

import java.io.PrintStream;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;

/**
    The simple (ha!) text renderer. Delegates to the ExperimentalTextRenderer,
    since that's where the action is nowadays -- see its javadoc for details.
    @author kers
*/
public class TextRenderer implements Renderer
    {    
    protected final ExperimentalTextRenderer.Configured configured;
    
    public TextRenderer( Resource config )
        { this( config, FileManager.get() ); }
    
    public TextRenderer( Resource config, FileManager fm )
        { this.configured = new ExperimentalTextRenderer.Configured( config, fm ); }
    
    public void render( Report r, PrintStream out )
        { configured.open( r.getPredicateRegister() ).render( r.model(), out ); }

    /**
        Answer the label string that will be rendered for the property 
        <code>p</code>.
    */
    public String labelFor( Property p )
        { return configured.labelFor( p ); }
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
