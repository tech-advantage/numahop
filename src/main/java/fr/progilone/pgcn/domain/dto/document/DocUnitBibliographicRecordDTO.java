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

	public DocUnitBibliographicRecordDTO() {
		
	}
}
