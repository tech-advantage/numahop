package fr.progilone.pgcn.domain.lot;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.google.common.base.MoreObjects;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.administration.CinesPAC;
import fr.progilone.pgcn.domain.administration.ExportFTPDeliveryFolder;
import fr.progilone.pgcn.domain.administration.InternetArchiveCollection;
import fr.progilone.pgcn.domain.administration.omeka.OmekaConfiguration;
import fr.progilone.pgcn.domain.administration.omeka.OmekaList;
import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.domain.checkconfiguration.CheckConfiguration;
import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exportftpconfiguration.ExportFTPConfiguration;
import fr.progilone.pgcn.domain.ftpconfiguration.FTPConfiguration;
import fr.progilone.pgcn.domain.ocrlangconfiguration.OcrLanguage;
import fr.progilone.pgcn.domain.platform.Platform;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.workflow.WorkflowModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe métier permettant de gérer les bibliothèques.
 */
@Entity
@Table(name = Lot.TABLE_NAME)
// Jackson
@JsonSubTypes({@JsonSubTypes.Type(name = "lot", value = Lot.class)})
// Audit
@AuditTable(value = Lot.AUDIT_TABLE_NAME)
public class Lot extends AbstractDomainObject {

    private static final Logger LOG = LoggerFactory.getLogger(Lot.class);

    /**
     * Nom des tables dans la base de données.
     */
    public static final String TABLE_NAME = "lot_lot";
    public static final String AUDIT_TABLE_NAME = "aud_lot_lot";

    /**
     * Projects associées
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project")
    private Project project;

    @OneToMany(mappedBy = "lot", fetch = FetchType.LAZY)
    private final Set<Delivery> deliveries = new HashSet<>();

    /**
     * Unités documentaires rattachées
     */
    @OneToMany(mappedBy = "lot", fetch = FetchType.LAZY)
    private final Set<DocUnit> docUnits = new HashSet<>();

    /**
     * Libellé
     */
    @Column(name = "label")
    private String label;

    /**
     * code UNIQUE pour le dossier de livraison
     */
    @Column(name = "code")
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_configuration_ftp")
    private FTPConfiguration activeFTPConfiguration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_configuration_check")
    private CheckConfiguration activeCheckConfiguration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_format_configuration")
    private ViewsFormatConfiguration activeFormatConfiguration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_configuration_export_ftp")
    private ExportFTPConfiguration activeExportFTPConfiguration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider")
    private User provider;

    /** langage selectionné pour ocr */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_ocr_lang")
    private OcrLanguage activeOcrLanguage;

    /**
     * Type attendu
     */
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private Type type;

    /**
     * Description
     */
    @Column(name = "description")
    private String description;

    /**
     * Etat
     */
    @Column(name = "active")
    private boolean active;

    /**
     * statut
     */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @Audited
    private LotStatus status;

    /**
     * Date de fin réelle
     */
    @Column(name = "real_end_date")
    private LocalDate realEndDate;

    /**
     * Notes concernant le conditionnement
     */
    @Column(name = "cond_notes")
    private String condNotes;

    /**
     * Notes concernant la numérisation
     */
    @Column(name = "num_notes")
    private String numNotes;

    /**
     * Date de livraison prévue
     */
    @Column(name = "delivery_date_forseen")
    private Date deliveryDateForseen;

    /**
     * Format des fichiers numériques
     */
    @Column(name = "required_format")
    private String requiredFormat;

    /**
     * Compression des fichiers numériques attendue.
     */
    @Column(name = "required_type_compression")
    private String requiredTypeCompression;

    /**
     * Taux max de compression attendu.
     */
    @Column(name = "required_taux_compression")
    private Integer requiredTauxCompression;

    /**
     * Resolution attendue.
     */
    @Column(name = "required_resolution")
    private String requiredResolution;

    /**
     * Profil colorimétrique attendu.
     */
    @Column(name = "required_colorspace")
    private String requiredColorspace;

    /**
     * Nom de la collection Internet Archive pour la diffusion
     */
    @ManyToOne
    @JoinColumn(name = "collection_ia")
    private InternetArchiveCollection collectionIA;

    /**
     * Plan de classement PAC
     */
    @ManyToOne
    @JoinColumn(name = "classement_pac")
    private CinesPAC planClassementPAC;

    /**
     * Collections Omeka pour la diffusion.
     */
    @ManyToOne
    @JoinColumn(name = "collection_omeka")
    private OmekaList omekaCollection;

    /**
     * Items Omeka pour la diffusion.
     */
    @ManyToOne
    @JoinColumn(name = "items_omeka")
    private OmekaList omekaItem;

