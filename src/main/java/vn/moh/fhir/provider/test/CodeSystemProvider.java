package vn.moh.fhir.provider.test;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.stereotype.Component;

import com.helger.commons.io.resource.ClassPathResource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.server.IResourceProvider;

@Component
public class CodeSystemProvider implements IResourceProvider {
    
    final static String[] CODE_SYSTEM_IDS = {
           "ethnic-groups"
    };
    
    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return CodeSystem.class;
    }
    
    @Search
    public List<CodeSystem> search(@OptionalParam(name ="name") StringType name) {
        
        var lst = new ArrayList<CodeSystem>();
        
        for(String codeSystemId : CODE_SYSTEM_IDS) {
            var jsonParser = FhirContext.forR4().newJsonParser();        
            var str = (new ClassPathResource("code-systems/" + codeSystemId + ".json")).getInputStream();              
            var codeSystem = (CodeSystem) jsonParser.parseResource(str);
            String keyword = name != null? name.getValue() : "";
            if(codeSystem.getName().contains(keyword)) {
                codeSystem.setConcept(null);
                lst.add(codeSystem);
            }
        }
        
        return lst;
    }

    @Read
    public Resource read(@IdParam IdType idType) {
        var jsonParser = FhirContext.forR4().newJsonParser();        
        var str = (new ClassPathResource("code-systems/" + idType.getIdPart() + ".json")).getInputStream();              
        return (CodeSystem) jsonParser.parseResource(str);
    }
}
