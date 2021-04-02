package vn.moh.fhir.provider;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
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
import vn.moh.fhir.model.entity.PatientEntity;
import vn.moh.fhir.service.PatientService;
import vn.moh.fhir.utils.DataUtils;
import vn.moh.fhir.utils.FhirUtils;

@Component
public class PatientProvider implements IResourceProvider{

    @Autowired private PatientService patientService;
    
    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Patient.class;
    }
    
    @Read
    public Resource read(@IdParam IdType idType) {
        var patientEntity = patientService.getById(idType.getIdPart());
        if(patientEntity == null) {
            FhirUtils.createOperationOutcome("No Patient with \"" + idType.getIdPart() + "\" found");
        }
        return patientEntity.toFhir();
    }    
    
    @Search
    public List<Patient> search(@OptionalParam(name ="name") StringType name,                            
            @Count Integer count, @Offset Integer offset) {
        
        var patientList = patientService.search(name != null? name.getValue(): "", offset, count);
        return DataUtils.transform(patientList, PatientEntity::toFhir);
    }
    
}