    /**
     * plateformes associées au lot
     */
    @OneToMany(mappedBy = "lot", fetch = FetchType.LAZY)
    private Set<Platform> platforms = new HashSet<>();

    /**
     * Modèle de workflow lié
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_model")
    private WorkflowModel workflowModel;

    /**
     * Flag indiquant si les fichiers ont ete sauvegardes suite à cloture.
     */
    @Column(name = "files_archived")
    private boolean filesArchived;

    /**
     * Configuration Omeka
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "omeka_configuration")
    private OmekaConfiguration omekaConfiguration;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_export_ftp_delivery_folder")
    private ExportFTPDeliveryFolder activeExportFTPDeliveryFolder;

    public String getRequiredFormat() {
        return requiredFormat;
    }

    public void setRequiredFormat(final String requiredFormat) {
        this.requiredFormat = requiredFormat;
    }

    public String getRequiredTypeCompression() {
        return requiredTypeCompression;
    }

    public void setRequiredTypeCompression(final String requiredTypeCompression) {
        this.requiredTypeCompression = requiredTypeCompression;
    }

    public Integer getRequiredTauxCompression() {
        return requiredTauxCompression;
    }

    public void setRequiredTauxCompression(final Integer requiredTauxCompression) {
        this.requiredTauxCompression = requiredTauxCompression;
    }

    public String getRequiredResolution() {
        return requiredResolution;
    }

    public void setRequiredResolution(final String requiredResolution) {
        this.requiredResolution = requiredResolution;
    }

    public String getRequiredColorspace() {
        return requiredColorspace;
    }

    public void setRequiredColorspace(final String requiredColorspace) {
        this.requiredColorspace = requiredColorspace;
    }

    public FTPConfiguration getActiveFTPConfiguration() {
        return activeFTPConfiguration;
    }

    public void setActiveFTPConfiguration(final FTPConfiguration activeFTPConfiguration) {
        this.activeFTPConfiguration = activeFTPConfiguration;
    }

    public CheckConfiguration getActiveCheckConfiguration() {
        return activeCheckConfiguration;
    }

    public void setActiveCheckConfiguration(final CheckConfiguration activeCheckConfiguration) {
        this.activeCheckConfiguration = activeCheckConfiguration;
    }

    public ViewsFormatConfiguration getActiveFormatConfiguration() {
        return activeFormatConfiguration;
    }

    public void setActiveFormatConfiguration(final ViewsFormatConfiguration activeFormatConfiguration) {
        this.activeFormatConfiguration = activeFormatConfiguration;
    }

    public InternetArchiveCollection getCollectionIA() {
        if (collectionIA != null) {
            return collectionIA;
        }
        // TODO: gérer l'initialisation au niveau du service, ou au moins pas dans le getter
        try {
            if (!Hibernate.isInitialized(project)) {
                Hibernate.initialize(project);
            }
            return project != null ? project.getCollectionIA()
                                   : null;
        } catch (final LazyInitializationException e) {
            LOG.warn("Problème d'initialisation (IA): {}", e.getMessage());
            return null;
        }
    }

    public void setCollectionIA(final InternetArchiveCollection collectionIA) {
        this.collectionIA = collectionIA;
    }

    public CinesPAC getPlanClassementPAC() {
        if (planClassementPAC != null) {
            return planClassementPAC;
        }
        // TODO: gérer l'initialisation au niveau du service, ou au moins pas dans le getter
        try {
            if (!Hibernate.isInitialized(project)) {
                Hibernate.initialize(project);
            }
            return project != null ? project.getPlanClassementPAC()
                                   : null;
        } catch (final LazyInitializationException e) {
            LOG.warn("Problème d'initialisation (PAC): {}", e.getMessage());
            return null;
        }
    }

    public void setPlanClassementPAC(final CinesPAC planClassementPAC) {
        this.planClassementPAC = planClassementPAC;
    }

    public User getProvider() {
        if (provider != null) {
            return provider;
        }
        // TODO: gérer l'initialisation au niveau du service, ou au moins pas dans le getter
        try {
            if (!Hibernate.isInitialized(project)) {
                Hibernate.initialize(project);
            }
            return project != null ? project.getProvider()
                                   : null;
        } catch (final LazyInitializationException e) {
            LOG.warn("Problème d'initialisation (Provider): {}", e.getMessage());
            return null;
        }
    }

    public void setProvider(final User provider) {
        this.provider = provider;
    }

    /**
     * type de statut
     */
    public enum LotStatus {
        /**
         * Créé
         */
        CREATED,
        /**
         * En cours
         */
        ONGOING,
        /**
         * En attente
         */
        PENDING,
        /**
         * Annulé
         */
        CANCELED,
        /**
         * Clôturé
         */
        CLOSED
    }

