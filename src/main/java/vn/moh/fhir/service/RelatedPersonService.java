package vn.moh.fhir.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import vn.moh.fhir.model.entity.RelatedPersonEntity;

@Service
public class RelatedPersonService {

	@Autowired private MongoTemplate mongoTemplate;

	public RelatedPersonEntity getById(String id) {
		var critera =  Criteria.where("id").is(id).and("_active").is(true);
		return mongoTemplate.findOne(new Query(critera), RelatedPersonEntity.class);
	}
	
	public List<RelatedPersonEntity> search(String patientId, Integer offset, Integer count) {
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
		
		return mongoTemplate.find(query, RelatedPersonEntity.class);							
	}
}
