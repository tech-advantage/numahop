package fr.progilone.pgcn.domain.dto.document;

import fr.progilone.pgcn.domain.document.BibliographicRecord;

/**
 * BibliographicRecord DTO
 * 
 * @author jbrunet
 * @see BibliographicRecord
 *
 */
public class SimpleBibliographicRecordDTO {
	
	private String identifier;
	private String title;
	private SimpleDocUnitDTO docUnit;

	public SimpleBibliographicRecordDTO(String identifier, String title) {
		super();
		this.identifier = identifier;
		this.title = title;
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

    public SimpleDocUnitDTO getDocUnit() {
        return docUnit;
    }

    public void setDocUnit(SimpleDocUnitDTO docUnit) {
        this.docUnit = docUnit;
    }

    public SimpleBibliographicRecordDTO() {
		
	}
}
