package com.hp.hpl.jena.eyeball.web.statistics.test;

import org.junit.Test;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.hp.hpl.jena.eyeball.web.statistics.*;
import com.hp.hpl.jena.eyeball.web.WebEyeballer;
import com.hp.hpl.jena.n3.IRIResolver;

/**
 *
 * @author peter
 */
@RunWith(JUnit4.class) public class EyeballingStatisticianTest extends BaseTest 
    {
    public EyeballingStatisticianTest() 
        {}
    
    static
        { IRIResolver.suppressExceptions(); }

    @Test public void testGatherEyeballingStatistics() 
        {
        WebEyeballer e = new WebEyeballer();
        e.setRepairFuncs( "repair" );
        e.setAssumes( "[owl]" );
        e.setInspectors( "[allTyped,cardinality,uri,literal,prefix,class,consistentType,property,vocabulary]" );
        e.setReportFormat( "N3" );
        e.setCollectStatistics( false );
        if (false) runStatisticianTest( e, new EyeballingStatistician(), "eyeballing" );
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