package fr.progilone.pgcn.service.document.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.administration.CinesPAC;
import fr.progilone.pgcn.domain.administration.InternetArchiveCollection;
import fr.progilone.pgcn.domain.administration.omeka.OmekaList;
import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.PhysicalDocument;
import fr.progilone.pgcn.domain.dto.administration.CinesPACDTO;
import fr.progilone.pgcn.domain.dto.administration.InternetArchiveCollectionDTO;
import fr.progilone.pgcn.domain.dto.administration.omeka.OmekaListDTO;
import fr.progilone.pgcn.domain.dto.document.DocUnitBibliographicRecordDTO;
import fr.progilone.pgcn.domain.dto.document.DocUnitDTO;
import fr.progilone.pgcn.domain.dto.library.LibraryDTO;
import fr.progilone.pgcn.domain.dto.lot.SimpleLotDTO;
import fr.progilone.pgcn.domain.dto.ocrlangconfiguration.OcrLanguageDTO;
import fr.progilone.pgcn.domain.dto.project.SimpleProjectDTO;
import fr.progilone.pgcn.domain.dto.train.SimpleTrainDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.train.Train;
import fr.progilone.pgcn.repository.document.BibliographicRecordRepository;
import fr.progilone.pgcn.service.administration.CinesPACService;
import fr.progilone.pgcn.service.administration.InternetArchiveCollectionService;
import fr.progilone.pgcn.service.administration.omeka.OmekaListService;
import fr.progilone.pgcn.service.library.LibraryService;
import fr.progilone.pgcn.service.lot.LotService;
import fr.progilone.pgcn.service.ocrlangconfiguration.OcrLanguageService;
import fr.progilone.pgcn.service.project.ProjectService;
import fr.progilone.pgcn.service.train.TrainService;

@Service
public class UIDocUnitMapper {

    private final BibliographicRecordRepository bibliographicRecordRepository;
    private final LibraryService libraryService;
    private final LotService lotService;
    private final ProjectService projectService;
    private final TrainService trainService;
    private final InternetArchiveCollectionService iaCollectionService;;
    private final CinesPACService cinesPACService;
    private final OmekaListService omekaListService;
    private final OcrLanguageService ocrLanguageService;

    @Autowired
    public UIDocUnitMapper(final BibliographicRecordRepository bibliographicRecordRepository,
                           final LibraryService libraryService,
                           final LotService lotService,
                           final ProjectService projectService,
                           final TrainService trainService,
                           final InternetArchiveCollectionService iaCollectionService,
                           final CinesPACService cinesPACService,
                           final OmekaListService omekaListService,
                           final OcrLanguageService ocrLanguageService) {
        this.libraryService = libraryService;
        this.projectService = projectService;
        this.lotService = lotService;
        this.bibliographicRecordRepository = bibliographicRecordRepository;
        this.trainService = trainService;
        this.iaCollectionService = iaCollectionService;
        this.cinesPACService = cinesPACService;
        this.omekaListService = omekaListService;
        this.ocrLanguageService = ocrLanguageService;
    }

