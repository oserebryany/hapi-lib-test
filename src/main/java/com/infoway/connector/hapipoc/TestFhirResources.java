package com.infoway.connector.hapipoc;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.infoway.connector.hapipoc.fhir.InfowayPatient;
import com.infoway.connector.hapipoc.fhir.InfowayFhirResoureFactory;
import com.infoway.connector.hapipoc.fhir.NBLabObservation;
import org.hl7.fhir.r4.model.*;

import java.util.logging.Logger;

public class TestFhirResources {
    private static final Logger LOGGER = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    public static void playWithFhir(String[] args) {
        LOGGER.info("=========== Test FHIR resources");

        /********
         * WORK IN PROGRESS.....................
         */

        FhirContext ctx = FhirContext.forR4();
        IParser parser = ctx.newJsonParser();
        //IFhirPath fhirPath = FhirContext.forR4().newFhirPath();

        InfowayPatient patient = InfowayFhirResoureFactory.createPatient();
        patient.setId("test123");

        Identifier id = patient.addIdentifier();
        //for JPID identifier
        /*
        {
            "coding": [
            {
                "system": "http://terminology.hl7.org/CodeSystem/v2-0203",
                    "code": "JPID"
            }
            ]
        }
        */
        Coding jpidIdentifier = new Coding("http://terminology.hl7.org/CodeSystem/v2-0203", "JPID", null);
        id.setType(new CodeableConcept().addCoding(jpidIdentifier));
        id.setSystem("https://fhir.infoway-inforoute.ca/NamingSystem/ca-nb-patient-healthcare-id");
        id.setValue("SAMPLE_12345");

        patient.setBirthsex(Enumerations.AdministrativeGender.FEMALE);
        patient.setGender(Enumerations.AdministrativeGender.FEMALE);

        parser.setPrettyPrint(true);
        String serialized = parser.encodeResourceToString(patient);
        LOGGER.info("---------------------- Patient FHIR JSON -------------------------------------");
        LOGGER.info(serialized);
        LOGGER.info("-------------------------------------------------------------------------------");


//        List<Extension> result = fhirPath.evaluate(patient, "Patient.extension('https://simplifier.net/phiaccess/extensionbirthsex')", Extension.class);
//        assert result.size() == 1; // succeeds

        Observation observation = new NBLabObservation();
        observation.setId("123");

        Narrative textNarrative = new Narrative();

        textNarrative.setStatus(Narrative.NarrativeStatus.ADDITIONAL);
        textNarrative.setDivAsString("<div>This is the narrative text<br/>this is line 2</div>");
        observation.setText(textNarrative);
        observation.getText().setStatus(Narrative.NarrativeStatus.ADDITIONAL);
        observation.getText().setDivAsString("<div>This is the narrative text<br/>this is line 2</div>");


        serialized = parser.encodeResourceToString(observation);
        LOGGER.info(serialized);

    }
}
