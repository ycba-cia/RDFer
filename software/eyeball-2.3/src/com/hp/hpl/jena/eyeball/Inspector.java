/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved - see end of file.
 	$Id: Inspector.java,v 1.7 2009/01/29 14:19:43 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball;

import java.util.*;

import com.hp.hpl.jena.eyeball.inspectors.NullInspector;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Statement;

/**
    An Inspector is a component that is given a model and the statements
    in it, and decides if there are any reports to be issued.
<p>
    The Eyeball framework will construct an Inspector object and call its
    methods in the following order:
    
    <ul>
      <li>once: begin( Report, OntModel assume )
      <li>once: inspectModel( Report, OntModel m )
      <li>lots: inspectStatement( Report r, Statement s )
      <li>once: end( Report r )
    </ul>
    
    The <code>Report</code> object <code>r</code> is the same
    one each time. The statements <code>s</code> are the statements
    from the base model of <code>m</code>. The model 
    <code>assume</code> is a single OntModel containing all the
    models taken as assumptions, <i>ie</i> the ontologies against which
    the inspection is made.
    
<p>
    If an inspector can operate by dealing with once statement at a time,
    it is reccomended that it do so. If an Eyeballing has multiple inspectors,
    they share a single iteration through the model under inspection for
    the <code>inspectStatement</code> methods. 
    
<p>
    It is the responsibility of the <code>begin</code> method to do
    necessary initialisation, such as declare the properties used in reports.
    
    @author kers
*/
public interface Inspector
    {
    /**
        Initialise this Inspector, given the assumed ontologies and the
        report object which will hold the reports from this inspection.
        This method should declare the report properties this inspector
        uses -- see the <code>Report</code> documentation.
    */
    public void begin( Report r, OntModel assume );
    
    /**
       Inspect the model as a whole. This is an opportunity to perform
       arbitrary queries over the model's statements and associated
       entities, eg its prefix-mappings.
    */
    public void inspectModel( Report r, OntModel m );
    
    /**
        Inspect a single statement <code>s</code> from the model being
        inspected. The order of the statements is not specified.
    */
    public void inspectStatement( Report r, Statement s );
    
    /**
        End the inspection. This method is called after all the statements have
        been individually inspected, so any final analyssi can be done.
    */
    public void end( Report r );
    
    /**
        This class is a utility class to contain the <code>createInspectorList</code>
        method.
        
        @author kers
    */
    public static class Operations
        {
        /**
            Answer an inspector which, for each of its methods, calls the
            corresponding methods of the inspectors in <code>inspectorSet</code>
            in some order.
        <p>
            If the list is empty, a <code>NullInspector</code> is returned. If it
            is a singleton, its only element is returned. Otherwise a new
            Inspector object is constructed.
        */
        public static Inspector create( Set<Inspector> inspectorSet )
            {
            int size = inspectorSet.size();
            return
                size == 0 ? new NullInspector()
                : size == 1 ? inspectorSet.iterator().next()
                : createInspectorList( inspectorSet, size )
                ;
            }

        private static Inspector createInspectorList( Set<Inspector> inspectorSet, final int size )
            {
            final Inspector [] inspectors = inspectorSet.toArray( new Inspector[inspectorSet.size()] );
            return new InspectorSet( inspectors, size );
            }
        }
    
    public static final class InspectorSet implements Inspector
        {
        private final Inspector[] inspectors;
    
        private final int size;
    
        private InspectorSet( Inspector[] inspectors, int size )
            {
            this.inspectors = inspectors;
            this.size = size;
            }
    
        public void begin( Report r, OntModel assume )
            {
            for (int i = 0; i < size; i += 1) inspectors[i].begin( r, assume );
            }
    
        public void inspectModel( Report r, OntModel m )
            {
            for (int i = 0; i < size; i += 1) inspectors[i].inspectModel( r, m );
            }
    
        public void inspectStatement( Report r, Statement s )
            {
            for (int i = 0; i < size; i += 1) inspectors[i].inspectStatement( r, s );
            }
    
        public void end( Report r )
            {
            for (int i = 0; i < size; i += 1) inspectors[i].end( r );
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