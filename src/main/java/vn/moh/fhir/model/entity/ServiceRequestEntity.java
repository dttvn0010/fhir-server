package vn.moh.fhir.model.entity;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.ServiceRequest.ServiceRequestIntent;
import org.hl7.fhir.r4.model.ServiceRequest.ServiceRequestPriority;
import org.hl7.fhir.r4.model.ServiceRequest.ServiceRequestStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.moh.fhir.model.base.AnnotationModel;
import vn.moh.fhir.model.base.CodeableConceptModel;
import vn.moh.fhir.model.base.IdentifierModel;
import vn.moh.fhir.model.base.QuantityModel;
import vn.moh.fhir.model.base.ReferenceModel;
import vn.moh.fhir.utils.DataUtils;

@JsonInclude(Include.NON_NULL)
@Document(collection = "service_request")
@CompoundIndex(def = "{'uuid':1, '_active':1, '_version':1, 'patient.reference':1, 'encounter.reference':1}", name = "index_by_default")
public class ServiceRequestEntity {

    @Id public ObjectId id;
    String uuid;
    int _version;
    boolean _active;
    
    List<IdentifierModel> identifier;
    List<ReferenceModel> basedOn;
    String status;
    String intent;
    String priority;
    List<CodeableConceptModel> category;
    CodeableConceptModel code;
    List<CodeableConceptModel> orderDetail;
    QuantityModel quantity;
    ReferenceModel patient;
    ReferenceModel encounter;
    Date occurrenceDate;
    Date authoredOn;
    ReferenceModel requester;
    CodeableConceptModel performerType;
    List<ReferenceModel> performer;
    ReferenceModel location;
    List<ReferenceModel> insurance;
    List<ReferenceModel> specimen;
    List<AnnotationModel> note;
    String patientInstruction;    
    
    public ServiceRequest toFhir() {
        var serviceRequest = new ServiceRequest();
        
        serviceRequest.setId(uuid);
        serviceRequest.setIdentifier(DataUtils.transform(identifier, IdentifierModel::toFhir));
        serviceRequest.setBasedOn(DataUtils.transform(basedOn, ReferenceModel::toFhir));
        
        if(!StringUtils.isEmpty(status)) {
            serviceRequest.setStatus(ServiceRequestStatus.fromCode(status));
        }
        
        if(!StringUtils.isEmpty(intent)) {
            serviceRequest.setIntent(ServiceRequestIntent.fromCode(intent));
        }
        
        if(!StringUtils.isEmpty(priority)) {
            serviceRequest.setPriority(ServiceRequestPriority.fromCode(priority));
        }
        
        serviceRequest.setCategory(DataUtils.transform(category, CodeableConceptModel::toFhir));
        
        if(code != null) {
            serviceRequest.setCode(code.toFhir());
        }
        
        serviceRequest.setOrderDetail(DataUtils.transform(orderDetail, CodeableConceptModel::toFhir));
        
        if(quantity != null) {
            serviceRequest.setQuantity(quantity.toFhir());
        }
        
        if(patient != null) {
            serviceRequest.setSubject(patient.toFhir());
        }
        
        if(encounter != null) {
            serviceRequest.setEncounter(encounter.toFhir());
        }
        
        if(occurrenceDate != null) {
            serviceRequest.setOccurrence(new DateTimeType(occurrenceDate));
        }
        
        serviceRequest.setAuthoredOn(authoredOn);
        
        if(requester != null) {
            serviceRequest.setRequester(requester.toFhir());
        }
        
        if(performerType != null) {
            serviceRequest.setPerformerType(performerType.toFhir());
        }
        
        serviceRequest.setPerformer(DataUtils.transform(performer, ReferenceModel::toFhir));
        
        if(location != null) {
            serviceRequest.addLocationReference(location.toFhir());
        }
        
        serviceRequest.setInsurance(DataUtils.transform(insurance, ReferenceModel::toFhir));
        
        serviceRequest.setSpecimen(DataUtils.transform(specimen, ReferenceModel::toFhir));
        
        serviceRequest.setNote(DataUtils.transform(note, AnnotationModel::toFhir));
        
        serviceRequest.setPatientInstruction(patientInstruction);
        
        
        return serviceRequest;
    }
    
    public ServiceRequestEntity() {
        
    }
    
    public ServiceRequestEntity(ServiceRequest serviceRequest) {
        if(serviceRequest != null) {
            this.uuid = serviceRequest.getId();
            
            if(this.uuid != null && this.uuid.startsWith(ResourceType.ServiceRequest + "/")) {
                this.uuid = this.uuid.replace(ResourceType.ServiceRequest + "/", "");
            }
            
            this.identifier = DataUtils.transform(serviceRequest.getIdentifier(), IdentifierModel::fromFhir);
            this.basedOn = DataUtils.transform(serviceRequest.getBasedOn(), ReferenceModel::fromFhir);
            
            if(serviceRequest.hasStatus()) {
                this.status = serviceRequest.getStatus().toCode();
            }
            
            if(serviceRequest.hasIntent()) {
                this.intent = serviceRequest.getIntent().toCode();
            }
            
            if(serviceRequest.hasPriority()) {
                this.priority = serviceRequest.getPriority().toCode();
            }
            
            this.category = DataUtils.transform(serviceRequest.getCategory(), CodeableConceptModel::fromFhir);
            
            if(serviceRequest.hasCode()) {
                this.code = CodeableConceptModel.fromFhir(serviceRequest.getCode());
            }
            
            this.orderDetail = DataUtils.transform(serviceRequest.getOrderDetail(), CodeableConceptModel::fromFhir);
            
            if(serviceRequest.hasQuantityQuantity()) {
                this.quantity = QuantityModel.fromFhir(serviceRequest.getQuantityQuantity());
            }
            
            if(serviceRequest.hasSubject()) {
                this.patient = ReferenceModel.fromFhir(serviceRequest.getSubject());
            }
            
            if(serviceRequest.hasEncounter()) {
                this.encounter = ReferenceModel.fromFhir(serviceRequest.getEncounter());
            }
            
            if(serviceRequest.hasOccurrenceDateTimeType()) {
                this.occurrenceDate = serviceRequest.getOccurrenceDateTimeType().getValue();
            }else if(serviceRequest.hasOccurrencePeriod()) {
                this.occurrenceDate = serviceRequest.getOccurrencePeriod().getStart();
            }
            
            this.authoredOn = serviceRequest.getAuthoredOn();
            
            if(serviceRequest.hasRequester()) {
                this.requester = ReferenceModel.fromFhir(serviceRequest.getRequester());
            }
            
            if(serviceRequest.hasPerformerType()) {
                this.performerType = CodeableConceptModel.fromFhir(serviceRequest.getPerformerType());
            }
            
            this.performer = DataUtils.transform(serviceRequest.getPerformer(), ReferenceModel::fromFhir);
            
            if(serviceRequest.hasLocationReference()) {
                this.location = ReferenceModel.fromFhir(serviceRequest.getLocationReferenceFirstRep());
            }
            
            this.insurance = DataUtils.transform(serviceRequest.getInsurance(), ReferenceModel::fromFhir);
            this.specimen = DataUtils.transform(serviceRequest.getSpecimen(), ReferenceModel::fromFhir);
            this.note = DataUtils.transform(serviceRequest.getNote(), AnnotationModel::fromFhir);
            this.patientInstruction = serviceRequest.getPatientInstruction();
            
        }
    }
    
    public static ServiceRequestEntity fromFhir(ServiceRequest serviceRequest) {
        if(serviceRequest != null) {
            return new ServiceRequestEntity(serviceRequest);
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
