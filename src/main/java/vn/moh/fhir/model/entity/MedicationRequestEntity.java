package vn.moh.fhir.model.entity;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.moh.fhir.model.base.AnnotationModel;
import vn.moh.fhir.model.base.CodeableConceptModel;
import vn.moh.fhir.model.base.DosageModel;
import vn.moh.fhir.model.base.IdentifierModel;
import vn.moh.fhir.model.base.ReferenceModel;
import vn.moh.fhir.utils.DataUtils;

@JsonInclude(Include.NON_NULL)
@Document(collection = "medication_request")
@CompoundIndex(def = "{'id':1, '_active':1, '_version':1, 'patient.reference':1, 'encounter.reference':1}", name = "index_by_default")
public class MedicationRequestEntity {

	@Id public ObjectId _id;
	String id;
	int _version;
	boolean _active;
	
	List<IdentifierModel> identifier;
	ReferenceModel patient;
	ReferenceModel encounter;
	IdentifierModel groupIdentifier;
	List<ReferenceModel> insurance;
	String status;
	CodeableConceptModel medicationCode;
	ReferenceModel medicationReference;
	Date authoredOn;
	ReferenceModel requester;
	List<AnnotationModel> note;
	List<DosageModel> dosageInstruction;
	
	public MedicationRequest toFhir() {
		var medicationRequest = new MedicationRequest();
		
		medicationRequest.setId(id);
		medicationRequest.setIdentifier(DataUtils.transform(identifier, IdentifierModel::toFhir));
		
		if(patient != null) {
			medicationRequest.setSubject(patient.toFhir());
		}
		
		if(encounter != null) {
			medicationRequest.setEncounter(encounter.toFhir());
		}
		
		if(groupIdentifier != null) {
			medicationRequest.setGroupIdentifier(groupIdentifier.toFhir());
		}
		
		medicationRequest.setInsurance(DataUtils.transform(insurance, ReferenceModel::toFhir));
		
		if(!StringUtils.isEmpty(status)) {
			medicationRequest.setStatus(MedicationRequestStatus.fromCode(status));
		}
		
		if(medicationReference != null) {
			medicationRequest.setMedication(medicationReference.toFhir());
		}
		
		if(medicationCode != null) {
			medicationRequest.setMedication(medicationCode.toFhir());
		}
		
		medicationRequest.setAuthoredOn(authoredOn);
		
		if(requester != null) {
			medicationRequest.setRequester(requester.toFhir());
		}
		
		medicationRequest.setNote(DataUtils.transform(note, AnnotationModel::toFhir));
		
		medicationRequest.setDosageInstruction(DataUtils.transform(dosageInstruction, DosageModel::toFhir));
		
		return medicationRequest;
	}
	
	public MedicationRequestEntity(MedicationRequest medicationRequest) {
		if(medicationRequest != null) {
			this.id = medicationRequest.getId();
			this.identifier = DataUtils.transform(medicationRequest.getIdentifier(), IdentifierModel::fromFhir);
			
			if(medicationRequest.hasSubject()) {
				this.patient = ReferenceModel.fromFhir(medicationRequest.getSubject());
			}
			
			if(medicationRequest.hasEncounter()) {
				this.encounter = ReferenceModel.fromFhir(medicationRequest.getEncounter());
			}
			
			if(medicationRequest.hasGroupIdentifier()) {
				this.groupIdentifier = IdentifierModel.fromFhir(medicationRequest.getGroupIdentifier());
			}
			
			this.insurance = DataUtils.transform(medicationRequest.getInsurance(), ReferenceModel::fromFhir);
			
			if(medicationRequest.hasStatus()) {
				this.status = medicationRequest.getStatus().toCode();
			}
			
			if(medicationRequest.hasMedicationReference()) {
				this.medicationReference = ReferenceModel.fromFhir(medicationRequest.getMedicationReference());
			}
			
			if(medicationRequest.hasMedicationCodeableConcept()) {
				this.medicationCode = CodeableConceptModel.fromFhir(medicationRequest.getMedicationCodeableConcept());
			}
			
			this.authoredOn = medicationRequest.getAuthoredOn();
			
			if(medicationRequest.hasRequester()) {
				this.requester = ReferenceModel.fromFhir(medicationRequest.getRequester());
			}
			
			this.note = DataUtils.transform(medicationRequest.getNote(), AnnotationModel::fromFhir);
			this.dosageInstruction = DataUtils.transform(medicationRequest.getDosageInstruction(), DosageModel::fromFhir);			
			
		}
	}
	
	public static MedicationRequestEntity fromFhir(MedicationRequest medicationRequest) {
		if(medicationRequest != null) {
			return new MedicationRequestEntity(medicationRequest);
		}
		return null;
	}
}

