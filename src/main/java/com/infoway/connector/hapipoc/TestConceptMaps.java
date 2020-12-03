package com.infoway.connector.hapipoc;

import com.infoway.connector.hapipoc.conceptmapping.ConceptMapper;
import com.infoway.connector.hapipoc.conceptmapping.MappingType;
import com.infoway.connector.hapipoc.util.PocLogging;

public class TestConceptMaps {

    public static void loadAndTestConceptMappers(String[] args) {
        PocLogging.log("=========== Test ConceptMaps");

        ConceptMapper conceptMapper = ConceptMapper.getInstance();

        //test mapping function

        String code = "F";
        String targetCode1 = conceptMapper.mapCode(MappingType.NB_LAB_OBSERVATION_STATUS, code);
        PocLogging.log(String.format("Mapping test: %s ==> %s (expect 'F ==> final')", code, targetCode1));

        code = "VS";
        String targetCode2 = conceptMapper.mapCode(MappingType.NB_LAB_OBSERVATION_INTERPRETATION, code);
        PocLogging.log(String.format("Mapping test: %s ==> %s (expect 'VS ==> S')", code, targetCode2));


//        String code = "work";
//        String targetCode1 = conceptMapper.mapCode(MappingType.ADDRESS_FHIR_V3, code);
//        PocLogging.log(String.format("Mapping test: %s ==> %s", code, targetCode1));
//
//        code = "02427648";
//        String targetCode2 = conceptMapper.mapCode(MappingType.MP_NTP, code);
//        PocLogging.log(String.format("Mapping test: %s ==> %s  (should be 9000672)", code, targetCode2));
//
//        code = "9014390";
//        String targetCode3 = conceptMapper.mapCode(MappingType.NTP_TM, code);
//        PocLogging.log(String.format("Mapping test: %s ==> %s (should be 8002346)", code, targetCode3));
    }
}
