package vn.gov.moh.fhir.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DecimalType;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.ValueSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.ConceptValidationOptions;
import ca.uhn.fhir.context.support.IValidationSupport;
import ca.uhn.fhir.context.support.ValidationSupportContext;
import ca.uhn.fhir.validation.ValidationOptions;
import ca.uhn.fhir.validation.ValidationResult;

@Component
public class FhirHelper {
    @Value("${fhir.domain}")
    private String fhirDomain;
    
    @Value("${fhir.terminology.server.url}")
    private String terminologyServerUrl;
    
    @Autowired private FhirContext fhirContext;
    
    private Map<String, IBaseResource> resourcesCache = new HashMap<>();
    
    @Bean
    public FhirContext getFhirContext() {
        return FhirContext.forR4();
    }
    
    public Extension createExtension(String url, CodeableConcept concept) {
        if(concept == null) return null;
        var extension = new Extension();
        extension.setUrl(url);
        extension.setValue(concept);
        return extension;
    }    
    
    public Extension createExtension(String url, String text) {
        if(text == null) return null;
        var extension = new Extension();
        extension.setUrl(url);
        extension.setValue(new StringType(text));
        return extension;
    }
    
    public Extension createExtension(String url, Integer value) {
        if(value == null) return null;
        var extension = new Extension();
        extension.setUrl(url);
        extension.setValue(new IntegerType(value));
        return extension;
    }
    
    public Extension createExtension(String url, Double value) {
        if(value == null) return null;
        var extension = new Extension();
        extension.setUrl(url);
        extension.setValue(new DecimalType(value));
        return extension;
    }
    
    public Extension createExtension(String url, Date date) {
        if(date == null) return null;
        var extension = new Extension();
        extension.setUrl(url);
        extension.setValue(new DateTimeType(date));
        return extension;
    }
    
    public Extension findExtension(List<Extension> extension, @Nonnull String url) {
        if(extension != null) {
            for(var ext : extension) {
                if(url.equals(ext.getUrl())) {
                    return ext;
                }
            }
        }
        return null;
    }
    
    public OperationOutcome createOperationOutcome (String message) {
        var outcome = new OperationOutcome();

        outcome.addIssue()
                .setCode(OperationOutcome.IssueType.EXCEPTION)
                .setSeverity(OperationOutcome.IssueSeverity.ERROR)
                .setDiagnostics(message)
                .setDetails(
                    new CodeableConcept().setText(message)
                );
        return outcome;
    }
    
    public OperationOutcome createOperationOutcome(String diagnostics, String message, IssueSeverity severity , IssueType issueType) {
        var outcome = new OperationOutcome();
        outcome.addIssue()
                .setCode(issueType)
                .setSeverity(severity)
                .setDiagnostics(diagnostics)
                .setDetails(
                    new CodeableConcept().setText(message)
                );
        return outcome;
    }
    
    public IBaseResource fetchRemoteResource(String url) {
        
        if(!url.startsWith(fhirDomain)) {
            return null;
        }
        
        if(resourcesCache.containsKey(url)) {
            return resourcesCache.get(url);
        }
        
        var jsonParser = fhirContext.newJsonParser();
        
        var restTemplate = new RestTemplate();
        
        var response = restTemplate.getForEntity(url.replace(fhirDomain, terminologyServerUrl), String.class);  
        
        var resource = jsonParser.parseResource(response.getBody());
        resourcesCache.put(url, resource);
        
        return resource;
    }
    
    public HashSet<String> getSupportCodeSystemUrls() {
        var jsonParser = fhirContext.newJsonParser();
        
        var restTemplate = new RestTemplate();
        var response = restTemplate.getForEntity(terminologyServerUrl + "/CodeSystem", String.class);
        var bundle = (Bundle) jsonParser.parseResource(response.getBody());
        
        var codeSystemUrls = new HashSet<String>();
        
        for(var entry : bundle.getEntry()) {
            var codeSystemId = entry.getResource().getId().replace(terminologyServerUrl + "/CodeSystem/", "");
            codeSystemUrls.add(fhirDomain + "/CodeSystem/" + codeSystemId);
        }
        
        return codeSystemUrls;        
    }
    
    public HashSet<String> getSupportValueSetUrls() {
        var jsonParser = fhirContext.newJsonParser();
        
        var restTemplate = new RestTemplate();
        var response = restTemplate.getForEntity(terminologyServerUrl + "/ValueSet", String.class);
        var bundle = (Bundle) jsonParser.parseResource(response.getBody());
                
        var valueSetUrls = new HashSet<String>();
        
        for(var entry : bundle.getEntry()) {
            var valueSetId = entry.getResource().getId().replace(terminologyServerUrl + "/ValueSet/", "");
            valueSetUrls.add(fhirDomain + "/ValueSet/" +  valueSetId);
        }
        
        return valueSetUrls;        
    }
    
    public ValidationResult validateResource(Resource resource, String profile) {
        var ctx = fhirContext;
        var customValidationSupport = new IValidationSupport() {
            @Override
            public FhirContext getFhirContext() {
                return ctx;
            }
            
            @SuppressWarnings("unchecked")
            @Override
            public <T extends IBaseResource> T fetchResource(Class<T> theClass, String theUri) {                 
                
                return (T) fetchRemoteResource(theUri);
            }
            
            @Override
            public boolean isCodeSystemSupported(ValidationSupportContext theValidationSupportContext, String theSystem) {
                var supportedCodeSystemUrls = getSupportCodeSystemUrls();
                return supportedCodeSystemUrls.contains(theSystem);
            }
            
            @Override
            public boolean isValueSetSupported(ValidationSupportContext theValidationSupportContext,
                    String theValueSetUrl) {
            
                var supportedValueSetUrls = getSupportValueSetUrls();
                return supportedValueSetUrls.contains(theValueSetUrl);
            }
            
            @Override
            public CodeValidationResult validateCodeInValueSet(ValidationSupportContext theValidationSupportContext,
                    ConceptValidationOptions theOptions, String theCodeSystem, String theCode, String theDisplay,
                    IBaseResource theValueSet) {
                
                if(theCode == null || theCodeSystem == null) {
                    return null;
                }
                
                if(theValueSet != null && theValueSet instanceof ValueSet) {
                    var valueSet = (ValueSet) theValueSet;
                    
                    for(var comp : valueSet.getCompose().getInclude()) {
                        var conceptList = comp.getConcept();
                        if(conceptList.size() == 0) {
                            var codeSystem = (CodeSystem) fetchRemoteResource(comp.getSystem());
                            if(codeSystem != null) {
                                for(var item : codeSystem.getConcept()) {
                                    var concept = new ValueSet.ConceptReferenceComponent();
                                    concept.setCode(item.getCode());
                                    conceptList.add(concept);
                                }
                            }
                        }
                        
                        for(var concept : conceptList) {
                            if(theCode.equals(concept.getCode()) && theCodeSystem.equals(comp.getSystem())) {
                                var result = new CodeValidationResult();
                                result.setCode(theCode);
                                result.setDisplay(theDisplay);
                                result.setCodeSystemName(theCodeSystem);
                                return result;
                            }
                        }
                    }
                }
                
                return null;
            }
            
            @Override
            public CodeValidationResult validateCode(ValidationSupportContext theValidationSupportContext,
                    ConceptValidationOptions theOptions, String theCodeSystem, String theCode, String theDisplay,
                    String theValueSetUrl) {
                
                var supportedCodeSystemUrls = getSupportCodeSystemUrls();
                
                if(theCode == null || theCodeSystem == null) {
                    return null;
                }
                
                if(theCode == null || theCodeSystem == null) {
                    return null;
                }
                
                if(supportedCodeSystemUrls.contains(theCodeSystem)) {
                    var codeSystem = (CodeSystem) fetchRemoteResource(theCodeSystem);
                    for(var concept : codeSystem.getConcept()) {
                        if(concept.getCode().equals(theCode)) {
                            var result = new CodeValidationResult();
                            result.setCode(theCode);
                            result.setDisplay(theDisplay);
                            result.setCodeSystemName(theCodeSystem);
                            return result;
                        }
                    }
                }
                
                return null;
            }
        };
        
        var validator = ctx.newValidator();
        var module = new FhirInstanceValidator(ctx);
        
        var chain = (ValidationSupportChain) module.getValidationSupport();
        chain.addValidationSupport(0, customValidationSupport);
        
        validator.registerValidatorModule(module);
        
        var options = new ValidationOptions();
        options.addProfileIfNotBlank(profile);

        if (resource.getMeta() != null && resource.getMeta().getProfile() != null) {
            for (var item : resource.getMeta().getProfile()) {
                options.addProfile(item.getValue());
            }
        }
        
        return validator.validateWithResult(resource, options);
    }
}
