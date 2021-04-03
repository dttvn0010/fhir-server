package vn.moh.fhir.utils;

import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DecimalType;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import org.hl7.fhir.r4.model.StringType;

public class FhirUtils {

    public static Extension createExtension(String url, CodeableConcept concept) {
        if(concept == null) return null;
        var extension = new Extension();
        extension.setUrl(url);
        extension.setValue(concept);
        return extension;
    }    
    
    public static Extension createExtension(String url, String text) {
        if(text == null) return null;
        var extension = new Extension();
        extension.setUrl(url);
        extension.setValue(new StringType(text));
        return extension;
    }
    
    public static Extension createExtension(String url, Integer value) {
        if(value == null) return null;
        var extension = new Extension();
        extension.setUrl(url);
        extension.setValue(new IntegerType(value));
        return extension;
    }
    
    public static Extension createExtension(String url, Double value) {
        if(value == null) return null;
        var extension = new Extension();
        extension.setUrl(url);
        extension.setValue(new DecimalType(value));
        return extension;
    }
    
    public static Extension createExtension(String url, Date date) {
        if(date == null) return null;
        var extension = new Extension();
        extension.setUrl(url);
        extension.setValue(new DateTimeType(date));
        return extension;
    }
    
    public static Extension findExtension(List<Extension> extension, @Nonnull String url) {
        if(extension != null) {
            for(var ext : extension) {
                if(url.equals(ext.getUrl())) {
                    return ext;
                }
            }
        }
        return null;
    }
    
    public static OperationOutcome createOperationOutcome (String message) {
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
    
    public static OperationOutcome createOperationOutcome(String diagnostics, String message, IssueSeverity severity , IssueType issueType) {
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
}
