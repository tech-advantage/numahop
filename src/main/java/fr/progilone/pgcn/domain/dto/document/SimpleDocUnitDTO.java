package fr.progilone.pgcn.domain.dto.document;

import java.util.List;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.AbstractDTO;
import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;

/**
 * DTO représentant une unité documentaire pour les select
 *
 * @author jbrunet
 * @see DocUnit
 */
public class SimpleDocUnitDTO extends AbstractDTO {

    private String identifier;
    private String label;
    private String pgcnId;
    private String digitizingNotes; 
    private SimpleLibraryDTO library;
    
    private List<SimpleDocUnitDTO> children;
    private String parentIdentifier;

    public SimpleDocUnitDTO(final String identifier, final String label, final String pgcnId, 
                            final String digitizingNotes, final SimpleLibraryDTO library, 
                            final List<SimpleDocUnitDTO> children, final String parentIdentifier) {
        super();
        this.identifier = identifier;
        this.label = label;
        this.pgcnId = pgcnId;
        this.digitizingNotes = digitizingNotes;
        this.library = library;
        this.children = children;
        setParentIdentifier(parentIdentifier);
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

    /**
     * @return the digitizingNotes
     */
    public String getDigitizingNotes() {
        return digitizingNotes;
    }

    /**
     * @param digitizingNotes the digitizingNotes to set
     */
    public void setDigitizingNotes(final String digitizingNotes) {
        this.digitizingNotes = digitizingNotes;
    }

    public SimpleDocUnitDTO() {
    }

    public SimpleLibraryDTO getLibrary() {
        return library;
    }

    public void setLibrary(final SimpleLibraryDTO library) {
        this.library = library;
    }

    public List<SimpleDocUnitDTO> getChildren() {
        return children;
    }

    public void setChildren(final List<SimpleDocUnitDTO> children) {
        this.children = children;
    }

    public String getParentIdentifier() {
        return parentIdentifier;
    }

    public void setParentIdentifier(final String parentIdentifier) {
        this.parentIdentifier = parentIdentifier;
    }

}
