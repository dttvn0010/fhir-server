package vn.gov.moh.fhir.provider;

import java.util.List;
import java.util.UUID;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.Specimen;
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
import vn.gov.moh.fhir.model.entity.SpecimenEntity;
import vn.gov.moh.fhir.service.SpecimenService;
import vn.gov.moh.fhir.utils.DataUtils;
import vn.gov.moh.fhir.utils.FhirHelper;

@Component
public class SpecimenProvider implements IResourceProvider {
    
    @Autowired private FhirHelper fhirUtils;    
    @Autowired private SpecimenService specimenService;
    
    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Specimen.class;
    }

    @Read
    public Resource read(@IdParam IdType idType) {
        var specimenEntity = specimenService.getByUuid(idType.getIdPart());
        if(specimenEntity == null) {
            fhirUtils.createOperationOutcome("No Specimen with \"" + idType.getIdPart() + "\" found");
        }
        return specimenEntity.toFhir();
    }    
    
    @Validate
    public MethodOutcome validate(@ResourceParam Specimen specimen, 
            @Validate.Mode ValidationModeEnum mode,
            @Validate.Profile String profile) {
        
        var result = fhirUtils.validateResource(specimen, profile);
        var outcome = new MethodOutcome();
        outcome.setOperationOutcome(result.toOperationOutcome());
        return outcome;       
    }
    
    @Create
    public MethodOutcome create(@ResourceParam Specimen specimen) {
        // validate resource
        var validateResult = fhirUtils.validateResource(specimen, null);
        
        if(!validateResult.isSuccessful()) {
            var outcome = new MethodOutcome();           
            outcome.setResource(validateResult.toOperationOutcome());
            return outcome;
        }
        
        // Save resource        
        var id = UUID.randomUUID().toString();
        specimen.setId(id);      
        var specimenEntity = SpecimenEntity.fromFhir(specimen);
        specimenEntity.set_Version(1);
        specimenEntity.set_Active(true);
        specimenEntity = specimenService.save(specimenEntity);
        
        specimen = specimenEntity.toFhir();
        
        var outcome = new MethodOutcome();
        outcome.setCreated(true);
        outcome.setOperationOutcome(fhirUtils.createOperationOutcome(                
                "urn:uuid:" + specimen.getId(),
                "Create succsess",
                IssueSeverity.INFORMATION, 
                IssueType.VALUE
         ));
        
        outcome.setResource(specimen);
        return outcome;
    }
    
    @Delete
    public void delete(@IdParam IdType id) {
        var specimenEntity = specimenService.getByUuid(id.getIdPart());
        
        if(specimenEntity != null) {
            specimenEntity.set_Active(false);
            specimenService.save(specimenEntity);
        }
    }
    
    @Search
    public List<Specimen> search(
            @OptionalParam(name ="patient") ReferenceParam patient,
            @Count Integer count, @Offset Integer offset) {
        
        
        var lst = specimenService.search(
                patient != null? patient.getValue(): "", 
                offset, 
                count);
        
        return DataUtils.transform(lst, SpecimenEntity::toFhir);
    }
}
