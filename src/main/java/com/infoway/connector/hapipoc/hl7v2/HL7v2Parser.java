package com.infoway.connector.hapipoc.hl7v2;
import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.CustomModelClassFactory;
import ca.uhn.hl7v2.parser.EncodingNotSupportedException;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import ca.uhn.hl7v2.parser.Parser;

public class HL7v2Parser {

        static public Message parseMessage(String messageText) {

                HapiContext context = new DefaultHapiContext();

                ModelClassFactory ourCustomModelClassFactory = new CustomModelClassFactory("com.infoway.connector.hapipoc.hl7v2.custommodel");
                context.setModelClassFactory(ourCustomModelClassFactory);
                /*
                 * "GenericParser" is able to handle both XML and ER7 (pipe & hat) encodings.
                 */
                Parser parser = context.getGenericParser();

                Message hapiMsg = null;
//                NBLab_ORU_R01 nbLabMsg = null;
                try
                {
                        // The parse method performs the actual parsing
//                        nbLabMsg = (NBLab_ORU_R01) parser.parse(messageText);
                        hapiMsg = parser.parse(messageText);
                } catch (EncodingNotSupportedException e) {
                        e.printStackTrace();
                        return null;
                } catch (HL7Exception e) {
                        e.printStackTrace();
                        return null;
                }

                return hapiMsg;
        }

}
