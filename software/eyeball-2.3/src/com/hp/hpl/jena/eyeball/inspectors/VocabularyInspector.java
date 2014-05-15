/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved - see end of file.
 	$Id: VocabularyInspector.java,v 1.15 2010/01/11 14:08:26 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.inspectors;

import java.util.*;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.vocabulary.*;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.*;

public class VocabularyInspector extends InspectorBase implements Inspector
    {
    protected final Set<String> openNamespaces = new HashSet<String>();
    
    public VocabularyInspector( Resource root )
        { 
        this(); 
        for (StmtIterator opens = root.listProperties( EYE.openNamespace ); opens.hasNext();)
            {
            Resource open = opens.nextStatement().getResource();
            openNamespaces.add( open.getURI() );
            }
        }
    
    public VocabularyInspector()
        {
        FileManager fm = FileManager.get();
        addVocabulary( fm.loadModel( RDF.getURI() ) );
        addVocabulary( fm.loadModel( RDFS.getURI() ) );
        addVocabulary( fm.loadModel( EyeballVocabularyBase.XSD_URI ) );
        }
    
    public final Map<String, Set<String>> map = new HashMap<String, Set<String>>();

    protected final Set<Resource> seen = new HashSet<Resource>();
    
    public void begin( Report r, OntModel assume )
        { 
        r.declareOrder( EYE.notFromSchema, EYE.onResource );
        addVocabulary( assume.getBaseModel() ); 
        }

    public void inspectModel( Report r, OntModel model )
        {
        for (Iterator<OntModel> models = model.listSubModels(); models.hasNext();)
            addVocabulary( models.next().getBaseModel() );
        }
    
    private void addVocabulary( Model model )
        {
        for (StmtIterator it = model.listStatements(); it.hasNext();)
            addVocabulary( it.nextStatement() );
        }
    
    private void addVocabulary( Statement s )
        {
        addVocabulary( s.getSubject() );
        addVocabulary( s.getPredicate() );
        addVocabulary( s.getObject() );
        }

    private void addVocabulary( RDFNode object )
        { if (object.isURIResource()) addVocabulary( object.asNode() ); }

    private void addVocabulary( Node node )
        {
        String ns = node.getNameSpace();
        String ln = node.getLocalName();
        Set<String> already = map.get( ns );
        if (already == null) map.put( ns, already = new HashSet<String>() );
        already.add( ln );
        }

    public void inspectStatement( Report r, Statement s )
        {
        inspectNode( r, s, s.getSubject() );
        inspectNode( r, s, s.getPredicate() );
        inspectNode( r, s, s.getObject() );
        }

    private void inspectNode( final Report r, final Statement s, RDFNode object )
        {
        object.visitWith( new RDFVisitor() 
            {
            public Object visitURI( Resource res, String uri )
                { inspectURI( r, s, res ); return null; }

            public Object visitBlank( Resource r, AnonId id )
                { return null; }

            public Object visitLiteral( Literal l )
                { return null; }
            });
        }

    public void inspectURI( Report r, Statement s, Resource res )
        {
        if (seen.add( res ))
            {
            String ns = res.getNameSpace();
            if (!openNamespaces.contains( ns ))
                {
                String localName = res.getLocalName();
                Set<String> localNames = map.get( ns );
                if (localNames != null && !localNames.contains( localName ))
                    {
                    r.createItem( s )
                        .addMainProperty( EYE.notFromSchema, res.getNameSpace() )
                        .addProperty( EYE.onResource, res )
                        ;
                    }
                }
            }
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