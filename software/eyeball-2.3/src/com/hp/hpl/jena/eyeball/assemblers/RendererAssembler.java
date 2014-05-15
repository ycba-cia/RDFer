/*
    (c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    All rights reserved - see end of file.
    $Id: RendererAssembler.java,v 1.4 2009/01/29 14:12:05 chris-dollin Exp $
*/
package com.hp.hpl.jena.eyeball.assemblers;

import java.lang.reflect.Constructor;

import com.hp.hpl.jena.assembler.Assembler;
import com.hp.hpl.jena.assembler.Mode;
import com.hp.hpl.jena.assembler.assemblers.AssemblerBase;
import com.hp.hpl.jena.eyeball.Renderer;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.rdf.model.Resource;

public class RendererAssembler extends AssemblerBase
    {
    public Object open( Assembler sub, Resource root, Mode mode )
        {
        String name = getUniqueString( root, EYE.className );
        try
            {
            Class<?> c = Class.forName( name );
            Constructor<?> co = c.getConstructor( new Class[] {Resource.class} );
            Renderer result = (Renderer) co.newInstance( new Object[] {root} );
            return result;
            }
        catch (Exception e)
            {
            throw new RuntimeException( "error loading/constructing " + name, e );
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