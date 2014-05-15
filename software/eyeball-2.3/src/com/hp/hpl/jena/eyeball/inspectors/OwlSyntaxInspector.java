/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved.
 	$Id: OwlSyntaxInspector.java,v 1.8 2009/01/29 12:20:00 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.inspectors;

import java.util.*;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;

/**
    The OwlSyntaxInspector performs some elementary OWL syntax checks.
    To begin with: that a Restriction has the right mix of properties.
<p>
    The single-statement pass is used to identify those resources that are
    (supposed to be) restrictions. The end phase inspects each such resource
    and produces a report if it doesn't have exactly one onProperty property
    and exactly one other constraint property.
    
    @author kers
*/
public class OwlSyntaxInspector extends InspectorBase
    {
    public OwlSyntaxInspector( Resource root )
        {}
    
    public OwlSyntaxInspector()
        {}
    
    protected Set<Resource> possibleRestrictions = new HashSet<Resource>();
    
    public void begin( Report r, OntModel assume )
        {
        r.declareOrder( EYE.suspiciousRestriction, EYE.forReason );
        r.declareOrder( EYE.suspiciousRestriction, EYE.equivalentClass );
        r.declareOrder( EYE.suspiciousRestriction, EYE.subClassOf );
        }
    
    public void inspectStatement( Report r, Statement s )
        {
        if (isRestrictionProperty( s.getPredicate() )) possibleRestrictions.add( s.getSubject() );
        }

    private boolean isRestrictionProperty( Property p )
        {
        return 
            p.equals( OWL.onProperty ) 
            || p.equals( OWL.hasValue )
            || p.equals( OWL.cardinality )
            || p.equals( OWL.minCardinality ) || p.equals( OWL.maxCardinality )
            || p.equals( OWL.allValuesFrom ) || p.equals( OWL.someValuesFrom )
            ;
        }

    public void end( Report r )
        {
        for (Iterator<Resource> s = possibleRestrictions.iterator(); s.hasNext();) 
            checkRestriction( r, s.next() );
        }

    private void checkRestriction( Report r, Resource rest )
        {
        int onPropertyCount = rest.listProperties( OWL.onProperty ).toList().size();
        int constraintCount = countConstraints( rest );
        if (onPropertyCount != 1 || constraintCount != 1)
            {
            ReportItem item = r.createItem();
            item.addMainProperty( EYE.suspiciousRestriction, createRestrictionSubject( rest, item ) );
            addReasons( item, onPropertyCount, constraintCount );
            addIdentificationProperties( rest, item, RDFS.subClassOf, EYE.subClassOf );
            addIdentificationProperties( rest, item, OWL.equivalentClass, EYE.equivalentClass );
            }
        }

    private Resource createRestrictionSubject( Resource rest, ReportItem item )
        {
        Resource subject = item.createSub( EYE.suspiciousRestriction );
        for (StmtIterator it = rest.listProperties(); it.hasNext();)
            {
            Statement s = it.nextStatement();
            subject.addProperty( s.getPredicate(), s.getObject() );
            }
        return subject;
        }
    
    private void addIdentificationProperties( Resource rest, ReportItem item, Property likedWith, Property reportAs )
        {
        for (StmtIterator classes = rest.getModel().listStatements( null, likedWith, rest ); classes.hasNext();)
            {
            Statement s = classes.nextStatement();
            Resource sup = s.getSubject();
            if (sup.isURIResource()) item.addProperty( reportAs, sup );
            }
        }

    private void addReasons( ReportItem item, int onPropertyCount, int constraintCount )
        {
        if (onPropertyCount == 0) addReason( item, EYE.missingOnProperty );
        else if (onPropertyCount > 1) addReason( item, EYE.multipleOnProperty );
        if (constraintCount == 0) addReason( item, EYE.missingConstraint );
        else if (constraintCount > 1) addReason( item, EYE.multipleConstraint );
        }

    private void addReason( ReportItem item, Resource reason )
        { item.addProperty( EYE.forReason, reason ); }

    private int countConstraints( Resource rest )
        {
        return 
            countProperties( rest, OWL.hasValue)
            + countProperties( rest, OWL.cardinality )
            + countProperties( rest, OWL.minCardinality )
            + countProperties( rest, OWL.maxCardinality )
            + countProperties( rest, OWL.allValuesFrom )
            + countProperties( rest, OWL.someValuesFrom )
            ;
        }

    private int countProperties( Resource r, Property p )
        { return r.listProperties( p ).toList().size(); }
    }

