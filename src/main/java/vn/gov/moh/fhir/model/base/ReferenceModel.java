package vn.gov.moh.fhir.model.base;

import org.hl7.fhir.r4.model.Reference;

public class ReferenceModel {

    String reference;
    String type;
    IdentifierModel identifier;
    String display;
    
    public Reference toFhir() {
        var ref = new Reference();
        ref.setReference(reference);
        ref.setType(type);
        if(identifier != null) {
            ref.setIdentifier(identifier.toFhir());
        }
        ref.setDisplay(display);
        return ref;
    }
    
    public ReferenceModel() {
        
    }
    
    public ReferenceModel(Reference ref) {
        if(ref != null) {
            this.reference = ref.getReference();
            this.type = ref.getType();
            if(ref.hasIdentifier()) {
                this.identifier = IdentifierModel.fromFhir(ref.getIdentifier());
            }
            this.display = ref.getDisplay();
        }
    }
    
    public static ReferenceModel fromFhir(Reference ref) {
        if(ref != null) {
            return new ReferenceModel(ref);
        }
        return null;
    }
}
