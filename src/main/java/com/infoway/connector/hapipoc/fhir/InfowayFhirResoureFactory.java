package com.infoway.connector.hapipoc.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.fhirpath.IFhirPath;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;

import java.util.List;

public class InfowayFhirResoureFactory {

    public static Patient createPatient() {
        IFhirPath fhirPath = FhirContext.forR4().newFhirPath();

        InfoWayPatient myPatient = new InfoWayPatient();
        //    myPatient.setPetName(new StringType("Adam"));
        myPatient.addExtension(new Extension("http://example.com/dontuse#petname", new StringType("Adam")));

        List<Extension> result = fhirPath.evaluate(myPatient, "Patient.extension('http://example.com/dontuse#petname')", Extension.class);
        assert result.size() == 1; // succeeds

        return myPatient;
    }

}
