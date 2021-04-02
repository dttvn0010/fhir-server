package vn.moh.fhir.provider;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.IdType;
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
import vn.moh.fhir.model.entity.DiagnosticReportEntity;
import vn.moh.fhir.service.DiagnosticReportService;
import vn.moh.fhir.utils.DataUtils;
import vn.moh.fhir.utils.FhirUtils;

@Component
public class DiagnosticReportProvider  implements IResourceProvider {

    @Autowired private DiagnosticReportService diagnosticReportService;
    
    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return DiagnosticReport.class;
    }

    @Read
    public Resource read(@IdParam IdType idType) {
        var diagnosticReportEntity = diagnosticReportService.getById(idType.getIdPart());
        if(diagnosticReportEntity == null) {
            FhirUtils.createOperationOutcome("No DiagnosticReport with \"" + idType.getIdPart() + "\" found");
        }
        return diagnosticReportEntity.toFhir();
    }    
    
    
    @Search
    public List<DiagnosticReport> search(
            @OptionalParam(name ="patient") ReferenceParam patient,
            @OptionalParam(name ="encounter") ReferenceParam encounter,
            @Count Integer count, @Offset Integer offset) {
        
        
        var lst = diagnosticReportService.search(
                patient != null? patient.getValue(): "", 
                encounter != null? encounter.getValue(): "", 
                offset, 
                count);
        
        return DataUtils.transform(lst, DiagnosticReportEntity::toFhir);
    }    
}
