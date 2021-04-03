package vn.moh.fhir.model.entity;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.FamilyMemberHistory;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.FamilyMemberHistory.FamilyHistoryStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.hl7.fhir.r4.model.StringType;

import vn.moh.fhir.model.base.AnnotationModel;
import vn.moh.fhir.model.base.CodeableConceptModel;
import vn.moh.fhir.model.base.IdentifierModel;
import vn.moh.fhir.model.base.ReferenceModel;
import vn.moh.fhir.utils.DataUtils;

@JsonInclude(Include.NON_NULL)
@Document(collection = "family_member_history")
@CompoundIndex(def = "{'uuid':1, '_active':1, '_version':1, 'patient.reference':1}", name = "index_by_default")
public class FamilyMemberHistoryEntity {

    public static class FamilyMemberHistoryCondition {
        CodeableConceptModel code;
        CodeableConceptModel outcome;
        Boolean contributedToDeath;
        String onset;
        List<AnnotationModel> note;
        
        public FamilyMemberHistory.FamilyMemberHistoryConditionComponent toFhir() {
            var condition = new FamilyMemberHistory.FamilyMemberHistoryConditionComponent();
            
            if(code != null ) {
                condition.setCode(code.toFhir());
            }
            
            if(outcome != null) {
                condition.setOutcome(outcome.toFhir());
            }
            
            if(contributedToDeath != null) {
                condition.setContributedToDeath(contributedToDeath);
            }
            
            condition.setOnset(new StringType(onset));
            condition.setNote(DataUtils.transform(note, AnnotationModel::toFhir));
            return condition;
        }
        
        public FamilyMemberHistoryCondition() {
            
        }
        
        public FamilyMemberHistoryCondition(FamilyMemberHistory.FamilyMemberHistoryConditionComponent condition) {
            if(condition != null) {
                if(condition.hasCode()) {
                    this.code = CodeableConceptModel.fromFhir(condition.getCode());
                }
                
                if(condition.hasOutcome()) {
                    this.outcome = CodeableConceptModel.fromFhir(condition.getOutcome());
                }
                
                if(condition.hasContributedToDeath()) {
                    this.contributedToDeath = condition.getContributedToDeath();
                }
                
                if(condition.hasOnset()) {
                    this.onset = condition.getOnset().primitiveValue();
                }
                
                this.note = DataUtils.transform(condition.getNote(), AnnotationModel::fromFhir);                
            }
        }
        
        public static FamilyMemberHistoryCondition fromFhir(FamilyMemberHistory.FamilyMemberHistoryConditionComponent condition) {
            if(condition != null) {
                return new FamilyMemberHistoryCondition(condition);
            }
            return null;
        }
    }
    
    @Id public ObjectId id;
    String uuid;
    int _version;
    boolean _active;
        
    List<IdentifierModel> identifier;
    String status;
    ReferenceModel patient;
    Date date;
    String name;
    CodeableConceptModel relationship;
    CodeableConceptModel sex;
    Date bornDate;    
    Date deceasedDate;
    Boolean deceased;
    List<AnnotationModel> note;
    List<FamilyMemberHistoryCondition> condition;
    
    public FamilyMemberHistory toFhir() {
        var fmh = new FamilyMemberHistory();
        
        fmh.setId(uuid);
        fmh.setIdentifier(DataUtils.transform(identifier, IdentifierModel::toFhir));
        
        if(!StringUtils.isEmpty(status)) {
            fmh.setStatus(FamilyHistoryStatus.fromCode(status));
        }
        
        if(patient != null) {
            fmh.setPatient(patient.toFhir());
        }
        
        fmh.setDate(date);
        
        if(relationship != null) {
            fmh.setRelationship(relationship.toFhir());
        }
        
        if(sex != null) {
            fmh.setSex(sex.toFhir());
        }
        
        if(bornDate != null) {
            fmh.setBorn(new DateTimeType(bornDate));
        }
        
        if(deceased != null) {
            fmh.setDeceased(new BooleanType(deceased));
        }
        
        if(deceasedDate != null) {
            fmh.setDeceased(new DateTimeType(deceasedDate));
        }
        
        fmh.setNote(DataUtils.transform(note, AnnotationModel::toFhir));
        fmh.setCondition(DataUtils.transform(condition, FamilyMemberHistoryCondition::toFhir));
    
        return fmh;
    }
    
    public FamilyMemberHistoryEntity() {
        
    }
    
    public FamilyMemberHistoryEntity(FamilyMemberHistory fmh) {
        if(fmh != null) {
            
            this.uuid = fmh.getId();
            
            if(this.uuid != null && this.uuid.startsWith(ResourceType.FamilyMemberHistory + "/")) {
                this.uuid = this.uuid.replace(ResourceType.FamilyMemberHistory + "/", "");
            }
            
            this.identifier = DataUtils.transform(fmh.getIdentifier(), IdentifierModel::fromFhir);
            if(fmh.hasStatus()) {
                this.status = fmh.getStatus().toCode();
            }
            
            if(fmh.hasPatient()) {
                this.patient = ReferenceModel.fromFhir(fmh.getPatient());
            }
            
            this.date = fmh.getDate();
            
            if(fmh.hasRelationship()) {
                this.relationship = CodeableConceptModel.fromFhir(fmh.getRelationship());
            }
            
            if(fmh.hasSex()) {
                this.sex = CodeableConceptModel.fromFhir(fmh.getSex());
            }
            
            if(fmh.hasBornDateType()) {
                this.bornDate = fmh.getBornDateType().getValue();
            }
             
            if(fmh.hasDeceasedDateType()) {
                this.deceasedDate = fmh.getDeceasedDateType().getValue();
                this.deceased = true;
                
            }if(fmh.hasDeceasedBooleanType()) {
                this.deceased = fmh.getDeceasedBooleanType().getValue();
            }
            
            this.note = DataUtils.transform(fmh.getNote(), AnnotationModel::fromFhir);
            this.condition = DataUtils.transform(fmh.getCondition(), FamilyMemberHistoryCondition::fromFhir);
        }        
    }
    
    public static FamilyMemberHistoryEntity fromFhir(FamilyMemberHistory fmh) {
        if(fmh != null) {
            return new FamilyMemberHistoryEntity(fmh);
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
