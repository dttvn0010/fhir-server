package vn.gov.moh.fhir.provider.test;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.helger.commons.io.resource.ClassPathResource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;

@Component
public class StructureDefinitionProvider implements IResourceProvider {

    @Autowired private FhirContext fhirContext;
    
    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return StructureDefinition.class;
    }
    
    @Read
    public Resource read(@IdParam IdType idType) {
        var jsonParser = fhirContext.newJsonParser();        
        var str = (new ClassPathResource("structure-definitions/" + idType.getIdPart() + ".json")).getInputStream();              
        return (StructureDefinition) jsonParser.parseResource(str);
    }

}
