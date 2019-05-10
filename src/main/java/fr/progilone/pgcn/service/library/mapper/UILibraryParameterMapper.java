package fr.progilone.pgcn.service.library.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.progilone.pgcn.domain.dto.library.AbstractLibraryParameterValueDTO;
import fr.progilone.pgcn.domain.dto.library.LibraryParameterDTO;
import fr.progilone.pgcn.domain.dto.library.LibraryParameterValueCinesDTO;
import fr.progilone.pgcn.domain.dto.library.LibraryParameterValuedDTO;
import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.library.LibraryParameter;
import fr.progilone.pgcn.domain.library.LibraryParameter.LibraryParameterType;
import fr.progilone.pgcn.domain.library.LibraryParameterValueCines;
import fr.progilone.pgcn.domain.library.LibraryParameterValueCines.LibraryParameterValueCinesType;
import fr.progilone.pgcn.repository.library.LibraryParameterCinesRepository;
import fr.progilone.pgcn.service.library.LibraryService;

/**
 * Mapper pour LibraryParameter/LibraryParameterValue
 * 
 * @author jbrunet
 * Créé le 24 févr. 2017
 */
@Service
public class UILibraryParameterMapper {

	@Autowired
	private LibraryParameterCinesRepository cinesParameterRepository;
	@Autowired
	private LibraryService libraryService;
	
	public void mapInto(final LibraryParameterDTO libraryParameterDTO, final LibraryParameter libraryParameter) {
		
	    libraryParameter.setType(LibraryParameterType.valueOf(libraryParameterDTO.getType()));
	    // Library
        final SimpleLibraryDTO library = libraryParameterDTO.getLibrary();
        if(library != null && library.getIdentifier() != null) {
            final Library lib = libraryService.findOne(library.getIdentifier());
            libraryParameter.setLibrary(lib);
        }
	    // Valeurs paramétrées
        final List<AbstractLibraryParameterValueDTO> values = libraryParameterDTO.getValues();
        if(values != null) {
            switch(libraryParameter.getType()) {
            case CINES_EXPORT: 
                // modifiable
                libraryParameter.setValues(values.stream().map(value -> {
                    final LibraryParameterValueCinesDTO parameterizedValue = (LibraryParameterValueCinesDTO) value;
                    if (parameterizedValue.getIdentifier() != null) {
                        final LibraryParameterValueCines oldValue = cinesParameterRepository.findOne(parameterizedValue.getIdentifier());
                        mapInto(parameterizedValue, oldValue);
                        return oldValue;
                    } else {
                        // Ajout d'une valeur
                        final LibraryParameterValueCines newValue = new LibraryParameterValueCines();
                        mapInto(parameterizedValue, newValue);
                        // sauvegarde effectuée au niveau du service
                        return newValue;
                    }
                }).collect(Collectors.toSet()));
                break;
            default:
                break;
            }
        }
	    
    }
	
	
	public void mapValuedInto(final LibraryParameterValuedDTO libraryParameterDTO, final LibraryParameter libraryParameter) {
        
        libraryParameter.setType(LibraryParameterType.valueOf(libraryParameterDTO.getType()));
        // Library
        final SimpleLibraryDTO library = libraryParameterDTO.getLibrary();
        if(library != null && library.getIdentifier() != null) {
            final Library lib = libraryService.findOne(library.getIdentifier());
            libraryParameter.setLibrary(lib);
        }
        // Valeurs paramétrées
        final List<LibraryParameterValueCinesDTO> values = libraryParameterDTO.getValues();
        if(values != null) {
            switch(libraryParameter.getType()) {
            case CINES_EXPORT: 
                // modifiable
                libraryParameter.setValues(values.stream().map(value -> {
                    
                    if (value.getIdentifier() != null) {
                        final LibraryParameterValueCines oldValue = cinesParameterRepository.findOne(value.getIdentifier());
                        mapInto(value, oldValue);
                        return oldValue;
                    } else {
                        // Ajout d'une valeur
                        final LibraryParameterValueCines newValue = new LibraryParameterValueCines();
                        mapInto(value, newValue);
                        // sauvegarde effectuée au niveau du service
                        return newValue;
                    }
                }).collect(Collectors.toSet()));
                break;
            default:
                break;
            }
        }
        
    }
	
	public void mapInto(final LibraryParameterValueCinesDTO libraryParameterValueDTO, final LibraryParameterValueCines libraryParameterValue) {
	    libraryParameterValue.setValue(libraryParameterValueDTO.getValue());
	    libraryParameterValue.setType(LibraryParameterValueCinesType.valueOf(libraryParameterValueDTO.getType()));
	}
}
