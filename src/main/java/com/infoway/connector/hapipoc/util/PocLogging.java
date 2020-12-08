package com.infoway.connector.hapipoc.util;

import org.apache.logging.log4j.util.MessageSupplier;

import java.util.logging.Level;
import java.sql.Timestamp;
import java.util.Map;
import java.util.logging.Logger;

public class PocLogging {
    private static final Logger LOGGER = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

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
        LOGGER.severe("  *** ERROR: " + msg);
    }

    public static void logMapStrings(Map<String, Object> map) {
        StringBuilder mapStr = new StringBuilder();
        map.forEach((key, value) -> mapStr.append("\r\n" + key + ":" + value));
        LOGGER.info(mapStr.toString());
        //map.forEach((key, value) -> log(key + ":" + value));
    }
}
