package fr.progilone.pgcn.domain.library;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.google.common.base.MoreObjects;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.domain.checkconfiguration.CheckConfiguration;
import fr.progilone.pgcn.domain.delivery.DeliverySlipConfiguration;
import fr.progilone.pgcn.domain.document.CheckSlipConfiguration;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportSlipConfiguration;
import fr.progilone.pgcn.domain.exportftpconfiguration.ExportFTPConfiguration;
import fr.progilone.pgcn.domain.ftpconfiguration.FTPConfiguration;
import fr.progilone.pgcn.domain.ocrlangconfiguration.OcrLangConfiguration;
import fr.progilone.pgcn.domain.platform.Platform;
import fr.progilone.pgcn.domain.user.Address;
import fr.progilone.pgcn.domain.user.Role;

/**
 * Classe métier permettant de gérer les bibliothèques.
 */
@Entity
@Table(name = Library.TABLE_NAME)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Library extends AbstractDomainObject {

    /**
     * Nom des tables dans la base de données.
     */
    public static final String TABLE_NAME = "lib_library";

    /**
     * Nom de la bibliothèque
     */
    @Column(name = "name", unique = true)
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String name;

    /**
     * Adresse de la bibliothèque
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "address")
    private Address address;

    /**
     * Site internet
     */
    @Column(name = "website")
    private String website;

    /**
     * Numéro de téléphone
     */
    @Column(name = "phone_number")
    private String phoneNumber;

    /**
     * Courriel
     */
    @Column(name = "email")
    private String email;

    /**
     * Préfixe
     */
    @Column(name = "prefix")
    private String prefix;

    /**
     * Numéro Z39.50
     */
    @Column(name = "number")
    private String number;

    /**
     * Etat
     */
    @Column(name = "active")
    private boolean active;

    /**
     * Institution
     */
    @Column(name = "institution")
    private String institution;

    /**
     * Identifiant service versant Cines
     */
    @Column(name = "cines_service")
    private String cinesService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role")
    private Role defaultRole;

    @Column(name = "superuser")
    private Boolean superuser = Boolean.FALSE;

    /**
     * Liste des plateformes associées à la bibliothèque
     */
    @OneToMany(mappedBy = "library", fetch = FetchType.LAZY)
    private final Set<Platform> platforms = new HashSet<>();

    @OneToMany(mappedBy = "library", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<FTPConfiguration> ftpConfigurations = new HashSet<>();

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "active_configuration_ftp")
    private FTPConfiguration activeFTPConfiguration;

    @OneToMany(mappedBy = "library", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<CheckConfiguration> checkConfigurations = new HashSet<>();

    @OneToOne(mappedBy = "library", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private DeliverySlipConfiguration deliverySlipConfiguration;
    
    @OneToOne(mappedBy = "library", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private CheckSlipConfiguration checkSlipConfiguration;
    
    @OneToOne(mappedBy = "library", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private ConditionReportSlipConfiguration condReportSlipConfiguration;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "active_configuration_check")
    private CheckConfiguration activeCheckConfiguration;
    
    @OneToMany(mappedBy = "library", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private final Set<ViewsFormatConfiguration> viewsFormatConfigurations = new HashSet<>();

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "active_format_configuration")
    private ViewsFormatConfiguration activeFormatConfiguration;
    
    @OneToMany(mappedBy = "library", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<ExportFTPConfiguration> exportFtpConfigurations = new HashSet<>();

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "active_configuration_export_ftp")
    private ExportFTPConfiguration activeExportFTPConfiguration;
    
    @OneToMany(mappedBy = "library", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<OcrLangConfiguration> ocrLangConfigurations = new HashSet<>();
    
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "active_configuration_ocr_lang")
    private OcrLangConfiguration activeOcrLangConfiguration;
    

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(final Address address) {
        this.address = address;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(final String website) {
        this.website = website;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(final String number) {
        this.number = number;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public Set<Platform> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(final Set<Platform> platforms) {
        this.platforms.clear();
        if (platforms != null) {
            platforms.forEach(this::addPlatform);
        }
    }

    public void addPlatform(final Platform platform) {
        if (platform != null) {
            this.platforms.add(platform);
        }
    }

    public String getCinesService() {
        return cinesService;
    }

    public void setCinesService(final String cinesService) {
        this.cinesService = cinesService;
    }

    public FTPConfiguration getActiveFTPConfiguration() {
        return activeFTPConfiguration;
    }

    public void setActiveFTPConfiguration(final FTPConfiguration activeFTPConfiguration) {
        this.activeFTPConfiguration = activeFTPConfiguration;
    }

    public Set<FTPConfiguration> getFtpConfigurations() {
        return ftpConfigurations;
    }

    public void setFtpConfigurations(final Set<FTPConfiguration> ftpConfigurations) {
        this.ftpConfigurations = ftpConfigurations;
    }

    public CheckConfiguration getActiveCheckConfiguration() {
        return activeCheckConfiguration;
    }

    public void setActiveCheckConfiguration(final CheckConfiguration activeCheckConfiguration) {
        this.activeCheckConfiguration = activeCheckConfiguration;
    }

    public Set<CheckConfiguration> getCheckConfigurations() {
        return checkConfigurations;
    }

    public void setCheckConfigurations(final Set<CheckConfiguration> checkConfigurations) {
        this.checkConfigurations = checkConfigurations;
    }

    public Role getDefaultRole() {
        return defaultRole;
    }

    public void setDefaultRole(final Role defaultRole) {
        this.defaultRole = defaultRole;
    }

    public Boolean getSuperuser() {
        return superuser;
    }

    public void setSuperuser(final Boolean superuser) {
        this.superuser = superuser;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("name", name).add("identifier", identifier).toString();
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(final String institution) {
        this.institution = institution;
    }

    public DeliverySlipConfiguration getDeliverySlipConfiguration() {
        return deliverySlipConfiguration;
    }

    public void setDeliverySlipConfiguration(final DeliverySlipConfiguration deliverySlipConfiguration) {
        this.deliverySlipConfiguration = deliverySlipConfiguration;
    }

    public CheckSlipConfiguration getCheckSlipConfiguration() {
        return checkSlipConfiguration;
    }

    public void setCheckSlipConfiguration(final CheckSlipConfiguration checkSlipConfiguration) {
        this.checkSlipConfiguration = checkSlipConfiguration;
    }

    public ConditionReportSlipConfiguration getCondReportSlipConfiguration() {
        return condReportSlipConfiguration;
    }

    public void setCondReportSlipConfiguration(final ConditionReportSlipConfiguration condReportSlipConfiguration) {
        this.condReportSlipConfiguration = condReportSlipConfiguration;
    }

    public ViewsFormatConfiguration getActiveFormatConfiguration() {
        return activeFormatConfiguration;
    }

    public void setActiveFormatConfiguration(final ViewsFormatConfiguration activeFormatConfiguration) {
        this.activeFormatConfiguration = activeFormatConfiguration;
    }

    public Set<ViewsFormatConfiguration> getViewsFormatConfigurations() {
        return viewsFormatConfigurations;
    }

    public Set<ExportFTPConfiguration> getExportFtpConfigurations() {
        return exportFtpConfigurations;
    }

    public void setExportFtpConfigurations(final Set<ExportFTPConfiguration> exportFtpConfigurations) {
        this.exportFtpConfigurations = exportFtpConfigurations;
    }

    public ExportFTPConfiguration getActiveExportFTPConfiguration() {
        return activeExportFTPConfiguration;
    }

    public void setActiveExportFTPConfiguration(final ExportFTPConfiguration activeExportFTPConfiguration) {
        this.activeExportFTPConfiguration = activeExportFTPConfiguration;
    }

    public Set<OcrLangConfiguration> getOcrLangConfigurations() {
        return ocrLangConfigurations;
    }

    public void setOcrLangConfigurations(final Set<OcrLangConfiguration> ocrLangConfigurations) {
        this.ocrLangConfigurations = ocrLangConfigurations;
    }

    public OcrLangConfiguration getActiveOcrLangConfiguration() {
        return activeOcrLangConfiguration;
    }

    public void setActiveOcrLangConfiguration(final OcrLangConfiguration activeOcrLangConfiguration) {
        this.activeOcrLangConfiguration = activeOcrLangConfiguration;
    }
    
}
