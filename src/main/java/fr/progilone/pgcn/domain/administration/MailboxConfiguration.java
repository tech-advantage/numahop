package fr.progilone.pgcn.domain.administration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.library.Library;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Sébastien on 03/01/2017.
 */
@Entity
@Table(name = MailboxConfiguration.TABLE_NAME)
public class MailboxConfiguration extends AbstractDomainObject {

    public static final String TABLE_NAME = "conf_email";
    public static final String TABLE_NAME_PROPERTY = "conf_email_prop";

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
     * Nom d'utilisateur
     */
    @Column(name = "username")
    private String username;

    /**
     * Mot de passe, crypté avec {@link fr.progilone.pgcn.service.util.CryptoService}
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password")
    private String password;

    /**
     * Serveur de messagerie
     */
    @Column(name = "host")
    private String host;

    /**
     * Port de connexion
     */
    @Column(name = "port")
    private Integer port;

    /**
     * Répertoire de la messagerie
     */
    @Column(name = "inbox")
    private String inbox;

    /**
     * Propriétés de configuration de la connexion à la messagerie
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = TABLE_NAME_PROPERTY, joinColumns = @JoinColumn(name = "conf_id"))
    public final Set<Property> properties = new HashSet<>();

    /**
     * La configuration est active / inactive
     */
    @Column(name = "active", nullable = false)
    public boolean active = true;

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(final Library library) {
        this.library = library;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public String getInbox() {
        return inbox;
    }

    public void setInbox(final String inbox) {
        this.inbox = inbox;
    }

    public Set<Property> getProperties() {
        return properties;
    }

    public void setProperties(Set<Property> properties) {
        this.properties.clear();
        if (properties != null) {
            properties.forEach(this::addProperty);
        }
    }

    public void addProperty(final Property property) {
        if (property != null) {
            this.properties.add(property);
        }
    }

    public void addProperty(final String property, final String value) {
        if (property != null) {
            Property prop = new Property();
            prop.setName(property);
            prop.setValue(value);
            this.properties.add(prop);
        }
    }

    @Override
    public String toString() {
        return "MailboxConfiguration{" + "label='"
               + label
               + '\''
               + ", username='"
               + username
               + '\''
               + ", host='"
               + host
               + '\''
               + ", inbox='"
               + inbox
               + '\''
               + ", active='"
               + active
               + '\''
               + '}';
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(final Integer port) {
        this.port = port;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    /**
     * Propriété de configuration de la connexion à la messagerie
     */
    @Embeddable
    public static class Property implements Serializable {

        /**
         * Nom de la propriété
         */
        @Column(name = "name")
        private String name;

        /**
         * Valeur de la propriété
         */
        @Column(name = "value")
        private String value;

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(final String value) {
            this.value = value;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            final Property property = (Property) o;
            return Objects.equals(name, property.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return "Property{" + "name='"
                   + name
                   + '\''
                   + ", value="
                   + value
                   + '}';
        }
    }
}
