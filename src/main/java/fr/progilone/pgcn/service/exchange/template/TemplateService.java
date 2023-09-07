package fr.progilone.pgcn.service.exchange.template;

import fr.progilone.pgcn.domain.exchange.template.Name;
import fr.progilone.pgcn.domain.exchange.template.Template;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.exchange.template.TemplateRepository;
import fr.progilone.pgcn.service.storage.FileStorageManager;
import jakarta.annotation.PostConstruct;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Gestion des templates Velocity
 */
@Service
public class TemplateService {

    private static final Logger LOG = LoggerFactory.getLogger(TemplateService.class);

    private final FileStorageManager fm;
    private final TemplateRepository templateRepository;

    // Stockage des fichiers importés
    @Value("${uploadPath.template}")
    private String templateDir;

    @Autowired
    public TemplateService(final FileStorageManager fm, final TemplateRepository templateRepository) {
        this.fm = fm;
        this.templateRepository = templateRepository;
    }

    @PostConstruct
    public void initialize() {
        fm.initializeStorage(templateDir);
    }

    @Transactional
    public void delete(final String id) {
        final Template template = templateRepository.findById(id).orElse(null);
        deleteTemplateFile(template);
        templateRepository.delete(template);
    }

    @Transactional
    public void deleteByLibrary(final Library library) {
        templateRepository.findByLibrary(library).forEach(template -> {
            deleteTemplateFile(template);
            templateRepository.delete(template);
        });
    }

    private void deleteTemplateFile(final Template template) {
        final File templateFile = getTemplateFile(template);
        if (templateFile != null) {
            FileUtils.deleteQuietly(templateFile);
        }
    }

    @Transactional(readOnly = true)
    public List<Template> findTemplate() {
        return templateRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Template> findTemplate(final Library library) {
        return templateRepository.findByLibrary(library);
    }

    @Transactional(readOnly = true)
    public List<Template> findTemplate(final Name name, final String libraryId) {
        return templateRepository.findByNameAndLibraryIdentifier(name, libraryId);
    }

    @Transactional(readOnly = true)
    public Template findByIdentifier(final String templateId) {
        return templateRepository.findByIdentifier(templateId);
    }

    /**
     * Retourne un {@link Reader} sur le fichier de ce template
     */
    @Transactional(readOnly = true)
    public InputStream getContentStream(final Template template) {
        try {
            final File templateFile = getTemplateFile(template);
            if (templateFile != null) {
                return new BufferedInputStream(new FileInputStream(templateFile));
            }
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * File correspondant au template passé en paramètre
     *
     * @return null si aucun fichier n'est trouvé
     */
    @Transactional(readOnly = true)
    public File getTemplateFile(final Template template) {

        if (template != null && template.getName() == Name.ReinitPassword) {
            return fm.uncheckedRetrieveFile(templateDir, template);
        } else {
            return fm.retrieveFile(templateDir, template);
        }

    }

    @Transactional
    public Template save(final Template template) throws PgcnValidationException {
        validate(template);
        return templateRepository.save(template);
    }

    @Transactional
    public Template save(final Template template, final MultipartFile file) {
        // Upload du fichier
        if (file != null && file.getSize() > 0) {
            final Template dbTemplate = templateRepository.findById(template.getIdentifier()).orElseThrow();
            // Suppression de l'ancien fichier
            deleteTemplateFile(dbTemplate);
            // Mise à jour du template
            dbTemplate.setOriginalFilename(file.getOriginalFilename());
            dbTemplate.setFileSize(file.getSize());
            final Template savedTemplate = templateRepository.save(dbTemplate);

            LOG.debug("Téléversement du template {}", savedTemplate);
            File templateFile;
            try (InputStream in = file.getInputStream()) {
                templateFile = fm.copyInputStreamToFile(in, templateDir, savedTemplate);

            } catch (final IOException e) {
                LOG.error(e.getMessage(), e);
                templateFile = null;
            }

            if (templateFile == null) {
                LOG.error("Une erreur s'est produite lors de la sauvegarde du template {} (Template {})", file.getOriginalFilename(), savedTemplate.getIdentifier());

            } else {
                LOG.debug("Fichier {} importé", templateFile.getAbsolutePath());
            }
            return savedTemplate;
        }
        return template;
    }

    private void validate(final Template template) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        final Library library = template.getLibrary();
        final Name templateName = template.getName();

        // library mandatory
        if (library == null) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.TPL_LIBRARY_MANDATORY).setField("library").build());
        }
        // name mandatory
        if (templateName == null) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.TPL_NAME_MANDATORY).setField("name").build());
        }

        if (library != null && templateName != null) {
            // name + engine + library unique
            final Long countDupl = template.getIdentifier() == null ? templateRepository.countByNameAndLibraryIdentifier(templateName, library.getIdentifier())
                                                                    : templateRepository.countByNameAndLibraryIdentifierAndIdentifierNot(templateName,
                                                                                                                                         library.getIdentifier(),
                                                                                                                                         template.getIdentifier());
            if (countDupl > 0) {
                errors.add(builder.reinit().setCode(PgcnErrorCode.TPL_DUPLICATE).addComplement("name: " + templateName).addComplement("library: " + library).build());
            }
        }

        /* Retour **/
        if (!errors.isEmpty()) {
            template.setErrors(errors);
            throw new PgcnValidationException(template, errors);
        }
    }
}
