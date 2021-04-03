package vn.moh.fhir.provider;

import java.util.List;
import java.util.UUID;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.MedicationRequest;
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
import vn.moh.fhir.model.entity.MedicationRequestEntity;
import vn.moh.fhir.service.MedicationRequestService;
import vn.moh.fhir.utils.DataUtils;
import vn.moh.fhir.utils.FhirHelper;

@Component
public class MedicationRequestProvider implements IResourceProvider {

    @Autowired private FhirHelper fhirUtils;
    @Autowired private MedicationRequestService medicationRequestService;
    
    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return MedicationRequest.class;
    }

    @Read
    public Resource read(@IdParam IdType idType) {
        var medicationRequestEntity = medicationRequestService.getByUuid(idType.getIdPart());
        if(medicationRequestEntity == null) {
            fhirUtils.createOperationOutcome("No MedicationRequest with \"" + idType.getIdPart() + "\" found");
        }
        return medicationRequestEntity.toFhir();
    }    
    
    @Validate
    public MethodOutcome validate(@ResourceParam MedicationRequest medicationRequest, 
            @Validate.Mode ValidationModeEnum mode,
            @Validate.Profile String profile) {
        
        var result = fhirUtils.validateResource(medicationRequest, profile);
        var outcome = new MethodOutcome();
        outcome.setOperationOutcome(result.toOperationOutcome());
        return outcome;       
    }
    
    @Create
    public MethodOutcome create(@ResourceParam MedicationRequest medicationRequest) {
        // validate resource
        var validateResult = fhirUtils.validateResource(medicationRequest, null);
        
        if(!validateResult.isSuccessful()) {
            var outcome = new MethodOutcome();           
            outcome.setResource(validateResult.toOperationOutcome());
            return outcome;
        }
        
        // Save resource          
        var id = UUID.randomUUID().toString();
        medicationRequest.setId(id);      
        var medicationRequestEntity = MedicationRequestEntity.fromFhir(medicationRequest);
        medicationRequestEntity.set_Version(1);
        medicationRequestEntity.set_Active(true);
        medicationRequestEntity = medicationRequestService.save(medicationRequestEntity);
        
        medicationRequest = medicationRequestEntity.toFhir();
        
        var outcome = new MethodOutcome();
        outcome.setCreated(true);
        outcome.setOperationOutcome(fhirUtils.createOperationOutcome(                
                "urn:uuid:" + medicationRequest.getId(),
                "Create succsess",
                IssueSeverity.INFORMATION, 
                IssueType.VALUE
         ));
        
        outcome.setResource(medicationRequest);
        return outcome;
    }
    
    @Delete
    public void delete(@IdParam IdType id) {
        var medicationRequestEntity = medicationRequestService.getByUuid(id.getIdPart());
        
        if(medicationRequestEntity != null) {
            medicationRequestEntity.set_Active(false);
            medicationRequestService.save(medicationRequestEntity);
        }
    }
    
    @Search
    public List<MedicationRequest> search(
            @OptionalParam(name ="patient") ReferenceParam patient,
            @OptionalParam(name ="encounter") ReferenceParam encounter,
            @Count Integer count, @Offset Integer offset) {
        
        
        var lst = medicationRequestService.search(
                patient != null? patient.getValue(): "", 
                encounter != null? encounter.getValue(): "", 
                offset, 
                count);
        
        return DataUtils.transform(lst, MedicationRequestEntity::toFhir);
    }
}
