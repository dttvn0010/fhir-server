package vn.moh.fhir.model.entity;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.hl7.fhir.r4.model.RelatedPerson;

import vn.moh.fhir.model.base.AddressModel;
import vn.moh.fhir.model.base.AttachmentModel;
import vn.moh.fhir.model.base.CodeableConceptModel;
import vn.moh.fhir.model.base.ContactPointModel;
import vn.moh.fhir.model.base.HumanNameModel;
import vn.moh.fhir.model.base.IdentifierModel;
import vn.moh.fhir.model.base.PeriodModel;
import vn.moh.fhir.model.base.ReferenceModel;
import vn.moh.fhir.utils.DataUtils;

@JsonInclude(Include.NON_NULL)
@Document(collection = "related_person")
@CompoundIndex(def = "{'id':1, '_active':1, '_version':1, 'patient.reference':1}", name = "index_by_default")
public class RelatedPersonEntity {
	
	@Id public ObjectId _id;
	String id;
	int _version;
	boolean _active;
	
	List<IdentifierModel> identifier;
	Boolean active;	
	ReferenceModel patient;
	List<CodeableConceptModel> relationship;
	HumanNameModel name;
	
	String gender;
	Date birthDate;
	
	List<ContactPointModel> telecom;
	List<AddressModel> address;
	
	List<AttachmentModel> photo;
	PeriodModel period;
	
	public RelatedPerson toFhir() {
		var relatedPerson = new RelatedPerson();
		
		relatedPerson.setId(id);
		relatedPerson.setIdentifier(DataUtils.transform(identifier, IdentifierModel::toFhir));
		
		if(active !=  null) {
			relatedPerson.setActive(active);
		}
		
		if(patient != null) {
			relatedPerson.setPatient(patient.toFhir());
		}
		
		relatedPerson.setRelationship(DataUtils.transform(relationship, CodeableConceptModel::toFhir));
		
		if(name != null) {
			relatedPerson.addName(name.toFhir());
		}
		
		if(!StringUtils.isEmpty(gender)) {
			relatedPerson.setGender(AdministrativeGender.fromCode(gender));
		}
		
		relatedPerson.setBirthDate(birthDate);
		
		relatedPerson.setTelecom(DataUtils.transform(telecom, ContactPointModel::toFhir));
		relatedPerson.setAddress(DataUtils.transform(address, AddressModel::toFhir));
		relatedPerson.setPhoto(DataUtils.transform(photo, AttachmentModel::toFhir));
		
		if(period != null) {
			relatedPerson.setPeriod(period.toFhir());
		}
		
		return relatedPerson;
	}
	
	public RelatedPersonEntity(RelatedPerson relatedPerson) {
		if(relatedPerson != null) {
			this.id = relatedPerson.getId();
			
			this.identifier = DataUtils.transform(relatedPerson.getIdentifier(), IdentifierModel::fromFhir);
			
			if(relatedPerson.hasActive()) {
				this.active = relatedPerson.getActive();
			}
			
			if(relatedPerson.hasPatient()) {
				this.patient = ReferenceModel.fromFhir(relatedPerson.getPatient());
			}
			
			this.relationship = DataUtils.transform(relatedPerson.getRelationship(), CodeableConceptModel::fromFhir);
			
			if(relatedPerson.hasName()) {
				this.name = HumanNameModel.fromFhir(relatedPerson.getNameFirstRep());
			}
			
			if(relatedPerson.hasGender()) {
				this.gender = relatedPerson.getGender().toCode();
			}
			
			this.birthDate = relatedPerson.getBirthDate();
			
			this.telecom = DataUtils.transform(relatedPerson.getTelecom(), ContactPointModel::fromFhir);
			this.address = DataUtils.transform(relatedPerson.getAddress(), AddressModel::fromFhir);
			this.photo = DataUtils.transform(relatedPerson.getPhoto(), AttachmentModel::fromFhir);
			
			if(relatedPerson.hasPeriod()) {
				this.period = PeriodModel.fromFhir(relatedPerson.getPeriod());
			}
		}
	}
	
	public RelatedPersonEntity fromFhir(RelatedPerson relatedPerson) {
		if(relatedPerson != null) {
			return new RelatedPersonEntity(relatedPerson);
		}
		return null;
	}
}
