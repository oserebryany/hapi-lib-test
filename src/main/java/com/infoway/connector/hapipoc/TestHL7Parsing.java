package com.infoway.connector.hapipoc;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.v24.message.ADT_A01;
import ca.uhn.hl7v2.model.v24.message.ORU_R01;
import com.infoway.connector.hapipoc.hl7v2.HL7v2Parser;
import com.infoway.connector.hapipoc.NB.NBLabORUMessageHelper;
import com.infoway.connector.hapipoc.util.FileUtil;
import com.infoway.connector.hapipoc.util.PocLogging;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class TestHL7Parsing {
    private static final Logger LOGGER = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    public static void parseHL7v2Messages(String[] args) {
        LOGGER.info("=========== TEST HL7 messages");

        List<String> messageList;

        if (args.length == 0 || args[0] == null) {
            messageList = loadDefaultTestMessages();
        } else {
            messageList = FileUtil.readHL7TextMessages(args[0]);
        }

        LOGGER.info(String.format("\nMessages loaded: %d", messageList.size()));

        for (String msg : messageList) {
            LOGGER.info("");
            LOGGER.info(">>>>> Start raw message");
            LOGGER.info(msg);
            LOGGER.info("<<<<< End raw message");
            String preprocessedMsg = NBLabORUMessageHelper.preProcessTextMessage(msg);
            Message hl7Message = HL7v2Parser.parseMessage(preprocessedMsg);
            if (hl7Message == null) {
                LOGGER.severe("Message failed to parse");
                continue;
            }

            LOGGER.info("Message Class: " + hl7Message.getClass());
            try {
                Segment msh = (Segment) hl7Message.get("MSH");
                String msgType = msh.getField(9, 0).encode().substring(0, 3);   //ADT, ORU, etc.
                LOGGER.info("Message Type: " + msgType);

                if (hl7Message instanceof ADT_A01) {
                    Segment zpiGenericSegment = (Segment) hl7Message.get("ZPI");
                    String firstPetName = zpiGenericSegment.getField(1, 0).encode();
                    String secondPetName = zpiGenericSegment.getField(1, 1).encode();
                    LOGGER.info("here 1");
                }
                if (hl7Message instanceof ORU_R01) {
                    String singleOrText = NBLabORUMessageHelper.singleOrTextual(hl7Message);
                    LOGGER.info("HL7 NB Lab observation type: " + singleOrText);
                }
            } catch (HL7Exception ex) {
                LOGGER.info("ERROR: Error parsing custom HL7 v2 message.  " + ex);
            }
        }
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
