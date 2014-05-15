package com.hp.hpl.jena.eyeball.repairtools;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.shared.JenaException;
import com.swabunga.spell.engine.*;
import com.swabunga.spell.event.SpellCheckEvent;
import com.swabunga.spell.event.SpellCheckListener;
import com.swabunga.spell.event.SpellChecker;
import com.swabunga.spell.event.StringWordTokenizer;

public class WordChecker implements SpellCheckListener, GenericWordChecker 
	{
    private SpellChecker spellCheck = null;
    private Iterator<Word> suggestions = null;
	
	public WordChecker( String wordList )
		{ setDictionary( wordList ); }

	/*
	 * @see com.hp.hpl.jena.eyeball.repairtools.GenericWordChecker#setDictionary(java.lang.String)
	*/
	public void setDictionary(String wordList) 
        {
		SpellDictionary dictionary = getDictionary( wordList );
		spellCheck = new SpellChecker( dictionary );							  // No nasty capitalisation, please!
		spellCheck.addSpellCheckListener( this );
		}

    private SpellDictionaryHashMap getDictionary( String wordList ) 
        {
        try
            { return new SpellDictionaryHashMap( new StringReader( "Eyeballischeckingthespellingofaword\n" + wordList ) ); }
        catch (IOException e)
            { throw new JenaException( e ); }
        }
	
	/*
	 * @see com.hp.hpl.jena.eyeball.repairtools.GenericWordChecker#spellCheck(java.lang.String)
	 */
	public Iterator<Word> spellCheck( String word )
		{
		spellCheck.checkSpelling( new StringWordTokenizer( "Eyeballischeckingthespellingofaword " + word ) );
		return this.suggestions;						   // STILL No nasty capitalisation, please!
		}
	
	public void spellingError( SpellCheckEvent event ) 
		{
		List<Word> suggestions = event.getSuggestions();
		if (suggestions.size() > 0)  this.suggestions = suggestions.iterator();
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