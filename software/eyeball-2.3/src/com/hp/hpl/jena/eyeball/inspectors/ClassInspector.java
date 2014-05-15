/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved - see end of file.
 	$Id: ClassInspector.java,v 1.11 2009/01/29 12:20:00 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.inspectors;

import java.util.*;

import com.hp.hpl.jena.eyeball.Report;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;

public class ClassInspector extends InspectorBase
    {
    public final Set<Resource> knownClasses = builtinClasses();

    public ClassInspector( Resource root )
        { this(); }

    public ClassInspector()
        {}
    
    public void begin( Report r, OntModel assume )
        {
        r.declareProperty( EYE.unknownClass );
        addClasses( assume ); 
        }

    public void inspectModel( Report r, OntModel model )
        {
        for (Iterator<OntModel> models = model.listSubModels(); models.hasNext();)
            addClasses( models.next() );
        addBaseClassesByProfileClassType( model );
        }

    private void addBaseClassesByProfileClassType( OntModel model )
        {
        Resource ontClassClass = model.getProfile().CLASS();
        StmtIterator it = model.getBaseModel().listStatements( null, RDF.type, ontClassClass );
        while (it.hasNext()) knownClasses.add( it.nextStatement().getSubject() );
        }
    
    private void addClasses( OntModel model )
        {
        for (Iterator<OntClass> it = model.listClasses(); it.hasNext();) knownClasses.add( it.next() );
        }

    public void inspectStatement( Report r, Statement s )
        {
        Property predicate = s.getPredicate();
        RDFNode ob = s.getObject();
        if (predicate.equals( RDFS.subClassOf )) inspectAllegedClass( r, s, s.getSubject() );
        if (predicate.equals( RDFS.subClassOf )) inspectAllegedClass( r, s, ob );
        if (predicate.equals( RDF.type )) inspectAllegedClass( r, s, ob );
        if (predicate.equals( RDFS.domain )) inspectAllegedClass( r, s, ob );
        if (predicate.equals( RDFS.range )) inspectAllegedClass( r, s, ob );
        }
    
    protected final Set<RDFNode> seen = new HashSet<RDFNode>();
    
    protected void inspectAllegedClass( Report r, Statement source, RDFNode res )
        {
        if (res.isURIResource() && seen.add( res ) && !knownClasses.contains( res ))
            r.createItem( source ).addMainProperty( EYE.unknownClass, res );
        }
    
    private static Set<Resource> builtinClasses()
        {
        Set<Resource> result = new HashSet<Resource>();
        addRDFClasses( result );
        addRDFSClasses( result );
        addOWLClasses( result );        
        addXSDClasses( result );
        return result;
        }

    private static void addXSDClasses( Set<Resource> result )
        {
        result.add( XSD.xfloat );
        result.add( XSD.xdouble );
        result.add( XSD.xint );
        result.add( XSD.xlong );
        result.add( XSD.xshort );
        result.add( XSD.xbyte );
        result.add( XSD.unsignedByte );
        result.add( XSD.unsignedShort );
        result.add( XSD.unsignedInt );
        result.add( XSD.unsignedLong );
        result.add( XSD.decimal );
        result.add( XSD.integer );
        result.add( XSD.nonPositiveInteger );
        result.add( XSD.nonNegativeInteger );
        result.add( XSD.positiveInteger );
        result.add( XSD.negativeInteger );
        result.add( XSD.xboolean );
        result.add( XSD.xstring );
        result.add( XSD.normalizedString );
        result.add( XSD.anyURI );
        result.add( XSD.token );
        result.add( XSD.Name );
        result.add( XSD.QName );
        result.add( XSD.language );
        result.add( XSD.NMTOKEN );
        result.add( XSD.ENTITY );
        result.add( XSD.ID );
        result.add( XSD.NCName );
        result.add( XSD.IDREF );
        result.add( XSD.NOTATION );
        result.add( XSD.hexBinary );
        result.add( XSD.base64Binary );
        result.add( XSD.date );
        result.add( XSD.time );
        result.add( XSD.dateTime );
        result.add( XSD.duration );
        result.add( XSD.gDay );
        result.add( XSD.gMonth );
        result.add( XSD.gYear );
        result.add( XSD.gYearMonth );
        result.add( XSD.gMonthDay );
        
        }

    private static void addOWLClasses( Set<Resource> result )
        {
        result.add( OWL.Class ); 
        result.add( OWL.DataRange ); 
        result.add( OWL.Ontology ); 
        result.add( OWL.DeprecatedClass ); 
        result.add( OWL.AllDifferent ); 
        result.add( OWL.DatatypeProperty ); 
        result.add( OWL.SymmetricProperty ); 
        result.add( OWL.TransitiveProperty ); 
        result.add( OWL.DeprecatedProperty ); 
        result.add( OWL.AnnotationProperty ); 
        result.add( OWL.Restriction ); 
        result.add( OWL.OntologyProperty ); 
        result.add( OWL.ObjectProperty ); 
        result.add( OWL.FunctionalProperty ); 
        result.add( OWL.InverseFunctionalProperty ); 
        result.add( OWL.Nothing );
        }

    private static void addRDFSClasses( Set<Resource> result )
        {
        result.add( RDFS.Class );
        result.add( RDFS.Datatype );
        result.add( RDFS.Resource );
        result.add( RDFS.Literal );
        result.add( RDFS.Container );
        }

    private static void addRDFClasses( Set<Resource> result )
        {
        result.add( RDF.Alt );
        result.add( RDF.Bag );
        result.add( RDF.Seq );
        result.add( RDF.List );
        result.add( RDF.Property );
        result.add( RDF.Statement );
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