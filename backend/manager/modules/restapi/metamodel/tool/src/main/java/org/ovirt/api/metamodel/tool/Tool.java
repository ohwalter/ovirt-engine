/*
Copyright (c) 2015 Red Hat, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.ovirt.api.metamodel.tool;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.ovirt.api.metamodel.analyzer.ModelAnalyzer;
import org.ovirt.api.metamodel.concepts.NameParser;
import org.ovirt.api.metamodel.concepts.PrimitiveType;
import org.ovirt.api.metamodel.concepts.Model;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;

@ApplicationScoped
public class Tool {
    // References to the objects used to generate code:
    @Inject
    private SchemaGenerator schemaGenerator;

    // The names of the command line options:
    private static final String MODEL_OPTION = "model";
    private static final String IN_SCHEMA_OPTION = "in-schema";
    private static final String OUT_SCHEMA_OPTION = "out-schema";

    public void run(String[] args) throws Exception {
        // Create the command line options:
        Options options = new Options();

        // Options for the locations of files and directories:
        options.addOption(Option.builder()
            .longOpt(MODEL_OPTION)
            .desc("The directory or .jar file containing the source model files.")
            .type(File.class)
            .required(true)
            .hasArg(true)
            .argName("DIRECTORY|JAR")
            .build()
        );
        options.addOption(Option.builder()
            .longOpt(IN_SCHEMA_OPTION)
            .desc("The XML schema input file.")
            .type(File.class)
            .required(true)
            .hasArg(true)
            .argName("FILE")
            .build()
        );
        options.addOption(Option.builder()
            .longOpt(OUT_SCHEMA_OPTION)
            .desc("The XML schema output file.")
            .type(File.class)
            .required(true)
            .hasArg(true)
            .argName("FILE")
            .build()
        );

        // Parse the command line:
        CommandLineParser parser = new DefaultParser();
        CommandLine line = null;
        try {
            line = parser.parse(options, args);
        }
        catch (ParseException exception) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.setSyntaxPrefix("Usage: ");
            formatter.printHelp("metamodel-tool [OPTIONS]", options);
            System.exit(1);
        }

        // Extract the locations of files and directories from the command line:
        File modelFile = (File) line.getParsedOptionValue(MODEL_OPTION);
        File inSchemaFile = (File) line.getParsedOptionValue(IN_SCHEMA_OPTION);
        File outSchemaFile = (File) line.getParsedOptionValue(OUT_SCHEMA_OPTION);

        // Analyze the model files:
        Model model = new Model();
        ModelAnalyzer modelAnalyzer = new ModelAnalyzer();
        modelAnalyzer.setModel(model);
        modelAnalyzer.analyzeSource(modelFile);

        // Generate the XML schema:
        schemaGenerator.setInFile(inSchemaFile);
        schemaGenerator.setOutFile(outSchemaFile);
        schemaGenerator.generate(model);
    }
}
