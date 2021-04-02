package vn.moh.fhir.model.base;

import java.util.Date;

import org.hl7.fhir.r4.model.Annotation;

public class AnnotationModel {

	ReferenceModel author;
	Date time;
	String text;
	
	public Annotation toFhir() {
		var ann = new Annotation();
		if(author != null) {
			ann.setAuthor(author.toFhir());
		}
		ann.setTime(time);
		ann.setText(text);
		return ann;
	}
	
	public AnnotationModel(Annotation ann) {
		if(ann != null) {
			if(ann.hasAuthorReference()) {
				this.author = ReferenceModel.fromFhir(ann.getAuthorReference());
			}
			this.time = ann.getTime();
			this.text = ann.getText();
		}
	}
	
	public static AnnotationModel fromFhir(Annotation ann) {
		if(ann != null) {
			return new AnnotationModel(ann);
		}
		return null;
	}
}
