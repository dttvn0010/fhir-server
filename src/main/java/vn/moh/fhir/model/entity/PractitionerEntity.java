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

import org.hl7.fhir.r4.model.Practitioner;

import vn.moh.fhir.model.base.AddressModel;
import vn.moh.fhir.model.base.AttachmentModel;
import vn.moh.fhir.model.base.CodeableConceptModel;
import vn.moh.fhir.model.base.ContactPointModel;
import vn.moh.fhir.model.base.HumanNameModel;
import vn.moh.fhir.model.base.IdentifierModel;
import vn.moh.fhir.model.base.PeriodModel;
import vn.moh.fhir.utils.DataUtils;

@JsonInclude(Include.NON_NULL)
@Document(collection = "practitioner")
@CompoundIndex(def = "{'id':1, '_active':1, '_version':1}", name = "index_by_default")
public class PractitionerEntity {

    public static class PractitionerQualification {
        List<IdentifierModel> identifier;
        CodeableConceptModel code;
        PeriodModel period;
        
        public Practitioner.PractitionerQualificationComponent toFhir() {
            var qualification = new Practitioner.PractitionerQualificationComponent();
            qualification.setIdentifier(DataUtils.transform(identifier, IdentifierModel::toFhir));
            if(code != null) {
                qualification.setCode(code.toFhir());
            }
            if(period != null) {
                qualification.setPeriod(period.toFhir());
            }
            return qualification;
        }    
        
        public PractitionerQualification(Practitioner.PractitionerQualificationComponent qualification) {
            if(qualification != null) {
                this.identifier = DataUtils.transform(qualification.getIdentifier(), IdentifierModel::fromFhir);
                if(qualification.hasCode()) {
                    this.code = CodeableConceptModel.fromFhir(qualification.getCode());
                }
                if(qualification.hasPeriod()) {
                    this.period = PeriodModel.fromFhir(qualification.getPeriod());
                }
            }
        }
        
        public static PractitionerQualification fromFhir(Practitioner.PractitionerQualificationComponent qualification) {
            if(qualification != null) {
                return new PractitionerQualification(qualification);
            }
            return null;
        }
    }
    
    @Id public ObjectId _id;
    String id;
    int _version;
    boolean _active;
    
    Boolean active;
    List<IdentifierModel> identifier;
    HumanNameModel name;
    List<ContactPointModel> telecom;
    List<AddressModel> address;
    String gender;
    Date birthDate;
    List<AttachmentModel> photo;
    List<PractitionerQualification> qualification;
    
    public Practitioner toFhir() {
        var practitioner = new Practitioner();
        
        practitioner.setId(id);
        
        if(active !=  null) {
            practitioner.setActive(active);
        }
        
        practitioner.setIdentifier(DataUtils.transform(identifier, IdentifierModel::toFhir));
        
        if(name != null) {
            practitioner.addName(name.toFhir());
        }
        
        practitioner.setTelecom(DataUtils.transform(telecom, ContactPointModel::toFhir));
        practitioner.setAddress(DataUtils.transform(address, AddressModel::toFhir));
        
        if(!StringUtils.isEmpty(gender)) {
            practitioner.setGender(AdministrativeGender.fromCode(gender));
        }
        
        practitioner.setBirthDate(birthDate);
        
        practitioner.setPhoto(DataUtils.transform(photo, AttachmentModel::toFhir));
        practitioner.setQualification(DataUtils.transform(qualification, PractitionerQualification::toFhir));
        
        return practitioner;
    }
    
    public PractitionerEntity(Practitioner practitioner) {
        if(practitioner != null) {
            this.id = practitioner.getId();
            
            if(practitioner.hasActive()) {
                this.active = practitioner.getActive();
            }
            
            this.identifier = DataUtils.transform(practitioner.getIdentifier(), IdentifierModel::fromFhir);
            
            if(practitioner.hasName()) {
                this.name = HumanNameModel.fromFhir(practitioner.getNameFirstRep());
            }
            
            this.telecom = DataUtils.transform(practitioner.getTelecom(), ContactPointModel::fromFhir);
            this.address = DataUtils.transform(practitioner.getAddress(), AddressModel::fromFhir);
            
            if(practitioner.hasGender()) {
                this.gender = practitioner.getGender().toCode();
            }
            
            this.birthDate = practitioner.getBirthDate();
            
            this.photo = DataUtils.transform(practitioner.getPhoto(), AttachmentModel::fromFhir);
            this.qualification = DataUtils.transform(practitioner.getQualification(), PractitionerQualification::fromFhir);
        }
    }
    
    public static PractitionerEntity fromFhir(Practitioner practitioner) {
        if(practitioner != null) {
            return new PractitionerEntity(practitioner);
        }
        return null;
    }
}
