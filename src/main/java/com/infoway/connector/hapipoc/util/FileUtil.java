package com.infoway.connector.hapipoc.util;

import ca.uhn.hl7v2.util.Hl7InputStreamMessageStringIterator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {


    private static InputStream getFileFromResourceAsStream(String fileName) {

        // The class loader
        ClassLoader classLoader = FileUtil.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        // the stream holding the file content
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        }

    }

    public static String readResourceFile(String fileName) {

        //PocLogging.log(String.format("readResourceFile: Reading contents of file: %s", fileName));

        InputStream is = getFileFromResourceAsStream(fileName);
        InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder sb = new StringBuilder();
        try {
            for (String line; (line = reader.readLine()) != null; ) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

//    public static String readResourceFile(String fileName) {
//
//        PocLogging.log(String.format("readResourceFile: Reading contents of file: %s", fileName));
//
//        Class clazz = FileUtil.class;
//        File file = new File(clazz.getClassLoader().getResource(fileName).getFile());
//        try {
//            StringBuilder sb = new StringBuilder();
//            InputStream is = new FileInputStream(file);
//            InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
//            BufferedReader reader = new BufferedReader(streamReader);
//            for (String line; (line = reader.readLine()) != null;) {
//                sb.append(line);
//            }
//            return sb.toString();
//        } catch (IOException ex) {
//            PocLogging.log(String.format("ERROR in readResouceFile, %s", ex));
//            throw new RuntimeException(ex);
//        }
//    }

    /*
    This uses HAPI's Hl7InputStreamMessageStringIterator, which is able to determine the start and end of
    HL7 v2 messages in any format.

    Also any non-message lines, like comments, are ignored.
     */

    public static List<String> readHL7TextMessages(String fileName) {

        PocLogging.log(String.format("readHL7TextMessages: Request to read messages from file: %s", fileName));

        File file = new File(fileName);

        List<String> messageList = new ArrayList<String>();
        try {
            InputStream is = new FileInputStream(file);
            is = new BufferedInputStream(is);
            Hl7InputStreamMessageStringIterator iter = new Hl7InputStreamMessageStringIterator(is);

            PocLogging.log(String.format("Reading data from file: %s", fileName));
            while (iter.hasNext()) {
                String nextStringMsg = iter.next();
                PocLogging.log(String.format("%s", nextStringMsg));
                messageList.add(nextStringMsg);
            }

            PocLogging.log(String.format("\n==> Read %d messages", messageList.size()));

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        catch (NullPointerException ex) {
            ex.printStackTrace();
        }

        return messageList;
    }

}