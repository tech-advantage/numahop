package fr.progilone.pgcn.domain.administration.omeka;

import com.google.common.base.MoreObjects;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.library.Library;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;

/**
 * Configuration des connexions Omeka.
 *
 */
@Entity
@Table(name = OmekaConfiguration.TABLE_NAME)
public class OmekaConfiguration extends AbstractDomainObject {

    public static final String TABLE_NAME = "conf_omeka";

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
     * Serveur de dépot FTP
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

    /**
     * Url d'acces aux fichiers via Omeka.
     */
    @Column(name = "access_url")
    private String accessUrl;

    /**
     * mail destination du fichier csv.
     */
    @Column(name = "mail_csv")
    private String mailCsv;

    /**
     * envoi des fichiers en SFTP
     */
    @Column(name = "sftp")
    private boolean sftp;

    /**
     * Types de fichiers à exporter.
     */
    @Column(name = "export_mets")
    private boolean exportMets;
    @Column(name = "export_master")
    private boolean exportMaster;
    @Column(name = "export_view")
    private boolean exportView;
    @Column(name = "export_thumb")
    private boolean exportThumb;
    @Column(name = "export_pdf")
    private boolean exportPdf;

    @OneToMany(mappedBy = "confOmeka", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OmekaList> omekaLists;

    @Column(name = "omekas")
    private boolean omekas;

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

    public String getAccessUrl() {
        return accessUrl;
    }

    public void setAccessUrl(final String accessUrl) {
        this.accessUrl = accessUrl;
    }

    public String getMailCsv() {
        return mailCsv;
    }

    public void setMailCsv(final String mailCsv) {
        this.mailCsv = mailCsv;
    }

    public boolean isExportMets() {
        return exportMets;
    }

    public void setExportMets(final boolean exportMets) {
        this.exportMets = exportMets;
    }

    public boolean isExportMaster() {
        return exportMaster;
    }

    public void setExportMaster(final boolean exportMaster) {
        this.exportMaster = exportMaster;
    }

    public boolean isExportView() {
        return exportView;
    }

    public void setExportView(final boolean exportView) {
        this.exportView = exportView;
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

    public List<OmekaList> getOmekaLists() {
        return omekaLists;
    }

    public void setOmekaLists(final List<OmekaList> omekaLists) {
        this.omekaLists = omekaLists;
    }

    public boolean isOmekas() {
        return omekas;
    }

    public void setOmekas(final boolean omekas) {
        this.omekas = omekas;
    }

    public boolean isSftp() {
        return sftp;
    }

    public void setSftp(final boolean sftp) {
        this.sftp = sftp;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .omitNullValues()
                          .add("label", label)
                          .add("active", active)
                          .add("server omeka", storageServer)
                          .add("port", port)
                          .add("server depot FTP", address)
                          .toString();
    }
}
