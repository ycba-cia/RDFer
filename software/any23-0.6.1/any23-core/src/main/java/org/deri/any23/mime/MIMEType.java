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

package org.deri.any23.mime;

/**
 * A MIME type with an optional q (quality) value.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class MIMEType implements Comparable<MIMEType> {

    private final static String MSG = "Cannot parse MIME type (expected type/subtype[;q=x.y] format): ";

    private final String type;

    private final String subtype;
    
    private final double q;

    /**
     * Parses the given MIME type string returning an instance of
     * {@link org.deri.any23.mime.MIMEType}.
     * The expected format for <code>mimeType</code> is
     * <code>type/subtype[;q=x.y]</code> .
     * An example of valid mime type is: <code>application/rdf+xml;q=0.9</code> 
     *
     * @param mimeType
     * @return the mime type instance.
     * @throws IllegalArgumentException if the <code>mimeType</code> is not well formatted.
     */
    public static MIMEType parse(String mimeType) {
        if (mimeType == null) return null;
        int i = mimeType.indexOf(';');
        double q = 1.0;
        if (i > -1) {
            String[] params = mimeType.substring(i + 1).split(";");
            for (String param : params) {
                int i2 = param.indexOf('=');
                if (i2 == -1) continue;
                if (!"q".equals(param.substring(0, i2).trim().toLowerCase())) continue;
                String value = param.substring(i2 + 1);
                try {
                    q = Double.parseDouble(value);
                } catch (NumberFormatException ex) {
                    continue;
                }
                if (q <= 0.0 || q >= 1.0) {
                    q = 1.0;
                }
            }
        } else {
            i = mimeType.length();
        }
        String type = mimeType.substring(0, i);
        int i2 = type.indexOf('/');
        if (i2 == -1) {
            throw new IllegalArgumentException(MSG + mimeType);
        }
        String p1 = type.substring(0, i2).trim().toLowerCase();
        String p2 = type.substring(i2 + 1).trim().toLowerCase();
        if ("*".equals(p1)) {
            if (!"*".equals(p2)) {
                throw new IllegalArgumentException(MSG + mimeType);
            }
            return new MIMEType(null, null, q);
        }
        if ("*".equals(p2)) {
            return new MIMEType(p1, null, q);
        }
        return new MIMEType(p1, p2, q);
    }

    private MIMEType(String type, String subtype, double q) {
        this.type = type;
        this.subtype = subtype;
        this.q = q;
    }

    public String getMajorType() {
        return (type == null ? "*" : type);
    }

    public String getSubtype() {
        return (subtype == null ? "*" : subtype);
    }

    public String getFullType() {
        return getMajorType() + "/" + getSubtype();
    }

    public double getQuality() {
        return q;
    }

    public boolean isAnyMajorType() {
        return type == null;
    }

    public boolean isAnySubtype() {
        return subtype == null;
    }

    public String toString() {
        if (q == 1.0) {
            return getFullType();
        }
        return getFullType() + ";q=" + q;
    }

    public int compareTo(MIMEType other) {
        return getFullType().compareTo(other.getFullType());
    }
    
}
