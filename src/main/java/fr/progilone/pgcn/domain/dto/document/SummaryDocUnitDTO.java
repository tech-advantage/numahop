package fr.progilone.pgcn.domain.dto.document;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.AbstractDTO;
import fr.progilone.pgcn.domain.dto.library.LibraryDTO;

/**
 * DTO représentant une unité documentaire est ses dépendences
 *
 * @author jbrunet
 * @see DocUnit
 */
public class SummaryDocUnitDTO extends AbstractDTO {

    private String identifier;
    private LibraryDTO library;
    private String pgcnId;
    private String label;
    private String type;
    private Boolean archivable;
    private Boolean distributable;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public LibraryDTO getLibrary() {
        return library;
    }

    public void setLibrary(LibraryDTO library) {
        this.library = library;
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
