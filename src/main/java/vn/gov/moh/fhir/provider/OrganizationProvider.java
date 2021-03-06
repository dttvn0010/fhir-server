package vn.gov.moh.fhir.provider;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Offset;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.server.IResourceProvider;
import vn.gov.moh.fhir.model.entity.OrganizationEntity;
import vn.gov.moh.fhir.service.OrganizationService;
import vn.gov.moh.fhir.utils.DataUtils;
import vn.gov.moh.fhir.utils.FhirHelper;

@Component
public class OrganizationProvider implements IResourceProvider {

    @Autowired private FhirHelper fhirUtils;
    @Autowired private OrganizationService organizationService;
    
    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Organization.class;
    }
    
    @Read
    public Resource read(@IdParam IdType idType) {
        var orgnanizationEntity = organizationService.getByUuid(idType.getIdPart());
        if(orgnanizationEntity == null) {
            fhirUtils.createOperationOutcome("No Organization with \"" + idType.getIdPart() + "\" found");
        }
        return orgnanizationEntity.toFhir();
    }    
    
    @Search
    public List<Organization> search(@OptionalParam(name ="name") StringType name,                            
            @Count Integer count, @Offset Integer offset) {
        
        var lst = organizationService.search(name != null? name.getValue(): "", offset, count);
        return DataUtils.transform(lst, OrganizationEntity::toFhir);
    }
}
