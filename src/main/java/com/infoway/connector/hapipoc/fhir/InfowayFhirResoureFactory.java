package com.infoway.connector.hapipoc.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.fhirpath.IFhirPath;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;

import java.util.List;

public class InfowayFhirResoureFactory {

    public static Patient createPatient() {
        InfoWayPatient myPatient = new InfoWayPatient();
        //    myPatient.setPetName(new StringType("Adam"));
        //myPatient.addExtension(new Extension("http://example.com/dontuse#petname", new StringType("Adam")));

        return myPatient;
    }

}
