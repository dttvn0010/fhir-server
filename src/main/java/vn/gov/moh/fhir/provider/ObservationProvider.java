package vn.gov.moh.fhir.provider;

import java.util.List;
import java.util.UUID;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Resource;
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
import ca.uhn.fhir.rest.annotation.Validate;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.ValidationModeEnum;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import vn.gov.moh.fhir.model.entity.ObservationEntity;
import vn.gov.moh.fhir.service.ObservationService;
import vn.gov.moh.fhir.utils.DataUtils;
import vn.gov.moh.fhir.utils.FhirHelper;

@Component
public class ObservationProvider implements IResourceProvider {

    @Autowired private FhirHelper fhirUtils;
    @Autowired private ObservationService observationService;
    
    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Observation.class;
    }

    @Read
    public Resource read(@IdParam IdType idType) {
        var observationEntity = observationService.getByUuid(idType.getIdPart());
        if(observationEntity == null) {
            fhirUtils.createOperationOutcome("No Observation with \"" + idType.getIdPart() + "\" found");
        }
        return observationEntity.toFhir();
    }    
    
    @Validate
    public MethodOutcome validate(@ResourceParam Observation observation, 
            @Validate.Mode ValidationModeEnum mode,
            @Validate.Profile String profile) {
        
        var result = fhirUtils.validateResource(observation, profile);
        var outcome = new MethodOutcome();
        outcome.setOperationOutcome(result.toOperationOutcome());
        return outcome;       
    }
        
    @Create
    public MethodOutcome create(@ResourceParam Observation observation) {
        // validate resource
        var validateResult = fhirUtils.validateResource(observation, null);
        
        if(!validateResult.isSuccessful()) {
            var outcome = new MethodOutcome();           
            outcome.setResource(validateResult.toOperationOutcome());
            return outcome;
        }
        
        // Save resource         
        var id = UUID.randomUUID().toString();
        observation.setId(id);      
        var observationEntity = ObservationEntity.fromFhir(observation);
        observationEntity.set_Version(1);
        observationEntity.set_Active(true);
        observationEntity = observationService.save(observationEntity);
        
        observation = observationEntity.toFhir();
        
        var outcome = new MethodOutcome();
        outcome.setCreated(true);
        outcome.setOperationOutcome(fhirUtils.createOperationOutcome(                
                "urn:uuid:" + observation.getId(),
                "Create succsess",
                IssueSeverity.INFORMATION, 
                IssueType.VALUE
         ));
        
        outcome.setResource(observation);
        return outcome;
    }
    
    @Delete
    public void delete(@IdParam IdType id) {
        var observationEntity = observationService.getByUuid(id.getIdPart());
        
        if(observationEntity != null) {
            observationEntity.set_Active(false);
            observationService.save(observationEntity);
        }
    }
    
    @Search
    public List<Observation> search(
            @OptionalParam(name ="patient") ReferenceParam patient,
            @OptionalParam(name ="encounter") ReferenceParam encounter,
            @Count Integer count, @Offset Integer offset) {
        
        
        var lst = observationService.search(
                patient != null? patient.getValue(): "", 
                encounter != null? encounter.getValue(): "", 
                offset, 
                count);
        
        return DataUtils.transform(lst, ObservationEntity::toFhir);
    }
}
