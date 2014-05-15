/*
 	(c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved - see end of file.
    $Id: LegacyTextRenderer.java,v 1.6 2009/01/29 12:20:15 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.renderers;

import java.io.PrintStream;
import java.util.*;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.vocabulary.*;

/**
    @author kers
*/
public class LegacyTextRenderer
    {
    protected List<Property> properties;

    protected PrefixMapping pm;

    protected Set<Resource> seen = new HashSet<Resource>();

    public LegacyTextRenderer( Model config, PredicateRegister r )
        {
        properties = r.getRegisteredPredicates();
        }

    public void render( Model m, PrintStream out )
        {
        pm = m;
        renderItems( out, m.listStatements( Eyeball.ANY, EYE.onStatement, Eyeball.ANY ) );
        renderItems( out, m.listStatements( null, RDF.type, EYE.Item ) );
        }

    private void renderItems( PrintStream out, StmtIterator it )
        {
        while (it.hasNext()) renderItem( out, it.nextStatement().getSubject() );
        }

    protected Map<Resource, String> labels;

    protected String getLabel( RDFNode p )
        {
        if (labels == null) labels = makeLabelMap();
        String label = labels.get( p );
        return label == null ? nicely( p ) : label;
        }

    private String nicely( RDFNode p )
        {
        Node node = p.asNode();
        if (node.isLiteral() && integer( node.getLiteralDatatypeURI() )) return node.getLiteralLexicalForm();
        if (node.isBlank()) return niceBnode( node );
        return node.toString( pm, true);
        }

    private Map<Node, String> bnodes = new HashMap<Node, String>();
    
    private int bnodeCounter = 1000;
    
    private String niceBnode( Node node )
        {
        String label = bnodes.get( node );
        if (label == null) bnodes.put( node, label = "_:b" + ++bnodeCounter );
        return label;
        }

    private boolean integer( String URI )
        { return URI != null && URI.matches( ".*int(|eger)" ); }

    protected String getDisplay( RDFNode p )
        {
        if (p.isAnon())
            {
            Resource rp = (Resource) p;
            if (rp.hasProperty( RDF.first )) return displayList( rp );
            if (rp.hasProperty( RDF.type, EYE.Set )) return displaySet( rp );
            return displayBnode( rp );
            }
        else
            return getLabel( p );
        }

    private String displaySet( Resource rp )
        {
        StringBuffer b = new StringBuffer( "{" );
        String gap = "";
        StmtIterator it = rp.listProperties( RDFS.member );
        while (it.hasNext())
            {
            Statement s = it.nextStatement();
            b.append( gap ); gap = ", ";
            b.append( nicely( s.getObject() ) );
            }
        return b.append( "}" ).toString();
        }

    private String displayBnode( Resource p )
        {
        StringBuffer b = new StringBuffer( "[" );
        String gap = "";
        StmtIterator it = p.listProperties();
        while (it.hasNext())
            {
            Statement s = it.nextStatement();
            b.append( gap ); gap = "; ";
            b.append( getLabel( s.getPredicate() ) );
            b.append( " " );
            b.append( nicely( s.getObject() ) );
            }
        return b.append( "]" ).toString();
        }

    private String displayList( Resource p )
        {
        StringBuffer b = new StringBuffer( "(" );
        String gap = "";
        while (!p.equals( RDF.nil ))
            {
            b.append( gap ); gap = ", ";
            b.append( p.getProperty( RDF.first ).getObject().asNode().toString( pm, true ) );
            p = p.getProperty( RDF.rest ).getResource();
            }
        return b.append( ")" ).toString();
        }

    protected Map<Resource, String> makeLabelMap()
        {
        Map<Resource, String> result = new HashMap<Resource, String>();
        StmtIterator labels = EYE.getSchema().listStatements( null, RDFS.label, (RDFNode) null );
        while (labels.hasNext()) addLabel( result, labels.nextStatement() );
        return result;
        }

    protected void addLabel( Map<Resource, String> map, Statement s )
        { map.put( s.getSubject(), s.getString() ); }

    protected void renderItem( PrintStream out, Resource item )
        {
        if (seen.add( item ))
            {
            boolean [] extra = {false};
            out.println();
            if (item.hasProperty( EYE.onStatement ))
                {
                renderStatement( out, item.getProperty( EYE.onStatement ).getResource() );
                extra[0] = true;
                }
            for (int i = 0; i < properties.size(); i += 1)
                renderProperty( out, extra, item.listProperties( properties.get(i) ) );
            }
        }

    protected void renderStatement( PrintStream out, Resource s )
        {
        out.print( "On statement: " );
        nice( out, s.getProperty( RDF.subject ).getObject() );
        nice( out, s.getProperty( RDF.predicate ).getObject() );
        nice( out, s.getProperty( RDF.object ).getObject() );
        out.println();
        }

    protected void nice( PrintStream out, RDFNode object )
        {
        out.print( " " );
        out.print( nicely( object ) );
        }

    protected void renderProperty( PrintStream out, boolean [] extra, StmtIterator statements )
        {
        while (statements.hasNext())
            {
            Statement statement = statements.nextStatement();
            if (extra[0]) out.print( "    " );
            out.println( getLabel( statement.getPredicate() ) + ": " + getDisplay( statement.getObject() ) );
            extra[0] = true;
            }
        }
    }


/*
 * (c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
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
