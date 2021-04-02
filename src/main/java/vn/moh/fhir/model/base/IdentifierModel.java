package vn.moh.fhir.model.base;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Identifier.IdentifierUse;

public class IdentifierModel {
    
    public String system;

    public String value;
    
    public CodeableConceptModel type;
    
    public PeriodModel period;
    
    public ReferenceModel assigner;

    public String use;
    
    public Identifier toFhir() {
        
        var identifier = new Identifier();
        identifier.setSystem(system);
        identifier.setValue(value);
        
        if(type != null) {
            identifier.setType(type.toFhir());
        }
        
        if(period != null) {
            identifier.setPeriod(period.toFhir());
        }
        
        if(assigner != null) {
            identifier.setAssigner(assigner.toFhir());
        }
        
        if(!StringUtils.isEmpty(use)) {
            identifier.setUse(IdentifierUse.fromCode(use));
        }
        
        return identifier;
    }
    
    public IdentifierModel(Identifier identifier) {
        if(identifier != null) {
            this.system = identifier.getSystem();
            this.value = identifier.getValue();
            
            if(identifier.hasType()) {
                this.type = CodeableConceptModel.fromFhir(identifier.getType());
            }
            
            if(identifier.hasPeriod()) {
                this.period = PeriodModel.fromFhir(identifier.getPeriod());
            }
            
            if(identifier.hasAssigner()) {
                this.assigner = ReferenceModel.fromFhir(identifier.getAssigner());
            }
            
            if(identifier.hasUse()) {
                this.use = identifier.getUse().toCode();
            }
        }
    }
    
    public static IdentifierModel fromFhir(Identifier identifier) {
        if(identifier != null) {
            return new IdentifierModel(identifier);
        }
        return null;
    }
}
