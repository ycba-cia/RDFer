/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 	All rights reserved - see end of file.
 	$Id: InspectorTestBase.java,v 1.5 2010/03/26 14:50:31 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.inspectors.test;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.eyeball.Inspector;
import com.hp.hpl.jena.eyeball.test.EyeballTestBase;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.iterator.Map1;
import com.hp.hpl.jena.util.iterator.WrappedIterator;

public abstract class InspectorTestBase extends EyeballTestBase
    {
    static final Class<?> [] RESOURCE = new Class<?>[] {Resource.class};

    private final Object [] ROOT = new Object[] {resourceInModel( "x rdf:type eye:Inspector" )};

    public InspectorTestBase()
        {}

    /**
         Subclass override to supply the concrete inspector class that they are
         testing.
    */
    protected abstract Class<? extends Inspector> getInspectorClass();
    
    public void testInspectorConstructor()
        { testInspectorConstructor( getInspectorClass() ); }
    
    /**
        Test that the presumptive inspector class has no-argument and
        one-Resource-argument constructors, and is an implementation
        of <code>Inspector</code>.
    */
    protected void testInspectorConstructor( Class<? extends Inspector> inspectorClass )
        {
        assertTrue( Inspector.class.isAssignableFrom( inspectorClass ) );
        try { inspectorClass.newInstance(); }
        catch (Exception e) { fail( "cannot construct a " + inspectorClass.getName() + " - it has no () constructor" ); }
        try { inspectorClass.getConstructor( RESOURCE ).newInstance( ROOT ); }
        catch (Exception e) { fail( "cannot construct a " + inspectorClass.getName() + " - it has no (Resource) constructor" ); }
        }

    protected void assertOrder( String order, List<? extends Resource> predicates )
        {
        Iterator<String> stream = listOfStrings( order ).iterator();
        Resource current = eyeResource( stream.next() );
        while (stream.hasNext())
            {
            Resource next = eyeResource( stream.next() );
            if (predicates.indexOf( current ) > predicates.indexOf( next )) 
                fail( "predicates " + current.getLocalName() + " and " + next.getLocalName() + " appear out of order in " + predicates );
            current = next;
            }            
        }

    protected static Resource eyeResource( String localName )
        { return ResourceFactory.createResource( EYE.getURI() + localName ); }
    
    protected Set<Resource> eyeResourceSet( String string )
        {
        Map1<String, Resource> asResource = new Map1<String, Resource>()
            {
            public Resource map1( String o ) { return eyeResource( o ); }
            };
        return WrappedIterator.create( listOfStrings( string ).iterator() ).mapWith( asResource ).toSet();
        }
    }


/*
 * (c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
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