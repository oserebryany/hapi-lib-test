package com.infoway.connector.hapipoc;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.infoway.connector.hapipoc.NB.NBLabTranslation;
import com.infoway.connector.hapipoc.util.FileUtil;
import org.hl7.fhir.r4.model.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class TestNBLabTranslation {
    private static final Logger LOGGER = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    public static void translateTestMessages(String[] args) {
        LOGGER.info("=========== Test NB Lab translation");

        List<String> messageList;

        if (args.length == 0 || args[0] == null) {
            messageList = loadDefaultTestMessages();
        } else {
            messageList = FileUtil.readHL7TextMessages(args[0]);
        }

        LOGGER.info(String.format("\nMessage loaded: %d", messageList.size()));

        NBLabTranslation nbLab = new NBLabTranslation();
        Bundle bundle = nbLab.process(messageList);


        FhirContext ctx = FhirContext.forR4();
        IParser parser = ctx.newJsonParser();

        parser.setPrettyPrint(true);
        String bundleJson = parser.encodeResourceToString(bundle);

        LOGGER.info("-------------------------------- BUNDLE JSON -----------------------");
        LOGGER.info(bundleJson);

        FileUtil.writeToFile("./fhiroutput/fhir.json", bundleJson);
    }


    private static List<String> loadDefaultTestMessages() {
        String RESOURCES_BASE = "src/main/resources/";
        List<String> messageList = new ArrayList();

//        messageList.add(Samples.ORU_R01_NBLab1_TextMessage);
//        messageList.add(Samples.ORU_R01_NBLab2_TextMessage);
//        messageList.add(Samples.ADT_A01_TextMessage);
//
//        messageList.add(Samples.ORU_R01_Standard1_TextMessage);
//        messageList.add(Samples.ORU_R01_Standard2_TextMessage);

        //messageList = FileUtil.readHL7TextMessages("HL7v2messages.txt");
        //messageList = FileUtil.readHL7TextMessages(RESOURCES_BASE + "NB-HL7v2/Sample1-NBLabHL7v2-ORU-S.txt");
        messageList = FileUtil.readHL7TextMessages(RESOURCES_BASE + "NB-HL7v2/Sample-1-2-3-4.txt");

        return messageList;
    }

}
