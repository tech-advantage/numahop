package fr.progilone.pgcn.domain.dto.check;

import java.time.LocalDateTime;

import fr.progilone.pgcn.domain.dto.AbstractDTO;

/**
 * Résultats de contrôles automatiques
 * 
 * @author jbrunet
 * Créé le 2 mars 2017
 */
public class AutomaticCheckResultDTO extends AbstractDTO {

    private String identifier;
    private AutomaticCheckTypeDTO check;
    private String result;
    private String message;
    private LocalDateTime createdDate;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public AutomaticCheckTypeDTO getCheck() {
        return check;
    }

    public void setCheck(AutomaticCheckTypeDTO check) {
        this.check = check;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
