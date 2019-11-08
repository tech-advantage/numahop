package fr.progilone.pgcn.service.document;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocProperty;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.document.BibliographicRecordDcDTO;
import fr.progilone.pgcn.domain.dto.document.BibliographicRecordMassUpdateDTO;
import fr.progilone.pgcn.repository.document.BibliographicRecordRepository;
import fr.progilone.pgcn.service.document.mapper.DocPropertyMapper;
import fr.progilone.pgcn.service.es.EsBibliographicRecordService;
import fr.progilone.pgcn.service.util.SortUtils;

@Service
public class BibliographicRecordService {

    private static final Logger LOG = LoggerFactory.getLogger(BibliographicRecordService.class);
    private static final int COL_MAX_WIDTH = 255;

    private final BibliographicRecordRepository bibliographicRecordRepository;
    private final EsBibliographicRecordService esBibliographicRecordService;
    private final DocPropertyService docPropertyService;
    private final DocPropertyTypeService docPropertyTypeService;

    @Autowired
    public BibliographicRecordService(final BibliographicRecordRepository bibliographicRecordRepository,
                                      final EsBibliographicRecordService esBibliographicRecordService,
                                      final DocPropertyService docPropertyService,
                                      final DocPropertyTypeService docPropertyTypeService) {
        this.bibliographicRecordRepository = bibliographicRecordRepository;
        this.esBibliographicRecordService = esBibliographicRecordService;
        this.docPropertyService = docPropertyService;
        this.docPropertyTypeService = docPropertyTypeService;
    }

    @Transactional
    public BibliographicRecord save(final BibliographicRecord record) {
        setDefaultValues(record);
        
        final BibliographicRecord savedRecord = bibliographicRecordRepository.save(record);
        
        for (final DocProperty property : record.getProperties()) {
            docPropertyService.save(property);
        }
        
        return savedRecord;
    }

