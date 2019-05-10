package fr.progilone.pgcn.service.statistics;

import static fr.progilone.pgcn.domain.workflow.WorkflowStateKey.DIFFUSION_DOCUMENT;
import static fr.progilone.pgcn.domain.workflow.WorkflowStateKey.DIFFUSION_DOCUMENT_LOCALE;
import static fr.progilone.pgcn.domain.workflow.WorkflowStateKey.DIFFUSION_DOCUMENT_OMEKA;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.administration.InternetArchiveCollection;
import fr.progilone.pgcn.domain.administration.omeka.OmekaList;
import fr.progilone.pgcn.domain.delivery.DeliveredDocument;
import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.document.DigitalDocument.DigitalDocumentStatus;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.statistics.StatisticsDocPublishedDTO;
import fr.progilone.pgcn.domain.dto.statistics.StatisticsDocRejectedDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.workflow.DocUnitState;
import fr.progilone.pgcn.domain.workflow.DocUnitWorkflow;
import fr.progilone.pgcn.repository.lot.LotRepository;
import fr.progilone.pgcn.repository.lot.helper.LotSearchBuilder;
import fr.progilone.pgcn.repository.workflow.DocUnitStateRepository;
import fr.progilone.pgcn.repository.workflow.helper.DocUnitWorkflowSearchBuilder;

@Service
public class StatisticsDocumentService {

    private final DocUnitStateRepository docUnitStateRepository;
    private final LotRepository lotRepository;

    public StatisticsDocumentService(final DocUnitStateRepository docUnitStateRepository, final LotRepository lotRepository) {
        this.docUnitStateRepository = docUnitStateRepository;
        this.lotRepository = lotRepository;
    }

    @Transactional(readOnly = true)
    public Page<StatisticsDocPublishedDTO> getDocPublishedStat(final List<String> libraries,
                                                               final List<String> projects,
                                                               final List<String> lots,
                                                               final LocalDate fromDate,
                                                               final LocalDate toDate,
                                                               final List<String> types,
                                                               final List<String> collections,
                                                               final Integer page,
                                                               final Integer size) {

        return docUnitStateRepository.findDocUnitStates(new DocUnitWorkflowSearchBuilder().setLibraries(libraries)
                                                                                          .setProjects(projects)
                                                                                          .setLots(lots)
                                                                                          .setFromDate(fromDate)
                                                                                          .setToDate(toDate)
                                                                                          .setTypes(types)
                                                                                          .setCollections(collections)
                                                                                          // Documents publiés
                                                                                          .addState(DIFFUSION_DOCUMENT)
                                                                                          .addState(DIFFUSION_DOCUMENT_LOCALE)
                                                                                          .addState(DIFFUSION_DOCUMENT_OMEKA),
                                                        new PageRequest(page, size)).map(this::initDto);
    }

