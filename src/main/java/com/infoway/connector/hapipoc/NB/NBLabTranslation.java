package com.infoway.connector.hapipoc.NB;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import com.infoway.connector.hapipoc.hl7v2.HL7v2Parser;
import com.infoway.connector.hapipoc.util.PocLogging;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Resource;

import java.util.ArrayList;
import java.util.List;

public class NBLabTranslation {

    /*
    Translate individual observation messages and build a response bundle
     */
    public Bundle process(List<String> hl7MessageList) {


        List<Resource> translatedResourceList = translateMessagesToResources(hl7MessageList);

        Bundle resultBundle = new Bundle();
        resultBundle.setType(Bundle.BundleType.SEARCHSET);
        for (Resource resource : translatedResourceList) {
            resultBundle.addEntry().setResource(resource);
        }

        return resultBundle;
    }

    /*
    Parse HL7 messages and create a corresponding FHIR Resource for each message
     */
    private List<Resource> translateMessagesToResources(List<String> hl7MessageList) {

        List<Resource> resourceList = new ArrayList<>();
        for(String msgString : hl7MessageList) {

            String preprocessedMsg = NBLabORUMessageHelper.preProcessTextMessage(msgString);
            Message hl7Message = HL7v2Parser.parseMessage(preprocessedMsg);
            if (hl7Message == null) {
                PocLogging.error("Message failed to parse");
                continue;
            }

            Resource fhirResource = hl7v2ToFhirResource(hl7Message);
            resourceList.add(fhirResource);
        }
        return resourceList;
    }

    /*
    Determine the type of resource to be created.
     */
    private Resource hl7v2ToFhirResource(Message hl7Message) {

        String singleOrTextual = NBLabORUMessageHelper.singleOrTextual(hl7Message);
        Resource resource = null;
        switch (singleOrTextual) {
            case "S":
                resource = NBLabORUMessageHelper.buildFhirObservation(hl7Message);
                break;
            case "T":
                resource = NBLabORUMessageHelper.buildFhirDiagnosticReport(hl7Message);
                break;
        }

        return resource;
    }
}
