package vn.moh.fhir.provider;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Practitioner;
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

import vn.moh.fhir.model.entity.PractitionerEntity;
import vn.moh.fhir.service.PractitionerService;
import vn.moh.fhir.utils.DataUtils;
import vn.moh.fhir.utils.FhirUtils;

@Component
public class PractitionerProvider implements IResourceProvider {

    @Autowired private PractitionerService practitionerService;
    
    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Practitioner.class;
    }
    
    @Read
    public Resource read(@IdParam IdType idType) {
        var practitionerEntity = practitionerService.getById(idType.getIdPart());
        if(practitionerEntity == null) {
            FhirUtils.createOperationOutcome("No Practitioner with \"" + idType.getIdPart() + "\" found");
        }
        return practitionerEntity.toFhir();
    }    
    
    @Search
    public List<Practitioner> search(@OptionalParam(name ="name") StringType name,                            
            @Count Integer count, @Offset Integer offset) {
        
        var lst = practitionerService.search(name != null? name.getValue(): "", offset, count);
        return DataUtils.transform(lst, PractitionerEntity::toFhir);
    }
}
