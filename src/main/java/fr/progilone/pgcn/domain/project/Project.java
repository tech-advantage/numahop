package fr.progilone.pgcn.domain.project;

import static fr.progilone.pgcn.service.es.EsConstant.ANALYZER_CI_AI;
import static fr.progilone.pgcn.service.es.EsConstant.ANALYZER_CI_AS;
import static fr.progilone.pgcn.service.es.EsConstant.ANALYZER_KEYWORD;
import static fr.progilone.pgcn.service.es.EsConstant.ANALYZER_PHRASE;
import static fr.progilone.pgcn.service.es.EsConstant.SUBFIELD_CI_AI;
import static fr.progilone.pgcn.service.es.EsConstant.SUBFIELD_CI_AS;
import static fr.progilone.pgcn.service.es.EsConstant.SUBFIELD_PHRASE;
import static fr.progilone.pgcn.service.es.EsConstant.SUBFIELD_RAW;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.google.common.base.MoreObjects;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.administration.CinesPAC;
import fr.progilone.pgcn.domain.administration.InternetArchiveCollection;
import fr.progilone.pgcn.domain.administration.omeka.OmekaList;
import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.domain.checkconfiguration.CheckConfiguration;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exportftpconfiguration.ExportFTPConfiguration;
import fr.progilone.pgcn.domain.ftpconfiguration.FTPConfiguration;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.platform.Platform;
import fr.progilone.pgcn.domain.train.Train;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.workflow.WorkflowModel;

/**
 * Classe métier permettant de gérer les projets.
 */
// Hibernate
@Entity
@Table(name = Project.TABLE_NAME)
// Jackson
@JsonSubTypes({@JsonSubTypes.Type(name = "project", value = Project.class)})
// Audit
@AuditTable(value = Project.AUDIT_TABLE_NAME)
// Elasticsearch
@Document(indexName = "#{elasticsearchIndexName}", type = Project.ES_TYPE, createIndex = false)
public class Project extends AbstractDomainObject {

    /**
     * Nom des tables dans la base de données.
     */
    public static final String TABLE_NAME = "proj_project";
    public static final String TABLE_NAME_PROJECT_LIBRARY = "proj_project_library";
    public static final String TABLE_NAME_PROJECT_PLATFORM = "proj_project_platform";
    public static final String TABLE_NAME_PROJECT_USER = "proj_project_user";
    public static final String ES_TYPE = "project";
    public static final String AUDIT_TABLE_NAME = "aud_proj_project";

    /**
     * Bibliothèque principale
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library")
    @Field(type = FieldType.Object)
    private Library library;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_configuration_ftp")
    private FTPConfiguration activeFTPConfiguration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_configuration_check")
    private CheckConfiguration activeCheckConfiguration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider")
    @Field(type = FieldType.Object)
    private User provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_format_configuration")
    private ViewsFormatConfiguration activeFormatConfiguration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_configuration_export_ftp")
    private ExportFTPConfiguration activeExportFTPConfiguration;
    /**
     * Nom
     */
    @Column(name = "name")
    @MultiField(mainField = @Field(type = FieldType.String),
                otherFields = {@InnerField(type = FieldType.String, suffix = SUBFIELD_RAW, index = FieldIndex.not_analyzed),
                               @InnerField(type = FieldType.String,
                                           suffix = SUBFIELD_CI_AI,
                                           indexAnalyzer = ANALYZER_CI_AI,
                                           searchAnalyzer = ANALYZER_CI_AI),
                               @InnerField(type = FieldType.String,
                                           suffix = SUBFIELD_CI_AS,
                                           indexAnalyzer = ANALYZER_CI_AS,
                                           searchAnalyzer = ANALYZER_CI_AS),
                               @InnerField(type = FieldType.String,
                                           suffix = SUBFIELD_PHRASE,
                                           indexAnalyzer = ANALYZER_PHRASE,
                                           searchAnalyzer = ANALYZER_PHRASE)})
    private String name;

    /**
     * Description
     */
    @Column(name = "description")
    private String description;

    /**
     * Date de début
     */
    @Column(name = "start_date")
    @Field(type = FieldType.Date)
    private LocalDate startDate;

    /**
     * Date de fin prévisionnelle
     */
    @Column(name = "forecast_end_date")
    @Field(type = FieldType.Date)
    private LocalDate forecastEndDate;

    /**
     * Date de fin réelle
     */
    @Column(name = "real_end_date")
    @Field(type = FieldType.Date)
    private LocalDate realEndDate;

    /**
     * Actif
     */
    @Column(name = "active")
    @Field(type = FieldType.Boolean)
    private boolean active;

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
     * statut
     */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @Field(type = FieldType.String, analyzer = ANALYZER_KEYWORD)
    @Audited
    private ProjectStatus status;

    /**
     * Lots associées
     */
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private final Set<Lot> lots = new HashSet<>();

    /**
     * Trains associés
     */
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private final Set<Train> trains = new HashSet<>();

    /**
     * Tierces plateformes associées
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = TABLE_NAME_PROJECT_PLATFORM,
               joinColumns = {@JoinColumn(name = "project", referencedColumnName = "identifier")},
               inverseJoinColumns = {@JoinColumn(name = "proj_platform", referencedColumnName = "identifier")})
    private final Set<Platform> associatedPlatforms = new HashSet<>();

    /**
     * Bibliothèques associées
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = TABLE_NAME_PROJECT_LIBRARY,
               joinColumns = {@JoinColumn(name = "project", referencedColumnName = "identifier")},
               inverseJoinColumns = {@JoinColumn(name = "proj_library", referencedColumnName = "identifier")})
    @Field(type = FieldType.Object)
    private final Set<Library> associatedLibraries = new HashSet<>();

    /**
     * Utilisateurs rattachés
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = TABLE_NAME_PROJECT_USER,
               joinColumns = {@JoinColumn(name = "project", referencedColumnName = "identifier")},
               inverseJoinColumns = {@JoinColumn(name = "proj_user", referencedColumnName = "identifier")})
    @Field(type = FieldType.Object)
    private final Set<User> associatedUsers = new HashSet<>();

    /**
     * Unité documentaire rattaché
     */
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private final Set<DocUnit> docUnits = new HashSet<>();

