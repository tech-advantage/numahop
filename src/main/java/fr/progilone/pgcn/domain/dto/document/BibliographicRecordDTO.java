package fr.progilone.pgcn.domain.dto.document;

import java.time.LocalDateTime;
import java.util.List;

import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.dto.AbstractVersionedDTO;
import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;

/**
 * BibliographicRecord DTO
 *
 * @author jbrunet
 * @see BibliographicRecord
 *
 */
public class BibliographicRecordDTO extends AbstractVersionedDTO {

	private String identifier;
	private String title;
	private String calames;
	private String sigb;
	private String sudoc;
	private String docElectronique;
	private SimpleLibraryDTO library;
	private SimpleDocUnitDTO docUnit;
	private List<DocPropertyDTO> properties;

	/** Ajout des infos de création */
    private String createdBy;
    private LocalDateTime createdDate;

    /** Ajout des infos de modifications */
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate;

    public BibliographicRecordDTO() {}

	public SimpleLibraryDTO getLibrary() {
        return library;
    }

    public void setLibrary(SimpleLibraryDTO library) {
        this.library = library;
    }

    public final SimpleDocUnitDTO getDocUnit() {
		return docUnit;
	}

	public void setDocUnit(SimpleDocUnitDTO docUnit) {
		this.docUnit = docUnit;
	}

	public final List<DocPropertyDTO> getProperties() {
		return properties;
	}

	public void setProperties(List<DocPropertyDTO> properties) {
		this.properties = properties;
	}

	public final String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public final String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public final String getCalames() {
		return calames;
	}

	public void setCalames(String calames) {
		this.calames = calames;
	}

	public final String getSigb() {
		return sigb;
	}

	public void setSigb(String sigb) {
		this.sigb = sigb;
	}

	public final String getSudoc() {
		return sudoc;
	}

	public void setSudoc(String sudoc) {
		this.sudoc = sudoc;
	}

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getDocElectronique() {
        return docElectronique;
    }

    public void setDocElectronique(String docElectronique) {
        this.docElectronique = docElectronique;
    }
}
