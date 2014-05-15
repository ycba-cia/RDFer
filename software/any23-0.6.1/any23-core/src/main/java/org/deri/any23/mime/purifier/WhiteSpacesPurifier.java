package org.deri.any23.mime.purifier;

import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation of {@link org.deri.any23.mime.purifier.Purifier} that removes all the eventual blank
 * characters at the header of a file that might prevents its <i>MIME Type</i> detection.
 *
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 */
public class WhiteSpacesPurifier implements Purifier {

    /**
     * {@inheritDoc}
     */
    public void purify(InputStream inputStream) throws IOException {
        if(!inputStream.markSupported())
            throw new IllegalArgumentException("Provided InputStream does not support marks");

        // mark the current position
        inputStream.mark(Integer.MAX_VALUE);
        int byteRead = inputStream.read();
        char charRead = (char) byteRead;
        while(isBlank(charRead) && (byteRead != -1)) {
            // if here means that the previos character must be removed, so mark.
            inputStream.mark(Integer.MAX_VALUE);            
            byteRead = inputStream.read();
            charRead = (char) byteRead;
        }
        // if exit go back to the last valid mark.
        inputStream.reset();
    }
    
    private boolean isBlank(char c) {
        return c == '\t' || c == '\n' || c == ' ' || c == '\r' || c == '\b' || c == '\f';
    }
}
