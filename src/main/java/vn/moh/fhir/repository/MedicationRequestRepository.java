package vn.moh.fhir.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.moh.fhir.model.entity.MedicationRequestEntity;

public interface MedicationRequestRepository extends MongoRepository<MedicationRequestEntity, ObjectId> {

}
