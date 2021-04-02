package vn.moh.fhir.provider;

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
import vn.moh.fhir.model.entity.OrganizationEntity;
import vn.moh.fhir.service.OrganizationService;
import vn.moh.fhir.utils.DataUtils;
import vn.moh.fhir.utils.FhirUtils;

@Component
public class OrganizationProvider implements IResourceProvider {

	@Autowired private OrganizationService organizationService;
	
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Organization.class;
	}
	
	@Read
    public Resource read(@IdParam IdType idType) {
		var orgnanizationEntity = organizationService.getById(idType.getIdPart());
		if(orgnanizationEntity == null) {
			FhirUtils.createOperationOutcome("No Organization with \"" + idType.getIdPart() + "\" found");
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
