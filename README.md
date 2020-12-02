## Accelerator project FHIR and HL7 POC

This application uses HAPI Java library to test the following concepts:
 1. Loading ConceptMaps and using them to remap codes
 1. Parse HL7 v2 messages and handle custom Segments
 1. Define custom FHIR resources (e.g. Patient)
 1. Initialize FHIR resource and deserialize as JSON (e.g. Observation)
 
The application is meant to run only as a command-line utility.

The application uses Maven shade plugin to create a fully packaged JAR, making it easier to run it as a command-line utility without the need to specify classpaths.

There are **two** ways to run the utility on the command line:

#### Run as a Maven target
`mvn java:exec`

Additional command-line parameters can be passed like so:

`mvn exec:java -Dexec.args="NBLabTest.txt NBLabFhirOut.txt"`     

#### Execute Java jar directly

`java -cp target/hapi-lib-test-1.0.jar com.infoway.connector.hapipoc.Main`

Additional command-line parameters can be added immediately following the command:

`java -cp target/hapi-lib-test-1.0.jar com.infoway.connector.hapipoc.Main NBLabTest.txt NBLabFhirOut.txt`
