/*
    (c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
    All rights reserved - see end of file.
    $Id: eyeball.java,v 1.40 2010/03/29 10:34:23 chris-dollin Exp $
*/

package jena;

import java.io.*;
import java.util.*;

import com.hp.hpl.jena.assembler.*;
import com.hp.hpl.jena.db.ModelRDB;
import com.hp.hpl.jena.extras.tools.query.*;
import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.loaders.*;
import com.hp.hpl.jena.eyeball.sign.*;
import com.hp.hpl.jena.eyeball.util.*;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.*;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class eyeball
    {
    public static void main( String [] args ) 
        { main( args, System.out ); }
    
    public static void main( String [] args, PrintStream out ) 
        { main( args, out, ExportModel.ignore ); }
    
    public static void main( String [] args, PrintStream out, ExportModel em )
        {
        Setup.declareEyeballAssemblers();   
//        ClassLoader cl = Thread.currentThread().getContextClassLoader();
//        Enumeration urls = cl.getResources("com/hp/hpl/activeCatalog/vocabularies/Validation.class");
//        while (urls.hasMoreElements()) System.err.println("Load from: " + urls.nextElement());
        eyeball eye = new eyeball( args );
        boolean allPassed = eye.run( out, em );
        if (eye.remark) System.err.println( allPassed ? "success" : "some problems reported" );
        System.exit( allPassed ? 0 : 1 );
        }

    static final String VERSION = "Eyeball 2.3RC1 (A Verbs Omen)";
    
    static final String allowed = 
        "check sign accept config root exclude include assume render"
        + " repair analyse analyze remark"
        + " version"
        + " set"
        ;

    private Model input = null;
    
    protected boolean allChecksPassed = true;
    
    final Arguments args;
    
    final Eyeball eyeball;
    
    final Model config;
    
    final Resource configRoot;
    
    boolean remark = false;
    
    public eyeball( String [] argStrings )
        {
        args = Arguments.readArguments( allowed, argStrings );
        remark = args.has( "remark" );
        if (args.hasSomeValues( "check"  ) || args.hasSomeValues( "sign" ) || args.hasSomeValues( "accept" ) || args.has( "version" ))
            {
            String rootString = args.valueFor( "root", "eye:eyeball" );
            Model baseConfig = loadBaseConfig();
            Resource root = baseConfig.createResource( baseConfig.expandPrefix( rootString ) );
            Model full = AssemblerHelp.fullModel( baseConfig );
            config = applyArguments( args, root, full );
            configRoot = root.inModel( config );
            eyeball = (Eyeball) Assembler.general.open( configRoot );
            }
        else
            throw new RuntimeException( "must specify -check, -sign, -accept, or -version" );
        }
    
    /**
         Apply this eyeball's Eyeball checks to each of the specified models and analyse / repair if requested.
         Also store produced models for ease of later access. Answer true if all inspections succeed.
    */
    public boolean run( PrintStream out )
        { return run( out, ExportModel.ignore ); }
    
    /**
        Answer true iff all the inspections succeed.
    */
    public boolean run( PrintStream out, ExportModel em )
        {
        OntLoader loader = createLoader();
        for (Iterator<String> elements = args.listFor( "check" ).iterator(); elements.hasNext();)
            checkOneModel( out, em, loader, elements.next() );
        for (Iterator<String> elements = args.listFor( "sign" ).iterator(); elements.hasNext();)
            signOneModel( out, em, loader, elements.next() );
        for (Iterator<String> elements = args.listFor( "accept" ).iterator(); elements.hasNext();)
            acceptOneModel( out, em, loader, elements.next() );
        if (args.has( "version" ))
            System.err.println( VERSION );
        return allChecksPassed;
        }
    
    private Model applyArguments( Arguments args, Resource givenRoot, Model givenConfigModel )
        {
        Model result = copyModel( givenConfigModel );
        Resource root = givenRoot.inModel( result );
        applyExcludes( args, root, result );
        applyIncludes( args, root, result );
        applyAssumes( args, root, result );
        applyRender( args, root, result );
        applySettings( args, root, result );
        return result;
        }

    private void applySettings( Arguments args, Resource root, Model result )
        {
        SetPropertiesFromString setModel = new SetPropertiesFromString( result );
        List<String> settings = args.listFor( "set", "" );
        for (Iterator<String> s = settings.iterator(); s.hasNext();) setModel.set( s.next() );            
        }

    private void applyRender( Arguments args, Resource root, Model result )
        {
        List<String> render = args.listFor(  "render", ""  );
        for (Iterator<String> rend = render.iterator(); rend.hasNext();)
            renderer( root, rend.next(), result );        
        }

    private void renderer( Resource root, String shortName, Model result )
        {
        Model toAdd = ModelFactory.createDefaultModel();
        Model sub = ResourceUtils.reachableClosure( root.inModel( result ) );
        ModelQuery q = ModelQuery.create();
        QueryVariable iv = q.createVariable();
        q.add( q.createVariable(), EYE.renderer, iv );
        q.add( iv, EYE.shortName, ResourceFactory.createPlainLiteral( shortName.toLowerCase() ) );
        for (Iterator<? extends List<Node>> renderers = q.run( sub ); renderers.hasNext();)
            {
            List<Node> row = renderers.next();
            toAdd.add( root, EYE.renderer, iv.value( row ) );
            }
        if (toAdd.isEmpty())
            System.err.println( "no render named " + shortName + " known; will use default renderer" );
        result.add( toAdd );
        }

    private void applyAssumes( Arguments args, Resource root, Model config )
        {
        List<String> assumes = args.listFor( "assume", "" );
        for (Iterator<String> as = assumes.iterator(); as.hasNext();) assume( config, root, as.next() );
        }

    private void assume( Model config, Resource root, String name )
        { 
    	ResIterator it = config.listSubjectsWithProperty( RDF.type, EYE.mirror );
    	boolean wasShortName = false;
    	while ( it.hasNext() )
    		{
    		Resource curr = it.nextResource();
    		if ( config.contains( curr, EYE.shortName, name ) )
    			{
    			wasShortName = true;
    			NodeIterator nameIt = config.listObjectsOfProperty( curr, EYE.path );
    			while ( nameIt.hasNext() )
    				root.addProperty( EYE.assume, nameIt.nextNode().asNode().getLiteralLexicalForm() ); 
    			}
    		}
    	if ( !wasShortName )
    		root.addProperty( EYE.assume, name ); 
        }

    private void applyIncludes( Arguments args, Resource root, Model result )
        {
        List<String> includes = args.listFor( "include", "" );
        // System.err.println( ">> includes for " + root + " are: " + includes );
        for (Iterator<String> ex = includes.iterator(); ex.hasNext();) include( result, root, ex.next() );
        }

    private void include( Model result, Resource root, String shortName )
        {
        Model toAdd = ModelFactory.createDefaultModel();
        Model sub = ResourceUtils.reachableClosure( root.inModel( result ) );
        // System.err.println( ">> include.sub" );
        // sub.write( System.err, "N3" );
        ModelQuery q = ModelQuery.create();
        QueryVariable iv = q.createVariable();
        q.add( q.createVariable(), EYE.inspector, iv );
        q.add( iv, EYE.shortName, ResourceFactory.createPlainLiteral( shortName ) );
        for (Iterator<? extends List<Node>> inspectors = q.run( sub ); inspectors.hasNext();)
            {
            List<Node> row = inspectors.next();
            toAdd.add( root, EYE.inspector, iv.value( row ) );
            }
        if (toAdd.isEmpty()) System.err.println( ">> no inspector named " + shortName + " to include"  );
        result.add( toAdd );
        }

    private void applyExcludes( Arguments args, Resource root, Model result )
        {
        List<String> excludes = args.listFor( "exclude", "" );
        for (Iterator<String> ex = excludes.iterator(); ex.hasNext();) exclude( result, root, ex.next() );
        }

    /**
         Answer a copy of <code>model</code>, including the prefixes.
    */
    private Model copyModel( Model model )
        {
        Model result = ModelFactory.createDefaultModel().add( model );
        result.setNsPrefixes( model );
        return result;
        }

    private void exclude( Model model, Resource root, String shortName )
        {
        Model toRemove = ModelFactory.createDefaultModel();
        ModelQuery q = ModelQuery.create();
        QueryVariable iv = q.createVariable( "i" );
        q.add( q.createVariable(), EYE.inspector, iv );
        q.add( iv, EYE.shortName, ResourceFactory.createPlainLiteral( shortName ) );
        for (Iterator<? extends List<Node>> inspectors = q.run( model ); inspectors.hasNext();)
            {
            List<Node> row = inspectors.next();
            Resource inspector = (Resource) iv.value( row );
            toRemove.add( root, EYE.inspector, inspector );
            }
        model.remove( toRemove );
        }

    /**
     	Answer a configuration model which is the union of all of the -config
        models, or etc/eyeball-config.n3 if there are no -config options.
    */
    protected Model loadBaseConfig()
        {
        Model config = ModelFactory.createDefaultModel();
        config.add( EYE.Eyeball, RDFS.subClassOf, JA.Object );
        config.setNsPrefix( "eye", EYE.getURI() );
        List<String> configFileNames = args.listFor( "config", "etc/eyeball-config.n3" );
        for (Iterator<String> it = configFileNames.iterator(); it.hasNext();) 
            FileManager.get().readModel( config, it.next() );
        return config;
        }

    private void acceptOneModel( PrintStream out, ExportModel em, OntLoader loader, String dataName )
        {
        Model input = loader.ontLoad( dataName ).getBaseModel();
        Report r = new Report()
            .declareOrder( EYE.signatureInclusionFails, EYE.requiredItems )
            .declareOrder( EYE.requiredItems, EYE.signedItems )
            .declareOrder( EYE.signedItems, EYE.missingItems )
            ;
        boolean ok = SignMaster.Check.checkSignature( r, configRoot, input ).ok;
        if (ok) 
            out.println( "signature checked" );
        else
            eyeball.render( r, out );
        }

    private void signOneModel( PrintStream out, ExportModel em, OntLoader loader, String dataName )
        {
        OntModel m = loader.ontLoad( dataName );
        input = m.getBaseModel();
        em.export( m );
        Report r = inspectAndReport( m );
        if (r.valid()) 
            outputSignedModel( System.out, dataName );
        else
            {
            allChecksPassed = false;
            eyeball.render( r, out );
            }
        }

    private void outputSignedModel( PrintStream out, String dataName )
        { // Assume the only existing signmaster ...
        Resource toStamp = Copy.copyStatements( configRoot, EYE.assume );
        Copy.addProperties( toStamp, configRoot, EYE.inspector );
        Copy.addStringProperties( toStamp, EYE.checked, singleton( dataName ) );
        toStamp.addProperty( EYE.version, "Eyeball 2.1" );
        toStamp.addProperty( EYE.comment, "Eventually, you will be able to provide a comment" );
        new SignMaster.Signer( "SignMaster 0.1", toStamp ).sign( input ).write( out, "N3" );
        }

    private List<String> singleton( String dataName )
        {
        List<String> result = new ArrayList<String>();
        result.add( dataName );
        return result;
        }

    private void checkOneModel( PrintStream out, ExportModel em, OntLoader loader, String dataName )
        {
        OntModel m = loader.ontLoad( dataName );
        input = m.getBaseModel();
        input.remove( SignMaster.Signer.getSignatures( input ) );
        em.export( m );
        Report r = inspectAndReport( m );
        if ( args.has( "repair" ) )
        	analyseAndRepairModel( out, dataName, m, r );
        else if ( args.has( "analyse" ) || args.has( "analyze" ) )
        	eyeball.analyse( r, m ); 
        eyeball.render( r, out );
        }

    private Report inspectAndReport( OntModel m )
        {
        Report r = eyeball.inspect( new Report(), m );
        r.setPrefixes( input );
        if (!r.valid()) allChecksPassed = false;
        return r;
        }

    private void analyseAndRepairModel( PrintStream out, String dataName, OntModel m, Report r )
        {
        eyeball.analyse( r, m );
        // Move m to a static OntModel - we don't want the doctor to perform work directly on the database!
        m = (OntModel) ModelFactory.createOntologyModel().add( m.getBaseModel().listStatements() );
        eyeball.doctorModel( r, m );
        Model repaired = m.getBaseModel();
        String outPath = args.valueFor( "repair", "" );
        String vocab = getVocabulary();
        
        if ( outPath.equals( "" ) ) // Default to eyeball-specified 'out' if the user didn't specify a location
        	{
        	if ( out != null )
        		{
            	repaired.write( out, vocab );
            	out.println();
        		}
        	}
        else // Output to the medium the user specified
        	try 
        		{
        		String bkpPath = outPath + ".old" + "-" + System.currentTimeMillis();
        		if ( outPath.startsWith( "jdbc:" ) )
        			{
        			if ( dataName.startsWith( "jdbc:" ) )// We need to backup the old data
            			new JDBCLoader().load( bkpPath ).add( input );
        			ModelRDB dbm = (ModelRDB) new JDBCLoader().load( outPath );
        			dbm.begin().remove( input ).add( repaired ).commit();
        			}
        		else
        			{
            		File outFile = new File( outPath );
            		if ( outFile.exists() ) // Don't overwrite the old file if we can help it!
            			outFile.renameTo( new File( bkpPath ) );
                    BufferedWriter modelOut = new BufferedWriter( new FileWriter( outPath ) );
                    repaired.write( modelOut, vocab );
                    modelOut.close();
        			}
        		} 
        	catch (IOException e) 
        		{ e.printStackTrace(); }
        }

    private String getVocabulary()
        {
        String vocab = "RDF/XML-ABBREV"; // Use this renderer if the user didn't specify an N3 preference
        if ( eyeball.getRenderer().getClass().toString().equals( "class com.hp.hpl.jena.eyeball.renderers.N3Renderer" ) )
        	vocab = "N3";
        return vocab;
        }
    
    public Eyeball getEyeball()
    	{ return eyeball; }

    /**
        Answer a loader that will apply the ont: and jdbc: pseudo-prefixes when
        loading a model described by a URI.
    */
    private OntLoader createLoader()
        {
        Loader db = new JDBCLoader();
        Loader base = new FileLoader();
        return new OntLoader( new JDBCSensitiveLoader( db, base ) );
        }
    }


/*
 * (c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
   (c) Copyright 2010 Epimorphics Limited.
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