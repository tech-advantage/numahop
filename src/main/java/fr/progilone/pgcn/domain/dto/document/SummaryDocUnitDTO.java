package fr.progilone.pgcn.domain.dto.document;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.AbstractDTO;

/**
 * DTO représentant une unité documentaire est ses dépendences
 *
 * @author jbrunet
 * @see DocUnit
 */
public class SummaryDocUnitDTO extends AbstractDTO {

    private String identifier;
    private String pgcnId;
    private String label;
    private String type;
    private Boolean archivable;
    private Boolean distributable;

    public SummaryDocUnitDTO() {
    }

    public SummaryDocUnitDTO(String identifier, String pgcnId, String label, String type, Boolean archivable, Boolean distributable) {
        this.identifier = identifier;
        this.pgcnId = pgcnId;
        this.label = label;
        this.type = type;
        this.archivable = archivable;
        this.distributable = distributable;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPgcnId() {
        return pgcnId;
    }

    public void setPgcnId(String pgcnId) {
        this.pgcnId = pgcnId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getArchivable() {
        return archivable;
    }

    public void setArchivable(Boolean archivable) {
        this.archivable = archivable;
    }

    public Boolean getDistributable() {
        return distributable;
    }

    public void setDistributable(Boolean distributable) {
        this.distributable = distributable;
    }
}
