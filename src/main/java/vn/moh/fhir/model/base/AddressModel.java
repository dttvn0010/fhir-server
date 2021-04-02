package vn.moh.fhir.model.base;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Address.AddressUse;
import org.hl7.fhir.r4.model.CodeableConcept;

import vn.moh.fhir.utils.FhirUtils;
import vn.moh.fhir.utils.Constants.ExtensionURL;

public class AddressModel {

	String use;
	String type;
	String text;  
	String line1;
	String line2;
	String postalCode;
	String country;
	
	CodeableConceptModel ward;
	CodeableConceptModel district;
	CodeableConceptModel city;
	
	PeriodModel period;
	
	public Address toFhir() {
		var address = new Address();
		if(!StringUtils.isEmpty(use)) {
			address.setUse(AddressUse.fromCode(use));
		}
		address.setText(type);
		address.setText(text);
				
		if(!StringUtils.isEmpty(line1)) {
			address.addLine(line1);
		}
		
		if(!StringUtils.isEmpty(line2)) {
			address.addLine(line2);
		}
		
		address.setPostalCode(postalCode);
		address.setCountry(country);
		
		if(ward != null) {
			address.addExtension(FhirUtils.createExtension(ExtensionURL.ADDRESS_WARD, ward.toFhir()));
		}
		
		if(district != null) {
			address.setDistrict(district.getDisplay());
			address.addExtension(FhirUtils.createExtension(ExtensionURL.ADDRESS_WARD, district.toFhir()));
		}
		
		if(city != null) {
			address.setCity(city.getDisplay());
			address.addExtension(FhirUtils.createExtension(ExtensionURL.ADDRESS_WARD, city.toFhir()));
		}
		
		return address;
	}
	
	public AddressModel(Address address) {
		if(address.hasUse()) {
			this.use = address.getUse().toCode();
		}
		
		if(address.hasType()) {
			this.type = address.getType().toCode();
		}
		
		this.text = address.getText();
		var lines = address.getLine();
		if(lines.size() > 0) {
			this.line1 = lines.get(0).getValue();
		}
		if(lines.size() > 1) {
			this.line2 = lines.get(1).getValue();
		}
		
		this.postalCode = address.getPostalCode();
		this.country = address.getCountry();
		
		var wardExt = FhirUtils.findExtension(address.getExtension(), ExtensionURL.ADDRESS_WARD);
		if(wardExt != null && wardExt.getValue() instanceof CodeableConcept) {
			this.ward = CodeableConceptModel.fromFhir((CodeableConcept) wardExt.getValue());
		}
		
		var districtExt = FhirUtils.findExtension(address.getExtension(), ExtensionURL.ADDRESS_DISTRICT);
		if(districtExt != null && districtExt.getValue() instanceof CodeableConcept) {
			this.district = CodeableConceptModel.fromFhir((CodeableConcept) districtExt.getValue());
			
		}else if(address.hasDistrict()) {
			this.district = new CodeableConceptModel(address.getDistrict());
		}
		
		var cityExt = FhirUtils.findExtension(address.getExtension(), ExtensionURL.ADDRESS_CITY);
		if(cityExt != null && cityExt.getValue() instanceof CodeableConcept) {
			this.city = CodeableConceptModel.fromFhir((CodeableConcept) cityExt.getValue());
			
		}else if(address.hasCity()) {
			this.city = new CodeableConceptModel(address.getCity());
		}
		
		if(address.hasPeriod()) {
			this.period = PeriodModel.fromFhir(address.getPeriod());
		}
		
	}
	
	public static AddressModel fromFhir(Address address) {
		if(address != null) {
			return new AddressModel(address);
		}
		return null;
	}
}
