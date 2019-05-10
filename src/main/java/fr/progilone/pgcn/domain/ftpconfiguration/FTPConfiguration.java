package fr.progilone.pgcn.domain.ftpconfiguration;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.google.common.base.MoreObjects;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.library.Library;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Classe métier permettant de gérer la configuration FTP.
 */
@Entity
@Table(name = FTPConfiguration.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "configurationFTP", value = FTPConfiguration.class)})
public class FTPConfiguration extends AbstractDomainObject {

    /**
     * Nom des tables dans la base de données.
     */
    public static final String TABLE_NAME = "conf_configuration_ftp";

    /**
     * Label
     */
    @Column(name = "label")
    private String label;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library")
    private Library library;

    /**
     * Adresse FTP de dépot
     */
    @Column(name = "address")
    private String address;

    /**
     * Login FTP
     */
    @Column(name = "login")
    private String login;

    /**
     * Mot de passe FTP
     */
    @Column(name = "password")
    private String password;

    @Column(name = "delivery_folder")
    private String deliveryFolder;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    public String getDeliveryFolder() {
        return deliveryFolder;
    }

    public void setDeliveryFolder(String deliveryFolder) {
        this.deliveryFolder = deliveryFolder;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).omitNullValues().add("label", label).add("address", address).add("login", login).toString();
    }
}
