/*
 (c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 All rights reserved - see end of file.
 $Id: OntLoader.java,v 1.6 2008/11/04 09:40:54 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.loaders;

import com.hp.hpl.jena.assembler.assemblers.OntModelSpecAssembler;
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.NotFoundException;

/**
    An OntLoader loads ontology models given their URIs, relying on a base
    Loader for the base model. A URI of the form ont:NAME:baseModel 
    constructs an ontology model with the base given by the baseModel
    and usign OntModelSpec.NAME as the specification. Other URIs are
    wrapped in RDFS inference.
    
    @author kers
*/

public class OntLoader
    {
    /**
        The loader used to get base models.
    */
    protected final Loader base;

    /**
        Initialise this OntLoader with a base-model loader <code>base</code>.
    */
    public OntLoader( Loader base )
        { this.base = base; }

    /**
         Answer an OWL_MEM OntModel with the base given by the base loader
         applied to <code>s</code>, unless the s ~~ "ont:NAME:baseName",
         in which case the constructing Spec is OntModelSpec.NAME. 
    */
    public OntModel ontLoad( String s )
        {
        if (s.matches( "ont:[A-Za-z_]+:.*" ))
            {
            int secondColon = s.indexOf( ':', 4 );
            String specName = s.substring( 4, secondColon );
            OntModelSpec spec = OntModelSpecAssembler.getOntModelSpecField( specName );
            if (spec == null) throw new NotFoundException( specName );
            return ModelFactory.createOntologyModel
                ( spec, base.load( s.substring( secondColon + 1 ) ) );
            }
        else
            {
            OntModelSpec spec = OntModelSpec.OWL_MEM;
            return ModelFactory.createOntologyModel( spec, base.load( s ) );
            }
        }
    }

/*
 * (c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP All rights
 * reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The name of the author may not
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */