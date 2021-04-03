package vn.gov.moh.fhir.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class FhirHelperFactory implements ApplicationContextAware{
    private static ApplicationContext applicationContext;    
    private static FhirHelper fhirHelper;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        FhirHelperFactory.applicationContext = applicationContext;        
    }
    
    public static FhirHelper getFhirHelper() {
        if(fhirHelper == null) {
            fhirHelper = applicationContext.getBean(FhirHelper.class);
        }
        return fhirHelper;
    }
}
