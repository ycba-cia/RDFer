/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved - see end of file.
 	$Id: SomeBoxesPanel.java,v 1.6 2009/01/29 14:12:04 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.gui;

import java.awt.Component;
import java.util.*;

import javax.swing.*;

public class SomeBoxesPanel extends JPanel
    {
    protected final JTextField f;
    protected final List<String> elements;
    protected final List<JCheckBox> boxes = new ArrayList<JCheckBox>();
    
    protected boolean show = false;
    protected boolean analyse = false;
    protected boolean repair = false;
    protected boolean interactive = false;
    protected String language = "N3";
    
    public SomeBoxesPanel( JTextField f, List<String> elements )
        { this.f = f; this.elements = elements; }

    public void setShowModel( boolean show )
        { this.show = show; }
    
    public boolean showModel()
        { return this.show; }

    public void setAnalyse( boolean analyse )
        { this.analyse = analyse; }
    
    public boolean analyse()
        { return this.analyse; }

    public void setRepair( boolean repair )
        { this.repair = repair; }
    
    public boolean repair()
        { return this.repair; }

    public void setInteractive( boolean interactive )
        { this.interactive = interactive; }
    
    public boolean interactive()
        { return this.interactive; }

    public void setLanguage( String language )
        { this.language = language; }
    
//    [{<=- languages = { "N3", "RDF/XML", "Simple Text + N3", "Simple Text + RDF/XML" }; -=>}]
    
    public String writerLanguage()
		{ 
    	if ( language == "Simple Text + N3" )
    		return "N3";
    	else if ( language == "Simple Text + RDF/XML" || language == "RDF/XML" )
    		return "RDF/XML-ABBREV";
    	else
    		return language;
		}
    
    public String eyeballLanguage()
		{
    	if ( language.startsWith( "Simple Text" ) )
    		return "text";
    	else
    		return language.toLowerCase(); 	
		}
    
    public Collection<String> getItems()
        {
        List<String> L = new ArrayList<String>();
        fromBoxes( L );
        String s = f.getText();
        if (!s.equals( "" )) L.add( s );
        L.addAll( elements );
        return L;
        }
    
    protected void fromBoxes( List<String> l )
        {
        for (int i = 0; i < boxes.size(); i += 1)
            {
            JCheckBox box = boxes.get(i);
            if (box.isSelected()) l.add( box.getText() );
            }
        }
    
    public Component addBox( JCheckBox box )
        {
        boxes.add( box );
        return box;
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