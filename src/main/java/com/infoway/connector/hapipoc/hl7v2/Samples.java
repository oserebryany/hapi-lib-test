package com.infoway.connector.hapipoc.hl7v2;

public class Samples {

    public static String ADT_A01_TextMessage =
            "MSH|^~\\&|IRIS^LAB|SANTER^829^829|AMB_R|SANTER|200803051508||ADT^A01|263206|P|2.4\r"
                    + "EVN||200803051509||||200803031508\r"
                    + "PID|||5520255^^^PK^PK~ZZZZZZ83M64Z148R^^^CF^CF~ZZZZZZ83M64Z148R^^^SSN^SSN^^20070103^99991231~^^^^TEAM||ZZZ^ZZZ||19830824|F||||||||||||||||||||||N\r"
                    + "ZPI|Fido~Fred|13\r"
                    + "PV1||I|6402DH^^^^^^^^MED. 1 - ONCOLOGIA^^OSPEDALE MAGGIORE DI LODI&LODI|||^^^^^^^^^^OSPEDALE MAGGIORE DI LODI&LODI|13936^TEST^TEST||||||||||5068^TEST2^TEST2||2008003369||||||||||||||||||||||||||200803031508\r"
                    + "PR1|1||1111^Mastoplastica|Protesi|20090224|02|";

    public static String ORU_R01_Standard1_TextMessage =
            "MSH|^~\\&|GHH LAB|ELAB-3|GHH OE|BLDG4|200202150930||ORU^R01|CNTRL-3456|P|2.4\r"
                    + "PID|||555-44-4444||EVERYWOMAN^EVE^E^^^^L|JONES|19620320|F|||153 FERNWOOD DR.^^STATESVILLE^OH^35292|||||||AC555444444||67-A4335^OH^20030520\r"
                    + "OBR|1|845439^GHH OE|1045813^GHH LAB|15545^GLUCOSE|||200202150730||||||||| 555-55-5555^PRIMARY^PATRICIA P^^^^MD^^|||||||||F||||||444-44-4444^HIPPOCRATES^HOWARD H^^^^MD\r"
                    + "OBX|1|SN|1554-5^GLUCOSE^POST 12H CFST:MCNC:PT:SER/PLAS:QN||^182|mg/dl|70_105|H|||F\r";

    public static String ORU_R01_Standard2_TextMessage =
            "MSH|^~\\&|LAB|SJR|||20201021115003||ORU^R01|Q326208810T332380099|D|2.4||xFx2020:10:21 11:50.505||||8859/1\r"
                    + "PID|1||840571^^^&829&L^NBDOHFAC||TESTING^WEEZIE||19740129|F|||42 LILA COURT^^QUISPAMSIS^NB-HL7v2^E2E6B1^CAN^H~~^^^^^^B~~^^^^^^M|||||||10960776^^^&RHA2|478955297\r"
                    + "OBR|1|305120183_SCM^^RHA2^RHAPONUM|MB-20-000435^^RHA2^RHAFONUM|21370^COVID-19 SJRH^RHA2LAB||20201021114300|20201021114300|||||||20201021114300||480^Andrew^Donald^M^^^^^MD&NBMS^^^^PRN|||||002BFW333|20201021115003||LAB|F|||||||||||||||||||Category En^Microbiology^L^Category Fr^Microbiologie^L|||External ID^002BFW333MB200004355Q326208810T332380099829^L829^Specimen Number^MB-20-000435^L829\r"
                    + "OBX|1|FT|XNB1842-4^SARS-CoV-2; Rapid Test; PCR/NAAT^LNEN^XNB1842-4^SARS-CoV-2; Test Rapide; RCP/TAAN^LNFR||Detection of COVID-19 (2019-nCoV) virus by PCR: NEGATIVE||||||F|||20201021115002\r"
                    + "OBX|2|FT|XNB955-5^Comment^LNEN^XNB955-5^Commentaire^LNFR||Reason for testing:contactx.brxHealth Care Worker:nox.brxAsymptomatic:yes||||||F|||20201021115002\r"
            ;

