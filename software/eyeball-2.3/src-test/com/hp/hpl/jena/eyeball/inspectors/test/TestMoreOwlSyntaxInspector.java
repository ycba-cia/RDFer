/*
    (c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    (c) Copyright 2010 Epimorphics Limited
 	All rights reserved.
 	$Id: TestMoreOwlSyntaxInspector.java,v 1.1 2010/03/26 14:50:31 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.inspectors.test;

import com.hp.hpl.jena.eyeball.Inspector;
import com.hp.hpl.jena.eyeball.inspectors.OwlSyntaxInspector;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class) public class TestMoreOwlSyntaxInspector extends InspectorTestBase
    {
    protected final MyTest toDo;
    
    public TestMoreOwlSyntaxInspector( MyTest toDo )
        { this.toDo = toDo; }
    
    @Test public void runMyTest()
        { toDo.doThis(); }

    static abstract class MyTest extends InspectorTestBase
        {
        @Override protected Class<? extends Inspector> getInspectorClass()
            { return OwlSyntaxInspector.class; }
    
        public abstract void doThis();
        }
    
    protected Class<? extends Inspector> getInspectorClass()
        { return OwlSyntaxInspector.class; }

    static final String [] constraints = 
        { "owl:hasValue X", "owl:allValuesFrom A", "owl:someValuesFrom B", 
           "owl:minCardinality 2", "owl:maxCardinality 17", "owl:cardinality 3" };

    @Parameters public static Collection<?> parameters() 
        {
        List<Object[]> result = new ArrayList<Object[]>();
        String onPropertyA = "owl:onProperty A", onPropertyB = "owl:onProperty B";
        for (String C1: constraints)
            {
            result.add( new Object[] {createSingleReasonTest( "eye:missingOnProperty", "[" + C1 + "]" ) } );
            result.add( new Object[] { createSingleReasonTest( "eye:multipleOnProperty", "[" + C1 + " & " + onPropertyA + " & " + onPropertyB +"]" ) } );
            for (String C2: constraints)
                if (C1 != C2)
                    result.add( new Object[] { createSingleReasonTest( "eye:multipleConstraint", "[" + C1 + " & " + C2 + " & " + onPropertyA + "]" ) } );
            }
        return result;
        }
    
    private static MyTest createSingleReasonTest( final String reason, final String restriction )
        {
        return new MyTest()
            {
            public void doThis()
                { 
                String expected = 
                    "[eye:mainProperty eye:suspiciousRestriction"
                    + " & eye:suspiciousRestriction %M & eye:forReason %R]"
                    .replaceAll( "%M", restriction )
                    .replaceAll( "%R", reason )
                    ;
                assertIsoModels( itemModel( expected ), TestOwlSyntaxInspector.inspect( ontModel( restriction ) ).model() ); 
                }
            };
        }
    }

/*
 * (c) Copyright 2005, 2006, 2007 Hewlett-Packard Development Company, LP       (
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