/*
 	(c) Copyright 2006 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited.
 	All rights reserved.
 	$Id: TestCommandLine.java,v 1.2 2010/03/26 14:50:33 chris-dollin Exp $
*/

package com.hp.hpl.jena.extras.commandline.test;

import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;

import com.hp.hpl.jena.extras.commandline.Arguments;

@RunWith(JUnit4.class) public class TestCommandLine
    {
    @Test public void testDefaultKeywordIsLastAllowed()
        {
        assertEquals( "foo", new Arguments( "foo" ).getDefaultKeyword() );
        assertEquals( "spoo", new Arguments( "foo spoo" ).getDefaultKeyword() );
        assertEquals( "goo", new Arguments( "spoo foo goo" ).getDefaultKeyword() );
        assertEquals( "floo", new Arguments( "foo goo spoo floo" ).getDefaultKeyword() );
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