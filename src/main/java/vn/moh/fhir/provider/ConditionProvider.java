package vn.moh.fhir.provider;

import java.util.List;
import java.util.UUID;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Resource;
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
        var conditionEntity = conditionService.getByUuid(idType.getIdPart());
        if(conditionEntity == null) {
            FhirUtils.createOperationOutcome("No Condition with \"" + idType.getIdPart() + "\" found");
        }
        return conditionEntity.toFhir();
    }    
    
    @Create
    public MethodOutcome create(@ResourceParam Condition condition) {
        var id = UUID.randomUUID().toString();
        condition.setId(id);      
        var patientEntity = ConditionEntity.fromFhir(condition);
        patientEntity.set_Version(1);
        patientEntity.set_Active(true);
        patientEntity = conditionService.save(patientEntity);
        
        condition = patientEntity.toFhir();
        
        var outcome = new MethodOutcome();
        outcome.setCreated(true);
        outcome.setOperationOutcome(FhirUtils.createOperationOutcome(                
                "urn:uuid:" + condition.getId(),
                "Create succsess",
                IssueSeverity.INFORMATION, 
                IssueType.VALUE
         ));
        
        outcome.setResource(condition);
        return outcome;
    }
    
    @Delete
    public void delete(@IdParam IdType id) {
        var conditionEntity = conditionService.getByUuid(id.getIdPart());
        
        if(conditionEntity != null) {
            conditionEntity.set_Active(false);
            conditionService.save(conditionEntity);
        }
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