    /**
     * Type de lot
     */
    public enum Type {
        PHYSICAL,
        DIGITAL
    }

    public Project getProject() {
        return project;
    }

    public void setProject(final Project project) {
        this.project = project;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public Type getType() {
        return type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public LotStatus getStatus() {
        return status;
    }

    public void setStatus(final LotStatus status) {
        this.status = status;
    }

    public String getCondNotes() {
        return condNotes;
    }

    public void setCondNotes(final String condNotes) {
        this.condNotes = condNotes;
    }

    public String getNumNotes() {
        return numNotes;
    }

    public void setNumNotes(final String numNotes) {
        this.numNotes = numNotes;
    }

    public Date getDeliveryDateForseen() {
        return deliveryDateForseen;
    }

    public void setDeliveryDateForseen(final Date deliveryDateForseen) {
        this.deliveryDateForseen = deliveryDateForseen;
    }

    public Set<Platform> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(final Set<Platform> platforms) {
        this.platforms = platforms;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public LocalDate getRealEndDate() {
        return realEndDate;
    }

    public void setRealEndDate(final LocalDate realEndDate) {
        this.realEndDate = realEndDate;
    }

    public Set<DocUnit> getDocUnits() {
        return docUnits;
    }

    public void setDocUnits(final Set<DocUnit> docUnits) {
        this.docUnits.clear();
        if (docUnits != null) {
            docUnits.forEach(this::addDocUnit);
        }
    }

    public void addDocUnit(final DocUnit docUnit) {
        if (docUnit != null) {
            this.docUnits.add(docUnit);
            docUnit.setLot(this);
        }
    }

    public WorkflowModel getWorkflowModel() {
        return workflowModel;
    }

    public void setWorkflowModel(final WorkflowModel workflowModel) {
        this.workflowModel = workflowModel;
    }

    public boolean isFilesArchived() {
        return filesArchived;
    }

    public void setFilesArchived(final boolean filesArchived) {
        this.filesArchived = filesArchived;
    }

    public OmekaList getOmekaCollection() {
        return omekaCollection;
    }

    public void setOmekaCollection(final OmekaList omekaCollection) {
        this.omekaCollection = omekaCollection;
    }

    public OmekaList getOmekaItem() {
        return omekaItem;
    }

    public void setOmekaItem(final OmekaList omekaItem) {
        this.omekaItem = omekaItem;
    }

    public OmekaConfiguration getOmekaConfiguration() {
        return omekaConfiguration;
    }

    public void setOmekaConfiguration(final OmekaConfiguration omekaConfiguration) {
        this.omekaConfiguration = omekaConfiguration;
    }

    public ExportFTPConfiguration getActiveExportFTPConfiguration() {
        return activeExportFTPConfiguration;
    }

    public void setActiveExportFTPConfiguration(final ExportFTPConfiguration activeExportFTPConfiguration) {
        this.activeExportFTPConfiguration = activeExportFTPConfiguration;
    }

    public OcrLanguage getActiveOcrLanguage() {
        return activeOcrLanguage;
    }

    public void setActiveOcrLanguage(final OcrLanguage activeOcrLanguage) {
        this.activeOcrLanguage = activeOcrLanguage;
    }

    public ExportFTPDeliveryFolder getActiveExportFTPDeliveryFolder() {
        return activeExportFTPDeliveryFolder;
    }

    public void setActiveExportFTPDeliveryFolder(final ExportFTPDeliveryFolder activeExportFTPDeliveryFolder) {
        this.activeExportFTPDeliveryFolder = activeExportFTPDeliveryFolder;
    }

    public Set<Delivery> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(final Set<Delivery> deliveries) {
        this.deliveries.clear();
        if (deliveries != null) {
            deliveries.forEach(this::addDelivery);
        }
    }

    public void addDelivery(final Delivery delivery) {
        if (delivery != null) {
            this.deliveries.add(delivery);
            delivery.setLot(this);
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .omitNullValues()
                          .add("project", project)
                          .add("label", label)
                          .add("type", type)
                          .add("description", description)
                          .add("active", active)
                          .add("status", status)
                          .add("condNotes", condNotes)
                          .add("numNotes", numNotes)
                          .add("deliveryDateForseen", deliveryDateForseen)
                          .add("code", code)
                          .toString();
    }
}
