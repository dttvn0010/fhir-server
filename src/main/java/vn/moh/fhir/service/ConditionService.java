package vn.moh.fhir.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import vn.moh.fhir.model.entity.ConditionEntity;
import vn.moh.fhir.repository.ConditionRepository;

@Service
public class ConditionService {

    @Autowired private ConditionRepository conditionRepository;
    @Autowired private MongoTemplate mongoTemplate;

    public ConditionEntity getByUuid(String uuid) {
        var critera =  Criteria.where("uuid").is(uuid).and("_active").is(true);
        return mongoTemplate.findOne(new Query(critera), ConditionEntity.class);
    }
    
    public ConditionEntity save(ConditionEntity conditionEntity) {
        return conditionRepository.save(conditionEntity);
    }
    
    public List<ConditionEntity> search(String patientId, String encounterId, Integer offset, Integer count) {
        var critera =  Criteria.where("_active").is(true);
                            
        if(!StringUtils.isEmpty(patientId)) {
            critera.and("patient.reference").is(ResourceType.Patient + "/" + patientId);
        }
        
        if(!StringUtils.isEmpty(encounterId)) {
            critera.and("encounter.reference").is(ResourceType.Encounter + "/" + encounterId);
        }
        
        var query = new Query(critera);
        
        if(offset != null) {
            query.skip(offset);
        }
        
        if(count != null) {
            query.limit(count);
        }
        
        return mongoTemplate.find(query, ConditionEntity.class);                            
    }
}
