package vn.gov.moh.fhir.provider;

import java.util.List;
import java.util.UUID;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Medication;
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
import ca.uhn.fhir.rest.annotation.Validate;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.ValidationModeEnum;
import ca.uhn.fhir.rest.server.IResourceProvider;
import vn.gov.moh.fhir.model.entity.MedicationEntity;
import vn.gov.moh.fhir.service.MedicationService;
import vn.gov.moh.fhir.utils.DataUtils;
import vn.gov.moh.fhir.utils.FhirHelper;

@Component
public class MedicationProvider  implements IResourceProvider {

    @Autowired private FhirHelper fhirUtils;
    @Autowired private MedicationService medicationService;
    
    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Medication.class;
    }
    
    @Read
    public Resource read(@IdParam IdType idType) {
        var medicationEntity = medicationService.getByUuid(idType.getIdPart());
        if(medicationEntity == null) {
            fhirUtils.createOperationOutcome("No Medication with \"" + idType.getIdPart() + "\" found");
        }
        return medicationEntity.toFhir();
    }    
    
    @Validate
    public MethodOutcome validate(@ResourceParam Medication medication, 
            @Validate.Mode ValidationModeEnum mode,
            @Validate.Profile String profile) {
        
        var result = fhirUtils.validateResource(medication, profile);
        var outcome = new MethodOutcome();
        outcome.setOperationOutcome(result.toOperationOutcome());
        return outcome;       
    }
    
    @Create
    public MethodOutcome create(@ResourceParam Medication medication) {
        // validate resource
        var validateResult = fhirUtils.validateResource(medication, null);
        
        if(!validateResult.isSuccessful()) {
            var outcome = new MethodOutcome();           
            outcome.setResource(validateResult.toOperationOutcome());
            return outcome;
        }
        
        // Save resource        
        var id = UUID.randomUUID().toString();
        medication.setId(id);      
        var medicationEntity = MedicationEntity.fromFhir(medication);
        medicationEntity.set_Version(1);
        medicationEntity.set_Active(true);
        medicationEntity = medicationService.save(medicationEntity);
        
        medication = medicationEntity.toFhir();
        
        var outcome = new MethodOutcome();
        outcome.setCreated(true);
        outcome.setOperationOutcome(fhirUtils.createOperationOutcome(                
                "urn:uuid:" + medication.getId(),
                "Create succsess",
                IssueSeverity.INFORMATION, 
                IssueType.VALUE
         ));
        
        outcome.setResource(medication);
        return outcome;
    }
    
    @Delete
    public void delete(@IdParam IdType id) {
        var medicationEntity = medicationService.getByUuid(id.getIdPart());
        
        if(medicationEntity != null) {
            medicationEntity.set_Active(false);
            medicationService.save(medicationEntity);
        }
    }
    
    @Search
    public List<Medication> search(@OptionalParam(name ="name") StringType name,                            
            @Count Integer count, @Offset Integer offset) {
        
        var lst = medicationService.search(name != null? name.getValue(): "", offset, count);
        return DataUtils.transform(lst, MedicationEntity::toFhir);
    }

}