    private StatisticsDocPublishedDTO initDto(final DocUnitState docUnitState) {
        final StatisticsDocPublishedDTO dto = new StatisticsDocPublishedDTO();
        dto.setWorkflowState(docUnitState.getKey());

        final DocUnitWorkflow docUnitWorkflow = docUnitState.getWorkflow();
        final DocUnit docUnit = docUnitWorkflow.getDocUnit();

        dto.setDocUnitIdentifier(docUnit.getIdentifier());
        dto.setDocUnitLabel(docUnit.getLabel());
        dto.setDocUnitPgcnId(docUnit.getPgcnId());
        dto.setDocUnitType(docUnit.getType());

        switch (docUnitState.getKey()) {
            case DIFFUSION_DOCUMENT:
                final InternetArchiveCollection collectionIA = docUnit.getCollectionIA();
                if (collectionIA != null) {
                    dto.setCollection(collectionIA.getName());
                }
                break;
            case DIFFUSION_DOCUMENT_OMEKA:
                final OmekaList collectionOmeka = docUnit.getOmekaCollection();
                if (collectionOmeka != null) {
                    dto.setCollection(collectionOmeka.getName());
                }
                break;
        }

        // Date de diffusion
        if (docUnitState.getEndDate() != null) {
            dto.setPublicationDate(docUnitState.getEndDate().toLocalDate());
        }

        final DocUnit parent = docUnit.getParent();
        if (parent != null) {
            dto.setParentIdentifier(parent.getIdentifier());
            dto.setParentLabel(parent.getLabel());
            dto.setParentPgcnId(parent.getPgcnId());
        }

        final Library library = docUnit.getLibrary();
        if (library != null) {
            dto.setLibraryIdentifier(library.getIdentifier());
            dto.setLibraryName(library.getName());
        }

        final Project project = docUnit.getProject();
        if (project != null) {
            dto.setProjectIdentifier(project.getIdentifier());
            dto.setProjectName(project.getName());
        }

        final Lot lot = docUnit.getLot();
        if (lot != null) {
            dto.setLotIdentifier(lot.getIdentifier());
            dto.setLotLabel(lot.getLabel());
        }

        // Nombre de pages
        final int nbPages = docUnit.getDigitalDocuments().stream().mapToInt(DigitalDocument::getNbPages).sum();
        dto.setNbPages(nbPages);

        return dto;
    }

    /**
     * Statistique documents rejetés (#376)
     *
     * @param libraries
     * @param projects
     * @param providers
     * @param fromDate
     * @param toDate
     * @param page
     * @param size
     * @return
     */
    @Transactional(readOnly = true)
    public Page<StatisticsDocRejectedDTO> getDocRejectedStats(final List<String> libraries,
                                                              final List<String> projects,
                                                              final List<String> providers,
                                                              final LocalDate fromDate,
                                                              final LocalDate toDate,
                                                              final Integer page,
                                                              final Integer size) {

        return lotRepository.search(new LotSearchBuilder().setLibraries(libraries)
                                                          .setProjects(projects)
                                                          .setProviders(providers)
                                                          .setLastDlvFrom(fromDate)
                                                          .setLastDlvTo(toDate), new PageRequest(page, size)).map(this::initDto);
    }

    private StatisticsDocRejectedDTO initDto(final Lot lot) {
        final StatisticsDocRejectedDTO dto = new StatisticsDocRejectedDTO();

        dto.setLotIdentifier(lot.getIdentifier());
        dto.setLotLabel(lot.getLabel());

        final Project project = lot.getProject();
        if (project != null) {
            dto.setProjectIdentifier(project.getIdentifier());
            dto.setProjectName(project.getName());

            final Library library = project.getLibrary();
            if (library != null) {
                dto.setLibraryIdentifier(library.getIdentifier());
                dto.setLibraryName(library.getName());
            }
        }

        // Prestataire
        final User provider = lot.getProvider();
        if (provider != null) {
            dto.setProviderIdentifier(provider.getIdentifier());
            dto.setProviderLogin(provider.getLogin());
            dto.setProviderFullName(provider.getFullName());
        }

        // Documents numérisés
        final List<DigitalDocument> docs = lot.getDocUnits().stream().flatMap(ud -> ud.getDigitalDocuments().stream()).collect(Collectors.toList());
        final long nbRejected = docs.stream().filter(doc -> doc.getStatus() == DigitalDocumentStatus.REJECTED).count();

        dto.setNbDocTotal(docs.size());
        dto.setNbDocRejected(nbRejected);

        if (dto.getNbDocTotal() > 0) {
            dto.setPctDocRejected((double) dto.getNbDocRejected() / (double) dto.getNbDocTotal());
        }

        // Date d'import
        docs.stream()
            .flatMap(d -> d.getDeliveries().stream())
            .distinct()
            .filter(delivered -> delivered.getDeliveryDate() != null)
            .min(Comparator.nullsLast(Comparator.comparing(DeliveredDocument::getDeliveryDate)))
            .ifPresent(lastestDelivery -> dto.setImportDate(lastestDelivery.getDeliveryDate()) );

        return dto;
    }
}
