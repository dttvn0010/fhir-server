package vn.gov.moh.fhir.provider;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.AllergyIntolerance;
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
import vn.gov.moh.fhir.model.entity.AllergyIntoleranceEntity;
import vn.gov.moh.fhir.service.AllergyIntoleranceService;
import vn.gov.moh.fhir.utils.DataUtils;
import vn.gov.moh.fhir.utils.FhirHelper;

@Component
public class AllergyIntoleranceProvider implements IResourceProvider{
    
    @Autowired private FhirHelper fhirUtils;
    @Autowired private AllergyIntoleranceService allergyIntoleranceService;

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return AllergyIntolerance.class;
    }
    
    @Read
    public Resource read(@IdParam IdType idType) {
        var allergyIntoleranceEntity = allergyIntoleranceService.getByUuid(idType.getIdPart());
        if(allergyIntoleranceEntity == null) {
            fhirUtils.createOperationOutcome("No AllergyIntolerance with \"" + idType.getIdPart() + "\" found");
        }
        return allergyIntoleranceEntity.toFhir();
    }    
        
    @Search
    public List<AllergyIntolerance> search(
            @OptionalParam(name ="patient") ReferenceParam patient,
            @OptionalParam(name ="encounter") ReferenceParam encounter,
            @Count Integer count, @Offset Integer offset) {
        
        
        var lst = allergyIntoleranceService.search(
                patient != null? patient.getValue(): "", 
                encounter != null? encounter.getValue(): "", 
                offset, 
                count);
        
        return DataUtils.transform(lst, AllergyIntoleranceEntity::toFhir);
    }
}
