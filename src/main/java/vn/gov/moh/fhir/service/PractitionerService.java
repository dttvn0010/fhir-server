package vn.gov.moh.fhir.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import vn.gov.moh.fhir.model.entity.PractitionerEntity;

@Service
public class PractitionerService {

    @Autowired private MongoTemplate mongoTemplate;

    public PractitionerEntity getByUuid(String uuid) {
        var critera =  Criteria.where("uuid").is(uuid).and("_active").is(true);
        return mongoTemplate.findOne(new Query(critera), PractitionerEntity.class);
    }
    
    public List<PractitionerEntity> search(String name, Integer offset, Integer count) {
        var critera =  Criteria.where("_active").is(true);
                            
        if(!StringUtils.isEmpty(name)) {
            critera.andOperator(
                new Criteria().orOperator(
                    Criteria.where("name.text").regex(name),
                    Criteria.where("name.given").regex(name),
                    Criteria.where("name.last").regex(name)
                )
            );
        }
        
        var query = new Query(critera);
        
        if(offset != null) {
            query.skip(offset);
        }
        
        if(count != null) {
            query.limit(count);
        }
        
        return mongoTemplate.find(query, PractitionerEntity.class);
    }
}
