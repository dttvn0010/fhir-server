package vn.moh.fhir.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.moh.fhir.model.entity.PatientEntity;

public interface PatientRepository extends MongoRepository<PatientEntity, ObjectId> {

}
