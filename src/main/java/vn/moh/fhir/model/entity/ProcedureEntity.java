package vn.moh.fhir.model.entity;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.Procedure.ProcedureStatus;
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
@Document(collection = "procedure")
@CompoundIndex(def = "{'uuid':1, '_active':1, '_version':1, 'patient.reference':1, 'encounter.reference':1}", name = "index_by_default")
public class ProcedureEntity {
    
    public static class ProcedurePerformer {
        ReferenceModel actor;
        ReferenceModel onBehalfOf;
        CodeableConceptModel function;
        
        public Procedure.ProcedurePerformerComponent toFhir() {
            var procedurePerformer = new Procedure.ProcedurePerformerComponent();
            if(actor != null) {
                procedurePerformer.setActor(actor.toFhir());
            }
            
            if(onBehalfOf != null) {
                procedurePerformer.setOnBehalfOf(onBehalfOf.toFhir());
            }
            
            if(function != null) {
                procedurePerformer.setFunction(function.toFhir());
            }
            
            return procedurePerformer;
        }
        
        public ProcedurePerformer() {
            
        }
        
        public ProcedurePerformer(Procedure.ProcedurePerformerComponent procedurePerformer) {
            if(procedurePerformer != null) {
                if(procedurePerformer.hasActor()) {
                    this.actor = ReferenceModel.fromFhir(procedurePerformer.getActor());
                }
                
                if(procedurePerformer.hasOnBehalfOf()) {
                    this.onBehalfOf = ReferenceModel.fromFhir(procedurePerformer.getOnBehalfOf());
                }
                
                if(procedurePerformer.hasFunction()) {
                    this.function = CodeableConceptModel.fromFhir(procedurePerformer.getFunction());
                }
            }
        }
        
        public static ProcedurePerformer fromFhir(Procedure.ProcedurePerformerComponent procedurePerformer) {
            if(procedurePerformer != null) {
                return new ProcedurePerformer(procedurePerformer);
            }
            return null;
        }
    }

    @Id public ObjectId id;
    String uuid;
    int _version;
    boolean _active;
    
    List<IdentifierModel> identifier;
    List<ReferenceModel> basedOn;
    List<ReferenceModel> partOf;
    String status;
    
    ReferenceModel patient;
    ReferenceModel encounter;
    
    CodeableConceptModel category;
    CodeableConceptModel code;
    
    Date performedDate;
    ReferenceModel recorder;
    ReferenceModel asserter;
    
    List<ProcedurePerformer> performer;
    ReferenceModel location;
    
    List<CodeableConceptModel> bodySite;
    CodeableConceptModel outcome;
    List<ReferenceModel> report;
    List<CodeableConceptModel> complication;
    List<ReferenceModel> complicationDetail;
    List<CodeableConceptModel> followUp;
    List<AnnotationModel> note;
    
    public Procedure toFhir() {
        var procedure = new Procedure();
        
        procedure.setId(uuid);
        procedure.setIdentifier(DataUtils.transform(identifier, IdentifierModel::toFhir));
        procedure.setBasedOn(DataUtils.transform(basedOn, ReferenceModel::toFhir));
        procedure.setPartOf(DataUtils.transform(partOf, ReferenceModel::toFhir));
        
        if(!StringUtils.isEmpty(status)) {
            procedure.setStatus(ProcedureStatus.fromCode(status));
        }
        
        if(patient != null) {
            procedure.setSubject(patient.toFhir());
        }
        
        if(encounter != null) {
            procedure.setEncounter(encounter.toFhir());
        }
        
        if(category != null) {
            procedure.setCategory(category.toFhir());
        }
        
        if(code != null) {
            procedure.setCode(code.toFhir());
        }
        
        if(performedDate !=  null) {
            procedure.setPerformed(new DateTimeType(performedDate));
        }
        
        if(recorder != null) {
            procedure.setRecorder(recorder.toFhir());
        }
        
        if(asserter != null) {
            procedure.setAsserter(asserter.toFhir());
        }
        
        procedure.setPerformer(DataUtils.transform(performer, ProcedurePerformer::toFhir));
        
        if(location != null) {
            procedure.setLocation(location.toFhir());
        }
        
        procedure.setBodySite(DataUtils.transform(bodySite, CodeableConceptModel::toFhir));
        
        if(outcome != null) {
            procedure.setOutcome(outcome.toFhir());
        }
        
        procedure.setReport(DataUtils.transform(report, ReferenceModel::toFhir));
        procedure.setComplication(DataUtils.transform(complication, CodeableConceptModel::toFhir));
        procedure.setComplicationDetail(DataUtils.transform(complicationDetail, ReferenceModel::toFhir));
        procedure.setFollowUp(DataUtils.transform(followUp, CodeableConceptModel::toFhir));
        procedure.setNote(DataUtils.transform(note, AnnotationModel::toFhir));
        
        return procedure;
    }
    
