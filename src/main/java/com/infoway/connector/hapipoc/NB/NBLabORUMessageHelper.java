package com.infoway.connector.hapipoc.NB;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.v24.message.ORU_R01;
import ca.uhn.hl7v2.model.v24.group.*;
import ca.uhn.hl7v2.model.v24.segment.MSH;
import ca.uhn.hl7v2.model.v24.segment.OBX;
import ca.uhn.hl7v2.model.v24.segment.OBR;
import ca.uhn.hl7v2.model.Varies;
import ca.uhn.hl7v2.model.Message;
import com.infoway.connector.hapipoc.conceptmapping.ConceptMapper;
import com.infoway.connector.hapipoc.conceptmapping.MappingType;
import org.hl7.fhir.r4.model.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * An HL7 message has the following components:
 *
 * MSH - Message Header Segment
 * RESPONSE (repeating)
 *   PATIENT
 *     PID - Patient Identification Segment
 *     PV1 – Patient Visit Segment
 *   ORDER OBSERVATION (repeating)
 *          OBR - Observation Request Segment
 *          OBSERVATION (repeating)
 *              ORC - Common Order Segment
 *              OBX – Observation Segment
 *
 */

public class NBLabORUMessageHelper {
    private static final Logger LOGGER = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

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

    /*
        Return "S" or "T"
     */
    public static String singleOrTextual(Message hl7Message) {
        ORU_R01 oru = (ORU_R01) hl7Message;
        ORU_R01_ORDER_OBSERVATION orderObservation = oru.getPATIENT_RESULT().getORDER_OBSERVATION();
        String singleOrTextual = "Unknown";
        try {
            Segment zobr = (Segment) orderObservation.get("ZBR");
            singleOrTextual = zobr.getField(1)[0].encode();
        } catch (ca.uhn.hl7v2.HL7Exception ex) {
            LOGGER.info("ORU Message does not contain ZBR segment, exception: " + ex);
        }
        return singleOrTextual;
    }

    public static Resource buildFhirObservation(Message hl7Message) {
        ORU_R01 oru = (ORU_R01) hl7Message;
        ORU_R01_PATIENT_RESULT patientResult = oru.getPATIENT_RESULT();

        Observation ob = new Observation();
        try {
            String identifierString = NBLabORUMessageHelper.extractIdentifier(patientResult);
            ob.addIdentifier().setValue(identifierString);

            String hl7ObSatus= NBLabORUMessageHelper.extractStatus(patientResult);
            String fhirStatus = ConceptMapper.getInstance().mapCode(MappingType.NB_LAB_OBSERVATION_STATUS, hl7ObSatus);
            ob.setStatus(Observation.ObservationStatus.fromCode(fhirStatus));

            Type value = NBLabORUMessageHelper.extractObservationValue(patientResult);
            ob.setValue(value);
        } catch (Exception ex) {
            LOGGER.log( Level.SEVERE, ex.toString(), ex );
            throw new RuntimeException(ex);
        }

        return ob;
    }

    public static Resource buildFhirDiagnosticReport(Message hl7Message) {
        ORU_R01 oru = (ORU_R01) hl7Message;
        ORU_R01_PATIENT_RESULT patientResult = oru.getPATIENT_RESULT();

        DiagnosticReport dr = new DiagnosticReport();
        try {
            String identifierString = NBLabORUMessageHelper.extractIdentifier(patientResult);
            dr.addIdentifier().setValue(identifierString);
        } catch (Exception ex) {
            LOGGER.log( Level.SEVERE, ex.toString(), ex );
            throw new RuntimeException(ex);
        }
        return dr;

    }

    /*
    Assume to use OBR-3 + OBX-3 + OBX-4
    TBD with Gevity.
     */
    private static String extractIdentifier(ORU_R01_PATIENT_RESULT patientResult) throws HL7Exception {
        ORU_R01_ORDER_OBSERVATION orderObservation = patientResult.getORDER_OBSERVATION();
        String obr3Identifier = orderObservation.getOBR().getObr3_FillerOrderNumber().getEi1_EntityIdentifier().encode();
        String obx3Identifier = orderObservation.getOBSERVATION().getOBX().getObx3_ObservationIdentifier().getCe1_Identifier().encode();
        String obx4 = orderObservation.getOBSERVATION().getOBX().getObx4_ObservationSubId().encode();

        String result = obr3Identifier;
        if (!obx3Identifier.isEmpty()) result = result + "-" + obx3Identifier;
        if (!obx4.isEmpty()) result = result + "-" + obx4;
        return result;
    }

    private static String extractStatus(ORU_R01_PATIENT_RESULT patientResult) throws HL7Exception {
        OBX obx = patientResult.getORDER_OBSERVATION().getOBSERVATION().getOBX();
        String status = obx.getObx11_ObservationResultStatus().encode();
        return status;
    }


//    private String extractX(ORU_R01_PATIENT_RESULT patientResult) throws HL7Exception {
//        String type = obx.getObx3_ObservationIdentifier().getCe2_Text().getValue();
//        String status = obx.getObservationResultStatus().getValue();
//        for (Varies varies : obx.getObx5_ObservationValue()) {
//            String value = varies.encode();
//            String obSum = String.format("VALUE: [%s], TYPE: [%s], STATUS: [%s]", value, type, status);
//            LOGGER.info(obSum);
//            observationDetailList.add(obSum);
//        }
//    }

    /*

     */
    private static Type extractObservationValue(ORU_R01_PATIENT_RESULT patientResult) throws HL7Exception {
        OBX obx = patientResult.getORDER_OBSERVATION().getOBSERVATION().getOBX();
        //use OBX-2 to determine the type
        //Observed types: FT (formatted text), TX (text), NM (numeric)

        Type typeOfValue = null;
        String valueTypeStr = obx.getObx2_ValueType().encode();
        switch (valueTypeStr) {
            case "TX":
            case "FT":
                StringType st = new StringType();
                st.setValue(obx.getObx5_ObservationValue()[0].encode());

                typeOfValue = st;
                break;
            case "NM":
                Quantity quantity = new Quantity();   //need value and units of measure
                String value = obx.getObx5_ObservationValue()[0].encode();
                String unit = obx.getObx6_Units().getCe1_Identifier().encode();
                quantity.setValue(Double.parseDouble(value));
                quantity.setUnit(unit);

                typeOfValue = quantity;
                break;
            default:
                throw new RuntimeException("Unrecognized observation value type: " + valueTypeStr);
        }

        return typeOfValue;
    }

//    Segment msh = (Segment) hl7Message.get("MSH");
//    String msgType = msh.getField(9, 0).encode().substring(0, 3);   //ADT, ORU, etc.


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
                        LOGGER.info("ORU Message does not contain ZBR segement, exception: " + ex);
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
                            LOGGER.info(obSum);
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
            LOGGER.info("Error processing message, Exception:\n" + ex);
            ex.printStackTrace();
        }
        return results;
    }
}