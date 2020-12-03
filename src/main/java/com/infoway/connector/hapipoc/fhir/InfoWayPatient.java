package com.infoway.connector.hapipoc.fhir;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.Extension;
import ca.uhn.fhir.model.api.annotation.ResourceDef;
import ca.uhn.fhir.model.primitive.StringDt;
import org.hl7.fhir.r4.model.*;

@ResourceDef(name="Patient",
        profile = "https://simplifier.net/phiaccess/accesspatientprofile")
public class InfoWayPatient extends Patient {
       private static final long serialVersionUID = 1L;
//
//       @Child(
//               name = "id",
//               order = 1,
//               min = 1,
//               max = 1,
//               modifier = true,
//               summary = true
//       )
//       protected IdType id;

       //*** birthsex
       @Child(name="birthsex", min=0, max=1)
       @Extension(url="https://simplifier.net/phiaccess/extensionbirthsex", definedLocally=true, isModifier=false)
       @Description(shortDefinition="A code classifying the person's sex assigned at birth")
       @ca.uhn.fhir.model.api.annotation.Binding(valueSet="http://hl7.org/fhir/ValueSet/administrative-gender")
       protected Enumeration<Enumerations.AdministrativeGender> birthsex;

       //*** ethnicity
       // https://build.fhir.org/ig/HL7/cda-core-2.0/StructureDefinition-Patient.html
       //https://terminology.hl7.org/2.0.0/ValueSet-v3-Ethnicity.html
       @Child(name="ethnicity", min=0, max=1)
       @Extension(url="https://simplifier.net/phiaccess/extensionethnicity", definedLocally=true, isModifier=false)
       @Description(shortDefinition="A code classifying the person's ethnic group")
       protected StringDt ethnicity;


}
