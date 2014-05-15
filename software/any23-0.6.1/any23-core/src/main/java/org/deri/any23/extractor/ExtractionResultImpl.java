/**
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
 *
 */

package org.deri.any23.extractor;

import org.deri.any23.extractor.html.MicroformatExtractor;
import org.deri.any23.rdf.Prefixes;
import org.deri.any23.writer.TripleHandler;
import org.deri.any23.writer.TripleHandlerException;
import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p/>
 * A default implementation of {@link ExtractionResult}; it receives
 * extraction output from one {@link Extractor} working on one document,
 * and passes the output on to a {@link TripleHandler}. It deals with
 * details such as creation of {@link ExtractionContext} objects
 * and closing any open contexts at the end of extraction.
 * <p/>
 * The {@link #close()} method must be invoked after the extractor has
 * finished processing.
 * <p/>
 * There is usually no need to provide additional implementations
 * of the ExtractionWriter interface.
 * <p/>
 *
 * @see org.deri.any23.writer.TripleHandler
 * @see org.deri.any23.extractor.ExtractionContext
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class ExtractionResultImpl implements TagSoupExtractionResult {

    private final ExtractionContext context;

    private final Extractor<?> extractor;

    private final TripleHandler tripleHandler;

    private final Collection<ExtractionResult> subResults = new ArrayList<ExtractionResult>();

    private final Set<Object> knownContextIDs = new HashSet<Object>();

    private boolean isClosed = false;

    private boolean isInitialized = false;

    private List<Error> errors;

    private List<ResourceRoot> resourceRoots;

    private List<PropertyPath> propertyPaths;

    public ExtractionResultImpl(
            ExtractionContext context,
            Extractor<?> extractor,
            TripleHandler tripleHandler
    ) {
        if(context == null) {
            throw new NullPointerException("context cannot be null.");
        }
        if(extractor == null) {
            throw new NullPointerException("extractor cannot be null.");
        }
        if(tripleHandler == null) {
            throw new NullPointerException("triple handler cannot be null.");
        }

        this.extractor       = extractor;
        this.tripleHandler   = tripleHandler;
        this.context         = context;
        knownContextIDs.add( context.getUniqueID() );
    }

    public boolean hasErrors() {
        return errors != null;
    }

    public int getErrorsCount() {
        return errors == null ? 0 : errors.size();
    }

    public void printErrorsReport(PrintStream ps) {
        ps.print(String.format("Context: %s [errors: %d] {\n", context, getErrorsCount()));
        if (errors != null) {
            for (Error error : errors) {
                ps.print(error.toString());
                ps.print("\n");
            }
        }
        // Printing sub results.
        for (ExtractionResult er : subResults) {
            er.printErrorsReport(ps);
        }
        ps.print("}\n");
    }

    public Collection<Error> getErrors() {
        return errors == null ? Collections.<Error>emptyList() : Collections.unmodifiableList(errors);
    }

    public ExtractionResult openSubResult(ExtractionContext context) {
        final String contextID = context.getUniqueID();
        if (knownContextIDs.contains(contextID)) {
            throw new IllegalArgumentException("Duplicate contextID: " + contextID);
        }
        knownContextIDs.add(contextID);

        checkOpen();
        ExtractionResult result =
                new ExtractionResultImpl(context, extractor, tripleHandler);
        subResults.add(result);
        return result;
    }

    public ExtractionContext getExtractionContext() {
        return context;
    }

    public void writeTriple(Resource s, URI p, Value o, URI g) {
        if (s == null || p == null || o == null) return;
        // Check for mal-constructed literals or BNodes, Sesame does not catch this.
        if (s.stringValue() == null || p.stringValue() == null || o.stringValue() == null) return;
        checkOpen();
        try {
            tripleHandler.receiveTriple(s, p, o, g, context);
        } catch (TripleHandlerException e) {
            throw new RuntimeException(
                    String.format("Error while receiving triple %s %s %s", s, p, o ),
                    e
            );
        }
    }

    public void writeTriple(Resource s, URI p, Value o) {
        writeTriple(s, p, o, null);
    }

    public void writeNamespace(String prefix, String uri) {
        checkOpen();
        try {
            tripleHandler.receiveNamespace(prefix, uri, context);
        } catch (TripleHandlerException e) {
            throw new RuntimeException(
                    String.format("Error while writing namespace %s:%s", prefix, uri),
                    e
            );
        }
    }

    public void notifyError(ErrorLevel level, String msg, int row, int col) {
        if(errors == null) {
            errors = new ArrayList<Error>();
        }
        errors.add( new Error(level, msg, row, col) );
    }

    public void close() {
        if (isClosed) return;
        isClosed = true;
        for (ExtractionResult subResult : subResults) {
            subResult.close();
        }
        if (isInitialized) {
            try {
                tripleHandler.closeContext(context);
            } catch (TripleHandlerException e) {
                throw new RuntimeException("Error while opening context", e);
            }
        }
    }

    private void checkOpen() {
        if (!isInitialized) {
            isInitialized = true;
            try {
                tripleHandler.openContext(context);
            } catch (TripleHandlerException e) {
                throw new RuntimeException("Error while opening context", e);
            }
            Prefixes prefixes = extractor.getDescription().getPrefixes();
            for (String prefix : prefixes.allPrefixes()) {
                try {
                    tripleHandler.receiveNamespace(prefix, prefixes.getNamespaceURIFor(prefix), context);
                } catch (TripleHandlerException e) {
                    throw new RuntimeException(String.format("Error while writing namespace %s", prefix),
                            e
                    );
                }
            }
        }
        if (isClosed) {
            throw new IllegalStateException("Not open: " + context);
        }
    }

    public void addResourceRoot(String[] path, Resource root, Class<? extends MicroformatExtractor> extractor) {
        if(resourceRoots == null) {
            resourceRoots = new ArrayList<ResourceRoot>();
        }
        resourceRoots.add( new ResourceRoot(path, root, extractor) );
    }

    public List<ResourceRoot> getResourceRoots() {
        List<ResourceRoot> allRoots = new ArrayList<ResourceRoot>();
        if(resourceRoots != null) {
            allRoots.addAll( resourceRoots );
        }
        for(ExtractionResult er : subResults) {
            ExtractionResultImpl eri = (ExtractionResultImpl) er;
            if( eri.resourceRoots != null ) {
                allRoots.addAll( eri.resourceRoots );
            }
        }
        return allRoots;
    }

    public void addPropertyPath(
            Class<? extends MicroformatExtractor> extractor,
            Resource propertySubject,
            Resource property,
            BNode object,
            String[] path
    ) {
        if(propertyPaths == null) {
            propertyPaths = new ArrayList<PropertyPath>();
        }
        propertyPaths.add( new PropertyPath(path, propertySubject, property, object, extractor) );
    }

    public List<PropertyPath> getPropertyPaths() {
        List<PropertyPath> allPaths = new ArrayList<PropertyPath>();
        if(propertyPaths != null) {
            allPaths.addAll( propertyPaths );
        }
        for(ExtractionResult er : subResults) {
            ExtractionResultImpl eri = (ExtractionResultImpl) er;
            if( eri.propertyPaths != null ) {
                allPaths.addAll( eri.propertyPaths );
            }
        }
        return allPaths;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(context.toString());
        sb.append('\n');
        if (errors != null) {
            sb.append("Errors {\n");
            for (Error error : errors) {
                sb.append('\t');
                sb.append(error.toString());
                sb.append('\n');
            }
        }
        sb.append("}\n");
        return sb.toString();
    }

}
