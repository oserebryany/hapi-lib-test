package com.infoway.connector.hapipoc.fhir;

import org.hl7.fhir.r4.model.Patient;

public class InfowayFhirResoureFactory {

    public static InfowayPatient createPatient() {
        InfowayPatient myPatient = new InfowayPatient();
        //    myPatient.setPetName(new StringType("Adam"));
        //myPatient.addExtension(new Extension("http://example.com/dontuse#petname", new StringType("Adam")));

        return myPatient;
    }

}
