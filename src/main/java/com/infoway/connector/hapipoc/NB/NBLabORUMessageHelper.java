package com.infoway.connector.hapipoc.NB;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.v24.message.ORU_R01;
import ca.uhn.hl7v2.model.v24.group.*;
import ca.uhn.hl7v2.model.v24.segment.OBR;
import ca.uhn.hl7v2.model.v24.segment.OBX;
import ca.uhn.hl7v2.model.Message;
import com.infoway.connector.hapipoc.conceptmapping.ConceptMapper;
import com.infoway.connector.hapipoc.conceptmapping.MappingType;
import org.hl7.fhir.r4.model.*;

import java.util.ArrayList;
import java.util.List;
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
            String identifierString = NBLabORUMessageHelper.extractObservationIdentifier(patientResult);
            ob.addIdentifier().setValue(identifierString);

            String hl7ObSatus = NBLabORUMessageHelper.extractObservationStatus(patientResult);
            String fhirStatus = ConceptMapper.getInstance().mapCode(MappingType.NB_LAB_OBSERVATION_STATUS, hl7ObSatus);
            ob.setStatus(Observation.ObservationStatus.fromCode(fhirStatus));

            String patientId = NBLabORUMessageHelper.extractPatientId(patientResult);
            ob.getSubject().setReference("Patient/"+patientId);

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
            //identifier
            String identifierString = NBLabORUMessageHelper.extractDiagnosticReportIdentifier(patientResult);
            dr.addIdentifier().setValue(identifierString);

            String hl7DrSatus = NBLabORUMessageHelper.extractDiagnosticReportStatus(patientResult);
            //for now, map it using the ObservationStatus.
            String fhirStatus = ConceptMapper.getInstance().mapCode(MappingType.NB_LAB_DIAGNOSTICREPORT_STATUS, hl7DrSatus);
            dr.setStatus(DiagnosticReport.DiagnosticReportStatus.fromCode(fhirStatus));

            //observation results - stored as text
            List<String> reportTextLines = NBLabORUMessageHelper.extractReportTextLines(patientResult);
            String resourceTextDivString = NBLabORUMessageHelper.prepareDivString(reportTextLines);
            dr.getText().setStatus(Narrative.NarrativeStatus.ADDITIONAL).getDiv().setValue(resourceTextDivString);
            LOGGER.info(resourceTextDivString);

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
    private static String extractObservationIdentifier(ORU_R01_PATIENT_RESULT patientResult) throws HL7Exception {
        ORU_R01_ORDER_OBSERVATION orderObservation = patientResult.getORDER_OBSERVATION();
        String obr3Identifier = orderObservation.getOBR().getObr3_FillerOrderNumber().getEi1_EntityIdentifier().encode();
        String obx3Identifier = orderObservation.getOBSERVATION().getOBX().getObx3_ObservationIdentifier().getCe1_Identifier().encode();
        String obx4 = orderObservation.getOBSERVATION().getOBX().getObx4_ObservationSubId().encode();

        String result = obr3Identifier;
        if (!obx3Identifier.isEmpty()) result = result + "-" + obx3Identifier;
        if (!obx4.isEmpty()) result = result + "-" + obx4;
        return result;
    }

    private static String extractDiagnosticReportIdentifier(ORU_R01_PATIENT_RESULT patientResult) throws HL7Exception {
        ORU_R01_ORDER_OBSERVATION orderObservation = patientResult.getORDER_OBSERVATION();
        /*
            For non-globally unique filler-id the filler/placer number must be combined with the universal service Id
             - OBR-2(if present)+OBR-3+OBR-4
        */
        String obr2EntityIdentifier = orderObservation.getOBR().getObr2_PlacerOrderNumber().getEi1_EntityIdentifier().encode();
        String obr3Identifier = orderObservation.getOBR().getObr3_FillerOrderNumber().getEi1_EntityIdentifier().encode();
        String obr4Identifier = orderObservation.getOBR().getObr4_UniversalServiceIdentifier().getCe1_Identifier().encode();

        String result = null;
        if (obr2EntityIdentifier.isEmpty())
            result = obr3Identifier;
        else
            result = obr2EntityIdentifier + "-" + obr3Identifier;
        result = result + "-" + obr4Identifier;
        return result;
    }

    private static String extractObservationStatus(ORU_R01_PATIENT_RESULT patientResult) throws HL7Exception {
        OBX obx = patientResult.getORDER_OBSERVATION().getOBSERVATION().getOBX();
        String status = obx.getObx11_ObservationResultStatus().encode();
        return status;
    }

    private static String extractDiagnosticReportStatus(ORU_R01_PATIENT_RESULT patientResult) throws HL7Exception {
        OBR obr = patientResult.getORDER_OBSERVATION().getOBR();
        String status = obr.getObr25_ResultStatus().encode();
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


    public static String extractPatientId(ORU_R01_PATIENT_RESULT patientResult) throws HL7Exception {
        String patientId = patientResult.getPATIENT().getPID().getPid3_PatientIdentifierList()[0].getCx1_ID().encode();
        return patientId;
    }


    private static List<String> extractReportTextLines(ORU_R01_PATIENT_RESULT patientResult) throws HL7Exception {
        List<String> rows = new ArrayList<>();
        for(ORU_R01_OBSERVATION orderObservation : patientResult.getORDER_OBSERVATION().getOBSERVATIONAll()) {
            rows.add(orderObservation.getOBX().getObx5_ObservationValue()[0].encode());
        }
        return rows;
    }

    //HAPI lib for "text" element does not allow <div> content to start with a "<", like in a case of a <br>.
    //Must remove a leading <br> if it exists.
    private static String prepareDivString(List<String> reportTextLines) {

        StringBuilder divContent = new StringBuilder();
        reportTextLines.forEach((String s) -> {
            divContent.append(s + "<br/>");
        });

        String stringContent = divContent.toString().trim();   //HAPI library does a .trim() anyways

        while (stringContent.startsWith("<br/>")) {
            stringContent = stringContent.replaceFirst("<br/>", "");
            stringContent.trim();
        }

        return stringContent;
    }

}