package vn.moh.fhir.provider;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Procedure;
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
import vn.moh.fhir.model.entity.ProcedureEntity;
import vn.moh.fhir.service.ProcedureService;
import vn.moh.fhir.utils.DataUtils;
import vn.moh.fhir.utils.FhirUtils;

@Component
public class ProcedureProvider implements IResourceProvider {

	@Autowired private ProcedureService procedureService;
	
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Procedure.class;
	}

	@Read
    public Resource read(@IdParam IdType idType) {
		var procedureEntity = procedureService.getById(idType.getIdPart());
		if(procedureEntity == null) {
			FhirUtils.createOperationOutcome("No Procedure with \"" + idType.getIdPart() + "\" found");
		}
		return procedureEntity.toFhir();
	}	
	
	
	@Search
	public List<Procedure> search(
			@OptionalParam(name ="patient") ReferenceParam patient,
			@OptionalParam(name ="encounter") ReferenceParam encounter,
            @Count Integer count, @Offset Integer offset) {
		
		
		var lst = procedureService.search(
				patient != null? patient.getValue(): "", 
				encounter != null? encounter.getValue(): "", 
				offset, 
				count);
		
		return DataUtils.transform(lst, ProcedureEntity::toFhir);
	}
}
