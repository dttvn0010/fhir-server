package vn.moh.fhir.model.base;

import org.hl7.fhir.r4.model.Coding;

public class CodingModel {
    String system;
    String version;
    String code;
    String display;
    
    public String getDisplay() {
        return display;
    }
    
    public Coding toFhir() {
        var coding = new Coding();
        coding.setSystem(system);
        coding.setVersion(version);
        coding.setCode(code);
        coding.setDisplay(display);
        return coding;
    }
    
    public CodingModel(Coding coding) {
        if(coding != null) {
            this.system = coding.getSystem();
            this.version = coding.getVersion();
            this.code = coding.getCode();
            this.display = coding.getDisplay();
        }        
    }
    
    public static CodingModel fromFhir(Coding coding) {
        if(coding != null) {
            return new CodingModel(coding);
        }
        return null;
    }
}
