/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved - see end of file.
 	$Id: Eyeball.java,v 1.27 2008/11/04 09:40:58 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball;

import java.io.PrintStream;

import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.*;

public class Eyeball
    {    
    protected final Inspector inspector;
    protected final OntModel assume;
    protected final Renderer renderer;
    protected final Analysis analysis;
    protected final Doctor doctor;
    
    private static final FileManager fileManager = FileManager.get();
    /**
        A Model containing the RDFS schema.
    */
    public static final Model rdfs = fileManager.loadModel( RDFS.getURI() );
    /**
        A Model containing the RDF schema.
    */
    public static final Model rdf = fileManager.loadModel( RDF.getURI() );
    /**
        A Model containing the (dynamic) union of the RDF and RDFS schemas.
    */
    public static final Model rdfBoth = ModelFactory.createUnion( Eyeball.rdf, Eyeball.rdfs );
    
    /**
        A handy constant to use as wildcard in listStatements et al.
    */
    public static final Property ANY = null;
    
    public Eyeball( Inspector inspector, OntModel assume, Renderer renderer )
        { this( inspector, assume, renderer, null, null ); }
    
    public Eyeball( Inspector inspector, OntModel assume, Renderer renderer, Analysis analysis, Doctor doctor )
    	{
        this.inspector = inspector;
        this.assume = assume;
        this.renderer = renderer;
    	this.analysis = analysis;
    	this.doctor = doctor;
    	}
    
    public static Model getRepairConfig()
    	{
    	Model configFile = ModelFactory.createDefaultModel()
            .read( "file:etc/eyeball-config.n3", "N3" );
    	Model config = ModelFactory.createDefaultModel()
            .add( configFile.listStatements( EYE.repairConfig, null, (RDFNode)null ) );
    	return config;
    	}
    
    public Inspector getInspector()
        { return inspector; }

    public Object getRenderer()
        { return renderer; }
    
    public OntModel getAssumptions()
        { return assume; }

    public Report inspect( Report r, OntModel m )
        {
        inspector.begin( r, assume );
        inspector.inspectModel( r, m );
        for (StmtIterator it = m.getBaseModel().listStatements(); it.hasNext();)
            inspector.inspectStatement( r, it.nextStatement() );
        inspector.end( r );
        return r;
        }
    
    public void analyse( Report r, OntModel m )
    	{ analysis.analyse( r, m, getRepairConfig() ); }
    
    public void doctorModel( Report r, OntModel m )
    	{
    	Model report = r.model();
    	doctor.doctorModel( report, m, getRepairConfig() );
    	r.setMitems( report );
    	}

    public void render( Report r, PrintStream out )
        { renderer.render( r, out ); }
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