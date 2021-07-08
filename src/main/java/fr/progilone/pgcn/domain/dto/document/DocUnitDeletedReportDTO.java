package fr.progilone.pgcn.domain.dto.document;

import fr.progilone.pgcn.domain.dto.AbstractDTO;

/**
 * Un DTO contenant des informations sur les échecs de suppression d'une unité documentaire
 *
 */
public class DocUnitDeletedReportDTO extends AbstractDTO {

    /**
     * Identifiant de l'unité documentaire
     */
    private String identifier;

    /**
     * Label de l'unité documentaire
     */
    private String label;

    public DocUnitDeletedReportDTO(final String identifier,
                                final String label) {
        this.identifier = identifier;
        this.label = label;
    }

	public final String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public final String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
