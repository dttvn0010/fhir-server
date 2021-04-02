package vn.moh.fhir.provider;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Offset;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.Search;
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
        var medicationEntity = medicationService.getById(idType.getIdPart());
        if(medicationEntity == null) {
            FhirUtils.createOperationOutcome("No Medication with \"" + idType.getIdPart() + "\" found");
        }
        return medicationEntity.toFhir();
    }    
    
    @Search
    public List<Medication> search(@OptionalParam(name ="name") StringType name,                            
            @Count Integer count, @Offset Integer offset) {
        
        var lst = medicationService.search(name != null? name.getValue(): "", offset, count);
        return DataUtils.transform(lst, MedicationEntity::toFhir);
    }

}
