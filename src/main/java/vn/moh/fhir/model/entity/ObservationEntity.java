package vn.moh.fhir.model.entity;


import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Observation.ObservationStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Range;
import org.hl7.fhir.r4.model.Ratio;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;

import vn.moh.fhir.model.base.CodeableConceptModel;
import vn.moh.fhir.model.base.IdentifierModel;
import vn.moh.fhir.model.base.PeriodModel;
import vn.moh.fhir.model.base.QuantityModel;
import vn.moh.fhir.model.base.RangeModel;
import vn.moh.fhir.model.base.RatioModel;
import vn.moh.fhir.model.base.ReferenceModel;
import vn.moh.fhir.utils.DataUtils;

@JsonInclude(Include.NON_NULL)
@Document(collection = "observation")
@CompoundIndex(def = "{'id':1, '_active':1, '_version':1, 'patient.reference':1, 'encounter.reference':1}", name = "index_by_default")
public class ObservationEntity {
    
    public static class ObservationValueType {
        final public static String QUANTITY = "Quantity";
        final public static String CODEABLE_CONCEPT = "CodeableConcept";
        final public static String STRING = "String";
        final public static String BOOLEAN = "Boolean";
        final public static String INTEGER = "Integer";
        final public static String RANGE = "Range";
        final public static String RATIO = "Ratio";
        final public static String DATE_TIME = "DateTime";
        final public static String PERIOD = "Period";
    }
    
    public static class ObservationValue {
        String datatype;
        QuantityModel valueQuantity;
        CodeableConceptModel valueCodeableConcept;
        String valueString;
        Boolean valueBoolean;
        Integer valueInteger;
        RangeModel valueRange;
        RatioModel valueRatio;
        Date valueDateTime;
        PeriodModel valuePeriod;        
        
        public Type toFhir() {
            if(ObservationValueType.QUANTITY.endsWith(datatype) && valueQuantity != null) {
                return valueQuantity.toFhir();
                
            }else if(ObservationValueType.CODEABLE_CONCEPT.endsWith(datatype) && valueCodeableConcept != null) {
                return valueCodeableConcept.toFhir();
                
            }else if(ObservationValueType.RANGE.endsWith(datatype) && valueRange != null) {
                return valueRange.toFhir();
                
            }else if(ObservationValueType.RATIO.endsWith(datatype) && valueRatio != null) {
                return valueRatio.toFhir();
                
            }else if(ObservationValueType.DATE_TIME.endsWith(datatype) && valueDateTime != null) {
                return new DateTimeType(valueDateTime);
                
            }else if(ObservationValueType.PERIOD.endsWith(datatype) && valuePeriod != null) {
                return valuePeriod.toFhir();
                
            }else if(ObservationValueType.STRING.endsWith(datatype) && valueString != null) {
                return new StringType(valueString);
                
            }else if(ObservationValueType.INTEGER.endsWith(datatype) && valueInteger != null) {
                return new IntegerType(valueInteger);
                
            }else if(ObservationValueType.BOOLEAN.endsWith(datatype) && valueBoolean != null) {
                return new BooleanType(valueBoolean);                
            }
            return null;
        }
        
        public ObservationValue(Type value) {
            if(value == null) return;
            
            if(value instanceof Quantity) {
                datatype = ObservationValueType.QUANTITY;
                valueQuantity = QuantityModel.fromFhir((Quantity)value);
                
            }else if(value instanceof CodeableConcept) {
                datatype = ObservationValueType.CODEABLE_CONCEPT;
                valueCodeableConcept = CodeableConceptModel.fromFhir((CodeableConcept) value);
                
            }else if(value instanceof Range) {
                datatype = ObservationValueType.RANGE;
                valueRange = RangeModel.fromFhir((Range) value);
                
            }else if(value instanceof Ratio) {
                datatype = ObservationValueType.RATIO;
                valueRatio = RatioModel.fromFhir((Ratio) value);
                
            }else if(value instanceof DateTimeType) {
                datatype = ObservationValueType.DATE_TIME;
                valueDateTime = ((DateTimeType) value).getValue();
                
            }else if(value instanceof Period) {
                datatype = ObservationValueType.PERIOD;
                valuePeriod = PeriodModel.fromFhir((Period) value);
                
            }else if(value instanceof StringType) {
                datatype = ObservationValueType.STRING;
                valueString = ((StringType) value).getValue();
                
            }else if(value instanceof IntegerType) {
                datatype = ObservationValueType.INTEGER;
                valueInteger = ((IntegerType) value).getValue();
                
            }else if(value instanceof BooleanType) {
                datatype = ObservationValueType.BOOLEAN;
                valueBoolean = ((BooleanType) value).getValue();                
            }
        }
        
