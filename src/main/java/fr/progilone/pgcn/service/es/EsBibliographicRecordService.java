package fr.progilone.pgcn.service.es;

import fr.progilone.pgcn.domain.AbstractDomainObject_;
import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.repository.document.BibliographicRecordRepository;
import fr.progilone.pgcn.repository.es.EsBibliographicRecordRepository;
import fr.progilone.pgcn.service.util.transaction.TransactionService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EsBibliographicRecordService extends AbstractElasticsearchOperations<BibliographicRecord> {

    private static final Logger LOG = LoggerFactory.getLogger(EsBibliographicRecordService.class);

    private final BibliographicRecordRepository bibliographicRecordRepository;
    private final EsBibliographicRecordRepository esBibliographicRecordRepository;
    private final TransactionService transactionService;

    @Value("${elasticsearch.bulk_size}")
    private Integer bulkSize;

    @Autowired
    public EsBibliographicRecordService(final BibliographicRecordRepository bibliographicRecordRepository,
                                        final EsBibliographicRecordRepository esBibliographicRecordRepository,
                                        final TransactionService transactionService) {
        this.bibliographicRecordRepository = bibliographicRecordRepository;
        this.esBibliographicRecordRepository = esBibliographicRecordRepository;
        this.transactionService = transactionService;
    }

    /**
     * Indexation de la notice dans le moteur de recherche
     *
     * @param identifier
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public BibliographicRecord index(final String identifier) {
        final BibliographicRecord record = bibliographicRecordRepository.findOne(identifier);
        if (record != null) {
            esBibliographicRecordRepository.indexEntity(record);
            return record;
        }
        return null;
    }

    /**
     * Indexation de la notice dans le moteur de recherche
     *
     * @param identifiers
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Iterable<BibliographicRecord> index(final List<String> identifiers) {
        if (CollectionUtils.isEmpty(identifiers)) {
            return Collections.emptyList();
        }
        final List<BibliographicRecord> records = bibliographicRecordRepository.findAllByIdentifierIn(identifiers);
        final List<BibliographicRecord> filteredRecords =
            records.stream().filter(rec -> StringUtils.isNotEmpty(rec.getDocUnitId())).collect(Collectors.toList());

        if (records.isEmpty()) {
            return Collections.emptyList();
        }
        esBibliographicRecordRepository.indexEntities(filteredRecords);
        return filteredRecords;
    }

    /**
     * Suppression de la notice du moteur de recherche
     *
     * @param record
     */
    @Override
    public void delete(final BibliographicRecord record) {
        esBibliographicRecordRepository.deleteEntity(record);
    }

    @Override
    public void delete(final Collection<BibliographicRecord> records) {
        esBibliographicRecordRepository.deleteEntities(records);
    }

    /**
     * Réindexation de toutes les notices disponibles
     *
     * @param index
     * @return
     */
    public long reindex(final String index) {
        long nbImported = 0;
        Page<BibliographicRecord> pageOfObjects = null;    // Chargement des objets par page de bulkSize éléments

        do {
            final TransactionStatus status = transactionService.startTransaction(true);

            // Chargement des objets
            final Pageable pageable = pageOfObjects == null ?
                                      new PageRequest(0, bulkSize, Sort.Direction.ASC, AbstractDomainObject_.identifier.getName()) :
                                      pageOfObjects.nextPageable();
            pageOfObjects = bibliographicRecordRepository.findAllByDocUnitState(DocUnit.State.AVAILABLE, pageable);

            // Traitement des unités documentaires
            final List<BibliographicRecord> entities = pageOfObjects.getContent();
            esBibliographicRecordRepository.index(index, entities);

            transactionService.commitTransaction(status);

            nbImported += entities.size();
            LOG.trace("{} / {} éléments indexés", nbImported, pageOfObjects.getTotalElements());

        } while (pageOfObjects.hasNext());

        return nbImported;
    }
}
