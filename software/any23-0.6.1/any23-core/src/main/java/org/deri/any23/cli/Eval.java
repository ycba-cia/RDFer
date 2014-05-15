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

package org.deri.any23.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.deri.any23.eval.LogEvaluator;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A default rover implementation. Goes and fetches a URL using an hint
 * as to what format should require, then tries to convert it to RDF.
 *
 * @author Gabriele Renzi
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
@ToolRunner.Description("Utility for processing output log.")
public class Eval {

    /**
     * A simple main for testing.
     *
     * @param args a url and an optional format name such as TURTLE,N3,N-TRIPLES,RDF/XML.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void main(String[] args) throws FileNotFoundException {
        new Eval().run(args);
    }

    private void run(String[] args) throws FileNotFoundException {
        Options options = new Options();
        Option inputFile = new Option("i", true, "input file");
        options.addOption(inputFile);
        Option inputDir = new Option("d", true, "input directory containing the log files");
        options.addOption(inputDir);
        Option outputFile = new Option("o", "output", true, "output file (defaults to stdout)");
        options.addOption(outputFile);

        CommandLineParser parser = new PosixParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            printHelp(options, e.getMessage());
            throw new IllegalStateException();
        }

        LogEvaluator l = new LogEvaluator(cmd.getOptionValue("o"));
        if (cmd.hasOption("i"))
            l.analyseFile(cmd.getOptionValue("i"));
        else if (cmd.hasOption("d"))
            l.analyseDirectory(cmd.getOptionValue("d"));
        else
            printHelp(options, "Must specify at least one option.");

        l.close();
	}

    private void printHelp(Options options, String msg) {
        System.err.println("***ERROR: " + msg);
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(this.getClass().getSimpleName() + " [file|url]", options, true);
        System.exit(1);
    }
    
}
