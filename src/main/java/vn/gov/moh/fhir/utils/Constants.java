package vn.gov.moh.fhir.utils;

public class Constants {

    public static interface IdentifierSystem{
        final public static String PATIENT_INSURANCE = "http://fhir.moh.gov.vn/sid/Patient-Insurance-Number";
        final public static String PRACTITIONER_QUALIFICATION_NUMBER = "http://fhir.moh.gov.vn/sid/Practitioner-Qualification-Number";
        final public static String MEDICAL_RECORD_NUMBER = "http://fhir.moh.gov.vn/sid/Medical-Record-Number";
        final public static String ORGANIZATION_CODE = "http://fhir.moh.gov.vn/sid/Organization-Code";
    }
    
    public static interface CodeSystemURL {
        final public static String MEDICATION = "http://fhir.moh.gov.vn/CodeSystem/Medication";
        final public static String PROCEDURE_CATEGORY = "http://fhir.moh.gov.vn/CodeSystem/Procedure-Category";
        final public static String PROCEDURE = "http://fhir.moh.gov.vn/CodeSystem/Procedure";
        final public static String OBSERVATION = "http://fhir.moh.gov.vn/CodeSystem/Observation";
        final public static String SPECIMEN_TYPE = "http://fhir.moh.gov.vn/CodeSystem/Specimen-Type";
        final public static String SPECIMEN_CONDITION = "http://fhir.moh.gov.vn/CodeSystem/Specimen-Condition";
    }
   
    public static interface ExtensionURL {
        // Patient        
        final public static String PATIENT_ETHNIC = "http://fhir.moh.gov.vn/StructureDefinition/Patient-Ethnic";
        final public static String PATIENT_RELIGION = "http://fhir.moh.gov.vn/StructureDefinition/Patient-Religion";
        final public static String PATIENT_EDUCATION = "http://fhir.moh.gov.vn/StructureDefinition/Patient-Education";
        final public static String PATIENT_JOB = "http://fhir.moh.gov.vn/StructureDefinition/Patient-Job";
        final public static String PATIENT_NATIONALITY = "http://fhir.moh.gov.vn/StructureDefinition/Patient-Nationality";        
        
        // Address
        final public static String ADDRESS_WARD = "http://fhir.moh.gov.vn/StructureDefinition/Address-Ward";
        final public static String ADDRESS_DISTRICT = "http://fhir.moh.gov.vn/StructureDefinition/Address-District";
        final public static String ADDRESS_CITY = "http://fhir.moh.gov.vn/StructureDefinition/Address-City";        
        
    }
    
}
