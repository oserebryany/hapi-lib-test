package com.infoway.connector.hapipoc.hl7v2.NB;

import ca.uhn.hl7v2.model.Message;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Observation;

import java.util.ArrayList;
import java.util.List;

public class NBLabTranslation {

    static public Observation translateHL7Message(Message  hl7Message) {
        Observation observation = new Observation();

        List<Identifier> identifierList = getObservationIdentifiers(hl7Message);
        observation.setIdentifier(identifierList);
        return observation;
    }

    static private List<Identifier> getObservationIdentifiers(Message hl7Message) {
        //OBX.3, OBX.4, OBR, etc, etc, etc.
        return new ArrayList<Identifier>();
    }
}
