package vn.gov.moh.fhir.provider;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.RelatedPerson;
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
import vn.gov.moh.fhir.model.entity.RelatedPersonEntity;
import vn.gov.moh.fhir.service.RelatedPersonService;
import vn.gov.moh.fhir.utils.DataUtils;
import vn.gov.moh.fhir.utils.FhirHelper;

@Component
public class RelatedPersonProvider implements IResourceProvider {
    
    @Autowired private FhirHelper fhirUtils;
    @Autowired private RelatedPersonService relatedPersonService;

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return RelatedPerson.class;
    }
    
    @Read
    public Resource read(@IdParam IdType idType) {
        var relatedPersonEntity = relatedPersonService.getByUuid(idType.getIdPart());
        if(relatedPersonEntity == null) {
            fhirUtils.createOperationOutcome("No RelatedPerson with \"" + idType.getIdPart() + "\" found");
        }
        return relatedPersonEntity.toFhir();
    }    
    
    
    @Search
    public List<RelatedPerson> search(
            @OptionalParam(name ="patient") ReferenceParam patient,
            @Count Integer count, @Offset Integer offset) {
        
        
        var lst = relatedPersonService.search(
                patient != null? patient.getValue(): "",  
                offset, 
                count);
        
        return DataUtils.transform(lst, RelatedPersonEntity::toFhir);
    }
}
