package com.infoway.connector.hapipoc;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.fhirpath.IFhirPath;
import ca.uhn.fhir.parser.IParser;
import com.infoway.connector.hapipoc.fhir.InfowayFhirResoureFactory;
import com.infoway.connector.hapipoc.fhir.NBLabObservation;
import com.infoway.connector.hapipoc.util.PocLogging;
import org.hl7.fhir.r4.model.*;

import java.util.List;

public class TestFhirResources {

    public static void playWithFhir(String[] args) {
        PocLogging.log("=========== Test FHIR resources");

        /********
         * WORK IN PROGRESS.....................
         */

        FhirContext ctx = FhirContext.forR4();
        IParser parser = ctx.newJsonParser();
        //IFhirPath fhirPath = FhirContext.forR4().newFhirPath();

        Patient patient = InfowayFhirResoureFactory.createPatient();
        patient.setId("test123");

        Identifier id = patient.addIdentifier();
        id.setSystem("http://terminology.hl7.org/CodeSystem/v2-0203");
        id.setValue("MRN001");

        Identifier identifier = new Identifier();
//        identifier.s
//
//        "system": "http://terminology.hl7.org/CodeSystem/v2-0203",
//                "code": "JPID"

        patient.addIdentifier();



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


        String serialized = parser.encodeResourceToString(observation);
        System.out.println(serialized);

    }
}
