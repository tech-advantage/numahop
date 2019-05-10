package fr.progilone.pgcn.service.exchange.template.loader;

import fr.progilone.pgcn.domain.exchange.template.Template;
import fr.progilone.pgcn.service.exchange.template.TemplateService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;

/**
 * Classe qui recherche une ressource dans la table exc_template
 */
public class TemplateResourceLoader implements ResourceLoader {

    private static final Logger LOG = LoggerFactory.getLogger(TemplateResourceLoader.class);

    private TemplateService templateService;

    public TemplateResourceLoader(final Object service) {
        String message = null;

        if (service == null) {
            message = "TemplateResourceLoader n'est pas initialisé correctement; aucun service n'est configuré.";
        } else if (!(service instanceof TemplateService)) {
            message =
                "TemplateResourceLoader n'est pas initialisé correctement; le service configuré n'est pas de type fr.progilone.pgcn.service.exchange.template.TemplateService";
        }

        if (message != null) {
            LOG.error(message);
            throw new RuntimeException(message);
        } else {
            templateService = (TemplateService) service;
            LOG.trace("TemplateResourceLoader est initialisé.");
        }
    }

    @Override
    public InputStream getResourceStream(final ResourceName resourceName) throws ResourceNotFoundException {
        final Template template = findTemplateByName(resourceName);
        final InputStream contentStream = templateService.getContentStream(template);

        if (contentStream != null) {
            return contentStream;
        } else {
            throw new ResourceNotFoundException("Le template "
                                                + resourceName.getName()
                                                + " pour la bibliothèque "
                                                + resourceName.getLibraryId()
                                                + " n'a pas pu être chargé");
        }
    }

    /**
     * Recherche du template par nom
     *
     * @param resourceName
     * @return un Template non null
     * @throws NullPointerException
     * @throws ResourceNotFoundException
     */
    public Template findTemplateByName(final ResourceName resourceName) {
        if (resourceName.isEmpty()) {
            throw new ResourceNotFoundException("Aucun template n'a été précisé.");
        }

        // Recherche du template par nom
        final List<Template> templates = templateService.findTemplate(resourceName.getName(), resourceName.getLibraryId());

        if (CollectionUtils.isEmpty(templates)) {
            throw new ResourceNotFoundException("TemplateResourceLoader: la ressource "
                                                + resourceName.getName()
                                                + " pour la bibliothèque "
                                                + resourceName.getLibraryId()
                                                + " n'existe pas.");
        } else if (templates.size() > 1) {
            LOG.warn("Plusieurs templates existent avec le nom {} pour la bibliothèque {}", resourceName.getName(), resourceName.getLibraryId());
        }

        return templates.get(0);
    }
}
