package vn.moh.fhir.model.entity;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.Immunization.ImmunizationStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.hl7.fhir.r4.model.StringType;

import vn.moh.fhir.model.base.AnnotationModel;
import vn.moh.fhir.model.base.CodeableConceptModel;
import vn.moh.fhir.model.base.IdentifierModel;
import vn.moh.fhir.model.base.QuantityModel;
import vn.moh.fhir.model.base.ReferenceModel;
import vn.moh.fhir.utils.DataUtils;
import vn.moh.fhir.utils.DateUtils;

@JsonInclude(Include.NON_NULL)
@Document(collection = "immunization")
@CompoundIndex(def = "{'uuid':1, '_active':1, '_version':1, 'patient.reference':1, 'encounter.reference':1}", name = "index_by_default")
public class ImmunizationEntity {

    public static class ImmunizationPerformer {
        CodeableConceptModel function;
        ReferenceModel actor;
        
        public Immunization.ImmunizationPerformerComponent toFhir() {
            var performer = new Immunization.ImmunizationPerformerComponent();
            if(function != null) {
                performer.setFunction(function.toFhir());
            }
            
            if(actor != null) {
                performer.setActor(actor.toFhir());
            }
            return performer;
        }
        
        public ImmunizationPerformer() {
            
        }
        
        public ImmunizationPerformer(Immunization.ImmunizationPerformerComponent performer) {
            if(performer != null) {
                if(performer.hasFunction()) {
                    this.function = CodeableConceptModel.fromFhir(performer.getFunction());
                }
                if(performer.hasActor()) {
                    this.actor = ReferenceModel.fromFhir(performer.getActor());
                }
            }
        }
        
        public static ImmunizationPerformer fromFhir(Immunization.ImmunizationPerformerComponent performer) {
            if(performer != null) {
                return new ImmunizationPerformer(performer);
            }
            return null;
        }
    }
    
    public static class ImmunizationReaction {
        Date date;
        ReferenceModel detail;
        Boolean reported;
        
        public Immunization.ImmunizationReactionComponent toFhir() {
            var reaction = new Immunization.ImmunizationReactionComponent();
            reaction.setDate(date);
            
            if(detail != null) {
                reaction.setDetail(detail.toFhir());
            }
            
            if(reported != null) {
                reaction.setReported(reported);
            }
            return reaction;
        }
        
        public ImmunizationReaction() {
            
        }
        
        public ImmunizationReaction(Immunization.ImmunizationReactionComponent reaction) {
            if(reaction != null) {
                this.date = reaction.getDate();
                
                if(reaction.hasDetail()) {
                    this.detail = ReferenceModel.fromFhir(reaction.getDetail());
                }
                
                if(reaction.hasReported()) {
                    this.reported = reaction.getReported();
                }
            }
        }
        
        public static ImmunizationReaction fromFhir(Immunization.ImmunizationReactionComponent reaction) {
            if(reaction != null) {
                return new ImmunizationReaction(reaction);
            }
            return null;
        }
    }
    
    public static class ImmunizationProtocolApplied {
        String series;
        ReferenceModel authority;
        List<CodeableConceptModel> targetDisease;
        String doseNumber;
        String seriesDoses;
        
        public Immunization.ImmunizationProtocolAppliedComponent toFhir() {
            var protocol = new Immunization.ImmunizationProtocolAppliedComponent();
            
            protocol.setSeries(series);
            
            if(authority != null) {
                protocol.setAuthority(authority.toFhir());
            }
            
            protocol.setTargetDisease(DataUtils.transform(targetDisease, CodeableConceptModel::toFhir));
            
            if(!StringUtils.isEmpty(doseNumber)) {
                protocol.setDoseNumber(new StringType(doseNumber));
            }
            
            if(!StringUtils.isEmpty(seriesDoses)) {
                protocol.setSeriesDoses(new StringType(seriesDoses));
            }
            
            return protocol;
        }
        
        public ImmunizationProtocolApplied() {
            
        }
        
        public ImmunizationProtocolApplied(Immunization.ImmunizationProtocolAppliedComponent protocol) {
            if(protocol != null) {
                this.series = protocol.getSeries();
                
                if(protocol.hasAuthority()) {
                    this.authority = ReferenceModel.fromFhir(protocol.getAuthority());
                }
                
                this.targetDisease = DataUtils.transform(protocol.getTargetDisease(), CodeableConceptModel::fromFhir);
                
                if(protocol.hasDoseNumberStringType()) {
                    this.doseNumber = protocol.getDoseNumberStringType().getValue();
                    
                }else if(protocol.hasDoseNumberPositiveIntType()) {
                    this.doseNumber = String.valueOf(protocol.getDoseNumberPositiveIntType());
                }
                
                if(protocol.hasSeriesDosesStringType()) {
                    this.seriesDoses = protocol.getSeriesDosesStringType().getValue();
                    
                }else if(protocol.hasSeriesDosesPositiveIntType()) {
                    this.seriesDoses = String.valueOf(protocol.getSeriesDosesPositiveIntType());
                }                
            }
        }
        
