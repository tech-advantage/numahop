package fr.progilone.pgcn.domain.dto.checkconfiguration;

import java.util.ArrayList;
import java.util.List;

import fr.progilone.pgcn.domain.dto.AbstractVersionedDTO;
import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;

public class CheckConfigurationDTO extends AbstractVersionedDTO {

    private String identifier;
    private String label;
    private SimpleLibraryDTO library;
    private Double minorErrorRate;
    private Double majorErrorRate;
    private Double sampleRate;
    private String sampleMode;
    private String separators;
    
    // contrôles auto
    private List<AutomaticCheckRuleDTO> automaticCheckRules;

    public CheckConfigurationDTO(String identifier,
                                 String label,
                                 SimpleLibraryDTO library,
                                 Double minorErrorRate,
                                 Double majorErrorRate,
                                 Double sampleRate,
                                 String sampleMode,
                                 String separators,
                                 List<AutomaticCheckRuleDTO> automaticCheckRules) {
        this.identifier = identifier;
        this.label = label;
        this.library = library;
        this.minorErrorRate = minorErrorRate;
        this.majorErrorRate = majorErrorRate;
        this.sampleRate = sampleRate;
        this.sampleMode = sampleMode;
        this.separators = separators;
        if (automaticCheckRules != null) {
            this.automaticCheckRules = automaticCheckRules;
        } else {
            this.automaticCheckRules = new ArrayList<>();
        }
    } 

    public CheckConfigurationDTO() {

    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public SimpleLibraryDTO getLibrary() {
        return library;
    }

    public void setLibrary(SimpleLibraryDTO library) {
        this.library = library;
    }

    public Double getMinorErrorRate() {
        return minorErrorRate;
    }

    public void setMinorErrorRate(Double minorErrorRate) {
        this.minorErrorRate = minorErrorRate;
    }

    public Double getMajorErrorRate() {
        return majorErrorRate;
    }

    public void setMajorErrorRate(Double majorErrorRate) {
        this.majorErrorRate = majorErrorRate;
    }

    public Double getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(Double sampleRate) {
        this.sampleRate = sampleRate;
    }

    public String getSampleMode() {
        return sampleMode;
    }

    public void setSampleMode(String sampleMode) {
        this.sampleMode = sampleMode;
    }

    public String getSeparators() {
        return separators;
    }

    public void setSeparators(String separators) {
        this.separators = separators;
    }

    public List<AutomaticCheckRuleDTO> getAutomaticCheckRules() {
        return automaticCheckRules;
    }

    public void setAutomaticCheckRules(List<AutomaticCheckRuleDTO> automaticCheckRules) {
        this.automaticCheckRules = automaticCheckRules;
    }

    public static final class Builder {
        private String identifier;
        private String label;
        private SimpleLibraryDTO library;
        private Double minorErrorRate;
        private Double majorErrorRate;
        private Double sampleRate;
        private String sampleMode;
        private String separators;
        private List<AutomaticCheckRuleDTO> automaticCheckRules;

        public Builder init() {
            this.identifier = null;
            this.label = null;
            this.library = null;
            this.minorErrorRate = null;
            this.majorErrorRate = null;
            this.sampleRate = null;
            this.sampleMode = null;
            this.separators = null;
            this.automaticCheckRules = null;
            return this;
        }

        public Builder setIdentifier(String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder setLabel(String label) {
            this.label = label;
            return this;
        }

        public Builder setLibrary(SimpleLibraryDTO library) {
            this.library = library;
            return this;
        }

        public Builder setMinorErrorRate(Double minorErrorRate) {
            this.minorErrorRate = minorErrorRate;
            return this;
        }

        public Builder setMajorErrorRate(Double majorErrorRate) {
            this.majorErrorRate = majorErrorRate;
            return this;
        }

        public Builder setSampleRate(Double sampleRate) {
            this.sampleRate = sampleRate;
            return this;
        }
        
        public Builder setSampleMode(String sampleMode) {
            this.sampleMode = sampleMode;
            return this;
        }
        
        public Builder setSeparators(String separators) {
            this.separators = separators;
            return this;
        }
        
        public Builder setAutomaticCheckRules(List<AutomaticCheckRuleDTO> automaticCheckRules) {
            this.automaticCheckRules = automaticCheckRules;
            return this;
        }
        

        public CheckConfigurationDTO build() {
            return new CheckConfigurationDTO(identifier, label, library, minorErrorRate, majorErrorRate, sampleRate, sampleMode, separators, automaticCheckRules);
        }
    }
}
