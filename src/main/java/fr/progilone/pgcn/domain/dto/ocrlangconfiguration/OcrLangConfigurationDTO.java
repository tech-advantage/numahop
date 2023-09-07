package fr.progilone.pgcn.domain.dto.ocrlangconfiguration;

import fr.progilone.pgcn.domain.dto.AbstractVersionedDTO;
import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;
import java.util.List;

public class OcrLangConfigurationDTO extends AbstractVersionedDTO {

    private String identifier;
    private String label;
    // private String description;
    private SimpleLibraryDTO library;
    private boolean active;

    private List<OcrLanguageDTO> ocrLanguages;

    public OcrLangConfigurationDTO(final String identifier, final String label, final SimpleLibraryDTO library, final boolean active, final List<OcrLanguageDTO> ocrLanguages) {
        this.identifier = identifier;
        this.label = label;
        this.library = library;
        this.active = active;
        this.ocrLanguages = ocrLanguages;
    }

    public OcrLangConfigurationDTO() {

    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public SimpleLibraryDTO getLibrary() {
        return library;
    }

    public void setLibrary(final SimpleLibraryDTO library) {
        this.library = library;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public List<OcrLanguageDTO> getOcrLanguages() {
        return ocrLanguages;
    }

    public void setOcrLanguages(final List<OcrLanguageDTO> ocrLanguages) {
        this.ocrLanguages = ocrLanguages;
    }

    public static final class Builder {

        private String identifier;
        private String label;
        private SimpleLibraryDTO library;
        private boolean active;
        private List<OcrLanguageDTO> ocrLanguages;

        public Builder init() {
            this.identifier = null;
            this.label = null;
            this.library = null;
            this.active = false;
            this.ocrLanguages = null;
            return this;
        }

        public Builder setIdentifier(final String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder setLabel(final String label) {
            this.label = label;
            return this;
        }

        public Builder setLibrary(final SimpleLibraryDTO library) {
            this.library = library;
            return this;
        }

        public Builder setOcrLanguages(final List<OcrLanguageDTO> ocrLanguages) {
            this.ocrLanguages = ocrLanguages;
            return this;
        }

        public OcrLangConfigurationDTO build() {
            return new OcrLangConfigurationDTO(identifier, label, library, active, ocrLanguages);
        }
    }
}