    public ProcedureEntity() {
        
    }
    
    public ProcedureEntity(Procedure procedure) {
        if(procedure != null) {
            this.uuid = procedure.getId();
            
            if(this.uuid != null && this.uuid.startsWith(ResourceType.Procedure + "/")) {
                this.uuid = this.uuid.replace(ResourceType.Procedure + "/", "");
            }
            
            
            this.identifier = DataUtils.transform(procedure.getIdentifier(), IdentifierModel::fromFhir);
            this.basedOn = DataUtils.transform(procedure.getBasedOn(), ReferenceModel::fromFhir);
            this.partOf =  DataUtils.transform(procedure.getPartOf(), ReferenceModel::fromFhir);
            
            if(procedure.hasStatus()) {
                this.status = procedure.getStatus().toCode();
            }
            
            if(procedure.hasSubject()) {
                this.patient = ReferenceModel.fromFhir(procedure.getSubject());
            }
            
            if(procedure.hasEncounter()) {
                this.encounter = ReferenceModel.fromFhir(procedure.getEncounter());
            }
            
            if(procedure.hasCategory()) {
                this.category = CodeableConceptModel.fromFhir(procedure.getCategory());
            }
            
            if(procedure.hasCode()) {
                this.code = CodeableConceptModel.fromFhir(procedure.getCode());
            }
            
            if(procedure.hasPerformedDateTimeType()) {
                this.performedDate = procedure.getPerformedDateTimeType().getValue();
                
            }else if(procedure.hasPerformedPeriod()) {
                this.performedDate = procedure.getPerformedPeriod().getStart();
            }
            
            if(procedure.hasRecorder()) {
                this.recorder = ReferenceModel.fromFhir(procedure.getRecorder());
            }
            
            if(procedure.hasAsserter()) {
                this.asserter = ReferenceModel.fromFhir(procedure.getAsserter());
            }
            
            this.performer = DataUtils.transform(procedure.getPerformer(), ProcedurePerformer::fromFhir);
             
            if(procedure.hasLocation()) {
                this.location = ReferenceModel.fromFhir(procedure.getLocation());
            }
            
            this.bodySite = DataUtils.transform(procedure.getBodySite(), CodeableConceptModel::fromFhir);
            
            if(procedure.hasOutcome()) {
                this.outcome = CodeableConceptModel.fromFhir(procedure.getOutcome());
            }
            
            this.report = DataUtils.transform(procedure.getReport(), ReferenceModel::fromFhir);
            this.complication = DataUtils.transform(procedure.getComplication(), CodeableConceptModel::fromFhir);
            this.complicationDetail = DataUtils.transform(procedure.getComplicationDetail(), ReferenceModel::fromFhir);
            this.followUp = DataUtils.transform(procedure.getFollowUp(), CodeableConceptModel::fromFhir);
            this.note = DataUtils.transform(procedure.getNote(), AnnotationModel::fromFhir);
        }
    }
    
    public static ProcedureEntity fromFhir(Procedure procedure) {
        if(procedure != null) {
            return new ProcedureEntity(procedure);
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
