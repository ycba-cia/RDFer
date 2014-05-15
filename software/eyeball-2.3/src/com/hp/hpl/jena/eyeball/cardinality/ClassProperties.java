/*
 (c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 All rights reserved - see end of file.
 $Id: ClassProperties.java,v 1.6 2009/01/20 09:23:08 chris-dollin Exp $
 */

package com.hp.hpl.jena.eyeball.cardinality;

import java.util.*;

import com.hp.hpl.jena.rdf.model.*;

public class ClassProperties
    {
    private final Map<Resource, PropertyCardinality> properties;

    public ClassProperties()
        { this.properties = new HashMap<Resource, PropertyCardinality>(); }

    public Set<Resource> onProperties()
        { return new HashSet<Resource>( properties.keySet() ); }

    public ClassProperties addProperty( Resource node, int min, int max )
        {
        PropertyCardinality pc = properties.get( node );
        PropertyCardinality x = pc == null
            ? new PropertyCardinality( node, min, max )
            : new PropertyCardinality
                (
                node,
                Math.max( min, pc.minCardinality() ),
                Math.min( max, pc.maxCardinality() )
                )
            ;
        properties.put( node, x );
        return this;
        }

    public PropertyCardinality get( Resource resource )
        { return properties.get( resource ); }

    }

/*
 * (c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The name of the author may not
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