    public static String ORU_R01_NBLab1_TextMessage =
            "MSH|^~\\&|LAB^LAB|SJR^829^829|||20201021115003||ORU^R01|Q326208810T332380099|D|2.4||\\F\\2020:10:21 11:50.505||||8859/1\r"
                    + "PID|1||840571^^^&829&L^NBDOHFAC||TESTING^WEEZIE||19740129|F|||42 LILA COURT^^QUISPAMSIS^NB-HL7v2^E2E6B1^CAN^H~~^^^^^^B~~^^^^^^M|||||||10960776^^^&RHA2|478955297\r"
                    + "OBR|1|305120183_SCM^^RHA2^RHAPONUM|MB-20-000435^^RHA2^RHAFONUM|21370^COVID-19 SJRH^RHA2LAB||20201021114300|20201021114300|||||||20201021114300||480^Andrew^Donald^M^^^^^MD&NBMS^^^^PRN|||||002BFW333|20201021115003||LAB|F|||||||||||||||||||Category En^Microbiology^L^Category Fr^Microbiologie^L|||External ID^002BFW333MB200004355Q326208810T332380099829^L829^Specimen Number^MB-20-000435^L829\r"
                    + "ZBR|S||||MB-20-000435\\F\\002BFW333MB200004355Q326208810T332380099829\\F\\RHA2\\F\\20201021115002\\F\\|5\\F\\Microbiology\\F\\Microbiologie|COVID-19 SJRH\\F\\||000000145574\r"
                    + "OBX|1|FT|XNB1842-4^SARS-CoV-2; Rapid Test; PCR/NAAT^LNEN^XNB1842-4^SARS-CoV-2; Test Rapide; RCP/TAAN^LNFR||Detection of COVID-19 (2019-nCoV) virus by PCR: NEGATIVE||||||F|||20201021115002\r"
                    + "ZBX|SARS-CoV-2; Rapid Test; PCR/NAAT\\F\\SARS-CoV-2; Test Rapide; RCP/TAAN\r"
                    + "OBX|2|FT|XNB955-5^Comment^LNEN^XNB955-5^Commentaire^LNFR||Reason for testing:contact\\.br\\Health Care Worker:no\\.br\\Asymptomatic:yes||||||F|||20201021115002\r"
            ;

    public static String ORU_R01_NBLab2_TextMessage = "MSH|^~\\&|LAB^LAB|SJR^829^829|||20201021115003||ORU^R01|Q326208810T332380099|D|2.4||\\F\\2020:10:21 11:50.505||||8859/1\r"
            + "PID|1||840571^^^&829&L^NBDOHFAC||TESTING^WEEZIE||19740129|F|||42 LILA COURT^^QUISPAMSIS^NB-HL7v2^E2E6B1^CAN^H~~^^^^^^B~~^^^^^^M|||||||10960776^^^&RHA2|478955297\r"
            + "OBR|1|305120183_SCM^^RHA2^RHAPONUM|MB-20-000435^^RHA2^RHAFONUM|21370^COVID-19 SJRH^RHA2LAB||20201021114300|20201021114300|||||||20201021114300||480^Andrew^Donald^M^^^^^MD&NBMS^^^^PRN|||||002BFW333|20201021115003||LAB|F|||||||||||||||||||Category En^Microbiology^L^Category Fr^Microbiologie^L|||External ID^002BFW333MB200004355Q326208810T332380099829^L829^Specimen Number^MB-20-000435^L829\r"
            + "ZOBR|S||||MB-20-000435\\F\\002BFW333MB200004355Q326208810T332380099829\\F\\RHA2\\F\\20201021115002\\F\\|5\\F\\Microbiology\\F\\Microbiologie|COVID-19 SJRH\\F\\||000000145574\r"
            + "OBX|1|FT|XNB1842-4^SARS-CoV-2; Rapid Test; PCR/NAAT^LNEN^XNB1842-4^SARS-CoV-2; Test Rapide; RCP/TAAN^LNFR||Detection of COVID-19 (2019-nCoV) virus by PCR: NEGATIVE||||||F|||20201021115002\r"
            + "ZOBX|SARS-CoV-2; Rapid Test; PCR/NAAT\\F\\SARS-CoV-2; Test Rapide; RCP/TAAN\r"
            + "OBX|2|FT|XNB955-5^Comment^LNEN^XNB955-5^Commentaire^LNFR||Reason for testing:contact\\.br\\Health Care Worker:no\\.br\\Asymptomatic:yes||||||F|||20201021115002\r"
            + "ZOBX|Comment\\F\\Commentaire\r";


}
