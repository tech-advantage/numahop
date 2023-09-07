package fr.progilone.pgcn.domain.dto.document;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.AbstractDTO;

/**
 * DTO représentant une unité documentaire min
 *
 * @author manu
 * @see DocUnit
 */
public class MinimalListDocUnitDTO extends AbstractDTO {

    private String identifier;
    private String label;
    private String pgcnId;

    public MinimalListDocUnitDTO() {
    }

    public MinimalListDocUnitDTO(final String identifier, final String label, final String pgcnId) {
        super();
        this.identifier = identifier;
        this.label = label;
        this.pgcnId = pgcnId;
    }

    public final String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public final String getPgcnId() {
        return pgcnId;
    }

    public void setPgcnId(final String pgcnId) {
        this.pgcnId = pgcnId;
    }

    public final String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

}
