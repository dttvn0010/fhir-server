package vn.moh.fhir.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import vn.moh.fhir.model.entity.MedicationEntity;
import vn.moh.fhir.repository.MedicationRepository;

@Service
public class MedicationService {

    @Autowired private MedicationRepository medicationRepository;
    @Autowired private MongoTemplate mongoTemplate;

    public MedicationEntity getByUuid(String uuid) {
        var critera =  Criteria.where("uuid").is(uuid).and("_active").is(true);
        return mongoTemplate.findOne(new Query(critera), MedicationEntity.class);
    }
    
    public MedicationEntity save(MedicationEntity medicationEntity) {
        return medicationRepository.save(medicationEntity);
    }
    
    public List<MedicationEntity> search(String name, Integer offset, Integer count) {
        var critera =  Criteria.where("_active").is(true);
                            
        if(!StringUtils.isEmpty(name)) {
            critera.and("code.text").regex(name);
        }
        
        var query = new Query(critera);
        
        if(offset != null) {
            query.skip(offset);
        }
        
        if(count != null) {
            query.limit(count);
        }
        
        return mongoTemplate.find(query, MedicationEntity.class);
    }
}
