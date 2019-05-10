package fr.progilone.pgcn.domain.dto.document;

import fr.progilone.pgcn.domain.dto.AbstractDTO;

/**
 * Created by lebouchp on 10/02/2017.
 */
public class SimpleDocPageDTO extends AbstractDTO {

    private String identifier;
    private Integer number;
    private String checkNotes;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getCheckNotes() {
        return checkNotes;
    }

    public void setCheckNotes(String checkNotes) {
        this.checkNotes = checkNotes;
    }
}
