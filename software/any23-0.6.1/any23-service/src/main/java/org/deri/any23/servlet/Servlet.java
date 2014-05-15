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

package org.deri.any23.servlet;

import org.deri.any23.extractor.ExtractionParameters;
import org.deri.any23.http.HTTPClient;
import org.deri.any23.servlet.conneg.Any23Negotiator;
import org.deri.any23.servlet.conneg.MediaRangeSpec;
import org.deri.any23.source.ByteArrayDocumentSource;
import org.deri.any23.source.DocumentSource;
import org.deri.any23.source.HTTPDocumentSource;
import org.deri.any23.source.StringDocumentSource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import static org.deri.any23.extractor.ExtractionParameters.ValidationMode;

/**
 * A <i>Servlet</i> that fetches a client-specified <i>URI</i>,
 * RDFizes the content, and returns it in a format chosen by the client.
 *
 * @author Gabriele Renzi
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class Servlet extends HttpServlet {

    public static final String DEFAULT_BASE_URI = "http://any23.org/tmp/";

    private static final long serialVersionUID = 8207685628715421336L;

    // RFC 3986: scheme = ALPHA *( ALPHA / DIGIT / "+" / "-" / "." )
    private final static Pattern schemeRegex =
            Pattern.compile("^[a-zA-Z][a-zA-Z0-9.+-]*:");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        final WebResponder responder = new WebResponder(this, resp);
        final String format = getFormatFromRequestOrNegotiation(req);
        final boolean report = isReport(req);
        if (format == null) {
            responder.sendError(406, "Client accept header does not include a supported output format", report);
            return;
        }
        final String uri = getInputURIFromRequest(req);
        if (uri == null) {
            responder.sendError(404, "Missing URI in GET request. Try /format/http://example.com/myfile", report);
            return;
        }
        final ExtractionParameters eps = getExtractionParameters(req);
        responder.runExtraction(createHTTPDocumentSource(responder, uri, report), eps, format, report);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final WebResponder responder = new WebResponder(this, resp);
        final boolean report = isReport(req);
        if (req.getContentType() == null) {
            responder.sendError(400, "Invalid POST request, no Content-Type for the message body specified", report);
            return;
        }
        final String uri = getInputURIFromRequest(req);
        final String format = getFormatFromRequestOrNegotiation(req);
        if (format == null) {
            responder.sendError(406, "Client accept header does not include a supported output format", report);
            return;
        }
        final ExtractionParameters eps = getExtractionParameters(req);
        if ("application/x-www-form-urlencoded".equals(getContentTypeHeader(req))) {
            if (uri != null) {
                log("Attempting conversion to '" + format + "' from URI <" + uri + ">");
                responder.runExtraction(createHTTPDocumentSource(responder, uri, report), eps, format, report);
                return;
            }
            if (req.getParameter("body") == null) {
                responder.sendError(400, "Invalid POST request, parameter 'uri' or 'body' required", report);
                return;
            }
            String type = null;
            if (req.getParameter("type") != null && !"".equals(req.getParameter("type"))) {
                type = req.getParameter("type");
            }
            log("Attempting conversion to '" + format + "' from body parameter");
            responder.runExtraction(
                    new StringDocumentSource(req.getParameter("body"), Servlet.DEFAULT_BASE_URI, type),
                    eps,
                    format,
                    report
            );
            return;
        }
        log("Attempting conversion to '" + format + "' from POST body");
        responder.runExtraction(
                new ByteArrayDocumentSource(
                        req.getInputStream(),
                        Servlet.DEFAULT_BASE_URI,
                        getContentTypeHeader(req)
                ),
                eps,
                format,
                report
        );
    }

    private String getFormatFromRequestOrNegotiation(HttpServletRequest request) {
        String fromRequest = getFormatFromRequest(request);
        if (fromRequest != null && !"".equals(fromRequest) && !"best".equals(fromRequest)) {
            return fromRequest;
        }
        MediaRangeSpec result = Any23Negotiator.getNegotiator().getBestMatch(request.getHeader("Accept"));
        if (result == null) {
            return null;
        }
        if ("text/turtle".equals(result.getMediaType())) {
            return "turtle";
        }
        if ("text/rdf+n3".equals(result.getMediaType())) {
            return "n3";
        }
        if ("text/rdf+nq".equals(result.getMediaType())) {
            return "nq";
        }
        if ("application/rdf+xml".equals(result.getMediaType())) {
            return "rdf";
        }
        if ("text/plain".equals(result.getMediaType())) {
            return "nt";
        }
        return "turtle";    // shouldn't happen
    }

    private String getFormatFromRequest(HttpServletRequest request) {
        if (request.getPathInfo() == null) return "best";
        String[] args = request.getPathInfo().split("/", 3);
        if (args.length < 2 || "".equals(args[1])) {
            if (request.getParameter("format") == null) {
                return "best";
            } else {
                return request.getParameter("format");
            }
        }
        return args[1];
    }

    private String getInputURIFromRequest(HttpServletRequest request) {
        if (request.getPathInfo() == null) return null;
        String[] args = request.getPathInfo().split("/", 3);
        if (args.length < 3) {
            if (request.getParameter("uri") != null) {
                return request.getParameter("uri").trim();
            }
            if (request.getParameter("url") != null) {
                return request.getParameter("url").trim();
            }
            return null;
        }
        String uri = args[2];
        if (request.getQueryString() != null) {
            uri = uri + "?" + request.getQueryString();
        }
        if (!hasScheme(uri)) {
            uri = "http://" + uri;
        } else if (hasOnlySingleSlashAfterScheme(uri)) {
            // This is to work around an issue where Tomcat 6.0.18 is
            // too smart for us. Tomcat normalizes double-slashes in
            // the path, and thus turns "http://" into "http:/" if it
            // occurs in the path. So we restore the double slash.
            uri = uri.replaceFirst(":/", "://");
        }
        return uri.trim();
    }


    private boolean hasScheme(String uri) {
        return schemeRegex.matcher(uri).find();
    }

    private final static Pattern schemeAndSingleSlashRegex =
            Pattern.compile("^[a-zA-Z][a-zA-Z0-9.+-]*:/[^/]");

    private boolean hasOnlySingleSlashAfterScheme(String uri) {
        return schemeAndSingleSlashRegex.matcher(uri).find();
    }

    private String getContentTypeHeader(HttpServletRequest req) {
        if (req.getHeader("Content-Type") == null) return null;
        if ("".equals(req.getHeader("Content-Type"))) return null;
        String contentType = req.getHeader("Content-Type");
        // strip off parameters such as ";charset=UTF-8"
        int index = contentType.indexOf(";");
        if (index == -1) return contentType;
        return contentType.substring(0, index);
    }

    private DocumentSource createHTTPDocumentSource(WebResponder responder, String uri, boolean report)
    throws IOException {
        try {
            if (!isValidURI(uri)) {
                throw new URISyntaxException(uri, "@@@");
            }
            return createHTTPDocumentSource(responder.getRunner().getHTTPClient(), uri);
        } catch (URISyntaxException ex) {
            responder.sendError(400, "Invalid input URI " + uri, report);
            return null;
        }
    }

    protected DocumentSource createHTTPDocumentSource(HTTPClient httpClient, String uri)
            throws IOException, URISyntaxException {
        return new HTTPDocumentSource(httpClient, uri);
    }

    private boolean isValidURI(String s) {
        try {
            URI uri = new URI(s);
            if (!"http".equals(uri.getScheme()) && !"https".equals(uri.getScheme())) {
                return false;
            }
        } catch (URISyntaxException e) {
            return false;
        }
        return true;
    }

    // TODO: add possibility to specify validation={none|validate|validate+fix}
    private ExtractionParameters getExtractionParameters(HttpServletRequest request) {
        final ValidationMode mode =
                request.getParameter("fix") != null
                        ?
                ValidationMode.ValidateAndFix
                        :
                ValidationMode.None;
        return new ExtractionParameters(mode);
    }

    private boolean isReport(HttpServletRequest request) {
        return request.getParameter("report") != null;
    }
    
}