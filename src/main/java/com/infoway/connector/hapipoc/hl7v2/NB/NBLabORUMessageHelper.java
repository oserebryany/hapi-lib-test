package com.infoway.connector.hapipoc.hl7v2.NB;

import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.v24.message.ORU_R01;
import ca.uhn.hl7v2.model.v24.group.*;
import ca.uhn.hl7v2.model.v24.segment.MSH;
import ca.uhn.hl7v2.model.v24.segment.OBX;
import ca.uhn.hl7v2.model.v24.segment.OBR;
import ca.uhn.hl7v2.model.Varies;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.PipeParser;
import com.infoway.connector.hapipoc.util.PocLogging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * An HL7 message has the following components:
 *
 * MSH - Message Header Segment
 * RESPONSE (repeating)
 *   PATIENT
 *     PID - Patient Identification Segment
 *     PV1 – Patient Visit Segment
 *   ORDER OBSERVATION (repeating)
 *     OBR - Observation Request Segment
 *       ORC - Common Order Segment
 *       OBR - Observation Request Segment
 *       OBX – Observation Segment (repeating)
 *
 */

public class NBLabORUMessageHelper {

    /*
    NB HL7 v2 messages have some custom segments which are 4 letters long:
        ZOBR
        ZOBX

    HAPI parser cannot handle 4-letter segments. Replace as follows:
        ZOBR = ZBR
        XOBX = ZBX
     */
    public static String preProcessTextMessage(String textMessage) {
        String newMsg = textMessage;
        newMsg = newMsg.replaceAll("ZOBR", "ZBR");
        newMsg = newMsg.replaceAll("ZOBX", "ZBX");
        return newMsg;
    }

    public static Map collectData(Message hl7Message) {
        Map results = new HashMap();
        try {
            //pipeParser.setValidationContext(new ca.uhn.hl7v2.validation.impl.NoValidation());
            ORU_R01 oru = (ORU_R01) hl7Message;
            MSH msh = oru.getMSH();
            results.put("sendingApp", msh.getSendingApplication().encode());
            results.put("sendingFacility", msh.getSendingFacility().encode());
            results.put("messageTimestamp", msh.getDateTimeOfMessage().encode());

            //
            // ORU_R01_PATIENT_RESULT group structure (a Group object)
            //
            // 1: ORU_R01_PATIENT (a Group object) optional
            // 2: ORU_R01_ORDER_OBSERVATION (a Group object) repeating
            //
            // See https://hapifhir.github.io/hapi-hl7v2/v23/apidocs/src-html/ca/uhn/hl7v2/model/v23/group/ORU_R01_RESPONSE.html
            //

            /* result structure (temporary)

                results is a List of PATIENT_RESULT
                    PATIENT_RESULT 1: List of ORDER_OBSERVATION MappingType
                        observation Map
                            attribute
                            attribute
                            List of OBSERVATION Lists
                                Observation List 1:
                                    attribute
                                    attribute

            */

            List<List> patientResultList = new ArrayList<List>();   //list of lists
            for (ORU_R01_PATIENT_RESULT patientResult : oru.getPATIENT_RESULTAll()) {
                //
                // ORU_R01_PATIENT_RESULT group structure (a Group object)
                //
                // 2: OBR (Observation request segment)
                // 3: NTE (Notes and comments segment) optional repeating
                // 4: ORU_R01_OBSERVATION (a Group object) repeating
                // 5: CTI (Clinical Trial Identification) optional repeating

                List<Map> orderObservationList = new ArrayList<Map>();
                for(ORU_R01_ORDER_OBSERVATION orderObservation : patientResult.getORDER_OBSERVATIONAll()) {
                    Map<String, Object> orderObservationMap = new HashMap<String, Object>();

                    //http://www.hl7.eu/HL7v2x/v24/std24/ch04.htm#Heading82
                    OBR obr = orderObservation.getOBR();
                    orderObservationMap.put("orderNumber", obr.getPlacerOrderNumber().encode());
                    orderObservationMap.put("requestedDateTime", obr.getRequestedDateTime().encode());
                    orderObservationMap.put("procedureCode", obr.getProcedureCode().encode());

                    String singleOrTextual = "Unknown";
                    try {
                        Segment zobr = (Segment) patientResult.getORDER_OBSERVATION().get("ZBR");
                        singleOrTextual = zobr.getField(1)[0].encode();
                    } catch (ca.uhn.hl7v2.HL7Exception ex) {
                        PocLogging.log("ORU Message does not contain ZBR segement, exception: " + ex);
                    }
                    orderObservationMap.put("SingleOrTextual", singleOrTextual);

                    //
                    // ORU_R01_OBSERVATION group structure (a Group object)
                    //
                    // 1: OBX (Observation segment) optional
                    // 2: NTE (Notes and comments segment) optional repeating
                    //
                    // See https://hapifhir.github.io/hapi-hl7v2/v23/apidocs/src-html/ca/uhn/hl7v2/model/v23/group/ORU_R01_OBSERVATION.html
                    //

                    List<List> observationList = new ArrayList<List>();
                    for (ORU_R01_OBSERVATION observation : orderObservation.getOBSERVATIONAll()) {
                        List<String> observationDetailList = new ArrayList<String>();
                        // HL7 OBX message segment (Observation segment)
                        //http://www.hl7.eu/HL7v2x/v24/std24/ch07.htm#Heading100
                        OBX obx = observation.getOBX();

                        //observationMap.put("identifier", obx.getObservationIdentifier().getCe2_Text().getValue());
                        String type = obx.getObx3_ObservationIdentifier().getCe2_Text().getValue();
                        String status = obx.getObservationResultStatus().getValue();
                        for (Varies varies : obx.getObx5_ObservationValue()) {
                            String value = varies.encode();
                            String obSum = String.format("VALUE: [%s], TYPE: [%s], STATUS: [%s]", value, type, status);
                            PocLogging.log(obSum);
                            observationDetailList.add(obSum);
                        }

                        observationList.add(observationDetailList);
                    }
                    orderObservationMap.put("observations", observationList);

                    orderObservationList.add(orderObservationMap);
                }

                patientResultList.add(orderObservationList);
            }
            results.put("patientResultsList", patientResultList);
        } catch (Exception ex) {
            PocLogging.log("Error processing message, Exception:\n" + ex);
            ex.printStackTrace();
        }
        return results;
    }
}