package vn.gov.moh.fhir;

import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;

import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.CorsInterceptor;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;
import vn.gov.moh.fhir.provider.AllergyIntoleranceProvider;
import vn.gov.moh.fhir.provider.ConditionProvider;
import vn.gov.moh.fhir.provider.DiagnosticReportProvider;
import vn.gov.moh.fhir.provider.EncounterProvider;
import vn.gov.moh.fhir.provider.FamilyMemberHistoryProvider;
import vn.gov.moh.fhir.provider.ImmunizationProvider;
import vn.gov.moh.fhir.provider.MedicationProvider;
import vn.gov.moh.fhir.provider.MedicationRequestProvider;
import vn.gov.moh.fhir.provider.ObservationProvider;
import vn.gov.moh.fhir.provider.OrganizationProvider;
import vn.gov.moh.fhir.provider.PatientProvider;
import vn.gov.moh.fhir.provider.PractitionerProvider;
import vn.gov.moh.fhir.provider.ProcedureProvider;
import vn.gov.moh.fhir.provider.RelatedPersonProvider;
import vn.gov.moh.fhir.provider.ServiceRequestProvider;
import vn.gov.moh.fhir.provider.SpecimenProvider;
import vn.gov.moh.fhir.provider.test.CodeSystemProvider;
import vn.gov.moh.fhir.provider.test.StructureDefinitionProvider;
import vn.gov.moh.fhir.provider.test.ValueSetProvider;

@Component
public class FhirRestfulServlet extends RestfulServer {

    private static final long serialVersionUID = 1L;
    @Autowired private AllergyIntoleranceProvider allergyIntoleranceProvider;
    @Autowired private ConditionProvider conditionProvider;
    @Autowired private DiagnosticReportProvider diagnosticReportProvider;
    @Autowired private EncounterProvider encounterProvider;
    @Autowired private FamilyMemberHistoryProvider familyMemberHistoryProvider;
    @Autowired private ImmunizationProvider immunizationProvider;
    @Autowired private MedicationProvider medicationProvider;
    @Autowired private MedicationRequestProvider medicationRequestProvider;
    @Autowired private ObservationProvider observationProvider;
    @Autowired private OrganizationProvider organizationProvider;
    @Autowired private PatientProvider patientProvider;
    @Autowired private PractitionerProvider practitionerProvider;
    @Autowired private ProcedureProvider procedureProvider;
    @Autowired private RelatedPersonProvider relatedPersonProvider;
    @Autowired private ServiceRequestProvider serviceRequestProvider;
    @Autowired private SpecimenProvider specimenProvider;
    @Autowired private CodeSystemProvider codeSystemProvider;
    @Autowired private ValueSetProvider valueSetProvider;
    @Autowired private StructureDefinitionProvider structureDefinitionProvider;
    
    
    @Override
    protected void initialize() throws ServletException {
        setResourceProviders(List.of(
            allergyIntoleranceProvider,
            conditionProvider,
            diagnosticReportProvider,            
            encounterProvider,
            familyMemberHistoryProvider,
            immunizationProvider,
            medicationProvider,
            medicationRequestProvider,
            observationProvider,
            organizationProvider,
            patientProvider,
            practitionerProvider,
            procedureProvider,
            relatedPersonProvider,
            serviceRequestProvider,
            specimenProvider,
            codeSystemProvider,
            valueSetProvider,
            structureDefinitionProvider            
        ));
        
        var config = new CorsConfiguration();
        var corsInterceptor = new CorsInterceptor(config);
        config.addAllowedHeader("Accept");
        config.addAllowedHeader("Content-Type");
        config.addAllowedOrigin("*");
        config.addExposedHeader("Location");
        config.addExposedHeader("Content-Location");
        config.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS"));
        registerInterceptor(corsInterceptor);
        registerInterceptor(new ResponseHighlighterInterceptor());
        setDefaultPrettyPrint(true);
    }
}
