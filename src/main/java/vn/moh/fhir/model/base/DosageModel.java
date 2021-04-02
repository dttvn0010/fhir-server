package vn.moh.fhir.model.base;

import java.util.List;

import org.hl7.fhir.r4.model.Dosage;

import vn.moh.fhir.utils.DataUtils;

public class DosageModel {

	public static class DosageDoseAndRate {
		CodeableConceptModel type;
		QuantityModel dose;
		RatioModel rate;
		
		public Dosage.DosageDoseAndRateComponent toFhir() {
			var doseAndRate = new Dosage.DosageDoseAndRateComponent();
			if(type != null) {
				doseAndRate.setType(type.toFhir());
			}
			if(dose != null) {
				doseAndRate.setDose(dose.toFhir());
			}
			if(rate != null) {
				doseAndRate.setRate(rate.toFhir());
			}
			return doseAndRate;
		}
		
		public DosageDoseAndRate(Dosage.DosageDoseAndRateComponent doseAndRate) {
			if(doseAndRate != null) {
				if(doseAndRate.hasType()) {
					this.type = CodeableConceptModel.fromFhir(doseAndRate.getType());
				}
				if(doseAndRate.hasDoseQuantity()) {
					this.dose = QuantityModel.fromFhir(doseAndRate.getDoseQuantity());
				}
				if(doseAndRate.hasRateRatio()) {
					this.rate = RatioModel.fromFhir(doseAndRate.getRateRatio());
				}
			}
		}
		
		public static DosageDoseAndRate fromFhir(Dosage.DosageDoseAndRateComponent doseAndRate) {
			if(doseAndRate != null) {
				return new DosageDoseAndRate(doseAndRate);
			}
			return null;				
		}
	}
	
	Integer sequence;
	String text;
	List<CodeableConceptModel> additionalInstruction;
	String patientInstruction;
	CodeableConceptModel site;
	CodeableConceptModel route;
	CodeableConceptModel method;
	List<DosageDoseAndRate> doseAndRate;
	RatioModel maxDosePerPeriod;
	QuantityModel maxDosePerAdministration;
	QuantityModel maxDosePerLifetime;
	
	public Dosage toFhir() {
		var dosage = new Dosage();
		if(sequence != null) {
			dosage.setSequence(sequence);
		}
		dosage.setText(text);
		dosage.setAdditionalInstruction(DataUtils.transform(additionalInstruction, CodeableConceptModel::toFhir));
		dosage.setPatientInstruction(patientInstruction);
		
		if(site != null) {
			dosage.setSite(site.toFhir());
		}
		
		if(route != null) {
			dosage.setRoute(route.toFhir());
		}
		
		dosage.setDoseAndRate(DataUtils.transform(doseAndRate, DosageDoseAndRate::toFhir));
		
		if(maxDosePerPeriod != null) {
			dosage.setMaxDosePerPeriod(maxDosePerPeriod.toFhir());
		}
		
		if(maxDosePerAdministration != null) {
			dosage.setMaxDosePerAdministration(maxDosePerAdministration.toFhir());
		}
		
		if(maxDosePerLifetime != null) {
			dosage.setMaxDosePerLifetime(maxDosePerLifetime.toFhir());
		}
		
		return dosage;
	}
	
	 public DosageModel(Dosage dosage) {
		 if(dosage != null) {
			 if(dosage.hasSequence()) {
				 this.sequence = dosage.getSequence();
			 }
			 
			 this.text = dosage.getText();
			 
			 this.additionalInstruction = DataUtils.transform(dosage.getAdditionalInstruction(), CodeableConceptModel::fromFhir);
			 
			 this.patientInstruction = dosage.getPatientInstruction();
			 
			 if(dosage.hasSite()) {
				 this.site = CodeableConceptModel.fromFhir(dosage.getSite());
			 }
			 
			 if(dosage.hasRoute()) {
				 this.route = CodeableConceptModel.fromFhir(dosage.getRoute());
			 }
			 
			 this.doseAndRate = DataUtils.transform(dosage.getDoseAndRate(), DosageDoseAndRate::fromFhir);
			 
			 if(dosage.hasMaxDosePerPeriod()) {
				 this.maxDosePerPeriod = RatioModel.fromFhir(dosage.getMaxDosePerPeriod());
			 }
			 
			 if(dosage.hasMaxDosePerAdministration()) {
				 this.maxDosePerAdministration = QuantityModel.fromFhir(dosage.getMaxDosePerAdministration());
			 }
			 
			 if(dosage.hasMaxDosePerLifetime()) {
				 this.maxDosePerLifetime = QuantityModel.fromFhir(dosage.getMaxDosePerLifetime());
			 }
		 }
	 }
	 
	 public static DosageModel fromFhir(Dosage dosage) {
		 if(dosage != null) {
			 return new DosageModel(dosage);
		 }
		 return null;
	 }
}
