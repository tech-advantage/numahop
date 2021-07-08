package fr.progilone.pgcn.domain.dto.document;

import fr.progilone.pgcn.domain.dto.AbstractDTO;

/**
 * DTO représentant une propriété
 *
 * @author jbrunet
 * @see fr.progilone.pgcn.domain.document.DocUnit
 */
public class DocPropertyDTO extends AbstractDTO {

    private String identifier;
    private String value;
    private Integer rank;
    private DocPropertyTypeDTO type;
    private Double weightedRank;

    public DocPropertyDTO() {
    }

    public final String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public final String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public final Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public final DocPropertyTypeDTO getType() {
        return type;
    }

    public void setType(DocPropertyTypeDTO type) {
        this.type = type;
    }

    public final Double getWeightedRank() {
        return weightedRank;
    }

    public void setWeightedRank(Double weightedRank) {
        this.weightedRank = weightedRank;
    }
}
