package fr.progilone.pgcn.domain.dto.document;

import org.apache.commons.lang3.StringUtils;

import fr.progilone.pgcn.domain.dto.AbstractDTO;

public class DocPageDTO extends AbstractDTO implements Comparable<DocPageDTO> {
    
    private String identifier;
    private Integer number;
    private String checkNotes;
    private SimpleDigitalDocumentDTO digitalDocument;
    
    public String getIdentifier() {
        return identifier;
    }
    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }
    public Integer getNumber() {
        return number;
    }
    public void setNumber(final Integer number) {
        this.number = number;
    }
    public String getCheckNotes() {
        return checkNotes;
    }
    public void setCheckNotes(final String checkNotes) {
        this.checkNotes = checkNotes;
    }
    public SimpleDigitalDocumentDTO getDigitalDocument() {
        return digitalDocument;
    }
    public void setDigitalDocument(final SimpleDigitalDocumentDTO digitalDocument) {
        this.digitalDocument = digitalDocument;
    }
    
    @Override
    public int compareTo(final DocPageDTO o) {
        
        if (getDigitalDocument().getDigitalId().equals(o.getDigitalDocument().getDigitalId())) {
            return getNumber().compareTo(o.getNumber());
        } else {
            return getDigitalDocument().getDigitalId().compareTo(o.getDigitalDocument().getDigitalId());
        }
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        final DocPageDTO otherDto = (DocPageDTO) obj;
        if ( ! StringUtils.equals(identifier, otherDto.getIdentifier())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
}
