package vn.moh.fhir.provider;

import java.util.List;
import java.util.UUID;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.Delete;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Offset;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.annotation.Validate;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.ValidationModeEnum;
import ca.uhn.fhir.rest.server.IResourceProvider;
import vn.moh.fhir.model.entity.PatientEntity;
import vn.moh.fhir.service.PatientService;
import vn.moh.fhir.utils.DataUtils;
import vn.moh.fhir.utils.FhirHelper;

@Component
public class PatientProvider implements IResourceProvider{

    @Autowired private FhirHelper fhirUtils;
    @Autowired private PatientService patientService;
    
    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Patient.class;
    }
    
    @Read
    public Resource read(@IdParam IdType idType) {
        var patientEntity = patientService.getByUuid(idType.getIdPart());
        if(patientEntity == null) {
            return fhirUtils.createOperationOutcome("No Patient with \"" + idType.getIdPart() + "\" found");
        }
        return patientEntity.toFhir();
    }    
    
    @Read
    public Resource readVersion(@IdParam IdType idType) {
        PatientEntity patientEntity = null;
        Integer version = null;
        
        if(idType.hasVersionIdPart()) {
            version = DataUtils.parseInt(idType.getVersionIdPart());
            if(version != null) {                
                patientEntity = patientService.getByUuidAndVersion(idType.getIdPart(), version);
            }
        }else {
            patientEntity = patientService.getByUuid(idType.getIdPart());
        }
        
        if(patientEntity == null) {
            fhirUtils.createOperationOutcome("No Patient with \"" + idType.getIdPart() + "\" and version " + version + " found");
        }
        
        return patientEntity.toFhir();
    }
    
    @Search
    public List<Patient> search(@OptionalParam(name ="name") StringType name,                            
            @Count Integer count, @Offset Integer offset) {
        
        var patientList = patientService.search(name != null? name.getValue(): "", offset, count);
        return DataUtils.transform(patientList, PatientEntity::toFhir);
    }
    
    @Validate
    public MethodOutcome validate(@ResourceParam Patient patient, 
            @Validate.Mode ValidationModeEnum mode,
            @Validate.Profile String profile) {
        
        var result = fhirUtils.validateResource(patient, profile);
        var outcome = new MethodOutcome();
        outcome.setOperationOutcome(result.toOperationOutcome());
        return outcome;       
    }
        
    @Create
    public MethodOutcome create(@ResourceParam Patient patient) {
        // validate resource
        var validateResult = fhirUtils.validateResource(patient, null);
        
        if(!validateResult.isSuccessful()) {
            var outcome = new MethodOutcome();           
            outcome.setResource(validateResult.toOperationOutcome());
            return outcome;
        }
        
        // Save resource        
        var id = UUID.randomUUID().toString();
        patient.setId(id);      
        var patientEntity = PatientEntity.fromFhir(patient);
        patientEntity.set_Version(1);
        patientEntity.set_Active(true);
        patientEntity = patientService.save(patientEntity);
        
        patient = patientEntity.toFhir();
        
        var outcome = new MethodOutcome();
        outcome.setCreated(true);
        outcome.setOperationOutcome(fhirUtils.createOperationOutcome(                
                "urn:uuid:" + patient.getId(),
                "Create succsess",
                IssueSeverity.INFORMATION, 
                IssueType.VALUE
         ));
        
        outcome.setResource(patient);
        return outcome;
    }
    
    @Update
    public MethodOutcome update(@IdParam IdType id, @ResourceParam Patient patient) {
        
        // Check if resource existed
        var oldPatientEntity = patientService.getByUuid(id.getIdPart());
        int oldVersion = 0;
        
        if(oldPatientEntity == null) {
            var outcome = new MethodOutcome();
            outcome.setCreated(false);
            outcome.setResource(fhirUtils.createOperationOutcome(
                    "No patient with id :\"" + id.getIdPart() + " \" found",
                    "Update fail",
                    IssueSeverity.ERROR, 
                    IssueType.VALUE
             ));
            return outcome;
        }
            
        if(oldPatientEntity != null) {
            oldVersion = oldPatientEntity.get_Version();
            oldPatientEntity.set_Active(false);
            patientService.save(oldPatientEntity);
        }
        
        // validate resource
        var validateResult = fhirUtils.validateResource(patient, null);
        
        if(!validateResult.isSuccessful()) {
            var outcome = new MethodOutcome();           
            outcome.setResource(validateResult.toOperationOutcome());
            return outcome;
        }
        
        // Save resouce        
        var patientEntity = PatientEntity.fromFhir(patient);
        patientEntity.set_Version(1 + oldVersion);
        patientEntity.set_Active(true);
        patientEntity = patientService.save(patientEntity);
        
        patient = patientEntity.toFhir();
        
        
        var outcome = new MethodOutcome();
        outcome.setCreated(false);
        outcome.setOperationOutcome(fhirUtils.createOperationOutcome(                
                "urn:uuid:" + patient.getId(),
                "Update succsess",
                IssueSeverity.INFORMATION, 
                IssueType.VALUE
         ));
        
        outcome.setResource(patient);
        return outcome;
    }
    
    @Delete
    public void delete(@IdParam IdType id) {
        var patientEntity = patientService.getByUuid(id.getIdPart());
        
        if(patientEntity != null) {
            patientEntity.set_Active(false);
            patientService.save(patientEntity);
        }
    }
}
