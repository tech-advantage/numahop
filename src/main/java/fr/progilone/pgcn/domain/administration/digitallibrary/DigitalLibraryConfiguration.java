package fr.progilone.pgcn.domain.administration.digitallibrary;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.library.Library;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Classe décrivant la configurtation de diffusion sur la bibliothèque numérique (ex: Limb Gallery)
 */
@Entity
@Table(name = DigitalLibraryConfiguration.TABLE_NAME)
public class DigitalLibraryConfiguration extends AbstractDomainObject {

    public static final String TABLE_NAME = "conf_digital_library";

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
     * port pour acces au serveur de stockage
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
     * Mot de passe, crypté avec {@link fr.progilone.pgcn.service.util.CryptoService}
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password")
    private String password;

    /**
     * répertoire de dépot.
     */
    @Column(name = "delivery_folder")
    private String deliveryFolder;

    /**
     * mail destination du fichier.
     */
    @Column(name = "mail")
    private String mail;

    /* Types de fichiers à exporter */
    @Column(name = "export_view")
    private boolean exportView;

    @Column(name = "export_print")
    private boolean exportPrint;

    @Column(name = "export_thumb")
    private boolean exportThumb;

    @Column(name = "export_pdf")
    private boolean exportPdf;

    @Column(name = "export_mets")
    private boolean exportMets;

    @Column(name = "export_aip_sip")
    private boolean exportAipSip;

    @Column(name = "export_alto")
    private boolean exportAlto;

    /**
     * Valeur par défaut des champs vides
     */
    @Column(name = "default_value")
    private String defaultValue;

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

    public String getDeliveryFolder() {
        return deliveryFolder;
    }

    public void setDeliveryFolder(final String deliveryFolder) {
        this.deliveryFolder = deliveryFolder;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(final String mail) {
        this.mail = mail;
    }

    public boolean isExportView() {
        return exportView;
    }

    public void setExportView(final boolean exportView) {
        this.exportView = exportView;
    }

    public boolean isExportPrint() {
        return exportPrint;
    }

    public void setExportPrint(final boolean exportMaster) {
        this.exportPrint = exportMaster;
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

    public boolean isExportAlto() {
        return exportAlto;
    }

    public void setExportAlto(final boolean exportAlto) {
        this.exportAlto = exportAlto;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return "DigitalLibraryConfiguration{" + "label='"
               + label
               + '\''
               + ", Serveur='"
               + address
               + '\''
               + ", active='"
               + active
               + '\''
               + '}';
    }

    public enum RecordFormat {
        MARC,
        CSV,
        EAD
    }
}
