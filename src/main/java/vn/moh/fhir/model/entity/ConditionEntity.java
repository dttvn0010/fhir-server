package vn.moh.fhir.model.entity;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.moh.fhir.model.base.AnnotationModel;
import vn.moh.fhir.model.base.CodeableConceptModel;
import vn.moh.fhir.model.base.IdentifierModel;
import vn.moh.fhir.model.base.ReferenceModel;
import vn.moh.fhir.utils.DataUtils;

@JsonInclude(Include.NON_NULL)
@Document(collection = "condition")
@CompoundIndex(def = "{'uuid':1, '_active':1, '_version':1, 'patient.reference':1, 'encounter.reference':1}", name = "index_by_default")
public class ConditionEntity {

    public static class ConditionStage {
        CodeableConceptModel summary;
        List<ReferenceModel> assessment;
        CodeableConceptModel type;
        
        public Condition.ConditionStageComponent toFhir() {
            var stage = new Condition.ConditionStageComponent ();
            if(summary != null) {
                stage.setSummary(summary.toFhir());
            }
            
            stage.setAssessment(DataUtils.transform(assessment, ReferenceModel::toFhir));
            
            if(type != null) {
                stage.setType(type.toFhir());
            }
            
            return stage;
        }
        
        public ConditionStage() {
            
        }
        
        public ConditionStage(Condition.ConditionStageComponent stage) {
            if(stage != null) {
                if(stage.hasSummary()) {
                    this.summary = CodeableConceptModel.fromFhir(stage.getSummary());
                }
                
                this.assessment = DataUtils.transform(stage.getAssessment(), ReferenceModel::fromFhir);
                
                if(stage.hasType()) {
                    this.type = CodeableConceptModel.fromFhir(stage.getType());
                }
            }
        }
        
        public static ConditionStage fromFhir(Condition.ConditionStageComponent stage) {
            if(stage != null) {
                return new ConditionStage(stage);
            }
            return null;
        }
    }
    
    public static class ConditionEvidence {
        List<CodeableConceptModel> code;
        List<ReferenceModel> detail;
        
        public Condition.ConditionEvidenceComponent toFhir() {
            var evidence = new Condition.ConditionEvidenceComponent();
            evidence.setCode(DataUtils.transform(code, CodeableConceptModel::toFhir));
            evidence.setDetail(DataUtils.transform(detail, ReferenceModel::toFhir));
            return evidence;
        }
        
        public ConditionEvidence() {
            
        }
        
        public ConditionEvidence(Condition.ConditionEvidenceComponent evidence) {
            if(evidence != null) {
                this.code = DataUtils.transform(evidence.getCode(), CodeableConceptModel::fromFhir);
                this.detail = DataUtils.transform(evidence.getDetail(), ReferenceModel::fromFhir);
            }
        }
        
        public static ConditionEvidence fromFhir(Condition.ConditionEvidenceComponent evidence) {
            if(evidence != null) {
                return new ConditionEvidence(evidence);
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
    List<CodeableConceptModel> category;
    CodeableConceptModel severity;
    CodeableConceptModel code;
    List<CodeableConceptModel> bodySite;
    ReferenceModel patient;
    ReferenceModel encounter;
    String onset;
    Date recordedDate;
    ReferenceModel recorder;
    ReferenceModel asserter;
    List<ConditionStage> state;
    List<ConditionEvidence> evidence;
    List<AnnotationModel> note;
    
    public Condition toFhir() {
        var condition = new Condition();
        
        condition.setId(uuid);
        
        condition.setIdentifier(DataUtils.transform(identifier, IdentifierModel::toFhir));
        
        if(clinicalStatus != null) {
            condition.setClinicalStatus(clinicalStatus.toFhir());
        }
        
        if(verificationStatus != null) {
            condition.setVerificationStatus(verificationStatus.toFhir());
        }
        
        condition.setCategory(DataUtils.transform(category, CodeableConceptModel::toFhir));
        
        if(severity != null) {
            condition.setSeverity(severity.toFhir());
        }
        
        if(code != null) {
            condition.setCode(code.toFhir());
        }
        
        condition.setBodySite(DataUtils.transform(bodySite, CodeableConceptModel::toFhir));
        
        if(patient != null) {
            condition.setSubject(patient.toFhir());
        }
        
        if(encounter != null) {
            condition.setEncounter(encounter.toFhir());
        }
        
        if(!StringUtils.isEmpty(onset)) {
            condition.setOnset(new StringType(onset));
        }
        
        condition.setRecordedDate(recordedDate);
        
        if(recorder != null) {
            condition.setRecorder(recorder.toFhir());
        }
        
        if(asserter != null) {
            condition.setAsserter(asserter.toFhir());
        }
        
        condition.setStage(DataUtils.transform(state, ConditionStage::toFhir));
        condition.setEvidence(DataUtils.transform(evidence, ConditionEvidence::toFhir));
        condition.setNote(DataUtils.transform(note, AnnotationModel::toFhir));
        
        return condition;
    }
    
    public ConditionEntity() {
        
    }
    
    public ConditionEntity(Condition condition) {
        if(condition != null) {
            this.uuid = condition.getId();
            
            if(this.uuid != null && this.uuid.startsWith(ResourceType.Condition + "/")) {
                this.uuid = this.uuid.replace(ResourceType.Condition + "/", "");
            }
            
            this.identifier = DataUtils.transform(condition.getIdentifier(), IdentifierModel::fromFhir);
            
            if(condition.hasClinicalStatus()) {
                this.clinicalStatus = CodeableConceptModel.fromFhir(condition.getClinicalStatus());
            }
            
            if(condition.hasVerificationStatus()) {
                this.verificationStatus = CodeableConceptModel.fromFhir(condition.getVerificationStatus());
            }
            
            this.category = DataUtils.transform(condition.getCategory(), CodeableConceptModel::fromFhir);
            
            if(condition.hasSeverity()) {
                this.severity = CodeableConceptModel.fromFhir(condition.getSeverity());
            }
            
            if(condition.hasCode()) {
                this.code = CodeableConceptModel.fromFhir(condition.getCode());
            }
            
            this.bodySite = DataUtils.transform(condition.getBodySite(), CodeableConceptModel::fromFhir);
            
            if(condition.hasSubject()) {
                this.patient = ReferenceModel.fromFhir(condition.getSubject());                        
            }
            
            if(condition.hasEncounter()) {
                this.encounter = ReferenceModel.fromFhir(condition.getEncounter());
            }
            
            if(condition.hasOnsetStringType()) {
                this.onset = condition.getOnset().primitiveValue();
            }
            
            this.recordedDate = condition.getRecordedDate();
            
            if(condition.hasRecorder()) {
                this.recorder = ReferenceModel.fromFhir(condition.getRecorder());
            }
            
            if(condition.hasAsserter()) {
                this.asserter = ReferenceModel.fromFhir(condition.getAsserter());
            }
            
            this.state = DataUtils.transform(condition.getStage(), ConditionStage::fromFhir);
            this.evidence = DataUtils.transform(condition.getEvidence(), ConditionEvidence::fromFhir);
            this.note = DataUtils.transform(condition.getNote(), AnnotationModel::fromFhir);            
        }
    }
    
    public static ConditionEntity fromFhir(Condition condition) {
        if(condition != null) {
            return new ConditionEntity(condition);
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
