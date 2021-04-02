package vn.moh.fhir.model.base;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.HumanName;

public class HumanNameModel {
    public String text;
    public String given;
    public String family;    
    
    public HumanName toFhir() {
    	var name = new HumanName();
    	name.setText(text);
    	name.setFamily(family);
    	if(!StringUtils.isEmpty(given)) {
    		name.addGiven(given);
    	}
    	return name;
    }
    
    public HumanNameModel(HumanName name) {
    	if(name != null) {
    		this.text = name.getText();
    		this.family = name.getFamily();
    		if(name.hasGiven()) {
    			this.given = name.getGivenAsSingleString();
    		}
    	}
    }
    
    public static HumanNameModel fromFhir(HumanName name) {
    	if(name != null) {
    		return new HumanNameModel(name);
    	}
    	return null;
    }
}
