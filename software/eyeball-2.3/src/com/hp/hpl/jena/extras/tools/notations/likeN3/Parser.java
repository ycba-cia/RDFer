/*
    (c) Copyright 2005 Hewlett-Packard Development Company, LP
    All rights reserved - see end of file.
    $Id: Parser.java,v 1.1 2010/03/19 14:34:05 chris-dollin Exp $
*/
package com.hp.hpl.jena.extras.tools.notations.likeN3;

import java.util.*;

import com.hp.hpl.jena.graph.*;
import com.hp.hpl.jena.graph.test.NodeCreateUtils;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;
import com.hp.hpl.jena.shared.*;
import com.hp.hpl.jena.vocabulary.RDF;

public class Parser
    {
    protected State s;
    
    public Parser( String source )
        {
        Lexer st = new Lexer( source ); // new StringTokenizer( source, " ;&,()[]", true );
        this.s = new State( st );
        }
    
    public static class GAP
        {
        protected Graph graph;
        protected PrefixMapping pm;
        
        public GAP( Model m )
            { this.graph = m.getGraph();
            this.pm = this.graph.getPrefixMapping(); }
        
        public void add( Node s, Node p, Node o )
            { graph.add( Triple.create( s, p, o ) ); }
        
        public Node node( String spelling )
            { return NodeCreateUtils.create( pm, spelling ); }
        
        public Node bnode()
            { return Node.createAnon(); }
        }
    
    public Model parseInto( Model m )
        { toplevel( s, new GAP( m ) ); return m; }

    public Resource resourceIn( Model m )
        { return topLevelresource( m, s, new GAP( m ) ); }
 
    private Resource topLevelresource( Model m, State s, GAP g )
        {
        if (s.at( "")) throw new JenaException( "no resource to parse" );
        Resource result = resource( m, s, g );
        while (s.at( ";" )) { s.advance(); statement( s, g ); }
        if (s.at( "" ) == false) throw new RuntimeException( "should be at end" ); 
        return result;
        }

    protected void toplevel( State s, GAP g )
        {
        if (s.at( "" )) return;
        statement( s, g );
        while (s.at( ";" )) { s.advance(); statement( s, g ); }
        if (s.at( "" ) == false) throw new RuntimeException( "should be at end" ); 
        }
        
    protected Resource resource( Model m, State s, GAP g )
        {
        Node object = object( g, s );
        addPredications( s, g, object );
        return (Resource)  ((ModelCom ) m).getNodeAs( object, Resource.class );
        }
        
    protected void statement( State s, GAP m )
        {
        Node object = object( m, s );
        addPredications( s, m, object );
        }
    
    protected void addPredications( State s, GAP m, Node subject )
        {
        if (s.startsSubject() == false) return;
        addObjects( s, m, subject, m.node( s.consume() ) );
        while (s.at( "&"))
            { 
            s.advance();
            addObjects( s, m, subject, m.node( s.consume() ) );            
            }
        }
    
    protected void addObjects( State s, GAP m, Node subject, Node predicate )
        {
        m.add( subject, predicate, object( m, s ) );
        while (s.at( "," ))
            {
            s.advance();
            m.add( subject, predicate, object( m, s ) );
            }
        }
    
    protected Node object( GAP m, State s )
        {
        if (s.at( "("))
            {
            List<Node> L = new ArrayList<Node>();
            s.advance();
            while (s.at( ")") == false) L.add( object( m, s ) );
            s.advance();
            Node result = RDF.nil.asNode();
            for (int i = L.size(); i > 0; i -= 1)
                {
                Node x = L.get(i - 1);
                Node cons = m.bnode();
                m.add( cons, RDF.first.asNode(), x );
                m.add( cons, RDF.rest.asNode(), result );
                result = cons;
                }
            return result;
            }
        else if (s.at( "[" ))
            {
            Node result = m.bnode();
            s.advance();
            while (s.startsSubject()) addPredications( s, m, result );
            if (s.at( "]") == false) throw new RuntimeException( "missing ']'" );
            s.advance();
            return result;
            }
        else if (s.st.current.length() == 1 && !Character.isLetterOrDigit( s.st.current.charAt(0)))
            throw new RuntimeException( "bad punctuation: " + s.st.current );
        else
            return m.node( s.consume() );
        }
    }

/*
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP
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