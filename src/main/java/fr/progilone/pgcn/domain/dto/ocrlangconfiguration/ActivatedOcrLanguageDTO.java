package fr.progilone.pgcn.domain.dto.ocrlangconfiguration;

import fr.progilone.pgcn.domain.dto.AbstractVersionedDTO;

public class ActivatedOcrLanguageDTO extends AbstractVersionedDTO {

    private String identifier;
    private OcrLangConfigurationDTO ocrLangConfiguration;
    private OcrLanguageDTO ocrLanguage;

    public ActivatedOcrLanguageDTO(final String identifier, final OcrLangConfigurationDTO ocrLangConfiguration, final OcrLanguageDTO ocrLanguage) {
        this.identifier = identifier;
        this.ocrLangConfiguration = ocrLangConfiguration;
        this.ocrLanguage = ocrLanguage;
    }

    public ActivatedOcrLanguageDTO() {

    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public OcrLangConfigurationDTO getOcrLangConfiguration() {
        return ocrLangConfiguration;
    }

    public void setOcrLangConfiguration(final OcrLangConfigurationDTO ocrLangConfiguration) {
        this.ocrLangConfiguration = ocrLangConfiguration;
    }

    public OcrLanguageDTO getOcrLanguage() {
        return ocrLanguage;
    }

    public void setOcrLanguage(final OcrLanguageDTO ocrLanguage) {
        this.ocrLanguage = ocrLanguage;
    }

}
