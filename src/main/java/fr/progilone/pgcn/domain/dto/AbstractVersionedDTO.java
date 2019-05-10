package fr.progilone.pgcn.domain.dto;

/**
 * Gestion des versions sur les objets DTO, inclut également la gestion
 * des erreurs de {@link AbstractDTO}
 * 
 * @author jbrunet
 * Créé le 6 juil. 2017
 */
public class AbstractVersionedDTO extends AbstractDTO {

    private long version;

    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}
