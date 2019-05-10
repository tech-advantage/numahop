package fr.progilone.pgcn.domain.dto.administration;

import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author jbrunet
 * Créé le 19 avr. 2017
 */
public class InternetArchiveConfigurationDTO {

    private String identifier;
    private String label;
    private String accesKey;
    private SimpleLibraryDTO library;
    private List<InternetArchiveCollectionDTO> collections;
    private boolean active;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getAccesKey() {
        return accesKey;
    }

    public void setAccesKey(String accesKey) {
        this.accesKey = accesKey;
    }

    public SimpleLibraryDTO getLibrary() {
        return library;
    }

    public void setLibrary(final SimpleLibraryDTO library) {
        this.library = library;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public List<InternetArchiveCollectionDTO> getCollections() {
        return collections;
    }

    public void setCollections(List<InternetArchiveCollectionDTO> collections) {
        this.collections = collections;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final InternetArchiveConfigurationDTO that = (InternetArchiveConfigurationDTO) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}
