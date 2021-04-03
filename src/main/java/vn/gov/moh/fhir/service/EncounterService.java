package vn.gov.moh.fhir.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import vn.gov.moh.fhir.model.entity.EncounterEntity;
import vn.gov.moh.fhir.repository.EncounterRepository;

@Service
public class EncounterService {

    @Autowired private EncounterRepository encounterRepository;
    @Autowired private MongoTemplate mongoTemplate;

    public EncounterEntity getByUuid(String uuid) {
        var critera =  Criteria.where("uuid").is(uuid).and("_active").is(true);
        return mongoTemplate.findOne(new Query(critera), EncounterEntity.class);
    }
    
    public EncounterEntity getByUuidAndVersion(String uuid, int _version) {
        var critera =  Criteria.where("uuid").is(uuid).and("_version").is(_version);
        return mongoTemplate.findOne(new Query(critera), EncounterEntity.class);
    }
    
    public EncounterEntity save(EncounterEntity encounterEntity) {
        return encounterRepository.save(encounterEntity);
    }
    
    public List<EncounterEntity> search(String patientId, Integer offset, Integer count) {
        var critera =  Criteria.where("_active").is(true);
                            
        if(!StringUtils.isEmpty(patientId)) {
            critera.and("patient.reference").is(ResourceType.Patient + "/" + patientId);
        }
        
        var query = new Query(critera);
        
        if(offset != null) {
            query.skip(offset);
        }
        
        if(count != null) {
            query.limit(count);
        }
        
        return mongoTemplate.find(query, EncounterEntity.class);                            
    }
}