        public static ObservationValue fromFhir(Type value) {
            if(value != null) {
                return new ObservationValue(value);
            }
            return null;
        }
    }
    
    public static class ObservationReferenceRange {
        QuantityModel low;
        QuantityModel high;
        CodeableConceptModel type;
        List<CodeableConceptModel> appliesTo;
        RangeModel age;
        String text;
        
        public Observation.ObservationReferenceRangeComponent toFhir() {
            var range = new Observation.ObservationReferenceRangeComponent();
            
            if(low != null) {
                range.setLow(low.toFhir());
            }
            
            if(high != null) {
                range.setHigh(high.toFhir());
            }
            
            if(type != null) {
                range.setType(type.toFhir());
            }
            
            range.setAppliesTo(DataUtils.transform(appliesTo, CodeableConceptModel::toFhir));
            
            if(age != null) {
                range.setAge(age.toFhir());
            }
            
            range.setText(text);
            
            return range;
        }
        
        public ObservationReferenceRange(Observation.ObservationReferenceRangeComponent range) {
            if(range != null) {
                if(range.hasLow()) {
                    this.low = QuantityModel.fromFhir(range.getLow());
                }
                
                if(range.hasHigh()) {
                    this.high = QuantityModel.fromFhir(range.getHigh());
                }
                
                if(range.hasType()) {
                    this.type = CodeableConceptModel.fromFhir(range.getType());
                }
                
                this.appliesTo = DataUtils.transform(range.getAppliesTo(), CodeableConceptModel::fromFhir);
                
                if(range.hasAge()) {
                    this.age = RangeModel.fromFhir(range.getAge());
                }
                
                this.text = range.getText();
            }
        }
        
        public static ObservationReferenceRange fromFhir(Observation.ObservationReferenceRangeComponent range) {
            if(range != null) {
                return new ObservationReferenceRange(range);
            }
            
            return null;
        }
    
    }
    
    public static class ObservationComponent {
        
        CodeableConceptModel code;
        ObservationValue value;
        CodeableConceptModel dataAbsentReason;
        List<CodeableConceptModel> interpretation;
        List<ObservationReferenceRange> referenceRange;
        
        public Observation.ObservationComponentComponent toFhir() {
            var comp = new Observation.ObservationComponentComponent();
            
            if(code != null) {
                comp.setCode(code.toFhir());
            }
            
            if(value != null) {
                comp.setValue(value.toFhir());
            }
            
            if(dataAbsentReason != null) {
                comp.setDataAbsentReason(dataAbsentReason.toFhir());
            }
            
            comp.setInterpretation(DataUtils.transform(interpretation, CodeableConceptModel::toFhir));
            comp.setReferenceRange(DataUtils.transform(referenceRange, ObservationReferenceRange::toFhir));            
                        
            return comp;
        }
        
        public ObservationComponent(Observation.ObservationComponentComponent comp) {
            if(comp != null) {
                if(comp.hasCode()) {
                    this.code = CodeableConceptModel.fromFhir(comp.getCode());
                }
                
                if(comp.hasValue()) {
                    this.value = ObservationValue.fromFhir(comp.getValue());
                }
                
                if(comp.hasDataAbsentReason()) {
                    this.dataAbsentReason = CodeableConceptModel.fromFhir(comp.getDataAbsentReason());
                }
                
                this.interpretation = DataUtils.transform(comp.getInterpretation(), CodeableConceptModel::fromFhir);
                this.referenceRange = DataUtils.transform(comp.getReferenceRange(), ObservationReferenceRange::fromFhir);
                
            }
        }
        
