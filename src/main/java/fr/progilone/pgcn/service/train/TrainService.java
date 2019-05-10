package fr.progilone.pgcn.service.train;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.PhysicalDocument;
import fr.progilone.pgcn.domain.dto.train.SimpleTrainDTO;
import fr.progilone.pgcn.domain.train.Train;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.repository.train.TrainRepository;
import fr.progilone.pgcn.service.document.conditionreport.ConditionReportService;
import fr.progilone.pgcn.service.es.EsTrainService;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TrainService {

    private final EsTrainService esTrainService;
    private final TrainRepository trainRepository;
    private final ConditionReportService conditionReportService;

    @Autowired
    public TrainService(final EsTrainService esTrainService,
                        final TrainRepository trainRepository,
                        final ConditionReportService conditionReportService) {
        this.esTrainService = esTrainService;
        this.trainRepository = trainRepository;
        this.conditionReportService = conditionReportService;
    }

    /**
     * Recherche de trains
     *
     * @param search
     * @param projects
     * @param active
     * @param trainStatuses
     * @param libraries
     * @param page
     * @param size
     * @return
     */
    @Transactional(readOnly = true)
    public Page<SimpleTrainDTO> search(final String search,
                                       final List<String> libraries,
                                       final List<String> projects,
                                       final boolean active,
                                       final List<String> trainStatuses,
                                       final LocalDate providerSendingDateFrom,
                                       final LocalDate providerSendingDateTo,
                                       final LocalDate returnDateFrom,
                                       final LocalDate returnDateTo,
                                       final Integer docNumber,
                                       final Integer page,
                                       final Integer size) {

        final Pageable pageRequest = new PageRequest(page, size);
        List<Train.TrainStatus> statuses = null;
        if (trainStatuses != null) {
            statuses = trainStatuses.stream().map(Train.TrainStatus::valueOf).collect(Collectors.toList());
        }
        final Page<Train> trains = trainRepository.search(search,
                                                          libraries,
                                                          projects,
                                                          active,
                                                          statuses,
                                                          providerSendingDateFrom,
                                                          providerSendingDateTo,
                                                          returnDateFrom,
                                                          returnDateTo,
                                                          docNumber,
                                                          pageRequest);
        final List<SimpleTrainDTO> trainDTOs = trains.getContent().stream().map(this::mapToSimpleTrainDTO).collect(Collectors.toList());
        return new PageImpl<>(trainDTOs, new PageRequest(trains.getNumber(), trains.getSize(), trains.getSort()), trains.getTotalElements());
    }

    @Transactional(readOnly = true)
    public List<Train> findAll(final List<String> libraries,
                               final List<String> projects,
                               final List<String> trains,
                               final List<Train.TrainStatus> status,
                               final LocalDate sendFrom,
                               final LocalDate sendTo,
                               final LocalDate returnFrom,
                               final LocalDate returnTo,
                               final Double insuranceFrom,
                               final Double insuranceTo) {

        return trainRepository.findAll(libraries, projects, trains, status, sendFrom, sendTo, returnFrom, returnTo, insuranceFrom, insuranceTo);
    }

    private SimpleTrainDTO mapToSimpleTrainDTO(final Train train) {
        return new SimpleTrainDTO(train.getIdentifier(),
                                  train.getLabel(),
                                  train.getDescription(),
                                  train.getStatus(),
                                  train.getProviderSendingDate(),
                                  train.getReturnDate());
    }

    @Transactional(readOnly = true)
    public Train getOne(final String identifier) {
        return trainRepository.findOneWithDependencies(identifier);
    }

    @Transactional(readOnly = true)
    public List<Train> findAllByActive(final boolean active) {
        return trainRepository.findAllByActive(active);
    }

    @Transactional
    public Train save(Train train) {
        return trainRepository.save(train);
    }

    @Transactional
    public void delete(final String id) {
        final Train train = trainRepository.findOne(id);
        trainRepository.delete(id);
        esTrainService.deleteAsync(train);
    }

    @Transactional(readOnly = true)
    public List<Train> findAllForProject(String id) {
        return trainRepository.findAllByProjectIdentifier(id);
    }

    @Transactional(readOnly = true)
    public List<Train> findAll(final Iterable<String> identifiers) {
        if (IterableUtils.isEmpty(identifiers)) {
            return Collections.emptyList();
        }
        return trainRepository.findAll(identifiers);
    }

    @Transactional(readOnly = true)
    public List<SimpleTrainDTO> findAllByProjectIds(final List<String> projectIds) {
        return trainRepository.findAllIdentifiersInProjectIds(projectIds);
    }

    /**
     * Recherche les lots par bibliothèque, groupés par statut
     *
     * @param libraries
     * @param projects
     * @return liste de map avec 2 clés: statut et décompte
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTrainGroupByStatus(final List<String> libraries, final List<String> projects) {
        final List<Object[]> results = trainRepository.getTrainGroupByStatus(libraries, projects); // status, count

        return results.stream().map(res -> {
            final Map<String, Object> resMap = new HashMap<>();
            resMap.put("status", res[0]);
            resMap.put("count", res[1]);
            return resMap;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public void writeCondReportSlipPDF(final ServletOutputStream outputStream, final String id) throws PgcnTechnicalException {
        final Train train = getOne(id);
        final Set<DocUnit> docUnits = train.getPhysicalDocuments().stream().map(PhysicalDocument::getDocUnit).collect(Collectors.toSet());
        final String reportTitle = "Bordereau d'envoi du train ".concat(train.getLabel());
        conditionReportService.writeSlipDocUnitsPDF(outputStream, docUnits, reportTitle);
    }

    @Transactional(readOnly = true)
    public void writeCondReportSlip(final ServletOutputStream outputStream, final String id, final String encoding, final char separator) throws IOException {
        final Train train = getOne(id);
        final Set<DocUnit> docUnits = train.getPhysicalDocuments().stream().map(PhysicalDocument::getDocUnit).collect(Collectors.toSet());
        conditionReportService.writeSlipDocUnitsCSV(outputStream, docUnits, encoding, separator);
    }
}
