package fr.progilone.pgcn.service.exchange.template.loader;

import com.google.common.base.MoreObjects;
import fr.progilone.pgcn.domain.exchange.template.Name;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Construction du nom permettant de retrouver un template à l'aide des ResourceLoader
 */
public class ResourceName {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceName.class);
    private static final char SEPARATOR = '#';

    private Name name;
    private String libraryId;

    public ResourceName(final Name name, final String libraryId) {
        this.name = name;
        this.libraryId = libraryId;
    }

    public ResourceName(final String resourceName) throws ResourceNotFoundException {
        try {
            final int idx = resourceName.indexOf(SEPARATOR);
            if (idx > 0) {
                this.name = Name.valueOf(resourceName.substring(0, idx));
                this.libraryId = resourceName.substring(idx + 1);
            }
        } catch (IllegalArgumentException e) {
            final String message = "Le nom " + resourceName
                                   + " ne correspond à aucun template.";
            LOG.debug(message);
            throw new ResourceNotFoundException(message);
        }
    }

    public boolean isEmpty() {
        return name == null || StringUtils.isEmpty(libraryId);
    }

    /**
     * Nom du template préfixé, utilisé avec DefaultResourceLoader
     *
     * @param prefix
     * @return
     */
    public String getPrefixedResourceName(final String prefix) {
        return prefix + name;
    }

    /**
     * Nom du template complet, pour la recherche générale de template
     *
     * @return
     */
    public String getResourceName() {
        return StringUtils.joinWith(String.valueOf(SEPARATOR), name.name(), libraryId);
    }

    public Name getName() {
        return name;
    }

    public void setName(final Name name) {
        this.name = name;
    }

    public String getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(final String libraryId) {
        this.libraryId = libraryId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("name", name).add("libraryId", libraryId).toString();
    }
}
