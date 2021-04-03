package vn.gov.moh.fhir.provider;

import java.util.List;
import java.util.UUID;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
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
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import vn.gov.moh.fhir.model.entity.EncounterEntity;
import vn.gov.moh.fhir.service.EncounterService;
import vn.gov.moh.fhir.utils.DataUtils;
import vn.gov.moh.fhir.utils.FhirHelper;

@Component
public class EncounterProvider  implements IResourceProvider  {

    @Autowired private FhirHelper fhirUtils;
    @Autowired private EncounterService encounterService;

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Encounter.class;
    }
    
    @Read
    public Resource read(@IdParam IdType idType) {
        var encounterEntity = encounterService.getByUuid(idType.getIdPart());
        if(encounterEntity == null) {
            fhirUtils.createOperationOutcome("No Encounter with \"" + idType.getIdPart() + "\" found");
        }
        return encounterEntity.toFhir();
    }    
    
    @Read
    public Resource readVersion(@IdParam IdType idType) {
        EncounterEntity encounterEntity = null;
        Integer version = null;
        
        if(idType.hasVersionIdPart()) {
            version = DataUtils.parseInt(idType.getVersionIdPart());
            if(version != null) {                
                encounterEntity = encounterService.getByUuidAndVersion(idType.getIdPart(), version);
            }
        }else {
            encounterEntity = encounterService.getByUuid(idType.getIdPart());
        }
        
        if(encounterEntity == null) {
            fhirUtils.createOperationOutcome("No Encounter with \"" + idType.getIdPart() + "\" and version " + version + " found");
        }
        
        return encounterEntity.toFhir();
    }
    
    @Search
    public List<Encounter> search(@OptionalParam(name ="name") StringType name,                            
            @Count Integer count, @Offset Integer offset) {
        
        var encounterList = encounterService.search(name != null? name.getValue(): "", offset, count);
        return DataUtils.transform(encounterList, EncounterEntity::toFhir);
    }
    
    @Validate
    public MethodOutcome validate(@ResourceParam Encounter encounter, 
            @Validate.Mode ValidationModeEnum mode,
            @Validate.Profile String profile) {
        
        var result = fhirUtils.validateResource(encounter, profile);
        var outcome = new MethodOutcome();
        outcome.setOperationOutcome(result.toOperationOutcome());
        return outcome;       
    }
    
    @Create
    public MethodOutcome create(@ResourceParam Encounter encounter) {
        // validate resource
        var validateResult = fhirUtils.validateResource(encounter, null);
        
        if(!validateResult.isSuccessful()) {
            var outcome = new MethodOutcome();           
            outcome.setResource(validateResult.toOperationOutcome());
            return outcome;
        }
        
        // Save resource
        
        var id = UUID.randomUUID().toString();
        encounter.setId(id);      
        var encounterEntity = EncounterEntity.fromFhir(encounter);
        encounterEntity.set_Version(1);
        encounterEntity.set_Active(true);
        encounterEntity = encounterService.save(encounterEntity);
        
        encounter = encounterEntity.toFhir();
        
        var outcome = new MethodOutcome();
        outcome.setCreated(true);
        outcome.setOperationOutcome(fhirUtils.createOperationOutcome(                
                "urn:uuid:" + encounter.getId(),
                "Create succsess",
                IssueSeverity.INFORMATION, 
                IssueType.VALUE
         ));
        
        outcome.setResource(encounter);
        return outcome;
    }
    
    @Update
    public MethodOutcome update(@IdParam IdType id, @ResourceParam Encounter encounter) {
        
        // Check if resource existed
        var oldEncounterEntity = encounterService.getByUuid(id.getIdPart());
        int oldVersion = 0;
        
        if(oldEncounterEntity == null) {
            var outcome = new MethodOutcome();
            outcome.setCreated(false);
            outcome.setResource(fhirUtils.createOperationOutcome(
                    "No Encounter with id :\"" + id.getIdPart() + " \" found",
                    "Update fail",
                    IssueSeverity.ERROR, 
                    IssueType.VALUE
             ));
            return outcome;
        }
        
        // validate resource
        var validateResult = fhirUtils.validateResource(encounter, null);
        
        if(!validateResult.isSuccessful()) {
            var outcome = new MethodOutcome();           
            outcome.setResource(validateResult.toOperationOutcome());
            return outcome;
        }
        
        // Save resource            
        if(oldEncounterEntity != null) {
            oldVersion = oldEncounterEntity.get_Version();
            oldEncounterEntity.set_Active(false);
            encounterService.save(oldEncounterEntity);
        }
        
        var encounterEntity = EncounterEntity.fromFhir(encounter);
        encounterEntity.set_Version(1 + oldVersion);
        encounterEntity.set_Active(true);
        encounterEntity = encounterService.save(encounterEntity);
        
        encounter = encounterEntity.toFhir();
        
        
        var outcome = new MethodOutcome();
        outcome.setCreated(false);
        outcome.setOperationOutcome(fhirUtils.createOperationOutcome(                
                "urn:uuid:" + encounter.getId(),
                "Update succsess",
                IssueSeverity.INFORMATION, 
                IssueType.VALUE
         ));
        
        outcome.setResource(encounter);
        return outcome;
    }
    
    @Delete
    public void delete(@IdParam IdType id) {
        var encounterEntity = encounterService.getByUuid(id.getIdPart());
        
        if(encounterEntity != null) {
            encounterEntity.set_Active(false);
            encounterService.save(encounterEntity);
        }
    }
    
    @Search
    public List<Encounter> search(
            @OptionalParam(name ="patient") ReferenceParam patient,
            @Count Integer count, @Offset Integer offset) {
        
        
        var lst = encounterService.search(
                patient != null? patient.getValue(): "",  
                offset, 
                count);
        
        return DataUtils.transform(lst, EncounterEntity::toFhir);
    }    
    
}
