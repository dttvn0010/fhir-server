package vn.moh.fhir.model.entity;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Encounter.EncounterLocationStatus;
import org.hl7.fhir.r4.model.Encounter.EncounterStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.moh.fhir.model.base.CodeableConceptModel;
import vn.moh.fhir.model.base.CodingModel;
import vn.moh.fhir.model.base.IdentifierModel;
import vn.moh.fhir.model.base.PeriodModel;
import vn.moh.fhir.model.base.ReferenceModel;
import vn.moh.fhir.utils.DataUtils;

@JsonInclude(Include.NON_NULL)
@Document(collection = "encounter")
@CompoundIndex(def = "{'id':1, '_active':1, '_version':1, 'patient.reference':1}", name = "index_by_default")
public class EncounterEntity {

    public static class EncounterLocation {
        ReferenceModel location;
        String status;
        PeriodModel period;
        
        public Encounter.EncounterLocationComponent toFhir() {
            var encLoc = new Encounter.EncounterLocationComponent();
            if(location != null) {
                encLoc.setLocation(location.toFhir());
            }
            if(!StringUtils.isEmpty(status)) {
                encLoc.setStatus(EncounterLocationStatus.fromCode(status));
            }
            if(period != null) {
                encLoc.setPeriod(period.toFhir());
            }
            return encLoc;
        }
        
        public EncounterLocation(Encounter.EncounterLocationComponent encLoc) {
            if(encLoc != null) {
                if(encLoc.hasLocation()) {
                    this.location = ReferenceModel.fromFhir(encLoc.getLocation());
                }
                if(encLoc.hasStatus()) {
                    this.status = encLoc.getStatus().toCode();
                }
                if(encLoc.hasPeriod()) {
                    this.period = PeriodModel.fromFhir(encLoc.getPeriod());
                }
            }
        }
        
        public static EncounterLocation fromFhir(Encounter.EncounterLocationComponent encLoc) {
            if(encLoc != null) {
                return new EncounterLocation(encLoc);
            }
            return null;
        }
    }
    
    public static class EncounterParticipant {
        List<CodeableConceptModel> type;
        PeriodModel period;
        ReferenceModel individual;
        
        public Encounter.EncounterParticipantComponent toFhir() {
            var encParticipant = new Encounter.EncounterParticipantComponent();
            encParticipant.setType(DataUtils.transform(type, CodeableConceptModel::toFhir));
            if(period != null) {
                encParticipant.setPeriod(period.toFhir());
            }
            if(individual != null) {
                encParticipant.setIndividual(individual.toFhir());
            }
            return encParticipant;
        }
        
        public EncounterParticipant(Encounter.EncounterParticipantComponent encParticipant) {
            if(encParticipant != null) {
                this.type = DataUtils.transform(encParticipant.getType(), CodeableConceptModel::fromFhir);
                if(encParticipant.hasPeriod()) {
                    this.period = PeriodModel.fromFhir(encParticipant.getPeriod());
                }
                if(encParticipant.hasIndividual()) {
                    this.individual = ReferenceModel.fromFhir(encParticipant.getIndividual());
                }
            }
        }
        
        public static EncounterParticipant fromFhir(Encounter.EncounterParticipantComponent encParticipant) {
            if(encParticipant != null) {
                return new EncounterParticipant(encParticipant);
            }
            return null;
        }
    }
    
    public static class EncounterDiagnosis {
        ReferenceModel condition;
        CodeableConceptModel use;
        Integer rank;
        
        public Encounter.DiagnosisComponent toFhir() {
            var encDiagnosis = new Encounter.DiagnosisComponent();
            if(condition != null) {
                encDiagnosis.setCondition(condition.toFhir());
            }
            if(use != null) {
                encDiagnosis.setUse(use.toFhir());
            }
            if(rank != null) {
                encDiagnosis.setRank(rank);
            }
            return encDiagnosis;
        }
        
        public EncounterDiagnosis(Encounter.DiagnosisComponent encDiagnosis) {
            if(encDiagnosis != null) {
                if(encDiagnosis.hasCondition()) {
                    this.condition = ReferenceModel.fromFhir(encDiagnosis.getCondition());
                }
                if(encDiagnosis.hasUse()) {
                    this.use = CodeableConceptModel.fromFhir(encDiagnosis.getUse());
                }
                if(encDiagnosis.hasRank()) {
                    this.rank = encDiagnosis.getRank();
                }
            }
        }
        
        public static  EncounterDiagnosis fromFhir(Encounter.DiagnosisComponent encDiagnosis) {
            if(encDiagnosis != null) {
                return new EncounterDiagnosis(encDiagnosis);
            }
            return null;
        }
    }
    
    public static class Hospitalization {
        ReferenceModel origin;
        CodeableConceptModel admitSource;
        ReferenceModel destination;
        CodeableConceptModel dischargeDisposition;
        
