package vn.moh.fhir.provider;

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
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;
import vn.moh.fhir.model.entity.MedicationEntity;
import vn.moh.fhir.service.MedicationService;
import vn.moh.fhir.utils.DataUtils;
import vn.moh.fhir.utils.FhirUtils;

@Component
public class MedicationProvider  implements IResourceProvider {

    @Autowired private MedicationService medicationService;
    
    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Medication.class;
    }
    
    @Read
    public Resource read(@IdParam IdType idType) {
        var medicationEntity = medicationService.getByUuid(idType.getIdPart());
        if(medicationEntity == null) {
            FhirUtils.createOperationOutcome("No Medication with \"" + idType.getIdPart() + "\" found");
        }
        return medicationEntity.toFhir();
    }    
    
    @Create
    public MethodOutcome create(@ResourceParam Medication medication) {
        var id = UUID.randomUUID().toString();
        medication.setId(id);      
        var medicationEntity = MedicationEntity.fromFhir(medication);
        medicationEntity.set_Version(1);
        medicationEntity.set_Active(true);
        medicationEntity = medicationService.save(medicationEntity);
        
        medication = medicationEntity.toFhir();
        
        var outcome = new MethodOutcome();
        outcome.setCreated(true);
        outcome.setOperationOutcome(FhirUtils.createOperationOutcome(                
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
