package vn.moh.fhir.provider;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Offset;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import vn.moh.fhir.model.entity.MedicationRequestEntity;
import vn.moh.fhir.service.MedicationRequestService;
import vn.moh.fhir.utils.DataUtils;
import vn.moh.fhir.utils.FhirUtils;

@Component
public class MedicationRequestProvider implements IResourceProvider {

	@Autowired private MedicationRequestService medicationRequestService;
	
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return MedicationRequest.class;
	}

	@Read
    public Resource read(@IdParam IdType idType) {
		var medicationRequestEntity = medicationRequestService.getById(idType.getIdPart());
		if(medicationRequestEntity == null) {
			FhirUtils.createOperationOutcome("No MedicationRequest with \"" + idType.getIdPart() + "\" found");
		}
		return medicationRequestEntity.toFhir();
	}	
	
	
	@Search
	public List<MedicationRequest> search(
			@OptionalParam(name ="patient") ReferenceParam patient,
			@OptionalParam(name ="encounter") ReferenceParam encounter,
            @Count Integer count, @Offset Integer offset) {
		
		
		var lst = medicationRequestService.search(
				patient != null? patient.getValue(): "", 
				encounter != null? encounter.getValue(): "", 
				offset, 
				count);
		
		return DataUtils.transform(lst, MedicationRequestEntity::toFhir);
	}
}
