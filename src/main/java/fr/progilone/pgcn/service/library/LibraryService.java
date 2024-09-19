package fr.progilone.pgcn.service.library;

import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.ocrlangconfiguration.OcrLanguageDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.ocrlangconfiguration.ActivatedOcrLanguage;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.administration.InternetArchiveConfigurationRepository;
import fr.progilone.pgcn.repository.administration.MailboxConfigurationRepository;
import fr.progilone.pgcn.repository.administration.SftpConfigurationRepository;
import fr.progilone.pgcn.repository.document.BibliographicRecordRepository;
import fr.progilone.pgcn.repository.document.DocUnitRepository;
import fr.progilone.pgcn.repository.document.conditionreport.PropertyConfigurationRepository;
import fr.progilone.pgcn.repository.exchange.ImportReportRepository;
import fr.progilone.pgcn.repository.library.LibraryParameterRepository;
import fr.progilone.pgcn.repository.library.LibraryRepository;
import fr.progilone.pgcn.repository.project.ProjectRepository;
import fr.progilone.pgcn.repository.user.UserRepository;
import fr.progilone.pgcn.repository.workflow.WorkflowGroupRepository;
import fr.progilone.pgcn.repository.workflow.WorkflowModelRepository;
import fr.progilone.pgcn.service.exchange.CSVMappingService;
import fr.progilone.pgcn.service.exchange.MappingService;
import fr.progilone.pgcn.service.exchange.template.TemplateService;
import fr.progilone.pgcn.service.ocrlangconfiguration.mapper.OcrLanguageMapper;
import fr.progilone.pgcn.service.storage.FileStorageManager;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LibraryService {

    private static final Logger LOG = LoggerFactory.getLogger(LibraryService.class);

    private final BibliographicRecordRepository bibliographicRecordRepository;
    private final InternetArchiveConfigurationRepository internetArchiveConfigurationRepository;
    private final CSVMappingService csvMappingService;
    private final DocUnitRepository docUnitRepository;
    private final FileStorageManager fm;
    private final ImportReportRepository importReportRepository;
    private final LibraryRepository libraryRepository;
    private final LibraryParameterRepository libraryParameterRepository;
    private final MailboxConfigurationRepository mailboxConfigurationRepository;
    private final MappingService mappingService;
    private final ProjectRepository projectRepository;
    private final PropertyConfigurationRepository propertyConfigurationRepository;
    private final UserRepository userRepository;
    private final SftpConfigurationRepository sftpConfigurationRepository;
    private final TemplateService templateService;
    private final WorkflowGroupRepository workflowGroupRepository;
    private final WorkflowModelRepository workflowModelRepository;

    // Stockage des fichiers importés
    @Value("${uploadPath.library}")
    private String libraryDir;

    public LibraryService(final BibliographicRecordRepository bibliographicRecordRepository,
                          final InternetArchiveConfigurationRepository internetArchiveConfigurationRepository,
                          final CSVMappingService csvMappingService,
                          final DocUnitRepository docUnitRepository,
                          final FileStorageManager fm,
                          final ImportReportRepository importReportRepository,
                          final LibraryRepository libraryRepository,
                          final LibraryParameterRepository libraryParameterRepository,
                          final MailboxConfigurationRepository mailboxConfigurationRepository,
                          final MappingService mappingService,
                          final ProjectRepository projectRepository,
                          final PropertyConfigurationRepository propertyConfigurationRepository,
                          final UserRepository userRepository,
                          final SftpConfigurationRepository sftpConfigurationRepository,
                          final TemplateService templateService,
                          final WorkflowGroupRepository workflowGroupRepository,
                          final WorkflowModelRepository workflowModelRepository) {
        this.bibliographicRecordRepository = bibliographicRecordRepository;
        this.internetArchiveConfigurationRepository = internetArchiveConfigurationRepository;
        this.csvMappingService = csvMappingService;
        this.docUnitRepository = docUnitRepository;
        this.fm = fm;
        this.importReportRepository = importReportRepository;
        this.libraryRepository = libraryRepository;
        this.libraryParameterRepository = libraryParameterRepository;
        this.mailboxConfigurationRepository = mailboxConfigurationRepository;
        this.mappingService = mappingService;
        this.projectRepository = projectRepository;
        this.propertyConfigurationRepository = propertyConfigurationRepository;
        this.userRepository = userRepository;
        this.sftpConfigurationRepository = sftpConfigurationRepository;
        this.templateService = templateService;
        this.workflowGroupRepository = workflowGroupRepository;
        this.workflowModelRepository = workflowModelRepository;
    }

    @PostConstruct
    public void initialize() {
        fm.initializeStorage(libraryDir);
    }

    @Transactional(readOnly = true)
    public Page<Library> search(final String search,
                                final List<String> libraries,
                                final String initiale,
                                final List<String> institutions,
                                final boolean isActive,
                                final Integer page,
                                final Integer size) {

        final Pageable pageRequest = PageRequest.of(page, size);
        return libraryRepository.search(search, libraries, initiale, institutions, isActive, pageRequest);
    }

    /**
     * Suppression d'une bibliothèque depuis son identifiant
     *
     * @param identifier
     *            l'identifiant d'une bibliothèque
     * @throws PgcnValidationException
     *             si la suppression de la bibliothèque échoue
     */
    @Transactional
    public void delete(final String identifier) throws PgcnValidationException {
        final Library library = findOneWithDependencies(identifier);
        if (library != null) {
            validateDeletion(library);

            // Configuration emails
            mailboxConfigurationRepository.deleteByLibrary(library);
            // Configuration Internet Archive
            internetArchiveConfigurationRepository.deleteByLibrary(library);
            // Configuration SFTP
            sftpConfigurationRepository.deleteByLibrary(library);
            // Lib parameters
            libraryParameterRepository.deleteByLibrary(library);
            // Mappings
            csvMappingService.deleteByLibrary(library);
            mappingService.deleteByLibrary(library);
            // Projets associés
            for (final Project project : projectRepository.findAllByAssociatedLibraries(library)) {
                project.getAssociatedLibraries().removeIf(lib -> StringUtils.equals(lib.getIdentifier(), identifier));
                projectRepository.save(project);
            }
            // Imports
            importReportRepository.findByLibraryIdentifier(identifier).forEach(report -> delete(report.getIdentifier()));
            // Templates
            templateService.deleteByLibrary(library);
            // Pptés des constats d'état
            propertyConfigurationRepository.deleteByLibrary(library);
            // TODO: platforms

            // Suppr. logo
            deleteLogo(library);
            // Suppr. library
            libraryRepository.delete(library);
        }
    }

    private void validateDeletion(final Library library) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        // Unités documentaires disponibles
        final Long libCount = docUnitRepository.countByLibraryAndState(library, DocUnit.State.AVAILABLE);
        if (libCount > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.LIBRARY_DEL_EXITS_DOCUNIT).setAdditionalComplement(libCount).build());
        }
        // Notices
        final Long recCount = bibliographicRecordRepository.countByLibraryAndDocUnitState(library, DocUnit.State.AVAILABLE);
        if (recCount > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.LIBRARY_DEL_EXITS_RECORD).setAdditionalComplement(libCount).build());
        }
        // Projets
        final Long projCount = projectRepository.countByLibrary(library);
        if (projCount > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.LIBRARY_DEL_EXITS_PROJ).setAdditionalComplement(projCount).build());
        }
        // Utilisateurs
        final Long userCount = userRepository.countByLibrary(library);
        if (userCount > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.LIBRARY_DEL_EXITS_USER).setAdditionalComplement(userCount).build());
        }
        // Workflow groups
        final Long wgroupCount = workflowGroupRepository.countByLibrary(library);
        if (wgroupCount > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.LIBRARY_DEL_EXITS_WGROUP).setAdditionalComplement(wgroupCount).build());
        }
        // Workflow models
        final Long wmodelCount = workflowModelRepository.countByLibrary(library);
        if (wmodelCount > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.LIBRARY_DEL_EXITS_WMODEL).setAdditionalComplement(wmodelCount).build());
        }

        if (!errors.isEmpty()) {
            library.setErrors(errors);
            throw new PgcnValidationException(library, errors);
        }
    }

    @Transactional(readOnly = true)
    public List<Library> findAll(final Iterable<String> ids) {
        return libraryRepository.findByIdentifierIn(ids, null);
    }

    @Transactional(readOnly = true)
    public List<Library> findAll() {
        return libraryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Library> findAllByActive(final boolean active) {
        return libraryRepository.findAllByActive(active);
    }

    @Transactional(readOnly = true)
    public Library findByIdentifier(final String id) {
        return libraryRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Library findOneWithDependencies(final String identifier) {
        return libraryRepository.findOneWithDependencies(identifier);
    }

    @Transactional(readOnly = true)
    public Library findOne(final String identifier) {
        return libraryRepository.findById(identifier).orElse(null);
    }

    @Transactional(readOnly = true)
    public Library findOneByName(final String name) {
        return libraryRepository.findByName(name);
    }

    @Transactional
    public Library save(final Library library) {
        final Library savedLibrary = libraryRepository.save(library);
        return findOneWithDependencies(savedLibrary.getIdentifier());
    }

    @Transactional(readOnly = true)
    public List<OcrLanguageDTO> findActifsOcrLanguagesByLibrary(final String libraryId) {

        final Library lib = libraryRepository.findOneWithActifsOcrLanguages(libraryId);
        final List<OcrLanguageDTO> langDtos = new ArrayList<>();

        if (lib != null && lib.getActiveOcrLangConfiguration() != null) {
            final Set<ActivatedOcrLanguage> activated = lib.getActiveOcrLangConfiguration().getActivatedOcrLanguages();
            langDtos.addAll(activated.stream().map(ActivatedOcrLanguage::getOcrLanguage).map(OcrLanguageMapper.INSTANCE::objToDTO).collect(Collectors.toList()));
        }
        return langDtos;
    }

    /**
     * Suppression des fichiers logo et aperçu de la bibliothèque
     *
     * @param library
     */
    public void deleteLogo(final Library library) {
        final File libraryFile = getLibraryLogo(library);
        if (libraryFile != null) {
            FileUtils.deleteQuietly(libraryFile);
        }
        final File thumbnailFile = getLibraryThumbnail(library);
        if (thumbnailFile != null) {
            FileUtils.deleteQuietly(thumbnailFile);
        }
    }

    /**
     * Logo de la bibliothèque
     *
     * @param library
     * @return null si aucun fichier n'est trouvé
     */
    @Transactional(readOnly = true)
    public File getLibraryLogo(final Library library) {
        final File logoFile = fm.getUploadFile(libraryDir,
                                               library.getIdentifier(),
                                               null,
                                               ViewsFormatConfiguration.FileFormat.MASTER.label().concat(".").concat(library.getIdentifier()));
        return fm.retrieveFile(logoFile);
    }

    /**
     * Aperçu du logo de la bibliothèque
     *
     * @param library
     * @return null si aucun fichier n'est trouvé
     */
    @Transactional(readOnly = true)
    public File getLibraryThumbnail(final Library library) {
        final File thumbnailFile = fm.getUploadFile(libraryDir,
                                                    library.getIdentifier(),
                                                    null,
                                                    ViewsFormatConfiguration.FileFormat.THUMB.label().concat(".").concat(library.getIdentifier()));
        return fm.retrieveFile(thumbnailFile);
    }

    /**
     * Téléversement du logo de la bibliothèque
     *
     * @param library
     * @param file
     */
    @Transactional
    public void uploadImage(final Library library, final MultipartFile file) {
        if (file != null && file.getSize() > 0) {
            LOG.debug("Téléversement du logo {} de la bibliothèque {}", file.getOriginalFilename(), library.getIdentifier());
            uploadImage(library, file, ViewsFormatConfiguration.FileFormat.MASTER);
            uploadImage(library, file, ViewsFormatConfiguration.FileFormat.THUMB);
        }
    }

    private void uploadImage(final Library library, final MultipartFile file, final ViewsFormatConfiguration.FileFormat format) {
        try (final InputStream in = file.getInputStream()) {
            fm.createThumbnail(in,
                               file.getContentType(),
                               format,
                               libraryDir,
                               null,
                               format.label() + "."
                                     + library.getIdentifier());
            LOG.debug("Le logo de la bibliothèque {} ({}) a été importé: {} (format {})", library.getName(), library.getIdentifier(), file.getOriginalFilename(), format.name());

        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