        public static ImmunizationProtocolApplied fromFhir(Immunization.ImmunizationProtocolAppliedComponent protocol) {
            if(protocol != null) {
                return new ImmunizationProtocolApplied(protocol);
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
    CodeableConceptModel vaccineCode;
    ReferenceModel patient;
    ReferenceModel encounter;
    String occurrence;
    Date recorded;
    ReferenceModel location;
    ReferenceModel manufacturer;
    String lotNumber;
    Date expirationDate;
    CodeableConceptModel site;
    CodeableConceptModel route;
    QuantityModel doseQuantity;
    List<ImmunizationPerformer> performer;
    List<AnnotationModel> note;
    List<ImmunizationReaction> reaction;
    List<ImmunizationProtocolApplied> protocolApplied;
    
    public Immunization toFhir() {
        var immunization = new Immunization();
        
        immunization.setId(uuid);
        immunization.setIdentifier(DataUtils.transform(identifier, IdentifierModel::toFhir));
        
        if(!StringUtils.isEmpty(status)) {
            immunization.setStatus(ImmunizationStatus.fromCode(status));
        }
        
        if(vaccineCode != null) {
            immunization.setVaccineCode(vaccineCode.toFhir());
        }
        
        if(patient != null) {
            immunization.setPatient(patient.toFhir());
        }
        
        if(encounter != null) {
            immunization.setEncounter(encounter.toFhir());
        }
        
        if(!StringUtils.isEmpty(occurrence)) {
            immunization.setOccurrence(new StringType(occurrence));
        }
        
        immunization.setRecorded(recorded);
        
        if(location != null) {
            immunization.setLocation(location.toFhir());
        }
        
        if(manufacturer != null) {
            immunization.setManufacturer(manufacturer.toFhir());
        }
        
        immunization.setLotNumber(lotNumber);
        
        immunization.setExpirationDate(expirationDate);
        
        if(site != null) {
            immunization.setSite(site.toFhir());
        }
        
        if(route != null) {
            immunization.setRoute(route.toFhir());
        }
        
        if(doseQuantity != null) {
            immunization.setDoseQuantity(doseQuantity.toFhir());
        }
        
        immunization.setPerformer(DataUtils.transform(performer, ImmunizationPerformer::toFhir));
        immunization.setReaction(DataUtils.transform(reaction, ImmunizationReaction::toFhir));
        immunization.setProtocolApplied(DataUtils.transform(protocolApplied, ImmunizationProtocolApplied::toFhir));
        immunization.setNote(DataUtils.transform(note, AnnotationModel::toFhir));
                
        return immunization;
    }
    
    public ImmunizationEntity() {
        
    }
    
    public ImmunizationEntity(Immunization immunization) {
        if(immunization != null) {
            this.uuid = immunization.getId();
            
            if(this.uuid != null && this.uuid.startsWith(ResourceType.Immunization + "/")) {
                this.uuid = this.uuid.replace(ResourceType.Immunization + "/", "");
            }
            
            this.identifier = DataUtils.transform(immunization.getIdentifier(), IdentifierModel::fromFhir);
            
            if(immunization.hasStatus()) {
                this.status = immunization.getStatus().toCode();
            }
            
            if(immunization.hasVaccineCode()) {
                this.vaccineCode = CodeableConceptModel.fromFhir(immunization.getVaccineCode());
            }
            
            if(immunization.hasPatient()) {
                this.patient = ReferenceModel.fromFhir(immunization.getPatient());
            }
            
            if(immunization.hasEncounter()) {
                this.encounter = ReferenceModel.fromFhir(immunization.getEncounter());
            }
            
            if(immunization.hasOccurrenceDateTimeType()) {
                this.occurrence = DateUtils.parseDateToString(immunization.getOccurrenceDateTimeType().getValue(), "yyyy-MM-dd");
                
            }else if(immunization.hasOccurrenceStringType()) {
                this.occurrence = immunization.getOccurrenceStringType().getValue();
            }
            
            this.recorded = immunization.getRecorded();
            
            if(immunization.hasLocation()) {
                this.location = ReferenceModel.fromFhir(immunization.getLocation());
            }
            
            if(immunization.hasManufacturer()) {
                this.manufacturer = ReferenceModel.fromFhir(immunization.getManufacturer());
            }
            
            this.lotNumber = immunization.getLotNumber();
            
            this.expirationDate = immunization.getExpirationDate();
            
            if(immunization.hasSite()) {
                this.site = CodeableConceptModel.fromFhir(immunization.getSite());
            }
            
            if(immunization.hasRoute()) {
                this.route = CodeableConceptModel.fromFhir(immunization.getRoute());
            }
            
            if(immunization.hasDoseQuantity()) {
                this.doseQuantity = QuantityModel.fromFhir(immunization.getDoseQuantity());
            }
            
            
            this.performer = DataUtils.transform(immunization.getPerformer(), ImmunizationPerformer::fromFhir);
            this.reaction = DataUtils.transform(immunization.getReaction(), ImmunizationReaction::fromFhir);
            this.protocolApplied = DataUtils.transform(immunization.getProtocolApplied(), ImmunizationProtocolApplied::fromFhir);
            this.note = DataUtils.transform(immunization.getNote(), AnnotationModel::fromFhir);
        }
    }
    
    public static ImmunizationEntity fromFhir(Immunization immunization) {
        if(immunization != null) {
            return new ImmunizationEntity(immunization);
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
