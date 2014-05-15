/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23.extractor.csv;

import org.apache.commons.csv.CSVParser;
import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionParameters;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.Extractor;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.deri.any23.rdf.RDFUtils;
import org.deri.any23.vocab.CSV;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.XMLSchema;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * This extractor produces <i>RDF</i> from a <i>CSV file</i> .
 * It automatically detects fields <i>delimiter</i>. If not able uses
 * the one provided in the <i>Any23</i> configuration.
 *
 * @see {@link CSVReaderBuilder}
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 */
public class CSVExtractor implements Extractor.ContentExtractor {

    private CSVParser csvParser;

    private URI[] headerURIs;

    private CSV csv = CSV.getInstance();

    public final static ExtractorFactory<CSVExtractor> factory =
            SimpleExtractorFactory.create(
                    "csv",
                    null,
                    Arrays.asList(
                            "text/csv;q=0.1"
                    ),
                    "example-csv.csv",
                    CSVExtractor.class
            );

    /**
     * {@inheritDoc}
     */
    public void setStopAtFirstError(boolean f) {
    }

    /**
     * {@inheritDoc}
     */
    public void run(
            ExtractionParameters extractionParameters,
            ExtractionContext extractionContext,
            InputStream in
            , ExtractionResult out
    ) throws IOException, ExtractionException {
        final URI documentURI = extractionContext.getDocumentURI();

        // build the parser
        csvParser = CSVReaderBuilder.build(in);

        // get the header and generate the URIs for column names
        String[] header = csvParser.getLine();
        headerURIs = processHeader(header, documentURI);

        // write triples to describe properties
        writeHeaderPropertiesMetadata(header, out);

        String[] nextLine;
        int index = 0;
        while ((nextLine = csvParser.getLine()) != null) {
            URI rowSubject = RDFUtils.uri(
                    documentURI.toString(),
                    "row/" + index
            );
            // add a row type
            out.writeTriple(rowSubject, RDF.TYPE, csv.rowType);
            // for each row produce its statements
            produceRowStatements(rowSubject, nextLine, out);
            // link the row to the document
            out.writeTriple(documentURI, csv.row, rowSubject);
            // the progressive row number
            out.writeTriple(
                    rowSubject,
                    csv.rowPosition,
                    new LiteralImpl(String.valueOf(index))
            );
            index++;
        }
        // add some CSV metadata such as the number of rows and columns
        addTableMetadataStatements(
                documentURI,
                out,
                index,
                headerURIs.length
        );
    }

    /**
     * @param number
     * @return
     */
    private boolean isNumber(String number) {
        try {
            Double.valueOf(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * It writes <i>RDF</i> statements representing properties of the header.
     *
     * @param header
     * @param out
     */
    private void writeHeaderPropertiesMetadata(String[] header, ExtractionResult out) {
        int index = 0;
        for (URI singleHeader : headerURIs) {
            if (index > headerURIs.length) {
                break;
            }
            if (!RDFUtils.isAbsoluteURI(header[index])) {
                out.writeTriple(
                        singleHeader,
                        RDFS.LABEL,
                        new LiteralImpl(header[index])
                );
            }
            out.writeTriple(
                    singleHeader,
                    csv.columnPosition,
                    new LiteralImpl(String.valueOf(index), XMLSchema.INTEGER)
            );
            index++;
        }
    }

    /**
     * It process the first row of the file, returning a list of {@link URI}s representing
     * the properties for each column. If a value of the header is an absolute <i>URI</i>
     * then it leave it as is. Otherwise the {@link CSV} vocabulary is used.
     *
     * @param header
     * @return an array of {@link URI}s identifying the column names.
     */
    private URI[] processHeader(String[] header, URI documentURI) {
        URI[] result = new URI[header.length];
        int index = 0;
        for (String h : header) {
            String candidate = h.trim();
            if (RDFUtils.isAbsoluteURI(candidate)) {
                result[index] = new URIImpl(candidate);
            } else {
                result[index] = normalize(candidate, documentURI);
            }
            index++;
        }
        return result;
    }

    private URI normalize(String toBeNormalized, URI documentURI) {
        String candidate = toBeNormalized;
        candidate = candidate.trim().toLowerCase().replace("?", "").replace("&", "");
        String[] tokens = candidate.split(" ");
        candidate = tokens[0];
        for (int i = 1; i < tokens.length; i++) {
            String firstChar = ("" + tokens[i].charAt(0)).toUpperCase();
            candidate += firstChar + tokens[i].substring(1);
        }
        return new URIImpl(documentURI.toString() + candidate);
    }

    /**
     * It writes on the provided {@link ExtractionResult}, the </>RDF statements</>
     * representing the row <i>cell</i>. If a  row <i>cell</i> is an absolute <i>URI</i>
     * then an object property is written, literal otherwise.
     *
     * @param rowSubject
     * @param values
     * @param out
     */
    private void produceRowStatements(
            URI rowSubject,
            String[] values,
            ExtractionResult out
    ) {
        int index = 0;
        for (String cell : values) {
            if (index >= headerURIs.length) {
                // there are some row cells that don't have an associated column name
                break;
            }
            if (cell.equals("")) {
                continue;
            }
            URI predicate = headerURIs[index];
            Value object = getObjectFromCell(cell);
            out.writeTriple(rowSubject, predicate, object);
            index++;
        }
    }

    private Value getObjectFromCell(String cell) {
        Value object;
        cell = cell.trim();
        if (RDFUtils.isAbsoluteURI(cell)) {
            object = new URIImpl(cell);
        } else {
            URI datatype = XMLSchema.STRING;
            if (isNumber(cell)) {
                datatype = XMLSchema.INTEGER;
            }
            object = new LiteralImpl(cell, datatype);
        }
        return object;
    }

    /**
     * It writes on the provided {@link ExtractionResult} some <i>RDF Statements</i>
     * on generic properties of the <i>CSV</i> file, such as number of rows and columns.
     *
     * @param documentURI
     * @param out
     * @param numberOfRows
     * @param numberOfColumns
     */
    private void addTableMetadataStatements(
            URI documentURI,
            ExtractionResult out,
            int numberOfRows,
            int numberOfColumns) {
        out.writeTriple(
                documentURI,
                csv.numberOfRows,
                new LiteralImpl(String.valueOf(numberOfRows), XMLSchema.INTEGER)
        );
        out.writeTriple(
                documentURI,
                csv.numberOfColumns,
                new LiteralImpl(String.valueOf(numberOfColumns), XMLSchema.INTEGER)
        );
    }

    /**
     * {@inheritDoc}
     */
    public ExtractorDescription getDescription() {
        return factory;
    }
}