        public Encounter.EncounterHospitalizationComponent toFhir() {
            var hospitalization = new Encounter.EncounterHospitalizationComponent();
            
            if(origin != null) {
                hospitalization.setOrigin(origin.toFhir());
            }
            
            if(admitSource != null) {
                hospitalization.setAdmitSource(admitSource.toFhir());
            }
            
            if(destination != null) {
                hospitalization.setDestination(destination.toFhir());
            }
            
            if(dischargeDisposition != null) {
                hospitalization.setDischargeDisposition(dischargeDisposition.toFhir());
            }
            
            return hospitalization;
        }
        
        public Hospitalization(Encounter.EncounterHospitalizationComponent hospitalization) {
            if(hospitalization != null) {
                if(hospitalization.hasOrigin()) {
                    this.origin = ReferenceModel.fromFhir(hospitalization.getOrigin());
                }
                
                if(hospitalization.hasAdmitSource()) {
                    this.admitSource = CodeableConceptModel.fromFhir(hospitalization.getAdmitSource());
                }
                
                if(hospitalization.hasDestination()) {
                    this.destination = ReferenceModel.fromFhir(hospitalization.getDestination());
                }
                
                if(hospitalization.hasDischargeDisposition()) {
                    this.dischargeDisposition = CodeableConceptModel.fromFhir(hospitalization.getDischargeDisposition());
                }
            }
        }
        
        public static Hospitalization fromFhir(Encounter.EncounterHospitalizationComponent hospitalization) {
            if(hospitalization != null) {
                return new Hospitalization(hospitalization);
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
    CodingModel class_;
    List<CodeableConceptModel> type;
    CodeableConceptModel serviceType;
    ReferenceModel patient;
    List<EncounterParticipant> participant;
    List<EncounterLocation> location;
    List<EncounterDiagnosis> diagnosis;
    ReferenceModel serviceProvider;
    Hospitalization hospitalization;
    PeriodModel period;    
    
    public Encounter toFhir() {
        var encounter = new Encounter();
        
        encounter.setId(id);
        encounter.setIdentifier(DataUtils.transform(identifier, IdentifierModel::toFhir));
        if(!StringUtils.isEmpty(status)) {
            encounter.setStatus(EncounterStatus.fromCode(status));
        }
        encounter.setType(DataUtils.transform(type, CodeableConceptModel::toFhir));
        
        if(serviceType != null) {
            encounter.setServiceType(serviceType.toFhir());
        }
        
        if(patient != null) {
            encounter.setSubject(patient.toFhir());
        }
        
        encounter.setParticipant(DataUtils.transform(participant, EncounterParticipant::toFhir));
        encounter.setLocation(DataUtils.transform(location, EncounterLocation::toFhir));
        encounter.setDiagnosis(DataUtils.transform(diagnosis, EncounterDiagnosis::toFhir));
        
        if(serviceProvider != null) {
            encounter.setServiceProvider(serviceProvider.toFhir());
        }
        
        if(hospitalization != null) {
            encounter.setHospitalization(hospitalization.toFhir());
        }
        
        if(period != null) {
            encounter.setPeriod(period.toFhir());
        }
        
        return encounter;
    }
    
    public EncounterEntity(Encounter encounter) {
        if(encounter != null) {
            this.id = encounter.getId();
            this.identifier = DataUtils.transform(encounter.getIdentifier(), IdentifierModel::fromFhir);
            
            if(encounter.hasStatus()) {
                this.status = encounter.getStatus().toCode();
            }
            
            this.type = DataUtils.transform(encounter.getType(), CodeableConceptModel::fromFhir);
            
            if(encounter.hasServiceType()) {
                this.serviceType = CodeableConceptModel.fromFhir(encounter.getServiceType());
            }
            
            if(encounter.hasSubject()) {
                this.patient = ReferenceModel.fromFhir(encounter.getSubject());
            }
            
            this.participant = DataUtils.transform(encounter.getParticipant(), EncounterParticipant::fromFhir);
            this.location = DataUtils.transform(encounter.getLocation(), EncounterLocation::fromFhir);
            this.diagnosis = DataUtils.transform(encounter.getDiagnosis(), EncounterDiagnosis::fromFhir);
            
            
            if(encounter.hasServiceProvider()) {
                this.serviceProvider = ReferenceModel.fromFhir(encounter.getServiceProvider());
            }
            
            if(encounter.hasHospitalization()) {
                this.hospitalization = Hospitalization.fromFhir(encounter.getHospitalization());                
            }
            
            if(encounter.hasPeriod()) {
                this.period = PeriodModel.fromFhir(encounter.getPeriod());
            }
        }
    }
    
    public static EncounterEntity fromFhir(Encounter encounter) {
        if(encounter != null) {
            return new EncounterEntity(encounter);
        }
        return null;
    }
 }
