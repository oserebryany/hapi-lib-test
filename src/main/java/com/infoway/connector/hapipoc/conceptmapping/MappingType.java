package com.infoway.connector.hapipoc.conceptmapping;

public enum MappingType {
    NB_LAB_OBSERVATION_STATUS("conceptmaps/NBLabObservationStatus.json"),
    NB_LAB_OBSERVATION_INTERPRETATION("conceptmaps/NBLabObservationInterpretation.json");
//    ADDRESS_FHIR_V3("conceptmaps/Address-FHIR-V3.json"),
//    MP_NTP("conceptmaps/MP-NTP-Mapping.json"),
//    NTP_TM("conceptmaps/NTP-TM-Mapping.json");

    private final String resourceFileName;

    private MappingType(String resourceFileName) {
        this.resourceFileName = resourceFileName;
    }

    public String getResourceFileName() {
        return resourceFileName;
    }

}
