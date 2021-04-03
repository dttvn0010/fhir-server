package vn.moh.fhir.provider;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.FamilyMemberHistory;
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
import vn.moh.fhir.model.entity.FamilyMemberHistoryEntity;
import vn.moh.fhir.service.FamilyMemberHistoryService;
import vn.moh.fhir.utils.DataUtils;
import vn.moh.fhir.utils.FhirUtils;

@Component
public class FamilyMemberHistoryProvider  implements IResourceProvider  {

    @Autowired private FamilyMemberHistoryService familyMemberHistoryService;

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return FamilyMemberHistory.class;
    }
    
    @Read
    public Resource read(@IdParam IdType idType) {
        var familiMemberHistoryEntity = familyMemberHistoryService.getByUuid(idType.getIdPart());
        if(familiMemberHistoryEntity == null) {
            FhirUtils.createOperationOutcome("No FamilyMemberHistory with \"" + idType.getIdPart() + "\" found");
        }
        return familiMemberHistoryEntity.toFhir();
    }    
    
    
    @Search
    public List<FamilyMemberHistory> search(
            @OptionalParam(name ="patient") ReferenceParam patient,
            @Count Integer count, @Offset Integer offset) {
        
        
        var lst = familyMemberHistoryService.search(
                patient != null? patient.getValue(): "",  
                offset, 
                count);
        
        return DataUtils.transform(lst, FamilyMemberHistoryEntity::toFhir);
    }
}
