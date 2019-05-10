package fr.progilone.pgcn.service.document.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocProperty;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.document.BibliographicRecordDTO;
import fr.progilone.pgcn.domain.dto.document.DocPropertyDTO;
import fr.progilone.pgcn.domain.dto.document.SimpleDocUnitDTO;
import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.service.document.DocPropertyService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.library.LibraryService;

@Service
public class UIBibliographicRecordMapper {

    private final DocPropertyService docPropertyService;
    private final DocUnitService docUnitService;
    private final LibraryService libraryService;
    private final UIDocPropertyMapper uiDocPropertyMapper;

    @Autowired
    public UIBibliographicRecordMapper(final DocPropertyService docPropertyService,
                                       final DocUnitService docUnitService,
                                       final LibraryService libraryService,
                                       final UIDocPropertyMapper uiDocPropertyMapper) {
        this.docPropertyService = docPropertyService;
        this.docUnitService = docUnitService;
        this.libraryService = libraryService;
        this.uiDocPropertyMapper = uiDocPropertyMapper;
    }

    public void mapInto(final BibliographicRecordDTO recordDTO, final BibliographicRecord record) {
        record.setDocElectronique(recordDTO.getDocElectronique());
        record.setCalames(recordDTO.getCalames());
        record.setSigb(recordDTO.getSigb());
        record.setSudoc(recordDTO.getSudoc());
        record.setTitle(recordDTO.getTitle());
        // DocUnit
        final SimpleDocUnitDTO doc = recordDTO.getDocUnit();
        if (doc != null && doc.getIdentifier() != null) {
            final DocUnit bddDoc = docUnitService.findOne(doc.getIdentifier());
            record.setDocUnit(bddDoc);
        }
        // Library
        final SimpleLibraryDTO library = recordDTO.getLibrary();
        if (library != null) {
            final Library bdlib = libraryService.findOne(library.getIdentifier());
            record.setLibrary(bdlib);
        }
        // Properties
        final List<DocPropertyDTO> properties = recordDTO.getProperties();
        if (properties != null) {
            // modifiable
            record.setProperties(properties.stream().map(property -> {
                if (property.getIdentifier() != null) {
                    final DocProperty oldProperty = docPropertyService.findOne(property.getIdentifier());
                    uiDocPropertyMapper.mapInto(property, oldProperty);
                    return oldProperty;
                } else {
                    // Ajout d'une propriété
                    final DocProperty newProperty = new DocProperty();
                    uiDocPropertyMapper.mapInto(property, newProperty);
                    // sauvegarde effectuée au niveau de la notice
                    return newProperty;
                }
            }).collect(Collectors.toSet()));
        }
    }
}
