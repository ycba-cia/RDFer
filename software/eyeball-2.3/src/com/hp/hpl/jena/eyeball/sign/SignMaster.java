/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved.
 	$Id: SignMaster.java,v 1.11 2009/01/29 12:20:13 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.sign;

import java.util.*;

import com.hp.hpl.jena.eyeball.Report;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.util.ResourceUtils;

/**
    interface for Eyeball signature operations.
<p>
    A SignMaster computes a "signature" value from a model. The signature isn't
    that strong, since Eyeball signing is meant as protection from sloppy mistakes
    rather than malicious attack. The complications arise from blank nodes
    (surprise). The signature should be reasonably robust against moving
    properties between blank nodes.
<p>
    The SignMaster also has a table of implementations, under the nested class
    <code>Known</code>, a standard way to sign RDF models under the
    class <code>Signer</code>, and a checker for those signatures under
    <code>Check</code>.
    
    @author kers
*/
public interface SignMaster
    {
    /**
        Answer the signature value of the model, computed from the triples it
        contains <i>excluding</i> eye:signature triples [and not eg the prefixes].
    */
    long signature( Model model );
    
    public static class Known
        {
        /**
            create a new SignMaster object with the given type name.
        */
        public static SignMaster create( String name )
            {
            Class<? extends SignMaster> foo = classes.get( name );
            try { return foo.newInstance(); }
            catch (Exception e) { throw new JenaException( e ); }
            }
        
        protected static final Map<String, Class<? extends SignMaster>> classes = new HashMap<String, Class<? extends SignMaster>>();
        
        static { classes.put( "SignMaster 0.1", SignMaster_0_1.class ); }
        }
    
    public static class UnsignedException extends JenaException
        {
        public UnsignedException( )
            { super( "no [or several] signature[s] found in model" ); }
        }
    
    public static class Check
        {
        /**
            Answer <b>true</b> if the model <code>m</code> is signed,
            <i>ie</i>, contains a statement with predicate 
            <code>eye:signature</code>.
        */
        public static boolean hasSignature( Model m )
            { return m.contains( null, EYE.signature, (RDFNode) null ); }
        
        /**
            Check the signature of <code>m</code> with an empty set of
            required assumptions and inspections.
        */
        public static SignatureCheckResult checkSignature( Model m )
            { return checkSignature( emptyModel.createResource(), m ); }
        
        protected static final Model emptyModel = ModelFactory.createDefaultModel();
        
        /**
            Check the model <code>m</code> to see if its signature matches
            its content. Answer <code>unsigned</code> if it is not signed,
            <code>failed</code> if the signature does not match, and
            <code>verified</code> if it does match. All the inspectors and
            assumptions of <code>req</code> must be present in 
            <code>m</code>'s signature.
        */       
        public static SignatureCheckResult checkSignature( Resource atLeast, Model m )
            { return checkSignature( new Report(), atLeast, m ); }

        public static SignatureCheckResult checkSignature( Report r, Resource req, Model m )
            {
            try
                {
                String name = getSignatureVersion( m );
                SignMaster sm = SignMaster.Known.create( name );
                // System.err.println( ">> comparing " + sm.signature( m ) + " with " + getSignatureValue( m ) );
                return 
                    sm.signature( m ) == getSignatureValue( m ) && contextChecks( r, req, m  ) 
                    ? SignatureCheckResult.verified 
                    : SignatureCheckResult.failed
                    ;
                }
            catch (UnsignedException e)
                { return SignatureCheckResult.unsigned; }
            }

        /**
            Answer true iff all the inspectors and assumptions of <code>req</code> 
            are a subset of those in the signature of <code>m</code>. 
        */
        private static boolean contextChecks( Report r, Resource req, Model m )
            {
            Resource sig = m.listSubjectsWithProperty( EYE.signature ).nextResource();
            return 
                checkByProperties( r, sig, EYE.inspector, req, EYE.inspector ) 
                && checkByProperties( r, sig, EYE.assumed, req, EYE.assume );
            }

        /**
             Answer true iff all the property values <code>sigProperty</code> of
             <code>sig</code> are a subset of all the <code>reqProperty</code>
             of <code>req</code>.
        */
        private static boolean checkByProperties( Report r, Resource sig, Property sigProperty, Resource req, Property reqProperty )
            {
            Set<RDFNode> required = req.listProperties( reqProperty ).mapWith( Statement.Util.getObject ).toSet();
            Set<RDFNode> signed = sig.listProperties( sigProperty ).mapWith( Statement.Util.getObject ).toSet();
            Set<RDFNode> difference = subtract( required, signed );
            boolean result = signed.containsAll( required );
            if (result == false)
                r.createItem().addMainProperty( EYE.signatureInclusionFails, reqProperty )
                    .addSetProperty( EYE.requiredItems, new ArrayList<RDFNode>( required ) )
                    .addSetProperty( EYE.signedItems, new ArrayList<RDFNode>( signed ) )
                    .addSetProperty( EYE.missingItems, new ArrayList<RDFNode>( difference ) )
                    ;
            return result;
            }

        private static <T> Set<T> subtract( Set<? extends T> toReduce, Set<? extends T> toSubtract )
            {
            Set<T> result = new HashSet<T>( toReduce );
            result.removeAll( toSubtract );
            return result;
            }

        private static long getSignatureValue( Model m )
            {
            List<Statement> sigs = m.listStatements( null, EYE.signature, (RDFNode) null ).toList();
            if (sigs.size() == 1) return sigs.get( 0 ).getLong();
            throw new UnsignedException();
            }

        private static String getSignatureVersion( Model m )
            {
            List<Statement> vers = m.listStatements( null, EYE.signatureVersion, (RDFNode) null ).toList();
            if (vers.size() == 1) return vers.get( 0 ).getString();
            throw new UnsignedException();
            }
        }    
    
    public class Signer
        {
        final protected SignMaster sm;
        final protected String signerName;
        final protected Resource info;
        
        public Signer( String signerName, Resource info )
            {
            this.sm = SignMaster.Known.create( signerName );
            this.info = info;
            this.signerName = signerName;
            }
    
        /**
            Sign the model <code>m</code> using the SignMaster 
            <code>sm</code> and the informative subgraph
            <code>info</code>. Any existing signature is stripped from
            <code>m</code> before it is signed.
        */
        public Model sign( Model m )
            {
            InfoStamp stamp = new InfoStamp( info );
            Model result = ModelFactory.createDefaultModel().add( m );
            result.setNsPrefixes( m );
            Resource root = stamp.stamp( Calendar.getInstance() );
            stripExistingSignatures( result );
            result.add( root.getModel() );
            result.add( root, EYE.signatureVersion, result.createTypedLiteral( signerName ) );
            result.add( root, EYE.signature, result.createTypedLiteral( sm.signature( result ) ) );
            return result;
            }

        private void stripExistingSignatures( Model m )
            { m.remove( getSignatures( m ) ); }
        
        /**
            Answer a model containing all the statements reachable from any
            Eyeball signature in <code>base</code>.
        */
        public static Model getSignatures( Model m )
            {
            Model signatures = ModelFactory.createDefaultModel();
            ResIterator sigs = m.listSubjectsWithProperty( EYE.signature );
            while (sigs.hasNext())
                signatures.add( ResourceUtils.reachableClosure( sigs.nextResource() ) );
            return signatures;
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