    @Transactional(readOnly = true)
    public List<BibliographicRecord> findAll() {
        return bibliographicRecordRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<BibliographicRecord> findAllByIdentifierIn(final List<String> identifiers) {
        if (IterableUtils.isEmpty(identifiers)) {
            return Collections.emptyList();
        }
        return bibliographicRecordRepository.findAllByIdentifierIn(identifiers);
    }

    @Transactional(readOnly = true)
    public BibliographicRecord getOne(final String identifier) {
        return bibliographicRecordRepository.findOneWithDependencies(identifier);
    }

    @Transactional
    public void delete(final String identifier) {
        final BibliographicRecord record = bibliographicRecordRepository.findOne(identifier);
        bibliographicRecordRepository.delete(record);
        esBibliographicRecordService.deleteAsync(record);
    }

    @Transactional
    public void delete(final List<String> identifiers) {
        for (final String identifier : identifiers) {
            delete(identifier);
        }
    }

    @Transactional(readOnly = true)
    public Page<BibliographicRecord> search(final String search,
                                            final List<String> libraries,
                                            final List<String> projects,
                                            final List<String> lots,
                                            final List<String> statuses,
                                            final List<String> trains,
                                            final LocalDate lastModifiedDateFrom,
                                            final LocalDate lastModifiedDateTo,
                                            final LocalDate createdDateFrom,
                                            final LocalDate createdDateTo,
                                            final Boolean orphan,
                                            final Integer page,
                                            final Integer size,
                                            final List<String> sorts) {
        Sort sort = SortUtils.getSort(sorts);
        if (sort == null) {
            sort = new Sort("docUnit.label");
        }
        final Pageable pageRequest = new PageRequest(page, size, sort);

        return bibliographicRecordRepository.search(search,
                                                    libraries,
                                                    projects,
                                                    lots,
                                                    statuses,
                                                    trains,
                                                    lastModifiedDateFrom,
                                                    lastModifiedDateTo,
                                                    createdDateFrom,
                                                    createdDateTo,
                                                    orphan,
                                                    pageRequest);
    }

    private void setDefaultValues(final BibliographicRecord record) {
        if (StringUtils.isEmpty(record.getTitle()) && record.getDocUnit() != null) {
            if (Hibernate.isInitialized(record.getDocUnit())) {
                record.setTitle(record.getDocUnit().getLabel());
            } else {
                Hibernate.initialize(record.getDocUnit());
                record.setTitle(record.getDocUnit().getLabel());
            }
        }
    }

    @Transactional(readOnly = true)
    public List<BibliographicRecord> findAllByDocUnitId(final String docUnitId) {
        return bibliographicRecordRepository.findAllByDocUnitIdentifier(docUnitId);
    }
    
    /**
     * Recherche de l'unité documentaire d'une notice
     *
     * @param recordId
     * @return
     */
    @Transactional(readOnly = true)
    public DocUnit findDocUnitByIdentifier(final String recordId) {
        return bibliographicRecordRepository.findDocUnitByIdentifier(recordId);
    }

    @Transactional(readOnly = true)
    public BibliographicRecordDcDTO bibliographicRecordToDcDTO(final BibliographicRecord record) {
        if(record == null) {
            return null;
        }
        final BibliographicRecordDcDTO dto = new BibliographicRecordDcDTO();        
        // il faut reordonner les props custom selon le rank
        final List<DocProperty> customProps = record.getProperties().stream()
                                .filter(p -> p.getType().getSuperType() == DocPropertyType.DocPropertySuperType.CUSTOM || p.getType().getSuperType() == DocPropertyType.DocPropertySuperType.CUSTOM_CINES)
                                .sorted(Comparator.comparing(DocProperty::getRank))
                                .collect(Collectors.toCollection(ArrayList::new));
        dto.setCustomProperties(DocPropertyMapper.INSTANCE.docPropsToDto(customProps));
                                
        record.getProperties().stream()
              .filter(p -> p.getType().getSuperType() == DocPropertyType.DocPropertySuperType.DC)
              .sorted(Comparator.comparing(DocProperty::getRank))
              .forEach(p -> {
                  try {
                      final String dcProperty = p.getType().getIdentifier();
                      final List<String> current = (List<String>) PropertyUtils.getSimpleProperty(dto, dcProperty);
                      current.add(p.getValue());

                  } catch (ReflectiveOperationException | IllegalArgumentException e) {
                      LOG.error(e.getMessage(), e);
                  }
              });
        return dto;
    }
    

    @Transactional
    public BibliographicRecord duplicate(final String id) {
        final BibliographicRecord record = bibliographicRecordRepository.findOneWithDependencies(id);

        final BibliographicRecord dupl = new BibliographicRecord();
        dupl.setTitle(record.getTitle());
        dupl.setSigb(record.getSigb());
        dupl.setSudoc(record.getSudoc());
        dupl.setCalames(record.getCalames());
        dupl.setDocElectronique(record.getDocElectronique());
        dupl.setLibrary(record.getLibrary());

        record.getProperties().forEach(p -> {
            final DocProperty duplP = new DocProperty();
            duplP.setValue(p.getValue());
            duplP.setType(p.getType());
            duplP.setLanguage(p.getLanguage());
            duplP.setRank(p.getRank());

            dupl.addProperty(duplP);
            duplP.setRecord(dupl);
        });

        return bibliographicRecordRepository.save(dupl);
    }

    /**
     * Mise à jour de plusieurs notices simultanément
     *
     * @param updates
     */
    @Transactional
    public List<BibliographicRecord> update(final BibliographicRecordMassUpdateDTO updates) {
        if (updates.isEmpty()) {
            return Collections.emptyList();
        }
        final List<BibliographicRecord> records = bibliographicRecordRepository.findAll(updates.getRecordIds());
        final List<DocPropertyType> types = docPropertyTypeService.findAll();
        final List<DocPropertyType> updatedTypes = updates.getProperties()
                                                          .stream()
                                                          .map(BibliographicRecordMassUpdateDTO.Update::getType)
                                                          .distinct()
                                                          .map(type -> types.stream()
                                                                            .filter(t -> StringUtils.equals(t.getIdentifier(), type))
                                                                            .findAny())
                                                          .filter(Optional::isPresent)
                                                          .map(Optional::get)
                                                          .collect(Collectors.toList());
        final List<BibliographicRecord> savedRecords = new ArrayList<>();

        for (final BibliographicRecord record : records) {
            // Mise à jour des champs
            updates.getFields().forEach(upd -> updateField(record, upd));
            // Suppression des propriété impactées par la mise à jour
            record.getProperties()
                  .removeIf(docProperty -> updatedTypes.stream()
                                                       .anyMatch(updatedType -> StringUtils.equals(updatedType.getIdentifier(),
                                                                                                   docProperty.getType().getIdentifier())));
            // Mise à jour des propriétés
            updates.getProperties().forEach(upd -> {
                updateProperty(record, upd, types).ifPresent(docPropertyService::save);
            });
            final BibliographicRecord savedRecord = bibliographicRecordRepository.save(record);
            savedRecords.add(savedRecord);
        }
        return savedRecords;
    }

    /**
     * Mise à jour d'un champ de la notice
     *
     * @param record
     * @param update
     */
    private void updateField(final BibliographicRecord record, final BibliographicRecordMassUpdateDTO.Update update) {
        try {
            PropertyUtils.setSimpleProperty(record, update.getType(), StringUtils.abbreviate(update.getValue(), COL_MAX_WIDTH));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * Mise à jour d'une propriété de la notice
     *
     * @param record
     * @param update
     * @param types
     * @return
     */
    private Optional<DocProperty> updateProperty(final BibliographicRecord record,
                                                 final BibliographicRecordMassUpdateDTO.Update update,
                                                 final List<DocPropertyType> types) {
        // On ne créé pas de propriété vide
        if (StringUtils.isBlank(update.getValue())) {
            return Optional.empty();
        }
        return types.stream().filter(type -> StringUtils.equals(type.getIdentifier(), update.getType())).findAny().map(type -> {
            final DocProperty property = new DocProperty();
            property.setType(type);
            property.setValue(update.getValue());
            record.addProperty(property);
            return property;
        });
    }
}
