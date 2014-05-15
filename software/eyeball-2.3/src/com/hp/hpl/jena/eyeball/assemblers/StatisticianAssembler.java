/*
 * statisticianAssembler.java
 *
 * Created on August 22, 2006, 2007, 2008, 10:49 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.hp.hpl.jena.eyeball.assemblers;

import com.hp.hpl.jena.assembler.*;
import com.hp.hpl.jena.assembler.assemblers.AssemblerBase;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.eyeball.web.statistics.Statistician;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.iterator.Map1;
import java.lang.reflect.Constructor;
import java.util.List;

/**
 *
 * @author peter
 */
public class StatisticianAssembler extends AssemblerBase
    {
    public Object open( Assembler sub, Resource root, Mode mode )
        {
        String name = getUniqueString( root, EYE.className );
        try
            {
            Class<?> cl = Class.forName( name );
            Constructor<?> co = cl.getConstructor( (Class[]) null );
            Statistician result = (Statistician) co.newInstance( (Object[]) null );
            return result;
            }
        catch (Exception e)
            {
            throw new RuntimeException( "error loading/constructing " + name, e );
            }
        }

    /**
     * Convenience method to generate our large Statistician from config model
     */
    public static Statistician getStatistician( final Assembler a,  Resource root, Model config )
        { // TODO test this class
        Map1<RDFNode, Statistician> openObject = new Map1<RDFNode, Statistician>() 
            {
            public Statistician map1( RDFNode n )
                {
                Resource r = n.as( Resource.class );
                Object o = a.open( r );
                return (Statistician) o;
                }
            };
        List<Statistician> statisticianList = config.listObjectsOfProperty( root, EYE.statistician ).mapWith( openObject ).toList();
        return Statistician.Operations.create( statisticianList );
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