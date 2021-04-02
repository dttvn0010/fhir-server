package vn.moh.fhir.provider;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Condition;
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
import vn.moh.fhir.model.entity.ConditionEntity;
import vn.moh.fhir.service.ConditionService;
import vn.moh.fhir.utils.DataUtils;
import vn.moh.fhir.utils.FhirUtils;

@Component
public class ConditionProvider implements IResourceProvider{

    @Autowired private ConditionService conditionService;

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Condition.class;
    }
    
    @Read
    public Resource read(@IdParam IdType idType) {
        var conditionEntity = conditionService.getById(idType.getIdPart());
        if(conditionEntity == null) {
            FhirUtils.createOperationOutcome("No Condition with \"" + idType.getIdPart() + "\" found");
        }
        return conditionEntity.toFhir();
    }    
    
    
    @Search
    public List<Condition> search(
            @OptionalParam(name ="patient") ReferenceParam patient,
            @OptionalParam(name ="encounter") ReferenceParam encounter,
            @Count Integer count, @Offset Integer offset) {
        
        
        var lst = conditionService.search(
                patient != null? patient.getValue(): "", 
                encounter != null? encounter.getValue(): "", 
                offset, 
                count);
        
        return DataUtils.transform(lst, ConditionEntity::toFhir);
    }    
}
