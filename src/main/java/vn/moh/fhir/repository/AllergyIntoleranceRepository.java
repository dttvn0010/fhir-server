package vn.moh.fhir.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.moh.fhir.model.entity.AllergyIntoleranceEntity;

public interface AllergyIntoleranceRepository extends MongoRepository<AllergyIntoleranceEntity, ObjectId>{

}
