package fr.progilone.pgcn.domain.dto.document.conditionreport;

import com.google.common.base.MoreObjects;
import fr.progilone.pgcn.domain.dto.AbstractVersionedDTO;
import java.util.Set;

public class PropertyConfigurationDTO extends AbstractVersionedDTO {

    private String identifier;
    private boolean required;
    private boolean allowComment;
    private boolean showOnCreation;
    private Set<String> types;
    private String descPropertyId;
    private String descPropertyLabel;
    private String internalProperty;
    private LibraryDTO library;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(final boolean required) {
        this.required = required;
    }

    public boolean isAllowComment() {
        return allowComment;
    }

    public void setAllowComment(final boolean allowComment) {
        this.allowComment = allowComment;
    }

    public boolean isShowOnCreation() {
        return showOnCreation;
    }

    public void setShowOnCreation(final boolean showOnCreation) {
        this.showOnCreation = showOnCreation;
    }

    public Set<String> getTypes() {
        return types;
    }

    public void setTypes(final Set<String> types) {
        this.types = types;
    }

    public String getDescPropertyId() {
        return descPropertyId;
    }

    public void setDescPropertyId(final String descPropertyId) {
        this.descPropertyId = descPropertyId;
    }

    public String getDescPropertyLabel() {
        return descPropertyLabel;
    }

    public void setDescPropertyLabel(final String descPropertyLabel) {
        this.descPropertyLabel = descPropertyLabel;
    }

    public String getInternalProperty() {
        return internalProperty;
    }

    public void setInternalProperty(final String internalProperty) {
        this.internalProperty = internalProperty;
    }

    public LibraryDTO getLibrary() {
        return library;
    }

    public void setLibrary(final LibraryDTO library) {
        this.library = library;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("identifier", identifier).toString();
    }

    public static class LibraryDTO {

        private String identifier;
        private String name;

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(final String identifier) {
            this.identifier = identifier;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("identifier", identifier).add("name", name).toString();
        }
    }
}
