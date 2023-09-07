package fr.progilone.pgcn.domain.dto.document.conditionreport;

import com.google.common.base.MoreObjects;

public class LightCondReportDetailDTO {

    private String identifier;

    private int nbViewTotal = 0;
    private Integer dim1;
    private Integer dim2;
    private Integer dim3;
    private String insurance;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public int getNbViewTotal() {
        return nbViewTotal;
    }

    public void setNbViewTotal(final int nbViewTotal) {
        this.nbViewTotal = nbViewTotal;
    }

    public Integer getDim1() {
        return dim1;
    }

    public void setDim1(final Integer dim1) {
        this.dim1 = dim1;
    }

    public Integer getDim2() {
        return dim2;
    }

    public void setDim2(final Integer dim2) {
        this.dim2 = dim2;
    }

    public Integer getDim3() {
        return dim3;
    }

    public void setDim3(final Integer dim3) {
        this.dim3 = dim3;
    }

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(final String insurance) {
        this.insurance = insurance;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("identifier", identifier).toString();
    }
}
