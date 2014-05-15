/*
    (c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    All rights reserved - see end of file.
    $Id: AllTypedInspector.java,v 1.9 2009/01/20 09:22:57 chris-dollin Exp $
*/
package com.hp.hpl.jena.eyeball.inspectors;

import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.eyeball.Report;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

public class AllTypedInspector extends InspectorBase 
    {
    public AllTypedInspector()
        { checkLiteralTypes = checkObjectTypes = false; }
    
    public AllTypedInspector( Resource r )
        {
        checkLiteralTypes = r.hasProperty( EYE.checkLiteralTypes, EYE.True );
        checkObjectTypes = r.hasProperty(  EYE.checkObjectTypes, EYE.True );
        }
    
    protected final boolean checkLiteralTypes;
    protected final boolean checkObjectTypes;
    
    protected final Set<RDFNode> seen = new HashSet<RDFNode>();

    public void begin( Report r, OntModel assume )
        { r.declareProperty( EYE.hasNoType ); }
    
    public void inspectStatement( Report r, Statement s )
        {
        inspectResource( r, s, s.getSubject() );
        RDFNode ob = s.getObject();
        if (checkLiteralTypes && ob.isLiteral()) inspectLiteral( r, s, (Literal) ob );
        if (checkObjectTypes && ob.isResource()) inspectResource( r, s, (Resource) ob );                     
        }
    
    private void inspectLiteral( Report r, Statement s, Literal ob )
        {
        if (ob.getLanguage().equals(  ""  ))
            if (ob.getDatatypeURI() == null) reportIfUnseen( r, s, ob );
        }

    private void inspectResource( Report r, Statement s, Resource res )
        { if (!res.hasProperty( RDF.type )) reportIfUnseen( r, s, res ); }

    private void reportIfUnseen( Report r, Statement s, RDFNode ob )
        { if (seen.add( ob )) r.createItem( s ).addMainProperty( EYE.hasNoType, ob ); }
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