package fr.progilone.pgcn.domain.dto.document.conditionreport;

import com.google.common.base.MoreObjects;

public class ConditionReportValueDTO {

    private String propertyId;
    private String propertyCode;
    private String propertyLabel;
    private String propertyType;
    private int propertyOrder;
    private String value;
    private String comment;

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(final String propertyId) {
        this.propertyId = propertyId;
    }

    public String getPropertyCode() {
        return propertyCode;
    }

    public void setPropertyCode(final String propertyCode) {
        this.propertyCode = propertyCode;
    }

    public String getPropertyLabel() {
        return propertyLabel;
    }

    public void setPropertyLabel(final String propertyLabel) {
        this.propertyLabel = propertyLabel;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(final String propertyType) {
        this.propertyType = propertyType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public int getPropertyOrder() {
        return propertyOrder;
    }

    public void setPropertyOrder(final int propertyOrder) {
        this.propertyOrder = propertyOrder;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("propertyId", propertyId).add("propertyLabel", propertyLabel).add("value", value).toString();
    }
}
