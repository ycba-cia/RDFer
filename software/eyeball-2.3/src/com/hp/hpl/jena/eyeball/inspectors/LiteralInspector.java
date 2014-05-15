/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved - see end of file.
 	$Id: LiteralInspector.java,v 1.15 2009/01/29 12:20:00 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.inspectors;

import java.util.*;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.eyeball.Report;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.*;

public class LiteralInspector extends InspectorBase
    {
    public LiteralInspector( Resource root )
        {}
    
    public LiteralInspector()
        {}
    
    public void begin( Report r, OntModel assume )
        {
        r.declareOrder( EYE.badLanguage, EYE.onLiteral );
        r.declareOrder( EYE.badLexicalForm, EYE.forDatatype );
        r.declareProperty( EYE.badDatatypeURI );
        }
    
    public void inspectStatement( Report r, Statement s )
        {
        RDFNode ob = s.getObject();
        if (ob.isLiteral()) inspectLiteral( r, s, (Literal) ob );
        }
    
    protected final Set<String> seen = new HashSet<String>();
    
    public void inspectLiteral( Report r, Statement s, Literal L )
        {
        RDFDatatype dt = L.getDatatype();
        String spelling = L.getLexicalForm();
        inspectLanguageCode( r, s, L, spelling );
        String dtURI = L.getDatatypeURI();
        inspectDatatypeURI( r, s, spelling, dtURI );
        inspectLexicalForm( r, s, dt, spelling, dtURI );
        }

    protected void inspectLanguageCode( Report r, Statement s, Literal L, String spelling )
        {
        String lang = L.getLanguage();
        if (!lang.matches( "|\\p{Alpha}+(-\\p{Alnum}+)*" )) 
            r.createItem( s ).addMainProperty( EYE.badLanguage, lang ).addProperty( EYE.onLiteral, spelling );
        }

    protected void inspectDatatypeURI( Report r, Statement s, String spelling, String dtURI )
        {
        if (dtURI != null)
            if (seen.add( dtURI ))
                if (badURI( dtURI ))
                    r.createItem( s ).addMainProperty( EYE.badDatatypeURI, dtURI ).addProperty( EYE.onLiteral, spelling );
        }

    protected void inspectLexicalForm( Report r, Statement s, RDFDatatype dt, String spelling, String dtURI )
        {
        if (dt != null)
            if (!dt.isValid( spelling ))
                r.createItem( s ).addMainProperty( EYE.badLexicalForm, spelling ).addProperty( EYE.forDatatype, ResourceFactory.createResource( dtURI ) );
        }

    private boolean badURI( String dtURI )
        { return URIInspector.getProblemMessagesFor( dtURI ).size() > 0; }
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