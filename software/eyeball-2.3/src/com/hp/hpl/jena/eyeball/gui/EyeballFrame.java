/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved - see end of file.
 	$Id: EyeballFrame.java,v 1.8 2009/01/29 14:12:04 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ListDataListener;

public class EyeballFrame extends JFrame
    {
    protected Controlled thing;
    
    public EyeballFrame( String text, Controlled thing )
        {
        this.thing = thing;
        setTitle( "Eyeball experimental GUI" );
        setSize( 800, 375 );
        JPanel top = new JPanel();
        top.setLayout( new BorderLayout() );
        getContentPane().add( top );
        top.add( createTabbedPane() );
        top.add( new JLabel( new ImageIcon( "etc/jena-logo-small.png" ), JLabel.RIGHT ), BorderLayout.SOUTH );
        this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        }
    
    protected JComponent createTabbedPane()
        {
        List<JCheckBox> inspectorCheckBoxes = new ArrayList<JCheckBox>();
        JTabbedPane pane = new JTabbedPane();
        pane.add( "source", createSourcePane( inspectorCheckBoxes ) );
        pane.add( "inspectors", createInspectorsPane( inspectorCheckBoxes ) );
        return pane;
        }
    
    static final String [] inspectors =
        {
        "+property=check for undeclared predicates",
        "+class=check for undeclared classes",
        "+literal=check literals for legal language codes and well-formed types",
        "+uri=check all URIs in RDF statements",
        "+vocabulary=check URIs from a namespace are declared in that schema",
        "+prefix=check prefix namespace usage",
        "-consistentType=check that individuals have consistent types",
        "-allTyped=check that every resource is typed",
        "-cardinality=check that cardinality restrictions are respected by base data",
        "-owl=check that restrictions have one onProperty and constraint"
        };
    
    protected JPanel createInspectorsPane( List<JCheckBox> inspectorCheckBoxes )
        {
        JPanel result = new JPanel();
        for (int i = 0; i < inspectors.length; i += 1)
            {
            String entry = inspectors[i];
            boolean selected = entry.charAt( 0 ) == '+';
            int eq = entry.indexOf( '=' );
            String name = entry.substring( 1, eq );
            String desc = entry.substring( eq + 1 );
            JCheckBox checkBox = new JCheckBox( name, selected );
            checkBox.setToolTipText( desc );
            inspectorCheckBoxes.add( checkBox );
            result.add( checkBox );
            }
        return result;
        }
    
    protected JComponent createSourcePane( List<JCheckBox> inspectorCheckBoxes )
        {
        JPanel result = new JPanel( );
        result.setLayout( new BoxLayout( result, BoxLayout.Y_AXIS ) );
        List<String> sources = new ArrayList<String>();
        List<String> schemas = new ArrayList<String>();
        JTextField textField = new JTextField( "testcases/ubertestcase.n3", 40 );
        SomeBoxesPanel A = createThingySelector( "schema filename: ", schemas );
        SomeBoxesPanel S = createThingySelector( "source filename: ", textField, sources );
        result.add( createChecked( inspectorCheckBoxes, A, S ) );
        result.add( createAssumed( A ) );
        return result;
        }
    
    protected JComponent createChecked( final List<JCheckBox> inspectorCheckBoxes, SomeBoxesPanel assumed, SomeBoxesPanel source )
        {
        JPanel result = new JPanel();
        result.setBorder( BorderFactory.createTitledBorder( "check sources" ) );
        result.setLayout( new BoxLayout( result, BoxLayout.Y_AXIS ) );
        result.add( source );
        JPanel sub = new JPanel();
        sub.setLayout(  new BoxLayout( sub, BoxLayout.X_AXIS ) );
        sub.add( new JLabel( "eyeball options: " ) );
        sub.add( createShowButton( assumed, source ) );
        sub.add( createAnalyseButton( assumed, source ) );
        sub.add( createRepairButton( assumed, source ) );
        sub.add( createInteractiveButton( assumed, source) );
        result.add( sub );
        JPanel outConf = new JPanel();
        outConf.setLayout(  new BoxLayout( outConf, BoxLayout.X_AXIS ) );
        outConf.add( new JLabel( "language: " ) );
        outConf.add( createLanguageComboBox( assumed, source ) );
        result.add( outConf );
        result.add( createGoButton( inspectorCheckBoxes, assumed, source ) );
        return result;
        }
    
    protected ListModel listModel( final List<String> elements )
        { return new ListModel()
            {
            public int getSize()
                { return elements.size(); }
    
            public Object getElementAt( int index )
                { return elements.get( index ); }
    
            public void addListDataListener( ListDataListener l )
                {
                }
    
            public void removeListDataListener( ListDataListener l )
                {
                }
            };
        }
    
    protected Component createMoreButton( final JTextField f, final JList list, final List<String> elements )
        {
        JButton result = new JButton( "more" );
        result.addActionListener
            (
            new ActionListener()
                {
                public void actionPerformed( ActionEvent e )
                    {
                    String item = f.getText().trim();
                    if (item.length() > 0)
                        {
                        elements.add( item );
                        list.setModel( listModel( elements ) );
                        }
                    f.setText( "" );
                    }
                }
            );
        return result;
    
        }
    
    protected final JFileChooser fileChooser = new JFileChooser( "." );
    
    protected JComponent createBrowseButton( final JTextField f )
        {
        JButton result = new JButton( "browse" );
        result.addActionListener
            (
            new ActionListener()
                {
                public void actionPerformed( ActionEvent e )
                    {
                    JFileChooser fc = fileChooser;
                    if (fc.showDialog( null, "choose" ) == JFileChooser.APPROVE_OPTION)
                        {
                        f.setText( fc.getSelectedFile().getAbsolutePath() );
                        }
                    }
                }
            );
        return result;
        }
    
    protected JComponent createAssumed( SomeBoxesPanel T )
        {
        JPanel result = new JPanel();
        result.setBorder( BorderFactory.createTitledBorder( "assume schemas" ) );
        result.setLayout( new BoxLayout( result, BoxLayout.Y_AXIS ) );
        result.add( T );
        result.add( createAssumedBoxes( T ) );
        return result;
        }
    
    protected JComponent createAssumedBoxes( SomeBoxesPanel T )
        {
        JPanel result = new JPanel();
        result.add( new JLabel( "built-in schemas: " ) );
        result.add( T.addBox( new JCheckBox( "owl" ) ) );
        result.add( T.addBox( new JCheckBox( "dc" ) ) );
        result.add( T.addBox( new JCheckBox( "dcterms" ) ) );
        result.add( T.addBox( new JCheckBox( "dc-all" ) ) );
        return result;
        }
    
    protected SomeBoxesPanel createThingySelector( String label, List<String> elements )
        {
        return createThingySelector( label, new JTextField( "", 40 ), elements );
        }
    
    protected SomeBoxesPanel createThingySelector( String label, JTextField f, List<String> elements )
        {
        SomeBoxesPanel result = new SomeBoxesPanel( f, elements );
        result.setLayout( new FlowLayout() );
        result.add( new JLabel( label ) );
        result.add( f );
        JList list = new JList( listModel( elements ) );
        result.add( list );
        result.add( createBrowseButton( f ) );
        result.add( createMoreButton( f, list, elements ) );
        return result;
        }
    
    protected void go( List<JCheckBox> inspectorCheckBoxes, SomeBoxesPanel assumed, SomeBoxesPanel checked )
        {
        thing.go( inspectorCheckBoxes, assumed, checked );
        }
    
    protected Component createGoButton
        ( final List<JCheckBox> inspectorCheckBoxes, final SomeBoxesPanel assumed, final SomeBoxesPanel source )
        {
        final JButton result = new JButton( "GO" );
        result.addActionListener
            (
            new ActionListener()
                {
                public void actionPerformed( ActionEvent e )
                    {
                    go( inspectorCheckBoxes, assumed, source );
                    }
                }
            );
        return result;
        }
    
    protected Component createShowButton
        ( final SomeBoxesPanel assumed, final SomeBoxesPanel source )
        {
        final JCheckBox result = new JCheckBox( "show model" );
        result.addActionListener
            (
            new ActionListener()
                {
                public void actionPerformed( ActionEvent e ) { source.setShowModel( result.isSelected() ); }
                }
            );
        return result;
        }
    
    protected Component createAnalyseButton
        ( final SomeBoxesPanel assumed, final SomeBoxesPanel source )
        {
        final JCheckBox result = new JCheckBox( "analyse report" );
        result.addActionListener
            (
            new ActionListener()
                {
                public void actionPerformed( ActionEvent e ) { source.setAnalyse( result.isSelected() ); }
                }
            );
        return result;
        }
    
    protected Component createRepairButton
        ( final SomeBoxesPanel assumed, final SomeBoxesPanel source )
        {
        final JCheckBox result = new JCheckBox( "repair model" );
        result.addActionListener
            (
            new ActionListener()
                {
                public void actionPerformed( ActionEvent e ) { source.setRepair( result.isSelected() ); }
                }
            );
        return result;
        }
    
    protected Component createInteractiveButton
        ( final SomeBoxesPanel assumed, final SomeBoxesPanel source )
        {
        final JCheckBox result = new JCheckBox( "interactive mode" );
        result.addActionListener
            (
            new ActionListener()
                {
                public void actionPerformed( ActionEvent e ) { source.setInteractive( result.isSelected() ); }
                }
            );
        return result;
        }
    
    static final String [] languages = { "N3", "RDF/XML", "Simple Text + N3", "Simple Text + RDF/XML" };
    
    protected Component createLanguageComboBox
	    ( final SomeBoxesPanel assumed, final SomeBoxesPanel source )
	    {
	    final JComboBox result = new JComboBox( languages );
	    result.setMaximumSize( new Dimension(200, 17) );
	    result.setSelectedIndex( 0 );
	    result.addActionListener
	        (
	        new ActionListener()
	            {
	            public void actionPerformed( ActionEvent e ) { source.setLanguage( (String)result.getSelectedItem() ); }
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