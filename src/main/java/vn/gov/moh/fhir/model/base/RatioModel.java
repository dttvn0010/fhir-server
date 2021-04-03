package vn.gov.moh.fhir.model.base;

import org.hl7.fhir.r4.model.Ratio;

public class RatioModel {

    QuantityModel numerator;
    QuantityModel denominator;
    
    public Ratio toFhir() {
        var ratio = new Ratio();
        if(numerator != null) {
            ratio.setNumerator(numerator.toFhir());
        }
        
        if(denominator != null) {
            ratio.setDenominator(denominator.toFhir());
        }
        
        return ratio;
    }
    
    public RatioModel() {
        
    }
    
    public RatioModel(QuantityModel numerator, QuantityModel denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }
    
    public RatioModel(Ratio ratio) {
        if(ratio != null) {
            if(ratio.hasNumerator()) {
                this.numerator = QuantityModel.fromFhir(ratio.getNumerator());
            }
            if(ratio.hasDenominator()) {
                this.denominator = QuantityModel.fromFhir(ratio.getDenominator());
            }
        }
    }
    
    public static RatioModel fromFhir(Ratio ratio) {
        if(ratio != null) {
            return new RatioModel(ratio);
        }
        return null;
    }
}
