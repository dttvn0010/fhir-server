package vn.gov.moh.fhir.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.gov.moh.fhir.model.entity.ObservationEntity;

public interface ObservationRepository extends MongoRepository<ObservationEntity, ObjectId> {

}
