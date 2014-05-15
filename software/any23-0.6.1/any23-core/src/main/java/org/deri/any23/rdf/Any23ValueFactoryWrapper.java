/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23.rdf;

import org.deri.any23.extractor.ErrorReporter;
import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Any23 specialization of the {@link org.openrdf.model.ValueFactory}.
 * It provides a wrapper to instantiate RDF objects.
 */
public class Any23ValueFactoryWrapper implements ValueFactory {

    private static final Logger logger = LoggerFactory.getLogger(Any23ValueFactoryWrapper.class);

    private final ValueFactory wrappedFactory;

    private ErrorReporter errorReporter;

    private String defaultLiteralLanguage;

    /**
     * Constructor with error reporter.
     *
     * @param factory the wrapped value factory, cannot be <code>null</code>.
     * @param er the error reporter.
     * @param defaultLitLanguage the default literal language.
     */
    public Any23ValueFactoryWrapper(
            final ValueFactory factory,
            ErrorReporter er,
            String defaultLitLanguage
    ) {
        if(factory == null) {
            throw new NullPointerException("factory cannot be null.");
        }
        wrappedFactory = factory;
        errorReporter  = er;
        defaultLiteralLanguage = defaultLitLanguage;
    }

    public Any23ValueFactoryWrapper(final ValueFactory vFactory, ErrorReporter er) {
        this(vFactory, er, null);
    }

    public Any23ValueFactoryWrapper(final ValueFactory vFactory) {
        this(vFactory, null, null);
    }

    public ErrorReporter getErrorReporter() {
        return errorReporter;
    }

    public void setErrorReporter(ErrorReporter er) {
        errorReporter = er;
    }

    public String getDefaultLiteralLanguage() {
        return defaultLiteralLanguage;
    }

    public BNode createBNode() {
        return wrappedFactory.createBNode();
    }

    public BNode createBNode(String id) {
        if (id == null) return null;
        return wrappedFactory.createBNode(id);
    }

    public Literal createLiteral(String content) {
        if (content == null) return null;
        return wrappedFactory.createLiteral(content, defaultLiteralLanguage);
    }

    public Literal createLiteral(boolean b) {
        return wrappedFactory.createLiteral(b);
    }

    public Literal createLiteral(byte b) {
        return wrappedFactory.createLiteral(b);
    }

    public Literal createLiteral(short i) {
        return wrappedFactory.createLiteral(i);
    }

    public Literal createLiteral(int i) {
        return wrappedFactory.createLiteral(i);
    }

    public Literal createLiteral(long l) {
        return wrappedFactory.createLiteral(l);
    }

    public Literal createLiteral(float v) {
        return wrappedFactory.createLiteral(v);
    }

    public Literal createLiteral(double v) {
        return wrappedFactory.createLiteral(v);
    }

    public Literal createLiteral(XMLGregorianCalendar calendar) {
        return wrappedFactory.createLiteral(calendar);
    }

    public Literal createLiteral(String label, String language) {
        if (label == null) return null;
        return wrappedFactory.createLiteral(label, language);
    }

    public Literal createLiteral(String pref, URI value) {
        if (pref == null) return null;
        return wrappedFactory.createLiteral(pref, value);
    }

    public Statement createStatement(Resource sub, URI pre, Value obj) {
        if (sub == null || pre == null || obj == null) {
            return null;
        }
        return wrappedFactory.createStatement(sub, pre, obj);
    }

    public Statement createStatement(Resource sub, URI pre, Value obj, Resource context) {
        if (sub == null || pre == null || obj == null) return null;
        return wrappedFactory.createStatement(sub, pre, obj, context);
    }

    /**
     * @param uriStr
     * @return a valid sesame URI or null if any exception occurred
     */
    public URI createURI(String uriStr) {
        if (uriStr == null) return null;
        try {
            return wrappedFactory.createURI(RDFUtils.fixURIWithException(uriStr));
        } catch (Exception e) {
            reportError(e);
            return null;
        }
    }

    /**
     * @return a valid sesame URI or null if any exception occurred
     */
    public URI createURI(String namespace, String localName) {
        if (namespace == null || localName == null) return null;
        return wrappedFactory.createURI(RDFUtils.fixURIWithException(namespace), localName);
    }

    /**
     * Fixes typical errors in URIs, and resolves relative URIs against a base URI.
     *
     * @param uri     A URI, relative or absolute, can have typical syntax errors
     * @param baseURI A base URI to use for resolving relative URIs
     * @return An absolute URI, sytnactically valid, or null if not fixable
     */
    public URI resolveURI(String uri, java.net.URI baseURI) {
        try {
            return wrappedFactory.createURI(baseURI.resolve(RDFUtils.fixURIWithException(uri)).toString());
        } catch (IllegalArgumentException iae) {
            reportError(iae);
            return null;
        }
    }

    /**
     * @param uri
     * @return a valid sesame URI or null if any exception occurred
     */
    public URI fixURI(String uri) {
        try {
            return wrappedFactory.createURI(RDFUtils.fixURIWithException(uri));
        } catch (Exception e) {
            reportError(e);
            return null;
        }
    }

    /**
     * Helper method to conditionally add a schema to a URI unless it's there, or null if link is empty.
     */
    public URI fixLink(String link, String defaultSchema) {
        if (link == null) return null;
        link = fixWhiteSpace(link);
        if ("".equals(link)) return null;
        if (defaultSchema != null && !link.startsWith(defaultSchema + ":")) {
            link = defaultSchema + ":" + link;
        }
        return fixURI(link);
    }

    public String fixWhiteSpace(String name) {
        return name.replaceAll("\\s+", " ").trim();
    }

    /**
     * Reports an error in the most appropriate way.
     * 
     * @param e error to be reported.
     */
    private void reportError(Exception e) {
        if(errorReporter == null) {
            logger.warn(e.getMessage());
        } else {
            errorReporter.notifyError(ErrorReporter.ErrorLevel.WARN, e.getMessage(), -1 , -1);
        }
    }

}
