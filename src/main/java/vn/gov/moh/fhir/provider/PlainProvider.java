package vn.gov.moh.fhir.provider;

import org.hl7.fhir.r4.model.Bundle;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.annotation.Transaction;
import ca.uhn.fhir.rest.annotation.TransactionParam;

@Component
public class PlainProvider {

    @Transaction
    public Bundle transaction(@TransactionParam Bundle bundle) {
        
        var result = new Bundle();
        return result;
    }    
}
