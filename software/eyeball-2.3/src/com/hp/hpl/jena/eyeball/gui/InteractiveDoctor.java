package com.hp.hpl.jena.eyeball.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.renderers.LegacyTextRenderer;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDF;

public class InteractiveDoctor {

	private StmtIterator it;
	private Model repair;
	private Model m;
	private String language;
	private Eyeball eye;
	private Resource curr;    
    private LegacyTextRenderer renderer;
    
	
    public InteractiveDoctor( Report r, Model m, String language, Eyeball eye )
    	{
    	this.repair = r.model();
    	this.it = r.model().listStatements( (Resource)null, RDF.type, EYE.Item );
    	this.m = m;
    	this.language = language;
    	this.eye = eye;
    	this.renderer = new LegacyTextRenderer( ModelFactory.createDefaultModel(), r.getPredicateRegister() );
    	nextIteration();
    	}
    
    private void nextIteration()
    	{
    	if ( it.hasNext() )
    		{
	    	curr = it.nextStatement().getSubject();
	    	ByteArrayOutputStream s = new ByteArrayOutputStream();
	    	PrintStream out = new PrintStream( s );
	    	
	    	Model thisRep = ModelFactory.createDefaultModel().add( repair.listStatements( curr, null, (RDFNode) null ) );
	    	Model toAdd = ModelFactory.createDefaultModel();
	    	StmtIterator it2 = thisRep.listStatements();
	    	while ( it2.hasNext() )
	    		{
	    		RDFNode thisNode = it2.nextStatement().getObject();
	    		if ( thisNode.isAnon() )
	    			toAdd.add( repair.listStatements( repair.createResource( thisNode.asNode().getBlankNodeId() ), null, (RDFNode)null ) );
	    		}
	    	thisRep.add( toAdd );
	    	
	    	renderer.render( thisRep, out );
	    	
			askYesNo( s.toString() );
    		}
    	else
    		{
    		eye.doctorModel( new Report().setMitems( repair ), (OntModel)m );
        	Eyeballer.outputModel( m, "Eyeball Repair model display", "This is the interactively repaired model", language );
    		}
    	}
    
    protected void askYesNo( String question )
        {
        JFrame frame = new JFrame();
        JPanel top = new JPanel();
        top.setLayout( new BorderLayout() );
        frame.setTitle( "Eyeball interrogation" );
        
        frame.getContentPane().add( new JScrollPane( top ) );
        top.add( new JLabel( "Next question:" ), BorderLayout.NORTH );
        JTextArea q = new JTextArea( question );
        q.setEditable( false );
        top.add( q );
        
        JPanel bottom = new JPanel();
        bottom.setLayout( new BorderLayout( ) );
        bottom.add( new JLabel( "    Would you like to carry out this repair?" ), BorderLayout.NORTH );
        JPanel buttons = new JPanel();
        buttons.add( createYesButton( frame ) );
        buttons.add( createNoButton( frame ) );
        bottom.add( buttons, BorderLayout.SOUTH );
        frame.add( bottom, BorderLayout.SOUTH );
        
        frame.setSize( 600, 300 );
        frame.setVisible( true );
        }
    
    protected Component createYesButton ( final JFrame f )
        {
        final JButton result = new JButton( "Yes" );
        result.addActionListener
            (
            new ActionListener()
                {
                public void actionPerformed( ActionEvent e )
                    {
                	f.dispose();
                	nextIteration();
                    }
                }
            );
        return result;
        }
    
    protected Component createNoButton ( final JFrame f )
        {
        final JButton result = new JButton( "No" );
        result.addActionListener
            (
            new ActionListener()
                {
                public void actionPerformed( ActionEvent e )
                    {
                	f.dispose();
                	repair.remove( repair.listStatements( curr, null, (RDFNode)null ) );
                	nextIteration();
                    }
                }
            );
        return result;
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