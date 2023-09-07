package fr.progilone.pgcn.domain.dto.document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;

public class BibliographicRecordMassUpdateDTO {

    private final List<String> recordIds = new ArrayList<>();
    private final List<Update> fields = new ArrayList<>();
    private final List<Update> properties = new ArrayList<>();

    public List<String> getRecordIds() {
        return recordIds;
    }

    public void setRecordIds(final List<String> recordIds) {
        this.recordIds.clear();
        recordIds.forEach(this::addRecordId);
    }

    private void addRecordId(final String recordId) {
        if (recordId != null) {
            this.recordIds.add(recordId);
        }
    }

    public List<Update> getFields() {
        return fields;
    }

    public void setFields(final List<Update> fields) {
        this.fields.clear();
        fields.forEach(this::addFieldUpdate);
    }

    private void addFieldUpdate(final Update update) {
        if (update != null) {
            this.fields.add(update);
        }
    }

    public List<Update> getProperties() {
        return properties;
    }

    public void setProperties(final List<Update> properties) {
        this.properties.clear();
        properties.forEach(this::addPropertyUpdate);
    }

    private void addPropertyUpdate(final Update update) {
        if (update != null) {
            this.properties.add(update);
        }
    }

    @JsonIgnore
    public boolean isEmpty() {
        return this.recordIds.isEmpty() || (this.fields.isEmpty() && this.properties.isEmpty());
    }

    public static class Update {

        private String type;
        private String value;

        public String getType() {
            return type;
        }

        public void setType(final String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(final String value) {
            this.value = value;
        }
    }
}
