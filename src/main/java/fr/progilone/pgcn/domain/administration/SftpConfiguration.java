package fr.progilone.pgcn.domain.administration;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.library.Library;

/**
 * Configuration d'une connexion SFTP, utilisée en particulier pour l'export CINES
 * <p>
 * Created by Sébastien on 29/12/2016.
 */
@Entity
@Table(name = SftpConfiguration.TABLE_NAME)
public class SftpConfiguration extends AbstractDomainObject {

    public static final String TABLE_NAME = "conf_sftp";

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
     * Serveur SFTP
     */
    @Column(name = "host")
    private String host;

    /**
     * Port de connexion
     */
    @Column(name = "port")
    private Integer port;

    /**
     * Répertoire distant dans lequel seront déposés les fichiers
     */
    @Column(name = "target_dir")
    private String targetDir;

    @OneToMany(mappedBy = "confPac", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CinesPAC> pacs;

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

    public Integer getPort() {
        return port;
    }

    public void setPort(final Integer port) {
        this.port = port;
    }

    public String getTargetDir() {
        return targetDir;
    }

    public void setTargetDir(final String targetDir) {
        this.targetDir = targetDir;
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
        return "ConfigurationSFTP{" +
               "label='" + label + '\'' +
               ", username='" + username + '\'' +
               ", host='" + host + '\'' +
               ", port=" + port +
               ", targetDir='" + targetDir + '\'' +
               ", active='" + active + '\'' +
               '}';
    }

    public List<CinesPAC> getPacs() {
        return pacs;
    }

    public void setPacs(List<CinesPAC> pacs) {
        this.pacs = pacs;
    }
}
