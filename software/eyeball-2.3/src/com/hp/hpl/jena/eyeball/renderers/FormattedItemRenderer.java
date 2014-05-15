/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved.
 	$Id: FormattedItemRenderer.java,v 1.4 2009/01/29 13:39:19 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.renderers;

import java.io.PrintStream;
import java.util.List;

import com.hp.hpl.jena.extras.tools.query.*;
import com.hp.hpl.jena.eyeball.PredicateRegister;
import com.hp.hpl.jena.eyeball.util.Bool;
import com.hp.hpl.jena.eyeball.vocabulary.*;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class FormattedItemRenderer implements ItemRenderer
    {
    protected final RendererServices services;
    protected final PredicateRegister register;
    protected final Resource config;
    
    public FormattedItemRenderer
        ( Resource config, RendererServices services, PredicateRegister register )
        { 
        this.config = config;
        this.services = services; 
        this.register = register;
        }
    
    protected String stringFor( Resource main, Resource sub )
        {
        if (main != null)
            {
            // the ?x where main format [on sub & use ?x].
            ModelQuery mq = ModelQuery.create();
            QueryVariable X = mq.createVariable(), Y = mq.createVariable();
            mq
                .add( config, EYE.formats, main ).add( main, EYE.format, X )
                .add( X, EYE.forPredicate, sub ).add( X, EYE.useFormat, Y )
                ;
            ExtendedIterator<? extends List<Node>> results = mq.run( config.getModel() );
            if (results.hasNext())
                return Y.literal( results.next() ).getLexicalForm();
            }
        return "<LP>: <DO>";
        }
    
    public void render( PrintStream out, Resource item )
        {
        Bool extra = new Bool();
        Statement mainS = item.getProperty( EyeballReporting.mainProperty );
        Resource main = mainS == null ? null : mainS.getResource();
        out.println();
        if (item.hasProperty( EYE.onStatement ))
            {
            services.renderStatement( out, item.getProperty( EYE.onStatement ).getResource() );
            extra.value = true;
            }
         List<Property> p = register.getRegisteredPredicates();
         for (int i = 0; i < p.size(); i += 1)
             renderProperty( out, extra, main, item.listProperties( p.get(i) ) );
         }
    
    protected void renderProperty( PrintStream out, Bool extra, Resource main, StmtIterator statements )
        {
        while (statements.hasNext())
            {
            Statement statement = statements.nextStatement();
            Property predicate = statement.getPredicate();
            if (extra.value) out.print( "    " );
            String format = stringFor( main, predicate );
            String toDisplay = format
                .replaceAll( "<LP>", services.getLabel( predicate ) )
                .replaceAll( "<DO>", services.getDisplay( statement.getObject() ) )
                ;
            // out.println( et.getLabel( predicate ) + ": " + et.getDisplay( statement.getObject() ) );
            out.println( toDisplay );
            extra.value = true;
            }
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


