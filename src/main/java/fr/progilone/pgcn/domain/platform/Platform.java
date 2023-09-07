package fr.progilone.pgcn.domain.platform;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.google.common.base.MoreObjects;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.project.Project;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Set;

/**
 * Classe métier permettant de gérer les tierces plateformes.
 */
@Entity
@Table(name = Platform.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "platform", value = Platform.class)})
public class Platform extends AbstractDomainObject {

    /**
     * Nom de la table dans la base de données.
     */
    public static final String TABLE_NAME = "plat_platform";

    /**
     * Libellé
     */
    @Column(name = "label")
    private String label;

    /**
     * URL d'accès
     */
    @Column(name = "url")
    private String url;

    /**
     * Numéro RCR
     */
    @Column(name = "rcr")
    private String rcr;

    /**
     * login
     */
    @Column(name = "login")
    private String login;

    /**
     * Mot de passe
     */
    @Column(name = "password")
    private String password;

    /**
     * Format attendu
     */
    @Column(name = "format")
    @Enumerated(EnumType.STRING)
    private Format format;

    /**
     * Type attendu
     */
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private Type type;

    /**
     * Bibliothèque principale
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library")
    private Library library;

    /**
     * Projets associées
     */
    @ManyToMany(mappedBy = "associatedPlatforms", fetch = FetchType.LAZY)
    private Set<Project> associatedProjects;

    /**
     * Lots associées
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot")
    private Lot lot;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRcr() {
        return rcr;
    }

    public void setRcr(String rcr) {
        this.rcr = rcr;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Set<Project> getAssociatedProjects() {
        return associatedProjects;
    }

    public void setAssociatedProjects(Set<Project> associatedProjects) {
        this.associatedProjects = associatedProjects;
    }

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    public Lot getLot() {
        return lot;
    }

    public void setLot(Lot lot) {
        this.lot = lot;
    }

    /**
     * Type d'utilisateur
     */
    public enum Format {
        DC,
        DCQ
    }

    /**
     * Type d'utilisateur
     */
    public enum Type {
        ARCHIVING,
        DIFFUSION
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .omitNullValues()
                          .add("label", label)
                          .add("url", url)
                          .add("rcr", rcr)
                          .add("login", login)
                          .add("password", password)
                          .add("format", format)
                          .add("type", type)
                          .add("associatedProjects", associatedProjects)
                          .add("library", library)
                          .add("lot", lot)
                          .toString();
    }
}
