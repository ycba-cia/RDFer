/*
 	(c) Copyright 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 	All rights reserved.
 	$Id: TestSetPropertiesFromString.java,v 1.4 2010/03/26 14:50:32 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.util.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.hp.hpl.jena.eyeball.test.EyeballTestBase;
import com.hp.hpl.jena.eyeball.util.SetPropertiesFromString;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.rdf.model.Model;

@RunWith(JUnit4.class) public class TestSetPropertiesFromString extends EyeballTestBase
    {
    public TestSetPropertiesFromString()
        {}

    @Test public void testSettingAssumesPrefix()
        { 
        testSettingStringAddsModel( EYE.getURI(), "eye:a eye:b eye:c", "a.b=c" );
        }

    @Test public void testSettingAllowsPrefix()
        {
        testSettingStringAddsModel( EYE.getURI(), "a eye:b eye:c", "eh:/a.b=c" );
        }

    @Test public void testSettingAllowsNumbers()
        {
        testSettingStringAddsModel( EYE.getURI(), "eye:a eye:b 17", "a.b=17" );
        }

    @Test public void testSettingAllowsStrings()
        {
        testSettingStringAddsModel( EYE.getURI(), "eye:a eye:b 'hello'", "a.b='hello'" );
        }

    private void testSettingStringAddsModel( String emptyPrefix, String expected, String settting )
        {
        Model m = model();
        m.setNsPrefix( "", emptyPrefix );
        new SetPropertiesFromString( m ).set( settting );
        assertIsoModels( model( expected ), m );
        }
    }


/*
 * (c) Copyright 2005, 2006, 2007 Hewlett-Packard Development Company, LP
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