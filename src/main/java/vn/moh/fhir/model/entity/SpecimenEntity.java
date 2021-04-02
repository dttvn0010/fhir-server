package vn.moh.fhir.model.entity;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Specimen;
import org.hl7.fhir.r4.model.Specimen.SpecimenStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.moh.fhir.model.base.AnnotationModel;
import vn.moh.fhir.model.base.CodeableConceptModel;
import vn.moh.fhir.model.base.IdentifierModel;
import vn.moh.fhir.model.base.QuantityModel;
import vn.moh.fhir.model.base.ReferenceModel;
import vn.moh.fhir.utils.DataUtils;

@JsonInclude(Include.NON_NULL)
@Document(collection = "specimen")
@CompoundIndex(def = "{'id':1, '_active':1, '_version':1, 'patient.reference':1, 'encounter.reference':1}", name = "index_by_default")
public class SpecimenEntity {
	
	 public static class SpecimenCollection {
		 ReferenceModel collector;
		 Date collectedDate;
		 QuantityModel quantity;
		 CodeableConceptModel method;
		 CodeableConceptModel bodySite;
		 
		 public Specimen.SpecimenCollectionComponent toFhir() {
			 var collection = new Specimen.SpecimenCollectionComponent();
			 
			 if(collector != null) {
				 collection.setCollector(collector.toFhir());
			 }
			 
			 if(collectedDate != null) {
				 collection.setCollected(new DateTimeType(collectedDate));
			 }
			 
			 if(quantity != null) {
				 collection.setQuantity(quantity.toFhir());
			 }
			 
			 if(method != null) {
				 collection.setMethod(method.toFhir());
			 }
			 
			 if(bodySite != null) {
				 collection.setBodySite(bodySite.toFhir());
			 }
			 
			 return collection;
		 }
		 
		 public SpecimenCollection(Specimen.SpecimenCollectionComponent collection) {
			 if(collection != null) {
				 if(collection.hasCollector()) {
					 this.collector = ReferenceModel.fromFhir(collection.getCollector());
				 }
				 
				 if(collection.hasCollectedDateTimeType()) {
					 this.collectedDate = collection.getCollectedDateTimeType().getValue();
					 
				 }else if(collection.hasCollectedPeriod()) {
					 this.collectedDate = collection.getCollectedPeriod().getStart();
				 }
				 
				 if(collection.hasQuantity()) {
					 this.quantity = QuantityModel.fromFhir(collection.getQuantity());
				 }
				 
				 if(collection.hasMethod()) {
					 this.method = CodeableConceptModel.fromFhir(collection.getMethod());
				 }
				 
				 if(collection.hasBodySite()) {
					 this.bodySite = CodeableConceptModel.fromFhir(collection.getBodySite());
				 }
				 
			 }
		 }
		 
		 public static SpecimenCollection fromFhir(Specimen.SpecimenCollectionComponent collection) {
			 if(collection != null) {
				 return new SpecimenCollection(collection);
			 }
			 
			 return null;
		 }
	 }

	 @Id public ObjectId _id;
	 String id;
	 int _version;
	 boolean _active;
		
	 List<IdentifierModel> identifier;
	 String status;
	 CodeableConceptModel type;
	 ReferenceModel patient;
	 Date receivedTime;
	 List<ReferenceModel> request;
	
	 SpecimenCollection collection;
	 List<CodeableConceptModel> condition;
	 List<AnnotationModel> note;
	
	public Specimen toFhir() {
		var specimen = new Specimen();
		
		specimen.setId(id);
		specimen.setIdentifier(DataUtils.transform(identifier, IdentifierModel::toFhir));
		
		if(!StringUtils.isEmpty(status)) {
			specimen.setStatus(SpecimenStatus.fromCode(status));
		}
		
		if(type != null) {
			specimen.setType(type.toFhir());
		}
		
		if(patient != null) {
			specimen.setSubject(patient.toFhir());
		}
		
		specimen.setReceivedTime(receivedTime);
		
		specimen.setRequest(DataUtils.transform(request, ReferenceModel::toFhir));
		
		if(collection != null) {
			specimen.setCollection(collection.toFhir());
		}
		
		specimen.setCondition(DataUtils.transform(condition, CodeableConceptModel::toFhir));
		specimen.setNote(DataUtils.transform(note, AnnotationModel::toFhir));
		
		return specimen;
	}
	
	
	public SpecimenEntity(Specimen specimen) {
		if(specimen != null) {
			this.id = specimen.getId();
			this.identifier = DataUtils.transform(specimen.getIdentifier(), IdentifierModel::fromFhir);
			
			if(specimen.hasStatus()) {
				this.status = specimen.getStatus().toCode();
			}
			
			if(specimen.hasType()) {
				this.type = CodeableConceptModel.fromFhir(specimen.getType());
			}
			
			if(specimen.hasSubject()) {
				this.patient = ReferenceModel.fromFhir(specimen.getSubject());
			}
			
			this.receivedTime = specimen.getReceivedTime();
			
			this.request = DataUtils.transform(specimen.getRequest(), ReferenceModel::fromFhir);
			
			if(specimen.hasCollection()) {
				this.collection = SpecimenCollection.fromFhir(specimen.getCollection());
			}
			
			this.condition = DataUtils.transform(specimen.getCondition(), CodeableConceptModel::fromFhir);
			this.note = DataUtils.transform(specimen.getNote(), AnnotationModel::fromFhir);
		}
	}
	
	public static SpecimenEntity fromFhir(Specimen specimen) {
		if(specimen != null) {
			return new SpecimenEntity(specimen);
		}
		
		return null;
	}
}
