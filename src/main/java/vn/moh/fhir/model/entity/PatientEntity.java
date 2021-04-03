package vn.moh.fhir.model.entity;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.ResourceType;

import vn.moh.fhir.model.base.AddressModel;
import vn.moh.fhir.model.base.AttachmentModel;
import vn.moh.fhir.model.base.CodeableConceptModel;
import vn.moh.fhir.model.base.ContactPointModel;
import vn.moh.fhir.model.base.HumanNameModel;
import vn.moh.fhir.model.base.IdentifierModel;
import vn.moh.fhir.model.base.ReferenceModel;
import vn.moh.fhir.utils.DataUtils;
import vn.moh.fhir.utils.FhirUtils;
import vn.moh.fhir.utils.Constants.ExtensionURL;

@JsonInclude(Include.NON_NULL)
@Document(collection = "patient")
@CompoundIndex(def = "{'uuid':1, '_active':1, '_version':1}", name = "index_by_default")
public class PatientEntity {    
    
    @Id public ObjectId id;
    String uuid;
    int _version;
    boolean _active;
    
    Boolean active;
    HumanNameModel name;
    List<IdentifierModel> identifier;
    String gender;
    Date birthDate;
    
    List<ContactPointModel> telecom;
    List<AddressModel> address;
    
    CodeableConceptModel maritalStatus;
    List<AttachmentModel> photo;    
    ReferenceModel managingOrganization;
    
    CodeableConceptModel education;
    CodeableConceptModel ethnic;
    CodeableConceptModel religion;
    CodeableConceptModel job;
    CodeableConceptModel nationality;
    
    public Patient toFhir() {
        var patient = new Patient();
        
        patient.setId(uuid);
        
        if(active !=  null) {
            patient.setActive(active);
        }
        
        if(name != null) {
            patient.addName(name.toFhir());
        }
        
        if(!StringUtils.isEmpty(gender)) {
            patient.setGender(AdministrativeGender.fromCode(gender));
        }
        
        patient.setBirthDate(birthDate);
        patient.setIdentifier(DataUtils.transform(identifier, IdentifierModel::toFhir));
        patient.setTelecom(DataUtils.transform(telecom, ContactPointModel::toFhir));
        patient.setAddress(DataUtils.transform(address, AddressModel::toFhir));
        
        if(maritalStatus != null) {
            patient.setMaritalStatus(maritalStatus.toFhir());
        }
        
        patient.setPhoto(DataUtils.transform(photo, AttachmentModel::toFhir));
        
        if(managingOrganization != null) {
            patient.setManagingOrganization(managingOrganization.toFhir());
        }
        
        if(education != null) {
            patient.addExtension(FhirUtils.createExtension(ExtensionURL.PATIENT_EDUCATION, education.toFhir()));
        }
        
        if(ethnic != null) {
            patient.addExtension(FhirUtils.createExtension(ExtensionURL.PATIENT_ETHNIC, ethnic.toFhir()));
        }
        
        if(religion != null) {
            patient.addExtension(FhirUtils.createExtension(ExtensionURL.PATIENT_RELIGION, religion.toFhir()));
        }
        
        if(job != null) {
            patient.addExtension(FhirUtils.createExtension(ExtensionURL.PATIENT_JOB, job.toFhir()));
        }
        
        if(nationality != null) {
            patient.addExtension(FhirUtils.createExtension(ExtensionURL.PATIENT_NATIONALITY, nationality.toFhir()));
        }
        
        return patient;        
    }
    
    public PatientEntity() {
        
    }
    
    public PatientEntity(Patient patient) {
        if(patient != null) {
            this.uuid = patient.getId();
            
            if(this.uuid != null && this.uuid.startsWith(ResourceType.Patient + "/")) {
                this.uuid = this.uuid.replace(ResourceType.Patient + "/", "");
            }
            
            if(patient.hasActive()) {
                this.active = patient.getActive();
            }
            
            if(patient.hasName()) {
                this.name = HumanNameModel.fromFhir(patient.getNameFirstRep());
            }
            
            if(patient.hasGender()) {
                this.gender = patient.getGender().toCode();
            }
            
            this.birthDate = patient.getBirthDate();
            this.identifier = DataUtils.transform(patient.getIdentifier(), IdentifierModel::fromFhir);
            this.telecom = DataUtils.transform(patient.getTelecom(), ContactPointModel::fromFhir);
            this.address = DataUtils.transform(patient.getAddress(), AddressModel::fromFhir);
                    
            if(patient.hasMaritalStatus()) {
                this.maritalStatus = CodeableConceptModel.fromFhir(patient.getMaritalStatus());
            }
            
            this.photo = DataUtils.transform(patient.getPhoto(), AttachmentModel::fromFhir);
            
            if(patient.hasManagingOrganization()) {
                this.managingOrganization = ReferenceModel.fromFhir(patient.getManagingOrganization());
            }
            
            var educationExt = FhirUtils.findExtension(patient.getExtension(), ExtensionURL.PATIENT_EDUCATION);
            if(educationExt != null && educationExt.getValue() instanceof CodeableConcept) {
                this.education = CodeableConceptModel.fromFhir((CodeableConcept) educationExt.getValue());
            }
            
            var ethnicExt = FhirUtils.findExtension(patient.getExtension(), ExtensionURL.PATIENT_ETHNIC);
            if(ethnicExt != null && ethnicExt.getValue() instanceof CodeableConcept) {
                this.ethnic = CodeableConceptModel.fromFhir((CodeableConcept) ethnicExt.getValue());
            }
            
            var religionExt = FhirUtils.findExtension(patient.getExtension(), ExtensionURL.PATIENT_RELIGION);
            if(religionExt != null && religionExt.getValue() instanceof CodeableConcept) {
                this.religion = CodeableConceptModel.fromFhir((CodeableConcept) religionExt.getValue());
            }
            
            var jobExt = FhirUtils.findExtension(patient.getExtension(), ExtensionURL.PATIENT_JOB);
            if(jobExt != null && jobExt.getValue() instanceof CodeableConcept) {
                this.job = CodeableConceptModel.fromFhir((CodeableConcept) jobExt.getValue());
            }
            
            var nationalityExt = FhirUtils.findExtension(patient.getExtension(), ExtensionURL.PATIENT_NATIONALITY);
            if(nationalityExt != null && nationalityExt.getValue() instanceof CodeableConcept) {
                this.nationality = CodeableConceptModel.fromFhir((CodeableConcept) nationalityExt.getValue());
            }
        }
    }
    
    public static PatientEntity fromFhir(Patient patient) {
        if(patient != null) {
            return new PatientEntity(patient);
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
