package com.infoway.connector.hapipoc.hl7v2.custommodel.notused.NB.v24.message;

import ca.uhn.hl7v2.model.v24.message.ORU_R01;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.parser.DefaultModelClassFactory;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import com.infoway.connector.hapipoc.hl7v2.custommodel.notused.NB.v24.segment.ZBR;

import java.util.Arrays;


public class NBLab_ORU_R01 extends ORU_R01 {

    // Add custom ZBR segment

    private static final long serialVersionUID = 1L;

    public NBLab_ORU_R01() throws HL7Exception {
        this(new DefaultModelClassFactory());
    }

    public NBLab_ORU_R01(ModelClassFactory factory) throws HL7Exception {
        super(factory);

        String[] segmentNamesInMessage = getNames();
        int indexOfOBR = Arrays.asList(segmentNamesInMessage).indexOf("OBR"); // look for precedign segment
        int indexOfZSegment = indexOfOBR + 1; // ZBR segment appears immediately after

        Class<ZBR> type = ZBR.class;
        boolean required = true;
        boolean repeating = false;

        this.add(type, required, repeating, indexOfZSegment); //add this segment to the message payload
    }

    public ZBR getZBRSegment() throws HL7Exception {
        return getTyped("ZBR", ZBR.class);
    }

}