package fr.progilone.pgcn.domain.dto.document;

import fr.progilone.pgcn.domain.dto.AbstractDTO;

import java.util.Set;

/**
 * Created by lebouchp on 19/01/2017.
 */
public class PreDeliveryDocumentDTO extends AbstractDTO {

    private String digitalId;
    private Integer pageNumber;
    private Set<PreDeliveryDocumentFileDTO> metaDataFiles;

    public PreDeliveryDocumentDTO(String digitalId, Integer pageNumber, Set<PreDeliveryDocumentFileDTO> metaDataFiles) {
        this.digitalId = digitalId;
        this.pageNumber = pageNumber;
        this.metaDataFiles = metaDataFiles;
    }
    
    public PreDeliveryDocumentDTO() {
        
    }

    public String getDigitalId() {
        return digitalId;
    }

    public void setDigitalId(String digitalId) {
        this.digitalId = digitalId;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Set<PreDeliveryDocumentFileDTO> getMetaDataFiles() {
        return metaDataFiles;
    }

    public void setMetaDataFiles(Set<PreDeliveryDocumentFileDTO> metaDataFiles) {
        this.metaDataFiles = metaDataFiles;
    }
}
