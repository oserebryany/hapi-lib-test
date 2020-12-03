package com.infoway.connector.hapipoc;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.infoway.connector.hapipoc.fhir.NBLabObservation;
import com.infoway.connector.hapipoc.util.PocLogging;
import org.hl7.fhir.r4.model.Narrative;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;

public class TestFhirResources {

    public static void playWithFhir(String[] args) {
        PocLogging.log("=========== Test FHIR resources");

        /********
         * WORK IN PROGRESS.....................
         */

        Patient patient = new Patient();

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
}
