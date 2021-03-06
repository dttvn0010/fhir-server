package vn.gov.moh.fhir.model.entity;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.hl7.fhir.r4.model.StringType;

import vn.gov.moh.fhir.model.base.AnnotationModel;
import vn.gov.moh.fhir.model.base.CodeableConceptModel;
import vn.gov.moh.fhir.model.base.IdentifierModel;
import vn.gov.moh.fhir.model.base.ReferenceModel;
import vn.gov.moh.fhir.utils.DataUtils;
import vn.gov.moh.fhir.utils.DateUtils;

@JsonInclude(Include.NON_NULL)
@Document(collection = "allergy_intolerance")
@CompoundIndex(def = "{'uuid':1, '_active':1, '_version':1, 'patient.reference':1, 'encounter.reference':1}", name = "index_by_default")
public class AllergyIntoleranceEntity {

    public static class AllergyIntoleranceReaction {
        CodeableConceptModel substance;
        List<CodeableConceptModel> manifestation;
        String description;
        Date onset;
        String severity;
        CodeableConceptModel exposureRoute;
        List<AnnotationModel> note;
        
        public AllergyIntolerance.AllergyIntoleranceReactionComponent toFhir() {
            var reaction = new AllergyIntolerance.AllergyIntoleranceReactionComponent();
            if(substance != null) {
                reaction.setSubstance(substance.toFhir());
            }
            reaction.setManifestation(DataUtils.transform(manifestation, CodeableConceptModel::toFhir));
            reaction.setOnset(onset);
            
            if(!StringUtils.isEmpty(severity)) {
                reaction.setSeverity(AllergyIntolerance.AllergyIntoleranceSeverity.fromCode(severity));
            }
            
            if(exposureRoute != null) {
                reaction.setExposureRoute(exposureRoute.toFhir());
            }
            
            reaction.setNote(DataUtils.transform(note, AnnotationModel::toFhir));
            
            return reaction;
        }
        
        public AllergyIntoleranceReaction() {
            
        }
        
        public AllergyIntoleranceReaction(AllergyIntolerance.AllergyIntoleranceReactionComponent reaction) {
            if(reaction != null) {
                if(reaction.hasSubstance()) {
                    this.substance = CodeableConceptModel.fromFhir(reaction.getSubstance());
                }
                
                this.manifestation = DataUtils.transform(reaction.getManifestation(), CodeableConceptModel::fromFhir);
                this.onset = reaction.getOnset();
                
                if(reaction.hasSeverity()) {
                    this.severity = reaction.getSeverity().toCode();
                }
                
                if(reaction.hasExposureRoute()) {
                    this.exposureRoute = CodeableConceptModel.fromFhir(reaction.getExposureRoute());
                }
                
                this.note = DataUtils.transform(reaction.getNote(), AnnotationModel::fromFhir);
            }
        }
        
        public static AllergyIntoleranceReaction fromFhir(AllergyIntolerance.AllergyIntoleranceReactionComponent reaction) {
            if(reaction != null) {
                return new AllergyIntoleranceReaction(reaction);
            }
            return null;
        }
        
    }
    
    @Id public ObjectId id;
    String uuid;
    int _version;
    boolean _active;
    
    List<IdentifierModel> identifier;
    CodeableConceptModel clinicalStatus;
    CodeableConceptModel verificationStatus;
    String type;
    List<String> category;
    String criticality;
    CodeableConceptModel code;
    ReferenceModel patient;
    ReferenceModel encounter;
    String onset;
    Date recordedDate;
    ReferenceModel recorder;
    ReferenceModel asserter;
    Date lastOccurrence;
    List<AllergyIntoleranceReaction> reaction;
    
