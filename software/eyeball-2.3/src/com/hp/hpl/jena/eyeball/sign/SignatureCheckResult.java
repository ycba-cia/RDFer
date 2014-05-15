/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved.
 	$Id: SignatureCheckResult.java,v 1.5 2008/11/04 09:40:57 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.sign;

/**
    The result from checking a signature: it can be verified, or failed, or
    failed because it wasn't signed.
    
    @author kers
*/
public class SignatureCheckResult
    {
    /**
        The item was signed and the signature works.
    */
    public static final SignatureCheckResult verified = new SignatureCheckResult( "verified", true );
    
    /**
        The item wasn't signed.
    */
    public static final SignatureCheckResult unsigned = new SignatureCheckResult( "unsigned", false );
    
    /**
        The iterm was signed but the signature check fails..
    */
    public static final SignatureCheckResult failed = new SignatureCheckResult( "failed", false );
    
    /**
        true iff the item was signed and the signature worked.
    */
    public final boolean ok;
    
    /**
        The string for toString() to print.
    */
    protected final String printString;
    
    /**
        There's only those three constants.
    */
    private SignatureCheckResult( String printString, boolean ok ) 
        { this.printString = printString; this.ok = ok; }
    
    public String toString()
        { return printString; }
    }

/*
 * (c) Copyright 2007 Hewlett-Packard Development Company, LP
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
