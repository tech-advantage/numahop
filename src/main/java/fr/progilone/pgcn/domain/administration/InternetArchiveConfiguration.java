package fr.progilone.pgcn.domain.administration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.library.Library;

import javax.persistence.*;
import java.util.List;

/**
 * Configuration des connexions Internet Archive
 *
 * @author jbrunet
 * Créé le 18 avr. 2017
 */
@Entity
@Table(name = InternetArchiveConfiguration.TABLE_NAME)
public class InternetArchiveConfiguration extends AbstractDomainObject {

    public static final String TABLE_NAME = "conf_internet_archive";

    /**
     * Libellé
     */
    @Column(name = "label", nullable = false)
    private String label;

    /**
     * Bibliothèque à laquelle appartient cette configuration
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "library")
    private Library library;

    /**
     * S3 access key
     */
    @Column(name = "access_key")
    private String accessKey;

    /**
     * S3 secret key, crypté avec {@link fr.progilone.pgcn.service.util.CryptoService}
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "secret_key")
    private String secretKey;

    /**
     * La configuration est active / inactive
     */
    @Column(name = "active", nullable = false)
    public boolean active = true;

    @OneToMany(mappedBy = "confIa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InternetArchiveCollection> collections;

    public String getLabel() {
        return label;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accesskey) {
        this.accessKey = accesskey;
    }

    @JsonIgnore
    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(final Library library) {
        this.library = library;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "InternetArchiveConfiguration{" +
               "label='" + label + '\'' +
               ", S3 access key='" + accessKey + '\'' +
               ", active='" + active + '\'' +
               '}';
    }

    public List<InternetArchiveCollection> getCollections() {
        return collections;
    }

    public void setCollections(List<InternetArchiveCollection> collections) {
        this.collections = collections;
    }
}
