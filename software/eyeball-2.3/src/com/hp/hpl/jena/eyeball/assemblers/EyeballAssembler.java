/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved - see end of file.
 	$Id: EyeballAssembler.java,v 1.7 2009/01/29 14:12:05 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.assemblers;

import java.util.*;
import com.hp.hpl.jena.assembler.*;
import com.hp.hpl.jena.assembler.assemblers.AssemblerBase;
import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.renderers.TextRenderer;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.Map1;

/**
    An EyeballAssembler constructs an Eyeball object from an RDF description.
    The Eyeball has its assumptions, renderer, and inspectors defined. The
    Assembler is parametrised by the FileManager used to load models
    identified by name.
*/
public class EyeballAssembler extends AssemblerBase
    {
    protected final FileManager fm;
    
    public EyeballAssembler( FileManager fm )
        { this.fm = fm; }
    
    public EyeballAssembler()
        { this( FileManager.get() ); }

    public Object open( Assembler sub, Resource root, Mode mode )
        {
        Inspector i = getInspector( sub, root );
        OntModel a = getAssumptions( sub, root );
        Renderer r = getRenderer( sub, root );
        Analysis an = getAnalysis( sub, root );
        Doctor d = getDoctor( sub, root );
        return new Eyeball( i, a, r, an, d );
        }

    private OntModel getAssumptions( final Assembler sub, Resource root )
        {
        OntModel result = ModelFactory.createOntologyModel();
        Map1<Statement, Model> openObject = new Map1<Statement, Model>() 
            {
            public Model map1( Statement arg0 )
                {
                Statement s = arg0;
                RDFNode ob = s.getObject();
                return ob.isResource() ? (Model) sub.openModel(  (Resource) ob  ) : fm.loadModel( ((Literal) ob).getLexicalForm() );
                }};
        List<Model> assumptions = root.listProperties( EYE.assume ).mapWith( openObject ).toList();
        for (Iterator<Model> it = assumptions.iterator(); it.hasNext();)
            result.add( it.next() );
        return result;
        }

    private Renderer getRenderer( Assembler a, Resource root )
        {
        Resource r = getUniqueResource( root, EYE.renderer );
        return r == null 
            ? new TextRenderer( root.getModel().createResource( root.getModel().expandPrefix( "eye:textRenderer" ) ) ) 
            : (Renderer) a.open( r )
            ;
        }
    
    /**
        A Map1 subclass whose map1 method accepts a Statement (S, P, O) 
        argument and answers the result of opening O (which must be a
        Resource) with the assembler a and checking that that result is an
        instance of needClass.
        
        @author kers
    */
    public static final class OpenObject<T> implements Map1<Statement, T>
        {
        private final Assembler a;
        private Class<? extends T> needClass;
    
        public OpenObject( Assembler a, Class<? extends T> needClass )
            {
            this.a = a;
            this.needClass = needClass;
            }
    
        public T map1( Statement statement )
            {
            Statement s = statement;
            Resource r = s.getResource();
            return checkAssembledObjectClass( a.open( r ) );
            }

        private T checkAssembledObjectClass( Object o )
            {
            if (!needClass.isInstance( o )) 
                throw new JenaException( "expected a " + needClass + " but got a " + o.getClass() );
            return needClass.cast( o );
            }
        }
    
    private Inspector getInspector( final Assembler a, Resource root )
        {
        OpenObject<Inspector> asInspector = new OpenObject<Inspector>( a, Inspector.class );
        Set<Inspector> inspectors = root.listProperties( EYE.inspector ).mapWith( asInspector ).toSet();
        return Inspector.Operations.create( inspectors );
        }
    
    private Analysis getAnalysis( final Assembler a, Resource root )
	    {
        OpenObject<Analysis> asAnalysis = new OpenObject<Analysis>( a, Analysis.class );
	    List<Analysis> analyses = root.listProperties( EYE.analysis ).mapWith( asAnalysis ).toList();
	    return Analysis.Operations.create( analyses );
	    }

	private Doctor getDoctor( final Assembler a, Resource root )
	    {
        OpenObject<Doctor> asDoctor = new OpenObject<Doctor>( a, Doctor.class );
        // System.err.println( ">> EyeballAssembler::getDoctor" );
	    List<Doctor> chosen = root.listProperties( EYE.doctor ).mapWith( asDoctor ).toList();
	    List<Doctor> newDoc = new ArrayList<Doctor>();
    	Model config = root.getModel(); 
    	int index = 1;
	    while (newDoc.size() < chosen.size())
	    	{
	    	try
	    		{
	    		NodeIterator it = config.listObjectsOfProperty( config.listSubjectsWithProperty( EYE.runPosition, config.createTypedLiteral( index ) ).nextResource(), EYE.className );
	    		while ( it.hasNext() )
	    			{
		    		String find = it.nextNode().asNode().getLiteral().getLexicalForm();
		    		for(Doctor d: chosen) // for ( int i = 0; i < chosen.length; i++)
		    			if ( d.getClass().getCanonicalName().equals( find ) )
		    				{
		    				newDoc.add( d );
		    				break; 
		    				}
	    			}
				index += 1;
	    		}
	    	catch ( NoSuchElementException e) { break; } // Finished iteration over config
	    	}	    
	    return Doctor.Operations.create( newDoc );
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