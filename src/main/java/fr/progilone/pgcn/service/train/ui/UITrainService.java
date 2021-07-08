package fr.progilone.pgcn.service.train.ui;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.PhysicalDocument;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReport;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail;
import fr.progilone.pgcn.domain.dto.statistics.StatisticsProviderTrainDTO;
import fr.progilone.pgcn.domain.dto.train.SimpleTrainDTO;
import fr.progilone.pgcn.domain.dto.train.TrainDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.train.Train;
import fr.progilone.pgcn.exception.PgcnBusinessException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.service.document.PhysicalDocumentService;
import fr.progilone.pgcn.service.document.conditionreport.ConditionReportDetailService;
import fr.progilone.pgcn.service.document.conditionreport.ConditionReportService;
import fr.progilone.pgcn.service.train.TrainService;
import fr.progilone.pgcn.service.train.mapper.SimpleTrainMapper;
import fr.progilone.pgcn.service.train.mapper.TrainMapper;
import fr.progilone.pgcn.service.train.mapper.UITrainMapper;
import fr.progilone.pgcn.service.util.transaction.VersionValidationService;

@Service
public class UITrainService {

    private final ConditionReportService conditionReportService;
    private final ConditionReportDetailService conditionReportDetailService;
    private final TrainService trainService;
    private final UITrainMapper uiTrainMapper;
    private final PhysicalDocumentService physicalDocumentService;

    @Autowired
    public UITrainService(final ConditionReportService conditionReportService,
                          final ConditionReportDetailService conditionReportDetailService,
                          final TrainService trainService,
                          final UITrainMapper uiTrainMapper,
                          final PhysicalDocumentService physicalDocumentService) {
        this.conditionReportService = conditionReportService;
        this.conditionReportDetailService = conditionReportDetailService;
        this.trainService = trainService;
        this.uiTrainMapper = uiTrainMapper;
        this.physicalDocumentService = physicalDocumentService;
    }

    @Transactional(readOnly = true)
    public TrainDTO getOne(final String id) {
        final Train train = trainService.getOne(id);
        return TrainMapper.INSTANCE.trainToTrainDTO(train);
    }

    @Transactional
    public TrainDTO update(final TrainDTO trainDTO) {
        final Train train = trainService.getOne(trainDTO.getIdentifier());

        // Contrôle d'accès concurrents
        VersionValidationService.checkForStateObject(train, trainDTO);
        uiTrainMapper.mapInto(trainDTO, train);
        try {
            final Train savedTrain = trainService.save(train);
            return getOne(savedTrain.getIdentifier());
        } catch (final PgcnBusinessException e) {
            throw new PgcnValidationException(trainDTO);
        }
    }

    @Transactional
    public void delete(final String id) {
        trainService.delete(id);
    }

    @Transactional
    public TrainDTO create(final TrainDTO trainDTO) {
        final Train train = new Train();
        uiTrainMapper.mapInto(trainDTO, train);
        setDefaultValues(train);

        final Train savedTrain = trainService.save(train);
        
        // Creation train renum à partir de docs rejetes
        if (! trainDTO.getPhysicalDocuments().isEmpty()) {
            trainDTO.getPhysicalDocuments().stream()
                                    .filter(doc -> doc.getIdentifier() != null)
                                    .forEach(doc -> {
                                        final PhysicalDocument phDoc = physicalDocumentService.findByIdentifier(doc.getIdentifier());
                                        phDoc.setTrain(savedTrain);
                                        physicalDocumentService.save(phDoc);
                                    });
        }
       
        return TrainMapper.INSTANCE.trainToTrainDTO(savedTrain);
    }

    @Transactional(readOnly = true)
    public List<TrainDTO> findAllActiveDTO() {
        final List<Train> trains = trainService.findAllByActive(true);
        return trains.stream().map(TrainMapper.INSTANCE::trainToTrainDTO).collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TrainDTO> findAllDTO() {
        final List<Train> trains = trainService.findAll();
        return trains.stream().map(TrainMapper.INSTANCE::trainToTrainDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TrainDTO> findAllForProject(final String projectId) {
        final List<Train> trains = trainService.findAllForProject(projectId);
        return trains.stream().map(TrainMapper.INSTANCE::trainToTrainDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SimpleTrainDTO> findAllSimpleForProject(final String projectId) {
        final List<Train> trains = trainService.findAllForProject(projectId);
        return trains.stream().map(SimpleTrainMapper.INSTANCE::trainToSimpleTrainDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StatisticsProviderTrainDTO> getProviderTrainStats(final List<String> libraries,
                                                                  final List<String> projects,
                                                                  final List<String> trains,
                                                                  final List<Train.TrainStatus> status,
                                                                  final LocalDate returnFrom,
                                                                  final LocalDate returnTo,
                                                                  final LocalDate sendFrom,
                                                                  final LocalDate sendTo,
                                                                  final Double insuranceFrom,
                                                                  final Double insuranceTo) {

        return trainService.findAll(libraries, projects, trains, status, sendFrom, sendTo, returnFrom, returnTo, insuranceFrom, insuranceTo)
                           .stream()
                           .map(train -> {
                               final StatisticsProviderTrainDTO dto = new StatisticsProviderTrainDTO();

                               // Train
                               dto.setTrainIdentifier(train.getIdentifier());
                               dto.setTrainLabel(train.getLabel());
                               dto.setStatus(train.getStatus());
                               dto.setSendingDate(train.getProviderSendingDate());
                               dto.setReturnDate(train.getReturnDate());

                               // Durée
                               if (train.getProviderSendingDate() != null && train.getReturnDate() != null) {
                                   final long duration = train.getProviderSendingDate().until(train.getReturnDate(), ChronoUnit.DAYS);
                                   dto.setDuration(duration);
                               }

                               final Set<PhysicalDocument> physicalDocuments = train.getPhysicalDocuments();
                               dto.setNbDoc(physicalDocuments != null ? physicalDocuments.size() : 0);

                               // Projet
                               final Project project = train.getProject();
                               if (project != null) {
                                   dto.setProjectIdentifier(project.getIdentifier());
                                   dto.setProjectName(project.getName());

                                   // Bibliothèque
                                   final Library library = project.getLibrary();
                                   if (library != null) {
                                       dto.setLibraryIdentifier(library.getIdentifier());
                                       dto.setLibraryName(library.getName());
                                   }
                               }

                               // Valeur d'assurance
                               // ud du train
                               final List<String> docUnitIds = train.getPhysicalDocuments()
                                                                    .stream()
                                                                    .map(PhysicalDocument::getDocUnit)
                                                                    .map(DocUnit::getIdentifier)
                                                                    .collect(Collectors.toList());
                               // constats d'état
                               final List<ConditionReport> condReports = conditionReportService.findDocUnitByIdentifierIn(docUnitIds);
                               // somme des valeurs d'assurance
                               final double totalInsurance = condReports.stream()
                                                                        .map(conditionReportDetailService::getLatest)
                                                                        .filter(Optional::isPresent)
                                                                        .map(Optional::get)
                                                                        .filter(detail -> detail.getInsurance() != null)
                                                                        .mapToDouble(ConditionReportDetail::getInsurance)
                                                                        .sum();
                               dto.setInsurance(totalInsurance);

                               return dto;
                           })
                           .collect(Collectors.toList());
    }

    private void setDefaultValues(final Train train) {
        if (train.getStatus() == null) {
            train.setStatus(Train.TrainStatus.CREATED);
        }
    }
}
