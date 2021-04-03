package vn.gov.moh.fhir.model.base;

import java.util.Date;

import org.hl7.fhir.r4.model.Period;

public class PeriodModel {

    Date start;
    Date end;
    
    public Period toFhir() {
        var period = new Period();
        period.setStart(start);
        period.setEnd(end);
        return period;
    }
    
    public PeriodModel() {
        
    }
    
    public PeriodModel(Date start, Date end    ) {
        this.start = start;
        this.end = end;
    }
    
    public PeriodModel(Period period) {
        if(period  != null) {
            this.start = period.getStart();
            this.end = period.getEnd();
        }
    }
    
    public static PeriodModel fromFhir(Period period) {
        if(period != null) {
            return new PeriodModel(period);
        }
        return null;
    }
    
    public Date getStart() {
        return start;
    }
    
    public Date getEnd() {
        return end;
    }
}
