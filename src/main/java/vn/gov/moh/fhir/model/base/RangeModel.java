package vn.gov.moh.fhir.model.base;

import org.hl7.fhir.r4.model.Range;

public class RangeModel {

    QuantityModel low;
    QuantityModel high;
    
    public Range toFhir()  {
        var range = new Range();
        
        if(low != null) {
            range.setLow(low.toFhir());
        }
        
        if(high != null) {
            range.setHigh(high.toFhir());
        }
        
        return range;
    }
    
    public RangeModel() {
        
    }
    
    public RangeModel(Range range) {
        if(range != null) {
            if(range.hasLow()) {
                this.low = QuantityModel.fromFhir(range.getLow());
            }
            if(range.hasHigh()) {
                this.high = QuantityModel.fromFhir(range.getHigh());
            }
        }
    }
    
    public static RangeModel fromFhir(Range range) {
        if(range != null) {
            return new RangeModel(range);
        }
        return null;        
    }
}
