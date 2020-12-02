package com.infoway.connector.hapipoc.util;

import java.util.Map;
import java.util.regex.Pattern;

public class PocLogging {

    public static void log(String msg) {

        //replace all life feeds with Carriage returns
        msg = msg.replaceAll("\\r", "\\r\\n");
        System.out.println( msg );
    }

    public static void logMapStrings(Map<String, Object> map) {
        map.forEach((key, value) -> log(key + ":" + value));
    }
}
