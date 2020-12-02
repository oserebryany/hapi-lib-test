package com.infoway.connector.hapipoc;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.v24.message.ADT_A01;
import ca.uhn.hl7v2.model.v24.message.ORU_R01;
import com.infoway.connector.hapipoc.conceptmapping.ConceptMapper;
import com.infoway.connector.hapipoc.conceptmapping.MappingType;
import com.infoway.connector.hapipoc.fhir.NBLabObservation;
import com.infoway.connector.hapipoc.hl7v2.HL7v2Parser;
import com.infoway.connector.hapipoc.hl7v2.NB.NBLabORUMessageHelper;
import com.infoway.connector.hapipoc.util.FileUtil;
import com.infoway.connector.hapipoc.util.PocLogging;
import org.hl7.fhir.r4.model.ConceptMap;
import org.hl7.fhir.r4.model.Narrative;
import org.hl7.fhir.r4.model.Observation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This application uses HAPI library to acheive the following:
 * 1. Define and instantiate new custom FHIR resources based on FHIR R4
 * 2. Use HAPI HL7 v2 parser to ingest a real HL7 v2 message
 * 2a.  Access custom HL7 v2 segments
 * 3. Assign values to custom FHIR resource from HL7 Parser
 * 4. Initialize a MappingType resource from a MappingType JSON resource
 * 5. Use MappingType resource to map values from HL7 v2 to the FHIR resource
 *
 * Use Observation resource
 *
 */
public class Main
{
    public static void main( String[] args )
    {
        PocLogging.log("=================================================================================");
        PocLogging.log("======= Starting HAPI FHIR test app==============================================");
        PocLogging.log("=================================================================================");

        /*
        Expected arguments:
            If none:  Input messages will be loaded from code.  Output will go to logs and standard output
            If 1 argument: Input file for messages.  Output will go to logs and standard output
            If 2 arguments: input file for messages, followed by file name for the output results/message (e.g. JSON FHIR messages)
         */

        //show args
        PocLogging.log(String.format("Arguments provided: %d", args.length));
        for(String arg: args) {
            PocLogging.log("   arg=" + arg);
        }

        PocLogging.log("Working Directory = " + System.getProperty("user.dir"));
        loadAndTestConceptMappers();

        PocLogging.log("=========== TEST HL7 messages");

        List<String> messageList;

        if (args.length == 0 || args[0] == null) {
            messageList = loadDefaultTestMessages();
        } else {
            messageList = FileUtil.readHL7TextMessages(args[0]);
        }

        PocLogging.log(String.format("\nMessages loaded: %d", messageList.size()));

        for (String msg: messageList) {
            PocLogging.log("");
            PocLogging.log(">>>>> Start raw message");
            PocLogging.log(msg);
            PocLogging.log("<<<<< End raw message");
            String preprocessedMsg = NBLabORUMessageHelper.preProcessTextMessage(msg);
            Message hl7Message = HL7v2Parser.parseMessage(preprocessedMsg);
            if (hl7Message == null) {
                PocLogging.error("Message failed to parse");
                continue;
            }

            PocLogging.log("Message Class: " + hl7Message.getClass());
            try {
                Segment msh = (Segment) hl7Message.get("MSH");
                String msgType = msh.getField(9, 0).encode().substring(0,3);   //ADT, ORU, etc.
                PocLogging.log("Message Type: " + msgType);

                if (hl7Message instanceof ADT_A01) {
                    Segment zpiGenericSegment = (Segment) hl7Message.get("ZPI");
                    String firstPetName  = zpiGenericSegment.getField(1, 0).encode();
                    String secondPetName = zpiGenericSegment.getField(1, 1).encode();
                    PocLogging.log("here 1");
                }
                if (hl7Message instanceof ORU_R01) {

                    Map oruData = NBLabORUMessageHelper.collectData(hl7Message);
                    PocLogging.logMapStrings(oruData);
                }
            } catch (HL7Exception ex) {
                PocLogging.log("ERROR: Error parsing custom HL7 v2 message.  " + ex);
            }
        }

        PocLogging.log("=================================================================================");
        PocLogging.log("============================= FHIR ==============================================");



        Observation observation = new NBLabObservation();
        observation.setId("123");

        Narrative textNarrative = new Narrative();

        textNarrative.setStatus(Narrative.NarrativeStatus.ADDITIONAL);
        textNarrative.setDivAsString("<div>This is the narrative text<br/>this is line 2</div>");
        observation.setText(textNarrative);
        observation.getText().setStatus(Narrative.NarrativeStatus.ADDITIONAL);
        observation.getText().setDivAsString("<div>This is the narrative text<br/>this is line 2</div>");

        // Instantiate a new parser
        FhirContext ctx = FhirContext.forR4();
        IParser parser = ctx.newJsonParser();

        String serialized = parser.encodeResourceToString(observation);
        System.out.println(serialized);
    }

    private static void loadAndTestConceptMappers() {
        PocLogging.log("=========== Test ConceptMaps");

        ConceptMapper conceptMapper = ConceptMapper.getInstance();

        //test mapping function
        String code = "work";
        String targetCode1 = conceptMapper.mapCode(MappingType.ADDRESS_FHIR_V3, code);
        PocLogging.log(String.format("Mapping test: %s ==> %s", code, targetCode1));

        code = "02427648";
        String targetCode2 = conceptMapper.mapCode(MappingType.MP_NTP, code);
        PocLogging.log(String.format("Mapping test: %s ==> %s  (should be 9000672)", code, targetCode2));

        code = "9014390";
        String targetCode3 = conceptMapper.mapCode(MappingType.NTP_TM, code);
        PocLogging.log(String.format("Mapping test: %s ==> %s (should be 8002346)", code, targetCode3));
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
