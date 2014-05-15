package com.hp.hpl.jena.eyeball.repairtools.doctor.test;

import com.hp.hpl.jena.eyeball.*;
import com.hp.hpl.jena.eyeball.test.EyeballTestBase;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class DoctorTestBase extends EyeballTestBase 
	{
	public DoctorTestBase() 
		{}
	
	public void runDoctorTest( String fileBase, Doctor d ) 
		{
		Model report = ModelFactory.createDefaultModel().read( fileBase + "reportoutput.n3", "N3" );
		Model m = ModelFactory.createOntologyModel().read( fileBase + "model.n3", "N3" );
		Model expected = ModelFactory.createOntologyModel().read( fileBase + "output.n3", "N3" );
		
		d.doctorModel(report, m, Eyeball.getRepairConfig());
		if ( !expected.isIsomorphicWith( m ) ) 
			{
            Model A = expected.difference( m );
            Model B = m.difference( expected );
            System.err.println( ">> expected has excess elements: " );
            A.write( System.err, "N3" );
            System.err.println( ">> result model has excess elements: " );
            B.write( System.err, "N3" );
            
			System.err.println( '\n' + "Expected :" );
			expected.write( System.err, "N3" );
			System.err.println( '\n' + "But received :" );
			m.write( System.err, "N3" );
			fail( "Model not correctly doctored!" );
			}
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