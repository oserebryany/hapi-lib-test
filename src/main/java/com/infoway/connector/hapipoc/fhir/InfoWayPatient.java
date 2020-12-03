package com.infoway.connector.hapipoc.fhir;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.Extension;
import ca.uhn.fhir.model.api.annotation.ResourceDef;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.util.ElementUtil;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.codesystems.AdministrativeGender;

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
//       @Child(name="ethnicity", min=0, max=1)
//       @Extension(url="https://simplifier.net/phiaccess/extensionethnicity", definedLocally=true, isModifier=false)
//       @Description(shortDefinition="A code classifying the person's ethnic group")
//       protected StringDt ethnicity;

       @Override
       public boolean isEmpty() {
              //return super.isEmpty() && ElementUtil.isEmpty(birthsex) && ElementUtil.isEmpty(ethnicity);
              return super.isEmpty() && ElementUtil.isEmpty(birthsex);
       }

       public Enumerations.AdministrativeGender getBirthsex() {
                /*
              copied from org.hl7.fhir.r4.model.Patient:getGender
               */
           return this.birthsex == null ? null : this.birthsex.getValue();
       }

       public void setBirthsex(Enumerations.AdministrativeGender value) {
              /*
              copied from org.hl7.fhir.r4.model.Patient:setGender
               */
              if (value == null)
                     this.birthsex = null;
              else {
                     if (this.birthsex == null)
                            this.birthsex = new Enumeration<Enumerations.AdministrativeGender>(new Enumerations.AdministrativeGenderEnumFactory());
                     this.birthsex.setValue(value);
              }
              //return this;
       }


}
