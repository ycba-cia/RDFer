/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved - see end of file.
 	$Id: Eyeballer.java,v 1.11 2009/03/24 14:17:13 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

import javax.swing.*;

import jena.*;

import com.hp.hpl.jena.assembler.Assembler;
import com.hp.hpl.jena.eyeball.Report;
import com.hp.hpl.jena.eyeball.assemblers.*;
import com.hp.hpl.jena.eyeball.loaders.FileLoader;
import com.hp.hpl.jena.eyeball.loaders.JDBCLoader;
import com.hp.hpl.jena.eyeball.loaders.JDBCSensitiveLoader;
import com.hp.hpl.jena.eyeball.loaders.Loader;
import com.hp.hpl.jena.eyeball.loaders.OntLoader;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.NotFoundException;

public class Eyeballer implements Controlled
    {
	eyeball eye;
    
	public static void outputModel( Model m, String title, String message, String language )
		{
	    ByteArrayOutputStream s = new ByteArrayOutputStream();
		try
	    	{ m.write( s, language ); }
	    catch ( Exception e )
	    	{ displayProblemBacktrace( e ); }
			drawOutput( title, message, s );
		}
    
	public void outputReport( Report r, String title, String message )
		{
	    ByteArrayOutputStream s = new ByteArrayOutputStream();
	    eye.getEyeball().render( r, new PrintStream( s ) );
		drawOutput( title, message, s );
		}
	
	public static void drawOutput( String title, String message, ByteArrayOutputStream s )
		{
		JTextArea a = new JTextArea();
		JScrollPane scrollPane = new JScrollPane( a );

	    JFrame frame = new JFrame();
	    frame.setTitle( title );
	    frame.setSize( 600, 400 );
	
	    JPanel top = new JPanel();
	    top.setLayout( new BorderLayout() );
	    top.add( new JLabel( message ), BorderLayout.NORTH );
	    frame.getContentPane().add( top );
	    top.add( scrollPane );
	    
	    JPanel bottom = new JPanel();
	    bottom.setLayout( new BorderLayout() );
	    bottom.add( createSaveButton( s ), BorderLayout.CENTER );
	    frame.add( bottom, BorderLayout.SOUTH );
	
	    a.setText( s.toString() );
	    a.setEditable( false );
	    frame.setVisible( true );
		}
	
	private static final JFileChooser fileChooser = new JFileChooser( "." );
	
	private static Component createSaveButton( final ByteArrayOutputStream content )
		{
        final JButton result = new JButton( "save eyeball result" );
        result.addActionListener
            (
            new ActionListener()
                {
                public void actionPerformed( ActionEvent e )
                    {
                	JFileChooser fc = fileChooser;
                    if (fc.showDialog( null, "Save result" ) == JFileChooser.APPROVE_OPTION)
                        {
                    	try
                    		{
                    		OutputStream out = new FileOutputStream( fc.getSelectedFile().getAbsolutePath() );
                    		try { content.writeTo( out ); }
                    		finally { out.close(); }
                    		}
                    	catch (Exception ex) { displayProblemBacktrace( ex ); }
                        }
                    }
                }
            );
        return result;
		}
	
    private OntLoader createLoader()
	    {
	    Loader db = new JDBCLoader();
	    Loader base = new FileLoader();
	    OntLoader loader = new OntLoader( new JDBCSensitiveLoader( db, base ) );
	    return loader;
	    }
	
    public void go( List<? extends JCheckBox> inspectorCheckBoxes, SomeBoxesPanel assumed, SomeBoxesPanel checked )
        {
        List<String> args = new ArrayList<String>();
        args.add( "-assume" );
        args.addAll( assumed.getItems() );
        args.add( "-check" );
        args.addAll( checked.getItems() );
        args.add( "-render" );
        args.add( checked.eyeballLanguage() );
        args.add( "-include" );
        for (Iterator<? extends JCheckBox> it = inspectorCheckBoxes.iterator(); it.hasNext();)
            {
            JCheckBox b = it.next();
            if (b.isSelected()) args.add( b.getText() );
            }
        /*if ( checked.repair() )			Strictly speaking these options should go into args
            args.add( "-repair" );				but they are generally unnecessary for what we are
        else if ( checked.analyse() )				doing with the eyeball!
        	args.add( "-analyse" );*/
        try 
        	{
        	Assembler.general
            	     .implementWith( EYE.Inspector, new InspectorAssembler() )
            	     .implementWith( EYE.Analysis, new AnalysisAssembler() )
            	     .implementWith( EYE.Doctor, new DoctorAssembler() )
            	     .implementWith( EYE.Eyeball, new EyeballAssembler() )
            	     .implementWith( EYE.Renderer, new RendererAssembler() );
        	eye = new eyeball( args.toArray( new String[args.size()] ) ); 
        	Iterator<String> it = checked.getItems().iterator();
        	while ( it.hasNext() )
        		{
        		OntLoader loader = createLoader();
        		OntModel m = loader.ontLoad( it.next() );
        		if ( checked.showModel() )
            		outputModel( m, "Input model display", "This is the input model", checked.writerLanguage() );
        		Report r = eye.getEyeball().inspect( new Report(), m );
            	if ( checked.analyse() || checked.repair() )
            		{
            		eye.getEyeball().analyse( r, m );
            		outputReport( r, "Eyeball Report", "This is the analysed report" );
            		if ( checked.repair() )
	        			{
            			if ( checked.interactive() )
            				new InteractiveDoctor( r, m, checked.writerLanguage(), eye.getEyeball() );
            			else
            				{
            				eye.getEyeball().doctorModel( r, m );
	    	        		outputModel( m, "Eyeball Repair model display", "This is the repaired model", checked.writerLanguage() );
            				}
	        			}
            		}
            	else
            		outputReport( r, "Eyeball Report", "This is the report" );
        		}
        	}
        catch (Exception e) { displayProblem( e ); }
        }
    
    public static void displayProblem( Exception e )
        {
        try
            { throw e; }
        catch (NotFoundException nf)
            { popup( nf.getMessage() ); }
        catch (Exception ee)
            { displayProblemBacktrace( e ); }
        }
    
    protected static void popup( String message )
        {
        JFrame frame = new JFrame();
        JPanel top = new JPanel();
        top.setLayout( new BorderLayout() );
        frame.setTitle( "Eyeball error report" );
        frame.getContentPane().add( new JScrollPane( top ) );
        top.add( new JLabel( "<html><font color=\"red\">&nbsp;&nbsp;oops!</font></html>" ), BorderLayout.NORTH );
        top.add( new JLabel( message ) );
        frame.setSize( 200, 80 );
        frame.setVisible( true );
        }
    
    /**
     	@param e
    */
    protected static void displayProblemBacktrace( Exception e )
        {
    	ByteArrayOutputStream s = new ByteArrayOutputStream();
        e.printStackTrace( new PrintStream( s ) );
        drawOutput( "Eyeball error report", "<html><font color=\"red\">&nbsp;&nbsp;alas</font>, an error occurred", s );
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