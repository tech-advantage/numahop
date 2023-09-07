package fr.progilone.pgcn.service.document.mapper;

import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.BibliographicRecord.PropertyOrder;
import fr.progilone.pgcn.domain.document.DocProperty;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.document.BibliographicRecordDTO;
import fr.progilone.pgcn.domain.dto.document.DocPropertyDTO;
import fr.progilone.pgcn.domain.dto.document.DocPropertyTypeDTO;
import fr.progilone.pgcn.domain.dto.document.SimpleDocUnitDTO;
import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.service.document.DocPropertyService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.library.LibraryService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

            if (record.getPropertyOrder() == PropertyOrder.BY_PROPERTY_TYPE) {
                createWeightedValueOrderByType(properties);
            } else {
                createWeightedValue(properties);
            }

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

    /**
     * Create weighted values for properties
     *
     * @param properties
     */
    public void createWeightedValueOrderByType(final List<DocPropertyDTO> properties) {
        final int maxPropertyRank = properties.stream()
                                              .mapToInt(p -> p.getRank() != null ? p.getRank()
                                                                                 : 0)
                                              .max()
                                              .orElse(Integer.MIN_VALUE);
        int i = 0;
        for (final DocPropertyDTO p : properties) {
            i++;
            final DocPropertyTypeDTO type = p.getType();
            final int rank = p.getRank() != null ? p.getRank()
                                                 : 0;
            p.setWeightedRank((double) (rank + i
                                        + type.getRank() * maxPropertyRank));
        }

    }

    /**
     * Create weighted values for properties
     *
     * @param properties
     */
    public void createWeightedValue(final List<DocPropertyDTO> properties) {
        int i = 0;
        for (final DocPropertyDTO p : properties) {
            i++;
            final int rank = p.getRank() != null ? p.getRank()
                                                 : 0;
            p.setWeightedRank((double) (rank + i));
        }
    }

}
