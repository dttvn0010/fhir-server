package vn.moh.fhir.model.base;

import java.util.List;

import org.hl7.fhir.r4.model.CodeableConcept;

import vn.moh.fhir.utils.DataUtils;

public class CodeableConceptModel {

    List<CodingModel> coding;
    String text;
    
    public String getDisplay() {
        if(coding != null && coding.size() > 0) {
            return coding.get(0).display;
        }
        return text;
    }
    
    public CodeableConcept toFhir() {
        var codeableConcept = new CodeableConcept();
        codeableConcept.setText(text);
        codeableConcept.setCoding(DataUtils.transform(coding, CodingModel::toFhir));
        return codeableConcept;
    }
    
    public CodeableConceptModel(String text) {
        this.text = text;
    }
    
    public CodeableConceptModel() {
        
    }
    
    public CodeableConceptModel(CodeableConcept codeableConcept) {
        if(codeableConcept != null) {
            this.text = codeableConcept.getText();
            this.coding = DataUtils.transform(codeableConcept.getCoding(), CodingModel::fromFhir);
        }
    }
    
    public static CodeableConceptModel fromFhir(CodeableConcept codeableConcept) {
        if(codeableConcept != null) {
            return new CodeableConceptModel(codeableConcept);
        }
        return null;
    }
}
