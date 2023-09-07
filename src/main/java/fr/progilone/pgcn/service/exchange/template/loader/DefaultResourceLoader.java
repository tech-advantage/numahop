package fr.progilone.pgcn.service.exchange.template.loader;

import java.io.InputStream;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.util.ClassUtils;

/**
 * Classe qui recherche une ressource dans le classpath, à partir d'un répertoire de base
 */
public class DefaultResourceLoader implements ResourceLoader {

    private String basePath;

    public DefaultResourceLoader(final String basePath) {
        this.basePath = basePath;
    }

    /**
     * @param resourceName
     * @return
     * @throws ResourceNotFoundException
     */
    @Override
    public InputStream getResourceStream(final ResourceName resourceName) throws ResourceNotFoundException {
        /* Inpiré de ClasspathResourceLoader.getResourceStream */
        if (resourceName.isEmpty()) {
            throw new ResourceNotFoundException("Aucun template n'a été précisé.");
        }

        final String name = resourceName.getPrefixedResourceName(basePath);
        final InputStream result;
        try {
            result = ClassUtils.getResourceAsStream(getClass(), name);
        } catch (Exception fnfe) {
            throw new ResourceNotFoundException("Une erreur est survenue au chargement du template " + name
                                                + ": "
                                                + fnfe.getMessage());
        }
        if (result == null) {
            throw new ResourceNotFoundException("Le template " + name
                                                + " n'a pas pu être chargé");
        }
        return result;
    }
}
