/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved - see end of file.
 	$Id: PrefixInspector.java,v 1.15 2009/01/29 13:39:20 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.inspectors;

import java.util.*;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl;

public class PrefixInspector extends InspectorBase implements Inspector
    {
    public PrefixInspector( Resource root )
        {}

    public PrefixInspector()
        {}
    
    public void begin( Report r, OntModel assume )
        {
        r.declareOrder( EYE.jenaPrefixFound, EYE.forNamespace );
        r.declareOrder( EYE.badNamespaceURI, EYE.onPrefix );
        r.declareOrder( EYE.onPrefix, EYE.forReason );
        r.declareOrder( EYE.forReason, EYE.expected );
        r.declareOrder( EYE.multiplePrefixesForNamespace, EYE.onPrefix );
        }
    
    public void inspectModel( Report r, OntModel m )
        {
        checkStandardPrefixes( r, m );
        checkJenaPrefixes( r, m );
        checkUnterminatedNamespaces( r, m );
        checkSharedNamespaces( r, m );
        }
    
    private void checkSharedNamespaces( Report r, OntModel m )
        {
        Map<String, Set<String>> inverse = createInverseMapping( m );
        for (Iterator<Map.Entry<String, Set<String>>> entries = inverse.entrySet().iterator(); entries.hasNext();)
            {
            Map.Entry<String, Set<String>> entry = entries.next();
            String namespace = entry.getKey();
            Set<String> prefixes = entry.getValue();
            if (prefixes.size() > 1)
                {
                ReportItem i = r.createItem()
                    .addMainProperty( EYE.multiplePrefixesForNamespace, namespace );
                for (Iterator<String> it = prefixes.iterator(); it.hasNext();)
                    i.addProperty( EYE.onPrefix, it.next() );
                }
            }
        }

    private Map<String, Set<String>> createInverseMapping( OntModel m )
        {
        Map<String, Set<String>> inverse = new HashMap<String, Set<String>>();
        Iterator<String> keys = m.getNsPrefixMap().keySet().iterator();
        while (keys.hasNext()) 
            {
            String prefix = keys.next();
            String namespace = m.getNsPrefixURI( prefix );
            Set<String> prefixes = inverse.get( namespace );
            if (prefixes == null) inverse.put( namespace, prefixes = new HashSet<String>() );
            prefixes.add( prefix );
            }
        return inverse;
        }

    private void checkUnterminatedNamespaces( Report r, OntModel m )
        {
        Iterator<String> keys = m.getNsPrefixMap().keySet().iterator();
        while (keys.hasNext())
            {
            String key = keys.next();
            String ns = m.getNsPrefixURI( key );
            if (unterminated( ns )) 
                r.createItem()
                    .addMainProperty( EYE.badNamespaceURI, ns )
                    .addProperty( EYE.onPrefix, key )
                    .addProperty( EYE.forReason, EYE.namespaceEndsWithNameCharacter )
                    ;
            }
        }

    private boolean unterminated( String ns )
        { return !PrefixMappingImpl.isNiceURI( ns ); }

    private void checkJenaPrefixes( Report r, OntModel m )
        {
        Iterator<String> keys = m.getNsPrefixMap().keySet().iterator();
        while (keys.hasNext())
            {
            String key = keys.next();
            if (key.matches( "j\\.[0-9]+" ))
                r.createItem()
                    .addMainProperty( EYE.jenaPrefixFound, key )
                    .addProperty( EYE.forNamespace, m.getNsPrefixURI( key ) )
                    ;
            }
        }

    protected void checkStandardPrefixes( Report r, Model toTest )
        {
        PrefixMapping toUse = PrefixMapping.Extended;
        Iterator<String> keys = toUse.getNsPrefixMap().keySet().iterator();
        while (keys.hasNext())
            {
            String key = keys.next();
            String modelValue = toTest.getNsPrefixURI( key );
            String expected = toUse.getNsPrefixURI( key );
            if (modelValue != null && !modelValue.equals( expected))
                {
                r.createItem()
                    .addMainProperty( EYE.badNamespaceURI, modelValue )
                    .addProperty( EYE.expected, expected )
                    .addProperty( EYE.onPrefix, key )
                    .addProperty( EYE.forReason, "non-standard namespace for prefix" );
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