    /**
     * Modèle de workflow lié
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_model")
    private WorkflowModel workflowModel;

    /**
     * Commentaire d'annulation.
     */
    @Column(name = "canceling_comment")
    private String cancelingComment;

    /**
     * Flag indiquant si les fichiers ont ete sauvegardes suite à cloture.
     */
    @Column(name = "files_archived")
    private boolean filesArchived;


    public Library getLibrary() {
        return library;
    }

    public void setLibrary(final Library library) {
        this.library = library;
    }

    public Set<Library> getAssociatedLibraries() {
        return associatedLibraries;
    }

    public void setAssociatedLibraries(final Set<Library> associatedLibraries) {
        this.associatedLibraries.clear();
        if (associatedLibraries != null) {
            associatedLibraries.forEach(this::addLibrary);
        }
    }

    public void addLibrary(final Library library) {
        if (library != null) {
            this.associatedLibraries.add(library);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getForecastEndDate() {
        return forecastEndDate;
    }

    public void setForecastEndDate(final LocalDate forecastEndDate) {
        this.forecastEndDate = forecastEndDate;
    }

    public LocalDate getRealEndDate() {
        return realEndDate;
    }

    public void setRealEndDate(final LocalDate realEndDate) {
        this.realEndDate = realEndDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(final ProjectStatus status) {
        this.status = status;
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
            docUnit.setProject(this);
        }
    }

    public Set<User> getAssociatedUsers() {
        return associatedUsers;
    }

    public void setAssociatedUsers(final Set<User> associatedUsers) {
        this.associatedUsers.clear();
        if (associatedUsers != null) {
            associatedUsers.forEach(this::addAssociatedUser);
        }
    }

    public void addAssociatedUser(final User user) {
        if (user != null) {
            this.associatedUsers.add(user);
        }
    }

    public Set<Platform> getAssociatedPlatforms() {
        return associatedPlatforms;
    }

    public void setAssociatedPlatforms(final Set<Platform> associatedPlatforms) {
        this.associatedUsers.clear();
        if (associatedPlatforms != null) {
            associatedPlatforms.forEach(this::addAssociatedPlatform);
        }
    }

    public void addAssociatedPlatform(final Platform platform) {
        if (platform != null) {
            this.associatedPlatforms.add(platform);
        }
    }

    public Set<Train> getTrains() {
        return trains;
    }

    public void setTrains(final Set<Train> trains) {
        this.trains.clear();
        if (trains != null) {
            trains.forEach(this::addTrain);
        }
    }

    public void addTrain(final Train train) {
        if (train != null) {
            this.trains.add(train);
        }
    }

    public Set<Lot> getLots() {
        return lots;
    }

    public void setLots(final Set<Lot> lots) {
        this.lots.clear();
        if (lots != null) {
            lots.forEach(this::addLot);
        }
    }

    public void addLot(final Lot lot) {
        if (lot != null) {
            this.lots.add(lot);
        }
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

    public InternetArchiveCollection getCollectionIA() {
        return collectionIA;
    }

    public void setCollectionIA(final InternetArchiveCollection collectionIA) {
        this.collectionIA = collectionIA;
    }

    public CinesPAC getPlanClassementPAC() {
        return planClassementPAC;
    }

    public void setPlanClassementPAC(final CinesPAC planClassementPAC) {
        this.planClassementPAC = planClassementPAC;
    }

    public User getProvider() {
        return provider;
    }

    public void setProvider(final User provider) {
        this.provider = provider;
    }

    public WorkflowModel getWorkflowModel() {
        return workflowModel;
    }

    public void setWorkflowModel(final WorkflowModel workflowModel) {
        this.workflowModel = workflowModel;
    }

    public String getCancelingComment() {
        return cancelingComment;
    }

    public void setCancelingComment(final String cancelingComment) {
        this.cancelingComment = cancelingComment;
    }

    public boolean isFilesArchived() {
        return filesArchived;
    }

    public void setFilesArchived(final boolean filesArchived) {
        this.filesArchived = filesArchived;
    }

    public ViewsFormatConfiguration getActiveFormatConfiguration() {
        return activeFormatConfiguration;
    }

    public void setActiveFormatConfiguration(final ViewsFormatConfiguration activeFormatConfiguration) {
        this.activeFormatConfiguration = activeFormatConfiguration;
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



    public ExportFTPConfiguration getActiveExportFTPConfiguration() {
        return activeExportFTPConfiguration;
    }

    public void setActiveExportFTPConfiguration(final ExportFTPConfiguration activeExportFTPConfiguration) {
        this.activeExportFTPConfiguration = activeExportFTPConfiguration;
    }



    /**
     * type de statut
     */
    public enum ProjectStatus {
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .omitNullValues()
                          .add("docUnits", docUnits)
                          .add("name", name)
                          .add("description", description)
                          .add("startDate", startDate)
                          .add("forecastEndDate", forecastEndDate)
                          .add("realEndDate", realEndDate)
                          .add("active", active)
                          .add("status", status)
                          .toString();
    }
}
