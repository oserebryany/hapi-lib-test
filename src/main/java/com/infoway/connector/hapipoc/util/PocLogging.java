package com.infoway.connector.hapipoc.util;

import java.sql.Timestamp;
import java.util.Map;
import java.util.regex.Pattern;

public class PocLogging {

    public static void log(String msg) {

        //replace all life feeds with Carriage returns
        msg = msg.replaceAll("\\r", "\\r\\n");

        Timestamp timestamp = new Timestamp (System.currentTimeMillis());
        System.out.println( timestamp.toString() + "  " + msg );
    }

    public static void error(String msg) {

        //replace all life feeds with Carriage returns
        msg = msg.replaceAll("\\r", "\\r\\n");

        Timestamp timestamp = new Timestamp (System.currentTimeMillis());
        System.out.println( timestamp.toString() + "  *** ERROR: " + msg );
    }

    public static void logMapStrings(Map<String, Object> map) {
        map.forEach((key, value) -> log(key + ":" + value));
    }
}
