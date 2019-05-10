package fr.progilone.pgcn.domain.dto.library;

import java.util.List;

import fr.progilone.pgcn.domain.dto.AbstractDTO;

/**
 * DTO représentant un paramètre de bibliothèque
 * 
 * @author jbrunet
 * Créé le 24 févr. 2017
 */
public class LibraryParameterDTO extends AbstractDTO {

    private String identifier;
    private String type;
    private SimpleLibraryDTO library;
    private List<AbstractLibraryParameterValueDTO> values;
    
    public LibraryParameterDTO() {}

    public String getIdentifier() {
        return identifier;
    }

	public final void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

    public final String getType() {
        return type;
    }

    public final void setType(String type) {
        this.type = type;
    }

    public final SimpleLibraryDTO getLibrary() {
        return library;
    }

    public final void setLibrary(SimpleLibraryDTO library) {
        this.library = library;
    }

    public final List<AbstractLibraryParameterValueDTO> getValues() {
        return values;
    }

    public final void setValues(List<AbstractLibraryParameterValueDTO> values) {
        this.values = values;
    }
}
