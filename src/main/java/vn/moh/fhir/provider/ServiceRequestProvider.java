package vn.moh.fhir.provider;

import java.util.List;
import java.util.UUID;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ServiceRequest;
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
import vn.moh.fhir.model.entity.ServiceRequestEntity;
import vn.moh.fhir.service.ServiceRequestService;
import vn.moh.fhir.utils.DataUtils;
import vn.moh.fhir.utils.FhirHelper;

@Component
public class ServiceRequestProvider implements IResourceProvider {

    @Autowired private FhirHelper fhirUtils;
    @Autowired private ServiceRequestService serviceRequestService;
    
    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return ServiceRequest.class;
    }

    @Read
    public Resource read(@IdParam IdType idType) {
        var serviceRequestEntity = serviceRequestService.getByUuid(idType.getIdPart());
        if(serviceRequestEntity == null) {
            fhirUtils.createOperationOutcome("No ServiceRequest with \"" + idType.getIdPart() + "\" found");
        }
        return serviceRequestEntity.toFhir();
    }    
    
    @Validate
    public MethodOutcome validate(@ResourceParam ServiceRequest serviceRequest, 
            @Validate.Mode ValidationModeEnum mode,
            @Validate.Profile String profile) {
        
        var result = fhirUtils.validateResource(serviceRequest, profile);
        var outcome = new MethodOutcome();
        outcome.setOperationOutcome(result.toOperationOutcome());
        return outcome;       
    }
        
    @Create
    public MethodOutcome create(@ResourceParam ServiceRequest serviceRequest) {
        // validate resource
        var validateResult = fhirUtils.validateResource(serviceRequest, null);
        
        if(!validateResult.isSuccessful()) {
            var outcome = new MethodOutcome();           
            outcome.setResource(validateResult.toOperationOutcome());
            return outcome;
        }
        
        // Save resource
        
        var id = UUID.randomUUID().toString();
        serviceRequest.setId(id);      
        var serviceRequestEntity = ServiceRequestEntity.fromFhir(serviceRequest);
        serviceRequestEntity.set_Version(1);
        serviceRequestEntity.set_Active(true);
        serviceRequestEntity = serviceRequestService.save(serviceRequestEntity);
        
        serviceRequest = serviceRequestEntity.toFhir();
        
        var outcome = new MethodOutcome();
        outcome.setCreated(true);
        outcome.setOperationOutcome(fhirUtils.createOperationOutcome(                
                "urn:uuid:" + serviceRequest.getId(),
                "Create succsess",
                IssueSeverity.INFORMATION, 
                IssueType.VALUE
         ));
        
        outcome.setResource(serviceRequest);
        return outcome;
    }
    
    @Delete
    public void delete(@IdParam IdType id) {
        var serviceRequestEntity = serviceRequestService.getByUuid(id.getIdPart());
        
        if(serviceRequestEntity != null) {
            serviceRequestEntity.set_Active(false);
            serviceRequestService.save(serviceRequestEntity);
        }
    }
        
    @Search
    public List<ServiceRequest> search(
            @OptionalParam(name ="patient") ReferenceParam patient,
            @OptionalParam(name ="encounter") ReferenceParam encounter,
            @Count Integer count, @Offset Integer offset) {
        
        
        var lst = serviceRequestService.search(
                patient != null? patient.getValue(): "", 
                encounter != null? encounter.getValue(): "", 
                offset, 
                count);
        
        return DataUtils.transform(lst, ServiceRequestEntity::toFhir);
    }
}
