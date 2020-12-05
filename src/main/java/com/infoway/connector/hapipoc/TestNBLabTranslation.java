package com.infoway.connector.hapipoc;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.v24.message.ADT_A01;
import ca.uhn.hl7v2.model.v24.message.ORU_R01;
import com.infoway.connector.hapipoc.NB.NBLabORUMessageHelper;
import com.infoway.connector.hapipoc.NB.NBLabTranslation;
import com.infoway.connector.hapipoc.hl7v2.HL7v2Parser;
import com.infoway.connector.hapipoc.util.FileUtil;
import com.infoway.connector.hapipoc.util.PocLogging;
import org.hl7.fhir.r4.model.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestNBLabTranslation {

    public static void translateTestMessages(String[] args) {
        PocLogging.log("=========== Test NB Lab translation");

        List<String> messageList;

        if (args.length == 0 || args[0] == null) {
            messageList = loadDefaultTestMessages();
        } else {
            messageList = FileUtil.readHL7TextMessages(args[0]);
        }

        PocLogging.log(String.format("\nMessage loaded: %d", messageList.size()));

        NBLabTranslation nbLab = new NBLabTranslation();
        Bundle bundle = nbLab.process(messageList);


        FhirContext ctx = FhirContext.forR4();
        IParser parser = ctx.newJsonParser();

        parser.setPrettyPrint(true);
        String bundleJson = parser.encodeResourceToString(bundle);

        PocLogging.log("-------------------------------- BUNDLE JSON -----------------------");
        PocLogging.log(bundleJson);
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
