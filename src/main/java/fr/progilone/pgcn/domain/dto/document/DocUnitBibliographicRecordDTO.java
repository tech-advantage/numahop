package fr.progilone.pgcn.domain.dto.document;

import java.util.List;

import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.dto.AbstractDTO;

/**
 * DocUnitBibliographicRecordDTO DTO
 * Sans lien avec le DocUnit
 * 
 * @author jbrunet
 * @see BibliographicRecord
 *
 */
public class DocUnitBibliographicRecordDTO extends AbstractDTO {
	
	private String identifier;
	private String title;
	private String calames;
	private String sigb;
	private String sudoc;
	private List<DocPropertyDTO> properties;

	public DocUnitBibliographicRecordDTO(String identifier, String title, String calames, String sigb, 
			String sudoc, List<DocPropertyDTO> properties) {
		super();
		this.identifier = identifier;
		this.title = title;
		this.calames = calames;
		this.sigb = sigb;
		this.sudoc = sudoc;
		this.properties = properties;
	}

	public final List<DocPropertyDTO> getProperties() {
		return properties;
	}

	public final void setProperties(List<DocPropertyDTO> properties) {
		this.properties = properties;
	}
	
	public final String getIdentifier() {
		return identifier;
	}

	public final void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public final String getTitle() {
		return title;
	}

	public final void setTitle(String title) {
		this.title = title;
	}

	public final String getCalames() {
		return calames;
	}

	public final void setCalames(String calames) {
		this.calames = calames;
	}

	public final String getSigb() {
		return sigb;
	}

	public final void setSigb(String sigb) {
		this.sigb = sigb;
	}

	public final String getSudoc() {
		return sudoc;
	}

	public final void setSudoc(String sudoc) {
		this.sudoc = sudoc;
	}

	public DocUnitBibliographicRecordDTO() {
		
	}
}
