package fr.progilone.pgcn.domain.dto.document;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.AbstractDTO;
import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;
import fr.progilone.pgcn.domain.dto.lot.SimpleLotDTO;
import fr.progilone.pgcn.domain.dto.project.SimpleProjectDTO;
import fr.progilone.pgcn.domain.dto.train.SimpleTrainDTO;

/**
 * DTO représentant une unité documentaire pour les listes
 *
 * @author jbrunet
 * @see DocUnit
 */
public class SimpleListDocUnitDTO extends AbstractDTO {

    private String identifier;
    private String label;
    private String pgcnId;
    private SimpleLibraryDTO library;
    private SimpleProjectDTO project;
    private SimpleLotDTO lot;
    private SimpleTrainDTO train;
    private String parentIdentifier;
    private String parentLabel;
    private String parentPgcnId;
    private boolean hasRecord;
    private String digitalDocumentStatus;
    private boolean changeTrainAuthorized;
    

    public SimpleListDocUnitDTO() {
    }

    public SimpleListDocUnitDTO(final String identifier,
                                final String label,
                                final String pgcnId,
                                final SimpleLibraryDTO library,
                                final SimpleProjectDTO project,
                                final SimpleLotDTO lot,
                                final SimpleTrainDTO train) {
        super();
        this.identifier = identifier;
        this.label = label;
        this.pgcnId = pgcnId;
        this.library = library;
        this.project = project;
        this.lot = lot;
        this.train = train;
    }

    public SimpleTrainDTO getTrain() {
        return train;
    }

    public void setTrain(final SimpleTrainDTO train) {
        this.train = train;
    }

    public final String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public final String getPgcnId() {
        return pgcnId;
    }

    public void setPgcnId(final String pgcnId) {
        this.pgcnId = pgcnId;
    }

    public final String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public final SimpleLibraryDTO getLibrary() {
        return library;
    }

    public void setLibrary(final SimpleLibraryDTO library) {
        this.library = library;
    }

    public final SimpleProjectDTO getProject() {
        return project;
    }

    public void setProject(final SimpleProjectDTO project) {
        this.project = project;
    }

    public SimpleLotDTO getLot() {
        return lot;
    }

    public void setLot(final SimpleLotDTO lot) {
        this.lot = lot;
    }

    public String getParentIdentifier() {
        return parentIdentifier;
    }

    public void setParentIdentifier(final String parentIdentifier) {
        this.parentIdentifier = parentIdentifier;
    }

    public String getParentLabel() {
        return parentLabel;
    }

    public void setParentLabel(final String parentLabel) {
        this.parentLabel = parentLabel;
    }

    public String getParentPgcnId() {
        return parentPgcnId;
    }

    public void setParentPgcnId(final String parentPgcnId) {
        this.parentPgcnId = parentPgcnId;
    }

    public boolean isHasRecord() {
        return hasRecord;
    }

    public void setHasRecord(final boolean hasRecord) {
        this.hasRecord = hasRecord;
    }

    public String getDigitalDocumentStatus() {
        return digitalDocumentStatus;
    }

    public void setDigitalDocumentStatus(final String digitalDocumentStatus) {
        this.digitalDocumentStatus = digitalDocumentStatus;
    }

    public boolean isChangeTrainAuthorized() {
        return changeTrainAuthorized;
    }

    public void setChangeTrainAuthorized(final boolean changeTrainAuthorized) {
        this.changeTrainAuthorized = changeTrainAuthorized;
    }

}
