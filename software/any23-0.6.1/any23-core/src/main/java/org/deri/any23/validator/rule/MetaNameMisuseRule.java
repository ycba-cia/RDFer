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

package org.deri.any23.validator.rule;

import org.deri.any23.validator.DOMDocument;
import org.deri.any23.validator.Rule;
import org.deri.any23.validator.RuleContext;
import org.deri.any23.validator.ValidationReport;
import org.deri.any23.validator.ValidationReportBuilder;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks whether the meta attribute name is used to contain a property.
 *
 * @see org.deri.any23.validator.rule.MetaNameMisuseFix
 * @author Davide Palmisano (palmisano@fbk.eu)
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MetaNameMisuseRule implements Rule {

    public static final String ERRORED_META_NODES = "errored-meta-nodes";

    public String getHRName() {
        return "meta-name-misuse-rule";
    }

    public boolean applyOn(
            DOMDocument document,
            RuleContext context,
            ValidationReportBuilder validationReportBuilder
    ) {
        List<Node> metaNodes = document.getNodes("/HTML/HEAD/META");
        boolean foundIssue = false;
        final List<Node> wrongMetaNodes = new ArrayList<Node>();
        for(Node metaNode : metaNodes) {
            Node nameNode = metaNode.getAttributes().getNamedItem("name");
            if(nameNode != null && nameNode.getTextContent().contains(":")) {
                foundIssue = true;
                wrongMetaNodes.add(metaNode);
                validationReportBuilder.reportIssue(
                        ValidationReport.IssueLevel.error,
                        "Error detected in meta node: name property contains a prefixed value.",
                        metaNode
                );
            }
        }
        context.putData(ERRORED_META_NODES, wrongMetaNodes);
        return foundIssue;
    }

}
