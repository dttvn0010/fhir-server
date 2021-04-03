package vn.gov.moh.fhir.model.base;

import java.util.Date;

import org.hl7.fhir.r4.model.Attachment;

public class AttachmentModel {
    String contentType;
    String language;
    byte[] data;
    String url;
    Integer size;
    byte[] hash;
    String title;
    Date creation;
    
    public Attachment toFhir() {
        var attachment = new Attachment();
        attachment.setContentType(contentType);
        attachment.setLanguage(language);
        attachment.setData(data);
        attachment.setUrl(url);
        
        if(size != null) {
            attachment.setSize(size);
        }
        
        attachment.setHash(hash);
        attachment.setTitle(title);
        attachment.setCreation(creation);
        return attachment;        
    }
    
    public AttachmentModel() {
        
    }
    
    public AttachmentModel(Attachment attachment) {
        if(attachment !=  null) {
            this.contentType = attachment.getContentType();
            this.language = attachment.getLanguage();
            this.data = attachment.getData();
            this.url = attachment.getUrl();
            if(attachment.hasSize()) {
                this.size = attachment.getSize();
            }
            this.hash = attachment.getHash();
            this.title = attachment.getTitle();
            this.creation = attachment.getCreation();
        }        
    }
    
    public static AttachmentModel fromFhir(Attachment attachment) {
        if(attachment != null) {
            return new AttachmentModel(attachment);
        }
        return null;
    }
}
