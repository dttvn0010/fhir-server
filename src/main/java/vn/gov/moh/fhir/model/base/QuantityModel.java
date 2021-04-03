package vn.gov.moh.fhir.model.base;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Quantity.QuantityComparator;

public class QuantityModel {

    Double value;
    String comparator;
    String unit;
    String system;
    String code;
    
    public Quantity toFhir() {
        var quantity = new Quantity();
        
        if(value != null) {
            quantity.setValue(value);
        }
        
        quantity.setUnit(unit);
        
        if(!StringUtils.isEmpty(comparator)) {
            quantity.setComparator(QuantityComparator.fromCode(comparator));
        }
        
        quantity.setSystem(system);
        quantity.setCode(code);
        return quantity;
    }
    
    public QuantityModel() {
        
    }
    
    public QuantityModel(double value, String unit) {
        this.value = value;
        this.unit = unit;
    }
    
    public QuantityModel(Quantity quantity) {
        if(quantity != null) {
            
            if(quantity.hasValue()) {
                this.value = quantity.getValue().doubleValue();
            }
            
            this.unit = quantity.getUnit();
            
            if(quantity.hasComparator()) {
                this.comparator = quantity.getComparator().toCode();
            }
            
            this.system = quantity.getSystem();
            this.code = quantity.getCode();            
        }
    }
    
    public static QuantityModel fromFhir(Quantity quantity) {
        if(quantity != null) {
            return new QuantityModel(quantity);
        }
        return null;
    }
}
