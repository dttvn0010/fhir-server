package vn.moh.fhir.model.entity;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.Medication.MedicationStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.moh.fhir.model.base.CodeableConceptModel;
import vn.moh.fhir.model.base.IdentifierModel;
import vn.moh.fhir.model.base.RatioModel;
import vn.moh.fhir.model.base.ReferenceModel;
import vn.moh.fhir.utils.DataUtils;

@JsonInclude(Include.NON_NULL)
@Document(collection = "medication")
@CompoundIndex(def = "{'id':1, '_active':1, '_version':1}", name = "index_by_default")
public class MedicationEntity {

	public static class MedicationBatch {
		Date expirationDate;
		String lotNumber;
		
		public Medication.MedicationBatchComponent toFhir() {
			var batch = new Medication.MedicationBatchComponent();
			batch.setExpirationDate(expirationDate);
			batch.setLotNumber(lotNumber);
			return batch;
		}
		
		public MedicationBatch(Medication.MedicationBatchComponent batch) {
			if(batch != null) {
				this.expirationDate = batch.getExpirationDate();
				this.lotNumber = batch.getLotNumber();
			}
		}
		
		public static MedicationBatch fromFhir(Medication.MedicationBatchComponent batch) {
			if(batch != null) {
				return new MedicationBatch(batch);
			}
			return null;
		}
	}
	
	public static class MedicationIngredient {
		CodeableConceptModel item;
		Boolean isActive;
		RatioModel strength;		
		
		public Medication.MedicationIngredientComponent toFhir() {
			var ingredient = new Medication.MedicationIngredientComponent();
			
			if(item != null) {
				ingredient.setItem(item.toFhir());
			}
			
			if(isActive != null) {
				ingredient.setIsActive(isActive);
			}
			
			if(strength != null) {
				ingredient.setStrength(strength.toFhir());
			}
			
			return ingredient;
		}
		
		public MedicationIngredient(Medication.MedicationIngredientComponent ingredient) {
			if(ingredient != null) {
				if(ingredient.hasItemCodeableConcept()) {
					this.item = CodeableConceptModel.fromFhir(ingredient.getItemCodeableConcept());
				}
				
				if(ingredient.hasIsActive()) {
					this.isActive = ingredient.getIsActive();
				}
				
				if(ingredient.hasStrength()) {
					this.strength = RatioModel.fromFhir(ingredient.getStrength());
				}
			}
		}
		
		public static MedicationIngredient fromFhir(Medication.MedicationIngredientComponent ingredient) {
			if(ingredient != null) {
				return new MedicationIngredient(ingredient);
			}
			return null;
		}
	}
	
	@Id public ObjectId _id;
	String id;
	int _version;
	boolean _active;
	
	List<IdentifierModel> identifier;
	CodeableConceptModel code;
	String status;
	ReferenceModel manufacturer;
	CodeableConceptModel form;
	RatioModel amount;
	MedicationBatch batch;
	List<MedicationIngredient> ingredient;
	
	public Medication toFhir() {
		var medication = new Medication();
		
		medication.setId(id);
		medication.setIdentifier(DataUtils.transform(identifier, IdentifierModel::toFhir));
		
		if(code != null) {
			medication.setCode(code.toFhir());
		}
		
		if(!StringUtils.isEmpty(status)) {
			medication.setStatus(MedicationStatus.fromCode(status));
		}
		
		if(manufacturer != null) {
			medication.setManufacturer(manufacturer.toFhir());
		}
		
		if(form !=  null) {
			medication.setForm(form.toFhir());
		}
		
		if(amount != null) {
			medication.setAmount(amount.toFhir());
		}
		
		if(batch != null) {
			medication.setBatch(batch.toFhir());
		}
		
		medication.setIngredient(DataUtils.transform(ingredient, MedicationIngredient::toFhir));
		
		return medication;
	}
	
	public MedicationEntity(Medication medication) {
		if(medication !=  null) {
			this.id = medication.getId();
			this.identifier = DataUtils.transform(medication.getIdentifier(), IdentifierModel::fromFhir);
			
			if(medication.hasCode()) {
				this.code = CodeableConceptModel.fromFhir(medication.getCode());
			}
			
			if(medication.hasStatus()) {
				this.status = medication.getStatus().toCode();
			}
			
			if(medication.hasManufacturer()) {
				this.manufacturer = ReferenceModel.fromFhir(medication.getManufacturer());
			}
			
			if(medication.hasForm()) {
				this.form = CodeableConceptModel.fromFhir(medication.getForm());
			}
			
			if(medication.hasAmount()) {
				this.amount = RatioModel.fromFhir(medication.getAmount());
			}
			
			if(medication.hasBatch()) {
				this.batch = MedicationBatch.fromFhir(medication.getBatch());
			}
			
			this.ingredient = DataUtils.transform(medication.getIngredient(), MedicationIngredient::fromFhir);
		}
	}
	
	public static MedicationEntity fromFhir(Medication medication) {
		if(medication != null) {
			return new MedicationEntity(medication);
		}
		return null;
	}
}
