/*
 	(c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
    All rights reserved - see end of file.
    $Id: EyeballTestBase.java,v 1.4 2010/03/26 14:50:31 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.test;

import java.util.*;

import com.hp.hpl.jena.assembler.test.AssemblerTestBase;
import com.hp.hpl.jena.extras.tools.notations.likeN3.Parser;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.vocabulary.RDF;

/**
     Base methods handy for Eyeball test cases.
*/
public abstract class EyeballTestBase extends AssemblerTestBase
    {
    public EyeballTestBase()
        { super( "EyeballTestBase" ); }

    protected <T> T justOne( Iterator<T> it )
        {
        if (it.hasNext() == false)
            throw new RuntimeException( "at least one result expected" );
        T result = it.next();
        if (it.hasNext())
            throw new RuntimeException( "only one result expected" );
        return result;
        }

    protected <T> Set<T> single( T item )
        { Set<T> result = new HashSet<T>();
        result.add( item );
        return result; }

    protected <T> Set<T> both( T x, T y )
        { Set<T> result = new HashSet<T>();
        result.add( x );
        result.add( y );
        return result; }

    protected OntModel ontModel()
        { return ontModel( "" ); }

    protected OntModel ontModel( String facts )
        {
        OntModel result =  ModelFactory.createOntologyModel();
        result.setNsPrefixes( PrefixMapping.Extended );
        setRequiredPrefixes( result );
        new Parser( facts ).parseInto( result );
        return result;
        }
    
    /**
        Answer an eyeballModel which also contains the statement
        <i>X rdf:type eye:Item</i> for each subject <i>X</i> in the contents.
    */
    protected Model itemModel( String contents )
        {
        Model result = model( contents );
        for (Resource s: listPureSubjects( result )) s.addProperty( RDF.type, EYE.Item );
        return result;
        }

    protected Model itemModel( String contents, Statement on )
        {
        Model result = model( contents );
        ReifiedStatement reified = result.createReifiedStatement( on );
        Set<Resource> subjects = listPureSubjects( result );
        for (Resource s: subjects)
            s.addProperty( RDF.type, EYE.Item ).addProperty( EYE.onStatement, reified );
        return result;
        }

    /**
        Answer those subjects of a model that do not also appear as objects.
    */
    private Set<Resource> listPureSubjects( Model result )
        {
        Set<Resource> answer = result.listSubjects().toSet();
        answer.removeAll( result.listObjects().toSet() );
        return answer;
        }

    /**
        Eyeball test models are constructed using the extended parser.
    */
    protected Model modelAddFacts( Model result, String s )
        { return new Parser( s ).parseInto( result ); }

    /**
        Eyeball models have an additional `eye` prefix.
    */
    protected Model setRequiredPrefixes( Model m )
        {
        m.setNsPrefix( "eye", EYE.uri );
        return super.setRequiredPrefixes( m );
        }
    }


/*
 * (c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
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
