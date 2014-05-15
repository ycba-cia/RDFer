package org.deri.any23.filter;

import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.writer.TripleHandler;
import org.deri.any23.writer.TripleHandlerException;
import org.junit.Test;
import org.mockito.verification.VerificationMode;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Test case for {@link IgnoreAccidentalRDFa}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class IgnoreAccidentalRDFaTest {


    @Test
    public void testBlockCSSTriple() throws TripleHandlerException {
        checkTriple("http://www.w3.org/1999/xhtml/vocab#stylesheet", never());
    }

    @Test
    public void testAcceptGenericTriple() throws TripleHandlerException {
        checkTriple("http://www.w3.org/1999/xhtml/vocab#license", times(1));
    }

    private void checkTriple(String predicate, VerificationMode verificationMode)
    throws TripleHandlerException {
        final String DOCUMENT_URI = "http://an.html.page";
        final TripleHandler mockTripleHandler = mock(TripleHandler.class);
        final ValueFactory valueFactory = new ValueFactoryImpl();
        ExtractionContext extractionContext = new ExtractionContext(
                "test-extractor",
                valueFactory.createURI(DOCUMENT_URI)
        );
        final IgnoreAccidentalRDFa ignoreAccidentalRDFa = new IgnoreAccidentalRDFa(mockTripleHandler, true);
        ignoreAccidentalRDFa.openContext(extractionContext);
        ignoreAccidentalRDFa.receiveTriple(
                valueFactory.createURI(DOCUMENT_URI),
                valueFactory.createURI(predicate),
                valueFactory.createURI("http://www.myedu.com/modules/20110519065453/profile.css"),
                valueFactory.createURI(DOCUMENT_URI),
                extractionContext
        );
        ignoreAccidentalRDFa.close();

        verify(
                mockTripleHandler,
                verificationMode
        ).receiveTriple(
                (Resource) any(),
                (URI) any(),
                (Value) any(),
                (URI) any(),
                (ExtractionContext) any()
        );
    }

}
