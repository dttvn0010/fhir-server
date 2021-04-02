package vn.moh.fhir.provider;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Offset;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import vn.moh.fhir.model.entity.EncounterEntity;
import vn.moh.fhir.service.EncounterService;
import vn.moh.fhir.utils.DataUtils;
import vn.moh.fhir.utils.FhirUtils;

@Component
public class EncounterProvider  implements IResourceProvider  {

    @Autowired private EncounterService encounterService;

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Encounter.class;
    }
    
    @Read
    public Resource read(@IdParam IdType idType) {
        var encounterEntity = encounterService.getById(idType.getIdPart());
        if(encounterEntity == null) {
            FhirUtils.createOperationOutcome("No Encounter with \"" + idType.getIdPart() + "\" found");
        }
        return encounterEntity.toFhir();
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
