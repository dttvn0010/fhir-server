package vn.gov.moh.fhir.provider.test;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.ValueSet;
import org.springframework.stereotype.Component;

import com.helger.commons.io.resource.ClassPathResource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.server.IResourceProvider;

@Component
public class ValueSetProvider implements IResourceProvider {
    final static String[] VALUE_SET_IDS = {
          "ethnic-groups"
    };

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return ValueSet.class;
    }
    
    @Search
    public List<ValueSet> search(@OptionalParam(name ="name") StringType name) {
        
        var lst = new ArrayList<ValueSet>();
        
        for(String valueSetId : VALUE_SET_IDS) {
            var jsonParser = FhirContext.forR4().newJsonParser();        
            var str = (new ClassPathResource("value-sets/" + valueSetId + ".json")).getInputStream();              
            var valueSet = (ValueSet) jsonParser.parseResource(str);
            String keyword = name != null? name.getValue() : "";
            if(valueSet.getName().contains(keyword)) {
                valueSet.setCompose(null);
                lst.add(valueSet);
            }
        }
        
        return lst;
    }

    @Read
    public Resource read(@IdParam IdType idType) {
        var jsonParser = FhirContext.forR4().newJsonParser();        
        var str = (new ClassPathResource("value-sets/" + idType.getIdPart() + ".json")).getInputStream();              
        return (ValueSet) jsonParser.parseResource(str);
    }
    
}
