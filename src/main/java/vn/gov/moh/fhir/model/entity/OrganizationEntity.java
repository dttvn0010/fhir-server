package vn.gov.moh.fhir.model.entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.gov.moh.fhir.model.base.AddressModel;
import vn.gov.moh.fhir.model.base.CodeableConceptModel;
import vn.gov.moh.fhir.model.base.IdentifierModel;
import vn.gov.moh.fhir.model.base.ReferenceModel;
import vn.gov.moh.fhir.utils.DataUtils;

@JsonInclude(Include.NON_NULL)
@Document(collection = "organization")
@CompoundIndex(def = "{'uuid':1, '_active':1, '_version':1}", name = "index_by_default")
public class OrganizationEntity {
    @Id public ObjectId id;
    String uuid;
    int _version;
    boolean _active;
    
    Boolean active;
    List<IdentifierModel> identifier;
    List<CodeableConceptModel> type;
    String name;    
    AddressModel address;
    ReferenceModel partOf;
    
    public Organization toFhir() {
        var org = new Organization();
        
        org.setId(uuid);
        
        if(active != null) {
            org.setActive(active);
        }
        
        org.setIdentifier(DataUtils.transform(identifier, IdentifierModel::toFhir));
        
        org.setType(DataUtils.transform(type, CodeableConceptModel::toFhir));
        
        org.setName(name);
        if(address != null) {
            org.addAddress(address.toFhir());
        }
        
        if(partOf != null) {
            org.setPartOf(partOf.toFhir());
        }
        
        return org;
    }
    
    public OrganizationEntity() {
        
    }
    
    public OrganizationEntity(Organization org) {
        if(org != null) {
            
            this.uuid = org.getId();
            
            if(this.uuid != null && this.uuid.startsWith(ResourceType.Organization + "/")) {
                this.uuid = this.uuid.replace(ResourceType.Organization + "/", "");
            }
            
            if(org.hasActive()) {
                this.active = org.getActive();
            }
            this.identifier = DataUtils.transform(org.getIdentifier(), IdentifierModel::fromFhir);
            this.type = DataUtils.transform(org.getType(), CodeableConceptModel::fromFhir);
            this.name = org.getName();
            
            if(org.hasAddress()) {
                this.address = AddressModel.fromFhir(org.getAddressFirstRep());
            }
            
            if(org.hasPartOf()) {
                this.partOf = ReferenceModel.fromFhir(org.getPartOf());
            }
        }        
    }
    
    public static OrganizationEntity fromFhir(Organization org) {
        if(org != null) {
            return new OrganizationEntity(org);
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
