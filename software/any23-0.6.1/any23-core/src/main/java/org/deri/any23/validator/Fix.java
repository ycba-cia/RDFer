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

package org.deri.any23.validator;

/**
 * This interface models a fix for an issue.
 *
 * @see org.deri.any23.validator.Rule
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
public interface Fix {

    /**
     * @return the human readable name for this fix.
     */
    String getHRName();

    /**
     * Executes this fix over a document.
     *
     * @param rule the rule triggering this fix.
     * @param context the rule context for this fix.
     * @param document the document to apply this fix.
     */
    void execute(Rule rule, RuleContext context, DOMDocument document);

}
