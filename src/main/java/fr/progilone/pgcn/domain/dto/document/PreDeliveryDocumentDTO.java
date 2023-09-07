package fr.progilone.pgcn.domain.dto.document;

import fr.progilone.pgcn.domain.dto.AbstractDTO;
import java.util.Set;

/**
 * Created by lebouchp on 19/01/2017.
 */
public class PreDeliveryDocumentDTO extends AbstractDTO {

    private String digitalId;
    private Integer pageNumber;
    private Set<String> pieces;
    private Set<PreDeliveryDocumentFileDTO> metaDataFiles;

    public PreDeliveryDocumentDTO(final String digitalId, final Integer pageNumber, final Set<String> pieces, final Set<PreDeliveryDocumentFileDTO> metaDataFiles) {
        this.digitalId = digitalId;
        this.pageNumber = pageNumber;
        this.pieces = pieces;
        this.metaDataFiles = metaDataFiles;
    }

    public PreDeliveryDocumentDTO() {

    }

    public String getDigitalId() {
        return digitalId;
    }

    public void setDigitalId(final String digitalId) {
        this.digitalId = digitalId;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(final Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Set<String> getPieces() {
        return pieces;
    }

    public void setPieces(final Set<String> pieces) {
        this.pieces = pieces;
    }

    public Set<PreDeliveryDocumentFileDTO> getMetaDataFiles() {
        return metaDataFiles;
    }

    public void setMetaDataFiles(final Set<PreDeliveryDocumentFileDTO> metaDataFiles) {
        this.metaDataFiles = metaDataFiles;
    }
}
