package vn.moh.fhir.model.base;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointUse;

public class ContactPointModel {

	String system;
	String value;
	String use;
	Integer rank;
	PeriodModel period;
	
	public ContactPoint toFhir() {
		var contactPoint = new ContactPoint();
		
		if(!StringUtils.isEmpty(system)) {
			contactPoint.setSystem(ContactPointSystem.fromCode(system));
		}
		
		contactPoint.setValue(value);
		
		if(!StringUtils.isEmpty(use)) {
			contactPoint.setUse(ContactPointUse.fromCode(use));
		}
		
		if(rank != null) {
			contactPoint.setRank(rank);
		}
		
		if(period != null) {
			contactPoint.setPeriod(period.toFhir());
		}
		
		return contactPoint;
	}
	
	public ContactPointModel(ContactPoint contactPoint) {
		if(contactPoint != null) {
			if(contactPoint.hasSystem()) {
				this.system = contactPoint.getSystem().toCode();
			}
			
			this.value = contactPoint.getValue();
			
			if(contactPoint.hasUse()) {
				this.use = contactPoint.getUse().toCode();
			}
			
			if(contactPoint.hasRank()) {
				this.rank = contactPoint.getRank();
			}
			
			if(contactPoint.hasPeriod()) {
				this.period = PeriodModel.fromFhir(contactPoint.getPeriod());
			}
		}
	}
	
	public static ContactPointModel fromFhir(ContactPoint contactPoint) {
		if(contactPoint != null) {
			return new ContactPointModel(contactPoint);
		}
		return null;
	}
}
