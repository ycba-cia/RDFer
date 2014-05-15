/*
 	(c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 	All rights reserved - see end of file.
 	$Id: TestPropertyCardinality.java,v 1.3 2010/03/26 14:50:33 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.cardinality.test;

import org.junit.experimental.theories.*;
import org.junit.runner.RunWith;

import com.hp.hpl.jena.eyeball.cardinality.PropertyCardinality;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.test.ModelTestBase;

@RunWith(Theories.class) public class TestPropertyCardinality extends ModelTestBase
    {
    public TestPropertyCardinality()
        { super( "TestPropertyCardinality" ); }

    @DataPoint public static String x = "x";
    @DataPoint public static String y = "y";
    @DataPoint public static String alpha = "alpha";
    @DataPoint public static String beta = "beta";
    
    @DataPoint public static int Zero = 0;
    @DataPoint public static int One = 1;
    @DataPoint public static int Two = 2;
    @DataPoint public static int Seventeen = 17;
    @DataPoint public static int Adams = 42;
    
    @Theory public void testPropertyCardinality( String name, int min, int max )
        {
        Resource res = resource( name );
        PropertyCardinality pc = new PropertyCardinality( res, min, max );
        assertEquals( min, pc.minCardinality() );
        assertEquals( max, pc.maxCardinality() );
        assertEquals( res, pc.property() );
        }
    }


/*
 * (c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
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
