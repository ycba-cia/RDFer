/*
 (c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 All rights reserved - see end of file.
 $Id: InspectorAssembler.java,v 1.6 2009/01/29 14:12:05 chris-dollin Exp $
 */

package com.hp.hpl.jena.eyeball.assemblers;

import java.lang.reflect.Constructor;
import java.util.*;

import com.hp.hpl.jena.assembler.*;
import com.hp.hpl.jena.assembler.assemblers.AssemblerBase;
import com.hp.hpl.jena.eyeball.Inspector;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.iterator.*;
import com.hp.hpl.jena.vocabulary.RDF;

/**
    Assembles an Inspector, taking into account those defined directly by
    a class name, those referred to by their URIs in an include, and those
    referred to by their short names in an includeByName.
    
 	@author kers
*/
public class InspectorAssembler extends AssemblerBase
    {
    public Object open( Assembler a, Resource root, Mode mode )
        {
        Set<Inspector> all = new HashSet<Inspector>(); 
        all.addAll( root.listProperties( EYE.className ).mapWith( getInspectorByClassString( root ) ).toSet() ); 
        all.addAll( root.listProperties( EYE.include ).mapWith( getInspectorByInclude( a ) ).toSet() ); 
        all.addAll( root.listProperties( EYE.includeByName ).mapWith( getInspectorByShortName( a, root ) ).toSet() );
        return Inspector.Operations.create( all );
        }
    
    /**
        Mapper to map statements (S eye:className name) to Inspector objects
        which are instantiations of the class with that name, passing in the 
        root object for any configuration properties.
    */
    static Map1<Statement, Inspector> getInspectorByClassString( final Resource root )
        {
        return new Map1<Statement, Inspector>() 
            {
            public Inspector map1( Statement s )
                { 
                String className = s.getString();
                try
                    {
                    Class<?> c = Class.forName( className );
                    Constructor<?> co = c.getConstructor( new Class[]{ Resource.class } );
                    return (Inspector) co.newInstance( new Object[] { root } );
                    }
                catch (Exception e)
                    {
                    throw new RuntimeException( "could not load/instantiate class " + className, e );
                    }
                }
            };
        }

    /**
        Mapper to map statements (S include I) to Inspector objects which
        which are assembled by using assembler `a` on the root resource `I`.
    */
    static Map1<Statement, Inspector> getInspectorByInclude( final Assembler a )
        {
        return new Map1<Statement, Inspector>()
            { 
            public Inspector map1( Statement s ) 
                { return (Inspector) a.open( s.getResource() ); }
            };
        }

    /**
        Filter which accepts resources which have type eye:Inspector.
    */
    static final Filter<Resource> isInspector = new Filter<Resource>() 
        {
        public boolean accept( Resource r )
            { return r.hasProperty( RDF.type, EYE.Inspector );  }
        };
    
    /**
        Mapper which maps a resource (with type eye:Inspector) to an Inspector
        by assembling it with `a`.
    */
    static Map1<Resource, Inspector> assembleWith( final Assembler a )
        {
        return new Map1<Resource, Inspector>()
            {
            public Inspector map1( Resource root ) { return (Inspector) a.open( root ); }  
            };
        }
    
    /**
        Mapper which maps statements (S includeByName N) to an inspector
        which is the composition of all the inspectors with short name N
        assembled using `a` in the model of S.
    */
    static Map1<Statement, Inspector> getInspectorByShortName( final Assembler a, final Resource root )
        {
        return new Map1<Statement, Inspector>()
            {
            public Inspector map1( Statement s )
                {
                String targetName = s.getString();
                return Inspector.Operations.create
                    ( root.getModel()
                    .listStatements( null, EYE.shortName, targetName )
                    .mapWith( Statement.Util.getSubject )
                    .filterKeep( isInspector )
                    .mapWith( assembleWith( a ) )
                    .toSet()
                    );
                }
            };
        }
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