    @Transactional(readOnly = true)
    public void mapInto(final DocUnitDTO docDTO, final DocUnit doc) {

        doc.setArchivable(docDTO.getArchivable());
        doc.setDistributable(docDTO.getDistributable());
        doc.setCheckDelay(docDTO.getCheckDelay());
        doc.setCheckEndTime(docDTO.getCheckEndTime());
        doc.setEmbargo(docDTO.getEmbargo());
        doc.setPgcnId(docDTO.getPgcnId());
        doc.setLabel(docDTO.getLabel());
        doc.setType(docDTO.getType());
        doc.setRights(docDTO.getRights());
        doc.setCondReportType(docDTO.getCondReportType());
        doc.setDigitizingNotes(docDTO.getDigitizingNotes());
        doc.setFoundRefAuthor(docDTO.getFoundRefAuthor());
        doc.setProgressStatus(docDTO.getProgressStatus());
        doc.setRequestDate(docDTO.getRequestDate());
        doc.setAnswerDate(docDTO.getAnswerDate());

        final InternetArchiveCollectionDTO iaCollection = docDTO.getCollectionIA();
        if (iaCollection != null && iaCollection.getIdentifier() != null) {
            final InternetArchiveCollection internetArchiveCollection = iaCollectionService.findOne(iaCollection.getIdentifier());
            doc.setCollectionIA(internetArchiveCollection);
        } else {
            doc.setCollectionIA(null);
        }

        final CinesPACDTO cinesPACDTO = docDTO.getPlanClassementPAC();
        if (cinesPACDTO != null && cinesPACDTO.getIdentifier() != null) {
            final CinesPAC cinesPAC = cinesPACService.findOne(cinesPACDTO.getIdentifier());
            doc.setPlanClassementPAC(cinesPAC);
        } else {
            doc.setPlanClassementPAC(null);
        }

        final OmekaListDTO collecOmeka = docDTO.getOmekaCollection();
        if (collecOmeka != null && collecOmeka.getIdentifier() != null) {
            final OmekaList omekaCollection = omekaListService.findOne(collecOmeka.getIdentifier());
            doc.setOmekaCollection(omekaCollection);
        } else {
            doc.setOmekaCollection(null);
        }

        final OmekaListDTO itemOmeka = docDTO.getOmekaItem();
        if (itemOmeka != null && itemOmeka.getIdentifier() != null) {
            final OmekaList omekaItem = omekaListService.findOne(itemOmeka.getIdentifier());
            doc.setOmekaItem(omekaItem);
        } else {
            doc.setOmekaItem(null);
        }
        
        // Biliographic Records
        final List<DocUnitBibliographicRecordDTO> recordsDTO = docDTO.getRecords();
        if (recordsDTO != null) {
            doc.setRecords(recordsDTO.stream()
                                     .map(record -> bibliographicRecordRepository.findOne(record.getIdentifier()))
                                     .collect(Collectors.toSet()));
        }

        // Library
        final LibraryDTO library = docDTO.getLibrary();
        if (library != null && library.getIdentifier() != null) {
            final Library lib = libraryService.findOne(library.getIdentifier());
            doc.setLibrary(lib);
        }

        // Lot
        final SimpleLotDTO lotDTO = docDTO.getLot();
        final Lot lot;
        if (lotDTO != null && lotDTO.getIdentifier() != null) {
            lot = lotService.getOne(lotDTO.getIdentifier());
        } else {
            lot = null;
        }
        doc.setLot(lot);

        // Project
        final SimpleProjectDTO projectDTO = docDTO.getProject();
        if (projectDTO != null && projectDTO.getIdentifier() != null) {
            final Project project = projectService.findByIdentifier(projectDTO.getIdentifier());
            doc.setProject(project);
        } else {
            doc.setProject(null);
        }
        
        // Notes de controle => digital Document
        if (doc.getDigitalDocuments() != null && doc.getDigitalDocuments().size() == 1) {
            final DigitalDocument digDoc = doc.getDigitalDocuments().iterator().next();
            if (CollectionUtils.isNotEmpty(docDTO.getDigitalDocuments())) {
                digDoc.setCheckNotes(docDTO.getDigitalDocuments().iterator().next().getCheckNotes());
            } 
        }

        // PhysicalDocuments
        if (doc.getPhysicalDocuments() != null && doc.getPhysicalDocuments().size() == 1) {
            final PhysicalDocument physicalDocument = doc.getPhysicalDocuments().iterator().next();
            physicalDocument.setDigitalId(docDTO.getDigitalId());
            if(docDTO.getPhysicalDocuments() != null && !docDTO.getPhysicalDocuments().isEmpty()) {
                final SimpleTrainDTO trainDTO = docDTO.getPhysicalDocuments().iterator().next().getTrain();
                if (trainDTO != null && trainDTO.getIdentifier() != null) {
                    final Train train = trainService.getOne(trainDTO.getIdentifier());
                    physicalDocument.setTrain(train);
                } else {
                    physicalDocument.setTrain(null);
                }
            }
        }
        
        // langage OCR
        final OcrLanguageDTO langOcr = docDTO.getActiveOcrLanguage();
        if (langOcr != null && langOcr.getIdentifier() != null) {
            doc.setActiveOcrLanguage(ocrLanguageService.getOne(langOcr.getIdentifier()));
        } else if (lot != null && lot.getActiveOcrLanguage() != null) {
            doc.setActiveOcrLanguage(lot.getActiveOcrLanguage());
        }
        
    }
}
