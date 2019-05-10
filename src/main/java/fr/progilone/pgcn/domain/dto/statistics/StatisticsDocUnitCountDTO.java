package fr.progilone.pgcn.domain.dto.statistics;

public class StatisticsDocUnitCountDTO {

    private String identifier;
    private String pgcnId;
    private boolean ia;
    private boolean cines;
    private Integer totalPage;
    private Long totalLength;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getPgcnId() {
        return pgcnId;
    }

    public void setPgcnId(final String pgcnId) {
        this.pgcnId = pgcnId;
    }

    public boolean isIa() {
        return ia;
    }

    public void setIa(final boolean ia) {
        this.ia = ia;
    }

    public boolean isCines() {
        return cines;
    }

    public void setCines(final boolean cines) {
        this.cines = cines;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(final Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Long getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(final Long totalLength) {
        this.totalLength = totalLength;
    }
}
