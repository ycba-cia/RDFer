/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 	All rights reserved.
 	$Id: TestSigning.java,v 1.3 2010/03/26 14:50:32 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.sign.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.hp.hpl.jena.eyeball.Report;
import com.hp.hpl.jena.eyeball.sign.*;
import com.hp.hpl.jena.eyeball.test.EyeballTestBase;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.rdf.model.*;

@RunWith(JUnit4.class) public class TestSigning extends EyeballTestBase
    {
    public TestSigning()
        {}

    @Test public void testX()
        {
        Model a = model( "a goo b" );
        Model b = modelAdd( model().add(  a  ), "x eye:signature y" );
        assertEquals( new SignMaster_0_1().signature( a ), new SignMaster_0_1().signature( b ) );
        }

    protected Resource transientSpoo( String assumed, String checked, String version, String comment )
        {
        return resourceInModel( "x eye:checked " + checked );
        }
    
    @Test public void testSigningWorks()
        {
        Model m = model( "a spoo b" );
        SignMaster.Signer s = new SignMaster.Signer( "SignMaster 0.1", transientSpoo( "assumed", "checked", "version", "comment" ) );
        Model mDash = s.sign( m );
        assertEquals( SignatureCheckResult.verified, SignMaster.Check.checkSignature( mDash ) );
        }        
        
    @Test public void testSigningFails()    
        {
        Model m = model( "a spoo b; c flarn d" );
        SignMaster.Signer s = new SignMaster.Signer( "SignMaster 0.1", transientSpoo( "assumed", "checked", "version", "comment" ) );
        Model mDash = s.sign( m );
        mDash.remove( statement( "a spoo b" ) );
        assertEquals( SignatureCheckResult.failed, SignMaster.Check.checkSignature( mDash ) );
        }
    
    @Test public void testSigningDiscardsOldSignatures()
        {
        Model m = model( "a spoo b" );
        SignMaster.Signer s = new SignMaster.Signer( "SignMaster 0.1", transientSpoo( "assumed", "checked", "version", "comment" ) );
        Model mDash = s.sign( m );
        Model mSecond = s.sign( mDash );
        assertEquals( 1, mSecond.listSubjectsWithProperty( EYE.signature ).toList().size() );
        assertEquals( SignatureCheckResult.verified, SignMaster.Check.checkSignature( mSecond ) );
        }
    
    @Test public void testSigningChecksConfigurations()
        {
        testSigningChecksInspectors( SignatureCheckResult.failed, "_x eye:inspector checked; _x eye:inspector other" );
        testSigningChecksInspectors( SignatureCheckResult.verified, "_x eye:inspector checked" );
        }
    
    public void testSigningChecksInspectors( SignatureCheckResult expected, String required )
        {
        Model m = model( "a spoo b; c flarn d" );
        SignMaster.Signer s = new SignMaster.Signer( "SignMaster 0.1", resourceInModel( "x eye:inspector " + "checked" ) );
        Model mDash = s.sign( m );
        Resource config = resourceInModel( required );
        Report r = new Report();
        assertEquals( expected, SignMaster.Check.checkSignature( config, mDash ) );
        assertEquals( expected, SignMaster.Check.checkSignature( r, config, mDash ) );
        }
    }

/*
 * (c) Copyright 2007 Hewlett-Packard Development Company, LP
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