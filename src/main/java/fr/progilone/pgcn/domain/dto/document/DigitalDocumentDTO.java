package fr.progilone.pgcn.domain.dto.document;

import fr.progilone.pgcn.domain.dto.AbstractVersionedDTO;
import fr.progilone.pgcn.domain.dto.check.AutomaticCheckResultDTO;
import java.util.List;

/**
 * DTO pour les documents numériques
 *
 * @author jbrunet
 *         Créé le 14 mars 2017
 */
public class DigitalDocumentDTO extends AbstractVersionedDTO {

    private String identifier;
    private String digitalId;
    private String checkNotes;
    private int nbPages;
    private String status;
    private SimpleDocUnitDTO docUnit;

    // Retours de contrôles
    private List<AutomaticCheckResultDTO> automaticCheckResults;

    public DigitalDocumentDTO() {
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getDigitalId() {
        return digitalId;
    }

    public void setDigitalId(String digitalId) {
        this.digitalId = digitalId;
    }

    public String getCheckNotes() {
        return checkNotes;
    }

    public void setCheckNotes(String checkNotes) {
        this.checkNotes = checkNotes;
    }

    public int getNbPages() {
        return nbPages;
    }

    public void setNbPages(int nbPages) {
        this.nbPages = nbPages;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SimpleDocUnitDTO getDocUnit() {
        return docUnit;
    }

    public void setDocUnit(SimpleDocUnitDTO docUnit) {
        this.docUnit = docUnit;
    }

    public List<AutomaticCheckResultDTO> getAutomaticCheckResults() {
        return automaticCheckResults;
    }

    public void setAutomaticCheckResults(List<AutomaticCheckResultDTO> checkResults) {
        this.automaticCheckResults = checkResults;
    }
}
