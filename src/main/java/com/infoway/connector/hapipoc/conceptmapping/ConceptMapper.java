package com.infoway.connector.hapipoc.conceptmapping;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.infoway.connector.hapipoc.util.FileUtil;
import com.infoway.connector.hapipoc.util.PocLogging;
import org.hl7.fhir.r4.model.ConceptMap;

import java.util.HashMap;
import java.util.Map;

//https://www.journaldev.com/1377/java-singleton-design-pattern-best-practices-examples#bill-pugh-singleton
public class ConceptMapper {    //a singleton class

    private Map<MappingType, Map> maps = new HashMap<>();
    private FhirContext ctx;
    private IParser parser;

    private ConceptMapper(){
        this.ctx = FhirContext.forR4();
        this.parser = ctx.newJsonParser();

        loadAllMaps();
    }

    private static class SingletonHelper{
        private static final ConceptMapper INSTANCE = new ConceptMapper();
    }

    public static ConceptMapper getInstance(){
        return SingletonHelper.INSTANCE;
    }

    private void clearAllMaps() {
        maps.clear();
    }

    private void loadAllMaps() {
        PocLogging.log("loadAllMaps: Clearing and reloading all concept maps");
        clearAllMaps();
        for (MappingType mType: MappingType.values()) {
            ConceptMap conceptMap = loadConceptMapResource(mType);
            Map mappings = extractMappingsFromConceptMap(conceptMap);
            maps.put(mType, mappings);
            PocLogging.log(String.format("  Added concept map %s, # of mapped codes: %d", mType.name(), mappings.size()));
        }
    }

    private Map extractMappingsFromConceptMap(ConceptMap conceptMap) {
        Map<String, String> mappings = new HashMap<>();
        for (ConceptMap.SourceElementComponent component: conceptMap.getGroup().get(0).getElement()) {
            String key = component.getCode();
            String value = component.getTarget().get(0).getCode();
            mappings.put(key, value);
        }
        return mappings;
    }

    private ConceptMap loadConceptMapResource(MappingType mType) {
        String fname = mType.getResourceFileName();
        PocLogging.log(String.format("Loading concept map %s from file: %s", mType.name(), fname));
        String conceptMapJsonString = FileUtil.readResourceFile(fname);
        ConceptMap conceptMap = this.parser.parseResource(ConceptMap.class, conceptMapJsonString);
        return conceptMap;
    }

    public Map getConceptMapping(MappingType mType) {
        return this.maps.get(mType);
    }

    public String mapCode(MappingType mType, String code) {
        Map<String, String> mapping = getConceptMapping(mType);
        String targetCode = mapping.get(code);
        return targetCode;
    }
}

