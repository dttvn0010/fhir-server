package vn.gov.moh.fhir.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import vn.gov.moh.fhir.model.entity.MedicationRequestEntity;
import vn.gov.moh.fhir.repository.MedicationRequestRepository;

@Service
public class MedicationRequestService {

    @Autowired private MedicationRequestRepository medicationRequestRepository;
    @Autowired private MongoTemplate mongoTemplate;

    public MedicationRequestEntity getByUuid(String uuid) {
        var critera =  Criteria.where("uuid").is(uuid).and("_active").is(true);
        return mongoTemplate.findOne(new Query(critera), MedicationRequestEntity.class);
    }
    
    public MedicationRequestEntity save(MedicationRequestEntity medicationRequestEntity) {
        return medicationRequestRepository.save(medicationRequestEntity);
    }
    
    public List<MedicationRequestEntity> search(String patientId, String encounterId, Integer offset, Integer count) {
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
        
        return mongoTemplate.find(query, MedicationRequestEntity.class);                            
    }
}