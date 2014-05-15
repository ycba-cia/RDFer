/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved.
 	$Id: ListInspector.java,v 1.16 2009/01/29 13:39:20 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.inspectors;

import java.util.*;

import com.hp.hpl.jena.extras.tools.query.*;
import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.vocabulary.EyeballReporting;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.*;

/**
     A ListInspector looks for list types T in the model, and properties with
     range T. Each such property value is checked for structural integrity:
     each node should have exactly one rdf:first and exactly one rdf:rest.
<p>
    A "list type" is rdf:List, or any subclass which looks like the typed
    list idiom:
    <pre>
        T subClassOf List
           ; subClassOf [onProperty rest; allValuesFrom T]
           ; subClassOf [onProperty first; allValuesFrom E]
    </pre> 
<p>
    A subclass of List defined as a subclass of any bnode which has
    rdf:first or rdf:rest as any property value, but is not shaped like the idiom,
    is deemed to be a "suspect" type, and reported.
*/
public class ListInspector extends InspectorBase
    {
    public ListInspector( Resource config )
        {}
    
    public ListInspector()
        {}

    protected final Map<Resource, Resource> idiomaticListTypes = new HashMap<Resource, Resource>();
    
    protected final Map<Object, Resource> propertiesWithListRange = new HashMap<Object, Resource>();
    
    protected final Set<Resource> suspectListTypes = new HashSet<Resource>();

    /**
        Answer a map of maplets (T -&gt; U) where T is a list type and U is
        the type of elements in that list. <i>Do not alter this map.</i>
        The map will be empty until <code>inspectModel</code> has
        been invoked.
    */
    public Map<Resource, Resource> getIdiomaticListTypes()
        { return idiomaticListTypes; }

    /**
        Answer a map of maplets (P -&gt; T) where P is a property with list
        type T as range. <i>Do not alter this map.</i> The map will be empty 
        until <code>inspectModel</code> has been invoked.
    */
    public Map<Object, Resource> getIdiomaticListProperties()
        { return propertiesWithListRange; }

    /**
        Answer the set of subclasses of List that look like they might have
        been intended to be defined using the typed-list idiom, but failed in
        some way.
    */
    public Set<Resource> getSuspectListTypes()
        { return suspectListTypes; }
    
    public void inspectModel( Report r, OntModel m )
        {
        r.declareProperty( EyeballReporting.suspectListIdiom );
        r.declareOrder( EyeballReporting.illFormedList, EyeballReporting.because );
//        r.declareOrder( EyeballReporting.illFormedList, EyeballReporting.hasNoFirst );
        idiomaticListTypes.put( RDF.List, RDFS.Resource );
        findListTypes( m );
        findSuspecListTypes( m );
        findListProperties( m );
        reportSuspectListTypes( r );
        }

    private void reportSuspectListTypes( Report r )
        {
        for (Iterator<Resource> it = suspectListTypes.iterator(); it.hasNext();)
            {
            Resource type = it.next();
            r.createItem().addMainProperty( EyeballReporting.suspectListIdiom, type );
            }
        }

    private void findListTypes( OntModel m )
        {
        ModelQuery mq = ModelQuery.create();
        QueryVariable 
            listClass = mq.createVariable( "class" ),
            restrictedFirst = mq.createVariable( "first" ),
            restrictedRest = mq.createVariable( "rest" ),
            elementType = mq.createVariable( "type" );
        mq.add( listClass, RDFS.subClassOf, RDF.List )
            .add( listClass, RDFS.subClassOf, restrictedFirst )
            .add( listClass, RDFS.subClassOf, restrictedRest )
            .add( restrictedFirst, OWL.onProperty, RDF.first )
            .add( restrictedFirst, OWL.allValuesFrom, elementType )
            .add( restrictedRest, OWL.onProperty, RDF.rest )
            .add( restrictedRest, OWL.allValuesFrom, listClass )
            ;
        ExtendedIterator<? extends List<Node>> answers = mq.run( m.getBaseModel() );
        while (answers.hasNext())
            {
            List<Node> bindings = answers.next();
            Resource type = listClass.resource( bindings );
            idiomaticListTypes.put( type, elementType.resource( bindings ) );
            }
        }
    
    private void findSuspecListTypes( OntModel om )
        {
        findPotentialSuspecListTypes( om );
        suspectListTypes.removeAll( idiomaticListTypes.keySet() );
        }

    /**
        Add to <code>brokenLists</code> those subclasses of <code>List</code>
        which a plausible candidates for being defined by the typed list idiom.
        "Plausible" is defined by:
        <ul>
            <li>subclass of rdf:List
            <li>and subclass of two bnodes
            <li>at least one of which mentions rdf:first or rdf:rest as an object
        </ul>
    */
    private void findPotentialSuspecListTypes( OntModel om )
        {
        ModelQuery mq = ModelQuery.create();
        QueryVariable 
            listClass = mq.createVariable( "class" ),
            restrictedFirst = mq.createVariable( "first" ),
            someProperty = mq.createVariable( "relatesTo" ),
            someObject = mq.createVariable( "someObject" );
        mq.add( listClass, RDFS.subClassOf, RDF.List )
            .add( listClass, RDFS.subClassOf, restrictedFirst )
            .add( restrictedFirst, someProperty, someObject )
            ;
        ExtendedIterator<? extends List<Node>> answers = mq.run( om.getBaseModel() );
        while (answers.hasNext())
            {
            List<Node> bindings = answers.next();
            // System.err.println( ">> " + listClass.resource( bindings ) + " subClassOf List, [" + someProperty.resource( bindings ) + " " + someObject.resource( bindings ) + "]" );
            Resource type = listClass.resource( bindings );
            Resource x = someObject.resource( bindings );
            if (x.equals( RDF.first ) || x.equals( RDF.rest ))
                suspectListTypes.add(  type );
            }
        }

    private void findListProperties( OntModel m )
        {
        for (Iterator<Resource> keys = idiomaticListTypes.keySet().iterator(); keys.hasNext();)
            {
            Resource key = keys.next();
            Iterator<Resource> properties = m.listStatements( Eyeball.ANY, RDFS.range, key ).mapWith( Statement.Util.getSubject );
            while (properties.hasNext())
                {                
                Object property = properties.next();
                if (!property.equals( RDF.rest ) ) propertiesWithListRange.put( property, key );
                }
            }
        }

    public void inspectStatement( Report r, Statement s )
        {
        if (propertiesWithListRange.containsKey( s.getPredicate() ))
            {
            Resource listType = propertiesWithListRange.get( s.getPredicate() );
            Resource elementType = idiomaticListTypes.get( listType );
            inspectList( r, s, s.getResource(), elementType ); // TODO what if it's a literal?
            }
        }

    public void inspectList( Report r, Statement s, Resource root )
        { inspectList( r, s, root, RDFS.Resource ); }
    
    public void inspectList( Report r, Statement s, Resource root, Resource type )
        {
        int index = 0;
        while (!root.equals( RDF.nil ))
            {
            index += 1;
            int countFirst = root.listProperties( RDF.first ).toList().size();
            if (countFirst != 1)
                reportFirstProblem( r, s, root, index, firstReason( countFirst ) );
            else
                {
                checkElementType( r, s, index, root, type );
                }
            List<RDFNode> restList = root.listProperties( RDF.rest ).mapWith( Statement.Util.getObject ).toList();
            int countRest = restList.size();
            if (countRest == 1)
                root = (Resource) restList.get( 0 );
            else
                {
                reportRestProblem( r, s, root, index, restReason( countRest ) );
                return;
                }
            }
        }

    private void checkElementType( Report r, Statement s, int index, Resource root, Resource type )
        {
        Statement first = root.getRequiredProperty( RDF.first );
        if (first.getObject().isLiteral())
            {
            System.err.println( "ISSUE: literal as list element" );
            return;
            }
        Resource x = first.getResource();
        if (!type.equals( RDFS.Resource ) && !x.hasProperty( RDF.type, type ))
            {
            ReportItem current = r.createItem( s )
                .addMainProperty( EyeballReporting.illTypedListElement, root );
            current.createSub( EyeballReporting.because )
                .addProperty( EyeballReporting.element, current.integer( index ) )
                .addProperty( EyeballReporting.shouldHaveType, type );
            }
        }

    private void reportFirstProblem( Report r, Statement s, Resource root, int index, Property reasonFirst )
        {
        ReportItem current = r.createItem( s )
            .addMainProperty( EyeballReporting.illFormedList , root );
        current.createSub( EyeballReporting.because )
            .addProperty( EyeballReporting.element, current.integer( index ))
            .addProperty( reasonFirst, root );
        }
    
    private void reportRestProblem( Report r, Statement s, Resource root, int index, Property reasonRests )
        {
        ReportItem current = r.createItem( s )
            .addMainProperty( EyeballReporting.illFormedList , root );
        current.createSub( EyeballReporting.because )
            .addProperty( EyeballReporting.element, current.integer( index ) )
            .addProperty( reasonRests, root )
            ;
        }

    /**
     	Answer the reason resource for a problem with <code>countRest</code>
        rdf:rest property values.
    */
    private Property restReason( int countRest )
        {
        return countRest == 0 ? EyeballReporting.hasNoRest : EyeballReporting.hasMultipleRests;
        }

    /**
        Answer the reason resource for a problem with <code>countFirst</code>
        rdf:first property values
    */
    private Property firstReason( int countFirst )
        {
        return countFirst == 0 ? EyeballReporting.hasNoFirst : EyeballReporting.hasMultipleFirsts;
        }
    }

