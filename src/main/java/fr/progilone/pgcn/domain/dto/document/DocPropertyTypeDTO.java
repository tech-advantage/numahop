package fr.progilone.pgcn.domain.dto.document;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.AbstractDTO;

/**
 * DTO représentant un type de propriété de doc
 * 
 * @author jbrunet
 * @see DocUnit
 */
public class DocPropertyTypeDTO extends AbstractDTO {

	private String identifier;
	private String label;
    private String superType;
    private Integer rank;
    
    public DocPropertyTypeDTO(String identifier, String label, String superType, Integer rank) {
		super();
		this.identifier = identifier;
		this.label = label;
		this.superType = superType;
		this.rank = rank;
	}



	public final String getIdentifier() {
		return identifier;
	}

	public final void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public final String getLabel() {
		return label;
	}

	public final void setLabel(String label) {
		this.label = label;
	}

	public final String getSuperType() {
		return superType;
	}



	public final void setSuperType(String superType) {
		this.superType = superType;
	}



	public final Integer getRank() {
		return rank;
	}



	public final void setRank(Integer rank) {
		this.rank = rank;
	}



	public DocPropertyTypeDTO() {
    }
}
