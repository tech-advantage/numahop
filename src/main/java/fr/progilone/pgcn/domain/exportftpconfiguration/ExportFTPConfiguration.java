package fr.progilone.pgcn.domain.exportftpconfiguration;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.google.common.base.MoreObjects;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.library.Library;

/**
 * Classe métier permettant de gérer la configuration d'export FTP.
 */
@Entity
@Table(name = ExportFTPConfiguration.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "exportConfigurationFTP", value = ExportFTPConfiguration.class)})
public class ExportFTPConfiguration extends AbstractDomainObject {

    /**
     * Nom des tables dans la base de données.
     */
    public static final String TABLE_NAME = "conf_export_ftp";

    /**
     * Label
     */
    @Column(name = "label")
    private String label;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library")
    private Library library;
    
    /**
     * La configuration est active / inactive
     */
    @Column(name = "active", nullable = false)
    private boolean active = true;
    
    /**
     * serveur de stockage Omeka
     */
    @Column(name = "storage_server")
    private String storageServer;
    
    /**
     * port pour acces au serveur de stockage Omeka
     */
    @Column(name = "port")
    private String port;

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
    
    /* Types de fichiers à exporter */
    @Column(name = "export_view")
    private boolean exportView;
    
    @Column(name = "export_master")
    private boolean exportMaster;
    
    @Column(name = "export_thumb")
    private boolean exportThumb;
    
    @Column(name = "export_pdf")
    private boolean exportPdf;
    
    @Column(name = "export_mets")
    private boolean exportMets;
    
    @Column(name = "export_aip_sip")
    private boolean exportAipSip;
    

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

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public String getStorageServer() {
        return storageServer;
    }

    public void setStorageServer(final String storageServer) {
        this.storageServer = storageServer;
    }

    public String getPort() {
        return port;
    }

    public void setPort(final String port) {
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(final String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public boolean isExportView() {
        return exportView;
    }

    public void setExportView(final boolean exportView) {
        this.exportView = exportView;
    }

    public boolean isExportMaster() {
        return exportMaster;
    }

    public void setExportMaster(final boolean exportMaster) {
        this.exportMaster = exportMaster;
    }

    public boolean isExportThumb() {
        return exportThumb;
    }

    public void setExportThumb(final boolean exportThumb) {
        this.exportThumb = exportThumb;
    }

    public boolean isExportPdf() {
        return exportPdf;
    }

    public void setExportPdf(final boolean exportPdf) {
        this.exportPdf = exportPdf;
    }

    public boolean isExportMets() {
        return exportMets;
    }

    public void setExportMets(final boolean exportMets) {
        this.exportMets = exportMets;
    }

    public boolean isExportAipSip() {
        return exportAipSip;
    }

    public void setExportAipSip(final boolean exportAipSip) {
        this.exportAipSip = exportAipSip;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).omitNullValues().add("label", label).add("address", address).add("login", login).toString();
    }

}
