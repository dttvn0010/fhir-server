package vn.gov.moh.fhir.model.entity;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.gov.moh.fhir.model.base.CodeableConceptModel;
import vn.gov.moh.fhir.model.base.IdentifierModel;
import vn.gov.moh.fhir.model.base.PeriodModel;
import vn.gov.moh.fhir.model.base.ReferenceModel;
import vn.gov.moh.fhir.utils.DataUtils;

@JsonInclude(Include.NON_NULL)
@Document(collection = "diagnostic_report")
@CompoundIndex(def = "{'uuid':1, '_active':1, '_version':1, 'patient.reference':1, 'encounter.reference':1}", name = "index_by_default")
public class DiagnosticReportEntity {

    public static class DiagnosticReportMedia {
        String comment;
        ReferenceModel link;
        
        public DiagnosticReport.DiagnosticReportMediaComponent toFhir() {
            var media = new DiagnosticReport.DiagnosticReportMediaComponent();
            media.setComment(comment);
            if(link != null) {
                media.setLink(link.toFhir());
            }
            return media;
        }
                
        public DiagnosticReportMedia() {
            
        }
        
        public DiagnosticReportMedia(DiagnosticReport.DiagnosticReportMediaComponent media) {
            if(media != null) {
                this.comment = media.getComment();
                if(media.hasLink()) {
                    this.link = ReferenceModel.fromFhir(media.getLink());
                }
            }
        }
        
        public static DiagnosticReportMedia fromFhir(DiagnosticReport.DiagnosticReportMediaComponent media) {
            if(media != null) {
                return new DiagnosticReportMedia(media);
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
    String status;
    List<CodeableConceptModel> category;
    CodeableConceptModel code;
    ReferenceModel patient;
    ReferenceModel encounter;
    PeriodModel effectivePeriod;
    Date issued;
    List<ReferenceModel> performer;
    List<ReferenceModel> resultsInterpreter;
    List<ReferenceModel> specimen;
    List<ReferenceModel> result;
    List<ReferenceModel> imagingStudy;
    String conclusion;
    List<DiagnosticReportMedia> media;
    List<CodeableConceptModel> conclusionCode;
    
    public DiagnosticReport toFhir() {
        var diagnosticReport = new DiagnosticReport();
        
        diagnosticReport.setId(uuid);        
        diagnosticReport.setIdentifier(DataUtils.transform(identifier, IdentifierModel::toFhir));
        diagnosticReport.setBasedOn(DataUtils.transform(basedOn, ReferenceModel::toFhir));
        
        if(!StringUtils.isEmpty(status)) {
            diagnosticReport.setStatus(DiagnosticReportStatus.fromCode(status));
        }
        
        diagnosticReport.setCategory(DataUtils.transform(category, CodeableConceptModel::toFhir));
        
        if(code != null) {
            diagnosticReport.setCode(code.toFhir());
        }
        
        if(patient != null) {
            diagnosticReport.setSubject(patient.toFhir());
        }
        
        if(encounter != null) {
            diagnosticReport.setEncounter(encounter.toFhir());
        }
        
        if(effectivePeriod != null) {
            diagnosticReport.setEffective(effectivePeriod.toFhir());
        }
        
        diagnosticReport.setIssued(issued);
        
        diagnosticReport.setPerformer(DataUtils.transform(performer, ReferenceModel::toFhir));
        diagnosticReport.setResultsInterpreter(DataUtils.transform(resultsInterpreter, ReferenceModel::toFhir));
        diagnosticReport.setSpecimen(DataUtils.transform(specimen, ReferenceModel::toFhir));
        diagnosticReport.setResult(DataUtils.transform(result, ReferenceModel::toFhir));
        diagnosticReport.setImagingStudy(DataUtils.transform(imagingStudy, ReferenceModel::toFhir));
        
        diagnosticReport.setConclusion(conclusion);
        
        diagnosticReport.setMedia(DataUtils.transform(media, DiagnosticReportMedia::toFhir));
        diagnosticReport.setConclusionCode(DataUtils.transform(conclusionCode, CodeableConceptModel::toFhir));
        
        return diagnosticReport;
    }
    
    public DiagnosticReportEntity() {
        
    }
    
    public DiagnosticReportEntity(DiagnosticReport diagnosticReport) {
        if(diagnosticReport != null) {
            this.uuid = diagnosticReport.getId();
            
            if(this.uuid != null && this.uuid.startsWith(ResourceType.DiagnosticReport + "/")) {
                this.uuid = this.uuid.replace(ResourceType.DiagnosticReport + "/", "");
            }
            
            this.identifier = DataUtils.transform(diagnosticReport.getIdentifier(), IdentifierModel::fromFhir);
            this.basedOn = DataUtils.transform(diagnosticReport.getBasedOn(), ReferenceModel::fromFhir);
            
            if(diagnosticReport.hasStatus()) {
                this.status = diagnosticReport.getStatus().toCode();
            }
            
            this.category = DataUtils.transform(diagnosticReport.getCategory(), CodeableConceptModel::fromFhir);
            
            if(diagnosticReport.hasCode()) {
                this.code = CodeableConceptModel.fromFhir(diagnosticReport.getCode());
            }
            
            if(diagnosticReport.hasSubject()) {
                this.patient = ReferenceModel.fromFhir(diagnosticReport.getSubject());
            }
            
            if(diagnosticReport.hasEncounter()) {
                this.encounter = ReferenceModel.fromFhir(diagnosticReport.getEncounter());
            }
            
            if(diagnosticReport.hasEffectivePeriod()) {
                this.effectivePeriod = PeriodModel.fromFhir(diagnosticReport.getEffectivePeriod());
            }else if(diagnosticReport.hasEffectiveDateTimeType()) {
                var start = diagnosticReport.getEffectiveDateTimeType().getValue();
                this.effectivePeriod = new PeriodModel(start, null);                
            }
            
            this.issued = diagnosticReport.getIssued();
            
            
            this.performer = DataUtils.transform(diagnosticReport.getPerformer(), ReferenceModel::fromFhir);
            this.resultsInterpreter = DataUtils.transform(diagnosticReport.getResultsInterpreter(), ReferenceModel::fromFhir);
            this.specimen = DataUtils.transform(diagnosticReport.getSpecimen(), ReferenceModel::fromFhir);
            this.result = DataUtils.transform(diagnosticReport.getResult(), ReferenceModel::fromFhir);
            this.imagingStudy = DataUtils.transform(diagnosticReport.getImagingStudy(), ReferenceModel::fromFhir);
            
            this.conclusion = diagnosticReport.getConclusion();
            
            this.media = DataUtils.transform(diagnosticReport.getMedia(), DiagnosticReportMedia::fromFhir);
            this.conclusionCode = DataUtils.transform(diagnosticReport.getConclusionCode(), CodeableConceptModel::fromFhir);
            
        }
    }
    
    public static DiagnosticReportEntity fromFhir(DiagnosticReport diagnosticReport) {
        if(diagnosticReport != null) {
            return new DiagnosticReportEntity(diagnosticReport);
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
