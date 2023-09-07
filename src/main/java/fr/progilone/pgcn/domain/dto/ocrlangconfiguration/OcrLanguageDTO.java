
package fr.progilone.pgcn.domain.dto.ocrlangconfiguration;

import fr.progilone.pgcn.domain.dto.AbstractDTO;

public class OcrLanguageDTO extends AbstractDTO {

    private String identifier;
    private String label;
    private String code;
    private boolean active = false;

    public OcrLanguageDTO(final String identifier, final String label, final String code, final boolean active) {
        this.identifier = identifier;
        this.label = label;
        this.code = code;
        this.active = active;
    }

    public OcrLanguageDTO() {

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

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

}
