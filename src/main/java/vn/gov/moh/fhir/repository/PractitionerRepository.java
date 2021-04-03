package vn.gov.moh.fhir.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.gov.moh.fhir.model.entity.PractitionerEntity;

public interface PractitionerRepository extends MongoRepository<PractitionerEntity, ObjectId>{

}
