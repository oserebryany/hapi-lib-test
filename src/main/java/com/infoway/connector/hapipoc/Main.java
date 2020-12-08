package com.infoway.connector.hapipoc;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 This application uses HAPI Java library to test the following concepts:

 - Loading ConceptMaps and using them to remap codes
 - Parse HL7 v2 messages and handle custom Segments
 - Define custom FHIR resources (e.g. Patient)
 - Initialize FHIR resource and deserialize as JSON (e.g. Observation)
 **/

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());
    private static final LogManager logManager = LogManager.getLogManager();
    static{
        try {
            logManager.readConfiguration(new FileInputStream("./logging.properties"));
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Error in loading configuration",exception);
        }
    }

    public static void main( String[] args )
    {
        LOGGER.info("================================ Starting HAPI FHIR test app =================================================");

        /*
        Expected arguments:
            If none:  Input messages will be loaded from code.  Output will go to logs and standard output
            If 1 argument: Input file for messages.  Output will go to logs and standard output
            If 2 arguments: input file for messages, followed by file name for the output results/message (e.g. JSON FHIR messages)
         */

        //show args
        LOGGER.info(String.format("Arguments provided: %d", args.length));
        for(String arg: args) {
            LOGGER.info("   arg=" + arg);
        }

        LOGGER.info("Working Directory = " + System.getProperty("user.dir"));


        TestNBLabTranslation.translateTestMessages(args);

//        TestConceptMaps.loadAndTestConceptMappers(args);
//        TestHL7Parsing.parseHL7v2Messages(args);
//        TestFhirResources.playWithFhir(args);

    }


}
