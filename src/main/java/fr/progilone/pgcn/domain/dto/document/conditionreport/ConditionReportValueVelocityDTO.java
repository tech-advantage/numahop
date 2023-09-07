package fr.progilone.pgcn.domain.dto.document.conditionreport;

import com.google.common.base.MoreObjects;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class ConditionReportValueVelocityDTO {

    private String propertyId;
    private String propertyCode;
    private String propertyLabel;
    private String propertyType;
    private final List<StringValue> values = new ArrayList<>();

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

    public List<StringValue> getValues() {
        return values;
    }

    public void setValues(final List<StringValue> values) {
        this.values.clear();
        this.values.addAll(values);
    }

    public void addValue(final String value, final ValueType type) {
        this.values.add(new StringValue(value, type));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("propertyId", propertyId).add("propertyLabel", propertyLabel).add("values", StringUtils.join(values, ", ")).toString();
    }

    public static final class StringValue {

        private final String value;
        private final ValueType type;

        public StringValue(final String value, final ValueType type) {
            this.value = value;
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public ValueType getType() {
            return type;
        }

        public boolean isComment() {
            return type == ValueType.COMMENT;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("value", value).add("type", type).toString();
        }
    }

    public enum ValueType {
        LIST,
        COMMENT
    }
}
