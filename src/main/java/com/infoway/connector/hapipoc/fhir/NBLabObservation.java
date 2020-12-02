package com.infoway.connector.hapipoc.fhir;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.ResourceDef;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Observation;

@ResourceDef(profile = "https://simplifier.net/PHIAccess/AccessLabResultObservation")
public class NBLabObservation extends Observation {
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

}
