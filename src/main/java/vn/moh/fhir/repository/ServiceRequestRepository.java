package vn.moh.fhir.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.moh.fhir.model.entity.ServiceRequestEntity;

public interface ServiceRequestRepository extends MongoRepository<ServiceRequestEntity, ObjectId>{

}
