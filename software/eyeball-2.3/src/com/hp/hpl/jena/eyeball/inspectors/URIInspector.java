/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved - see end of file.
 	$Id: URIInspector.java,v 1.22 2009/03/23 15:36:34 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.inspectors;

import java.util.*;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.vocabulary.*;
import com.hp.hpl.jena.iri.*;
import com.hp.hpl.jena.n3.IRIResolver;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.impl.Util;

public class URIInspector extends InspectorBase implements Inspector
    {    
    public URIInspector()
        {}
    
    public URIInspector( Resource root )
        { 
        config.addAll( root.listProperties( EyeballAssembling.check ).mapWith( Statement.Util.getObject ).toList() ); 
        reportEmptyLocalNames = oneBoolean( root.listProperties( EYE.reportEmptyLocalNames ).mapWith( Statement.Util.getObject ).toSet() );
        }
    
    private boolean oneBoolean( Set<RDFNode> set )
        { return set.size() == 1 && isTrue( set.iterator().next() ); }

    private boolean isTrue( Object toCheck )
        { 
        return 
            toCheck instanceof Literal 
            && ((Literal) toCheck ).getLexicalForm().equals( "true" ); 
        }

    protected boolean reportEmptyLocalNames = false;
    
    protected final List<RDFNode> config = new ArrayList<RDFNode>();

    protected final static IRIFactoryI i = IRIFactory.semanticWebImplementation();
    
    /**
        Switch off N3 IRI exceptions.
    */
    static
        { IRIResolver.suppressExceptions(); }
    
    protected final HashSet<String> seen = new HashSet<String>();
    
    public void begin( Report r, OntModel assume )
        { r.declareOrder(  EYE.badURI, EYE.forReason ); }
    
    public void inspectStatement( Report r, Statement s )
        {
        inspectURI( r, s, s.getSubject() );
        inspectURI( r, s, s.getPredicate() );
        inspectURI( r, s, s.getObject() );
        }

    private void inspectURI( final Report r, final Statement s, RDFNode object )
        {
        object.visitWith( new RDFVisitor() 
            {
            public Object visitURI( Resource res, String uri )
                { inspectURI( r, s, uri ); return null; }

            public Object visitBlank( Resource r, AnonId id )
                { return null; }

            public Object visitLiteral( Literal l )
                { return null; }
            });
        }

    public void inspectURI( Report r, Statement s, String URI )
        {        
        if (seen.add( URI ))
            {
            List<RDFNode> messages = getProblemMessagesFor( URI, reportEmptyLocalNames );
            addPatternMatchViolations( messages, URI );
            if (messages.size() > 0) generateViolationReport( r, s, messages, URI );
            }
        }

    private void generateViolationReport( Report r, Statement s, List<RDFNode> messages, String URI ) 
        {
        ReportItem i = r.createItem( s );
        i.addMainProperty( EYE.badURI, URI );
        for (Iterator<RDFNode> it = messages.iterator(); it.hasNext();)
            i.addProperty( EYE.forReason, it.next() );
        }

    public static List<RDFNode> getProblemMessagesFor( String URI ) 
        { return getProblemMessagesFor( URI, false ); }
    
    public static List<RDFNode> getProblemMessagesFor( String URI, boolean reportEmptyLocalNames ) 
        {
        List<RDFNode> intermediate = getPr( URI );
        if (Util.splitNamespace( URI ) == URI.length() && reportEmptyLocalNames) 
            intermediate.add( EYE.uriHasNoLocalname );
        return intermediate;
        }

    private static List<RDFNode> getPr( String URI )
        {
        return URI.indexOf( ' ' ) > -1 
            ? single( EYE.uriContainsSpaces )
            : getViolationMessages( URI );
        }

    private static List<RDFNode> getViolationMessages( String URI ) 
        {
        try
            {
            List<RDFNode> messages = new ArrayList<RDFNode>();
            Iterator<Violation> it = i.construct( URI ).violations( true );
            while (it.hasNext()) messages.add( asNode( it.next() ) );
            return messages;
            }
        catch (IRIException e)
            { return single( asNode( e.getViolation() ) ); }
        }

    private void addPatternMatchViolations( List<RDFNode> messages, String uri )
        {
        for (int i = 0; i < config.size(); i += 1)
            {
            Resource thisCheck = (Resource) config.get(i);
            String prefix = thisCheck.getProperty( EyeballAssembling.prefix ).getString();
            checkProhibitions( messages, thisCheck, prefix, uri );
            checkRequirements( messages, thisCheck, prefix, uri );
            }
        }

    private void checkRequirements( List<RDFNode> messages, Resource thisCheck, String prefix, String uri )
        {
        StmtIterator requirements = thisCheck.listProperties( EyeballAssembling.require );
        List<RDFNode> potentialMessages = new ArrayList<RDFNode>();
        while (requirements.hasNext())
            {
            String require = requirements.nextStatement().getString();
            if (uri.matches( prefix + require )) return;
            potentialMessages.add( literal( "did not match required pattern: " + require + " for prefix " + prefix ) );
            }
        messages.addAll( potentialMessages );
        }

    private void checkProhibitions( List<RDFNode> messages, Resource thisCheck, String prefix, String uri )
        {
        StmtIterator prohibitions = thisCheck.listProperties( EyeballAssembling.prohibit );
        while (prohibitions.hasNext())
            {
            String prohibit = prohibitions.nextStatement().getString();
            if (uri.matches( prefix + prohibit ))
                messages.add( literal( "matched prohibited pattern: " + prohibit + " for prefix " + prefix ) );
            }
        }

    private static RDFNode asNode( Violation violation )
        { 
        String message = violation.getShortMessage();
        if (message.matches( ".*LOWERCASE_PREFERRED.*SCHEME.*" )) return EYE.schemeShouldBeLowercase;
        if (message.matches( ".*REQUIRED_COMPONENT_MISSING.*SCHEME.*" )) return EYE.uriHasNoScheme;
        if (message.matches( ".*REQUIRED_COMPONENT_MISSING.*HOST.*" )) return EYE.uriNoHttpAuthority;
        if (message.matches( ".*UNREGISTERED_IANA_SCHEME.*SCHEME.*" )) return EYE.unrecognisedScheme;
        return literal( message );
        }

    private static RDFNode literal( String message )
        { return ResourceFactory.createPlainLiteral( message ); }
    
    private static List<RDFNode> single( RDFNode node )
        {
        List<RDFNode> result = new ArrayList<RDFNode>();
        result.add( node );
        return result;
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