        public static ObservationComponent fromFhir(Observation.ObservationComponentComponent comp) {
            if(comp != null) {
                return new ObservationComponent(comp);
            }
        
            return null;            
        }
    }
    
    @Id public ObjectId _id;
    String id;
    int _version;
    boolean _active;
    
    List<IdentifierModel> identifier;
    List<ReferenceModel> basedOn;
    List<ReferenceModel> partOf;
    String status;
    List<CodeableConceptModel> category;
    CodeableConceptModel code;
    ReferenceModel patient;
    ReferenceModel encounter;
    Date effectiveStart;    
    Date effectiveEnd;
    Date issue;
    List<ReferenceModel> performer;
    ObservationValue value;    
    
    public Observation toFhir() {
        var obs = new Observation();
        
        obs.setId(id);
        obs.setIdentifier(DataUtils.transform(identifier, IdentifierModel::toFhir));        
        obs.setBasedOn(DataUtils.transform(basedOn, ReferenceModel::toFhir));
        obs.setPartOf(DataUtils.transform(partOf, ReferenceModel::toFhir));
        
        if(!StringUtils.isEmpty(status)) {
            obs.setStatus(ObservationStatus.fromCode(status));
        }
        
        obs.setCategory(DataUtils.transform(category, CodeableConceptModel::toFhir));
        
        if(code != null) {
            obs.setCode(code.toFhir());
        }
        
        if(patient != null) {
            obs.setSubject(patient.toFhir());
        }
        
        if(encounter != null) {
            obs.setEncounter(encounter.toFhir());
        }
                    
        if(effectiveStart != null && effectiveEnd == null) {
            obs.setEffective(new DateTimeType(effectiveStart));
            
        }else if(effectiveStart != null || effectiveEnd != null) {
            var period = new PeriodModel(effectiveStart, effectiveEnd);
            obs.setEffective(period.toFhir());
        }
        
        obs.setIssued(issue);
        
        obs.setPerformer(DataUtils.transform(performer, ReferenceModel::toFhir));
        
        if(value != null) {
            obs.setValue(value.toFhir());
        }
        
        return obs;
    }
    
    public ObservationEntity(Observation obs) {
        if(obs != null) {
            
            this.id = obs.getId();
            this.identifier = DataUtils.transform(obs.getIdentifier(), IdentifierModel::fromFhir);
            this.basedOn = DataUtils.transform(obs.getBasedOn(), ReferenceModel::fromFhir);
            this.partOf = DataUtils.transform(obs.getPartOf(), ReferenceModel::fromFhir);
            
            if(obs.hasStatus()) {
                this.status = obs.getStatus().toCode();
            }
            
            if(obs.hasSubject()) {
                this.patient = ReferenceModel.fromFhir(obs.getSubject());
            }
            
            if(obs.hasEncounter()) {
                this.encounter = ReferenceModel.fromFhir(obs.getEncounter());
            }
            
            if(obs.hasEffectiveDateTimeType()) {
                this.effectiveStart = obs.getEffectiveDateTimeType().getValue();
                
            }else if(obs.hasEffectiveInstantType()) {
                this.effectiveStart = obs.getEffectiveInstantType().getValue();
                
            }else if(obs.hasEffectivePeriod()) {
                var period = PeriodModel.fromFhir(obs.getEffectivePeriod());
                this.effectiveStart = period.getStart();
                this.effectiveEnd = period.getEnd();
            }
            
            this.issue = obs.getIssued();
            
            this.performer = DataUtils.transform(obs.getPerformer(), ReferenceModel::fromFhir);
            
            if(obs.hasValue()) {
                this.value = ObservationValue.fromFhir(obs.getValue());
            }
        }
    }
    
    public static ObservationEntity fromFhir(Observation obs) {
        if(obs != null) {
            return new ObservationEntity(obs);
        }
        
        return null;
    }    
}