    public AllergyIntolerance toFhir() {
        var allergyIntolerance = new AllergyIntolerance();
        
        allergyIntolerance.setId(uuid);
        
        allergyIntolerance.setIdentifier(DataUtils.transform(identifier, IdentifierModel::toFhir));
        
        if(clinicalStatus != null) {
            allergyIntolerance.setClinicalStatus(clinicalStatus.toFhir());
        }
        
        if(!StringUtils.isEmpty(type)) {
            allergyIntolerance.setType(AllergyIntoleranceType.fromCode(type));
        }
        
        if(patient != null) {
            allergyIntolerance.setPatient(patient.toFhir());
        }
        
        if(encounter != null) {
            allergyIntolerance.setEncounter(encounter.toFhir());
        }
        
        if(!StringUtils.isEmpty(onset)) {
            allergyIntolerance.setOnset(new StringType(onset));
        }
        
        allergyIntolerance.setRecordedDate(recordedDate);
        
        if(recorder != null) {
            allergyIntolerance.setRecorder(recorder.toFhir());
        }
        
        if(asserter != null) {
            allergyIntolerance.setAsserter(asserter.toFhir());
        }
        
        allergyIntolerance.setLastOccurrence(lastOccurrence);
        
        allergyIntolerance.setReaction(DataUtils.transform(reaction, AllergyIntoleranceReaction::toFhir));
        
        return allergyIntolerance;
    }
    
    public AllergyIntoleranceEntity() {
        
    }
    
    public AllergyIntoleranceEntity(AllergyIntolerance allergyIntolerance) {
        if(allergyIntolerance != null) {
            this.uuid = allergyIntolerance.getId();
            
            if(this.uuid != null && this.uuid.startsWith(ResourceType.AllergyIntolerance + "/")) {
                this.uuid = this.uuid.replace(ResourceType.AllergyIntolerance + "/", "");
            }
            
            this.identifier = DataUtils.transform(allergyIntolerance.getIdentifier(), IdentifierModel::fromFhir);
            
            if(allergyIntolerance.hasClinicalStatus()) {
                this.clinicalStatus = CodeableConceptModel.fromFhir(allergyIntolerance.getClinicalStatus());
            }
            
            if(allergyIntolerance.hasType()) {
                this.type = allergyIntolerance.getType().toCode();
            }
            
            if(allergyIntolerance.hasPatient()) {
                this.patient = ReferenceModel.fromFhir(allergyIntolerance.getPatient());
            }
            
            if(allergyIntolerance.hasEncounter()) {
                this.encounter = ReferenceModel.fromFhir(allergyIntolerance.getEncounter());
            }
            
            if(allergyIntolerance.hasOnsetStringType()) {
                this.onset = allergyIntolerance.getOnset().primitiveValue();
                
            }else if(allergyIntolerance.hasOnsetDateTimeType()) {
                this.onset = DateUtils.parseDateToString(allergyIntolerance.getOnsetDateTimeType().getValue(), "yyyy-MM-dd");
                
            }else if(allergyIntolerance.hasOnsetPeriod()) {
                var period = allergyIntolerance.getOnsetPeriod();
                String start = DateUtils.parseDateToString(period.getStart(), "yyyy-MM-dd");
                String end = DateUtils.parseDateToString(period.getEnd(), "yyyy-MM-dd");
                this.onset = start + " -> " + end;
            }
            
            this.recordedDate = allergyIntolerance.getRecordedDate();
            
            if(allergyIntolerance.hasReaction()) {
                this.recorder = ReferenceModel.fromFhir(allergyIntolerance.getRecorder());
            }
            
            if(allergyIntolerance.hasAsserter()) {
                this.asserter = ReferenceModel.fromFhir(allergyIntolerance.getAsserter());
            }
            
            this.lastOccurrence = allergyIntolerance.getLastOccurrence();
            
            this.reaction = DataUtils.transform(allergyIntolerance.getReaction(), AllergyIntoleranceReaction::fromFhir);
        
        }
    }
    
    public static AllergyIntoleranceEntity fromFhir(AllergyIntolerance allergyIntolerance) {
        if(allergyIntolerance != null) {
            return new AllergyIntoleranceEntity(allergyIntolerance);
        }
        
        return null;
    }
    
    public int get_Version() {
        return _version;
    }
    
    public void set_Version(int _version) {
        this._version = _version;
    }
    
    public void set_Active(boolean _active) {
        this._active = _active;
    }
}
