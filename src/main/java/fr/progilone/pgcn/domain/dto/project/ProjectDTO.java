package fr.progilone.pgcn.domain.dto.project;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.progilone.pgcn.domain.dto.AbstractVersionedDTO;
import fr.progilone.pgcn.domain.dto.administration.CinesPACDTO;
import fr.progilone.pgcn.domain.dto.administration.InternetArchiveCollectionDTO;
import fr.progilone.pgcn.domain.dto.administration.omeka.OmekaListDTO;
import fr.progilone.pgcn.domain.dto.administration.viewsFormat.SimpleViewsFormatConfigurationDTO;
import fr.progilone.pgcn.domain.dto.checkconfiguration.SimpleCheckConfigurationDTO;
import fr.progilone.pgcn.domain.dto.ftpconfiguration.SimpleFTPConfigurationDTO;
import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;
import fr.progilone.pgcn.domain.dto.user.SimpleUserDTO;
import fr.progilone.pgcn.domain.dto.workflow.SimpleWorkflowModelDTO;

/**
 * DTO représentant un projet
 */
public class ProjectDTO extends AbstractVersionedDTO {

    private String identifier;
    private SimpleLibraryDTO library;
    private String name;
    private String description;
    private boolean active;
    private LocalDate startDate;
    private LocalDate forecastEndDate;
    private LocalDate realEndDate;
    private String status;
    private SimpleFTPConfigurationDTO activeFTPConfiguration;
    private SimpleCheckConfigurationDTO activeCheckConfiguration;
    private SimpleViewsFormatConfigurationDTO activeFormatConfiguration;
    private InternetArchiveCollectionDTO collectionIA;
    private CinesPACDTO planClassementPAC;
    private SimpleUserDTO provider;
    private SimpleWorkflowModelDTO workflowModel;
    private String cancelingComment;
    private OmekaListDTO omekaCollection;
    private OmekaListDTO omekaItem;

    private final Set<SimpleLibraryDTO> associatedLibraries = new HashSet<>();
    private final Set<SimpleUserDTO> associatedUsers = new HashSet<>();
    private final List<SimpleUserDTO> otherProviders = new ArrayList<>();
    /**
     * Ajout des infos de création
     */
    private String createdBy;
    private LocalDateTime createdDate;
    /**
     * Ajout des infos de modifications
     */
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate;

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public final String getDescription() {
        return description;
    }

    public final void setDescription(final String description) {
        this.description = description;
    }

    public final void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public final void setName(final String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public SimpleLibraryDTO getLibrary() {
        return library;
    }

    public void setLibrary(final SimpleLibraryDTO library) {
        this.library = library;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public Set<SimpleLibraryDTO> getAssociatedLibraries() {
        return associatedLibraries;
    }

    public void setAssociatedLibraries(final Set<SimpleLibraryDTO> associatedLibraries) {
        this.associatedLibraries.clear();
        this.associatedLibraries.addAll(associatedLibraries);
    }

    public Set<SimpleUserDTO> getAssociatedUsers() {
        return associatedUsers;
    }

    public void setAssociatedUsers(final Set<SimpleUserDTO> associatedUsers) {
        this.associatedUsers.clear();
        this.associatedUsers.addAll(associatedUsers);
    }

    public SimpleFTPConfigurationDTO getActiveFTPConfiguration() {
        return activeFTPConfiguration;
    }

    public void setActiveFTPConfiguration(final SimpleFTPConfigurationDTO activeFTPConfiguration) {
        this.activeFTPConfiguration = activeFTPConfiguration;
    }

    public SimpleCheckConfigurationDTO getActiveCheckConfiguration() {
        return activeCheckConfiguration;
    }

    public void setActiveCheckConfiguration(final SimpleCheckConfigurationDTO activeCheckConfiguration) {
        this.activeCheckConfiguration = activeCheckConfiguration;
    }

    public SimpleViewsFormatConfigurationDTO getActiveFormatConfiguration() {
        return activeFormatConfiguration;
    }

    public void setActiveFormatConfiguration(final SimpleViewsFormatConfigurationDTO activeFormatConfiguration) {
        this.activeFormatConfiguration = activeFormatConfiguration;
    }

    public InternetArchiveCollectionDTO getCollectionIA() {
        return collectionIA;
    }

    public void setCollectionIA(final InternetArchiveCollectionDTO collectionIA) {
        this.collectionIA = collectionIA;
    }

    public CinesPACDTO getPlanClassementPAC() {
        return planClassementPAC;
    }

    public void setPlanClassementPAC(final CinesPACDTO planClassementPAC) {
        this.planClassementPAC = planClassementPAC;
    }

    public SimpleUserDTO getProvider() {
        return provider;
    }

    public void setProvider(final SimpleUserDTO provider) {
        this.provider = provider;
    }

    public List<SimpleUserDTO> getOtherProviders() {
        return otherProviders;
    }

    public void setOtherProviders(final List<SimpleUserDTO> otherProviders) {
        this.otherProviders.clear();
        this.otherProviders.addAll(otherProviders);
    }

    public void addOtherProvider(final SimpleUserDTO provider) {
        this.otherProviders.add(provider);
    }

    public SimpleWorkflowModelDTO getWorkflowModel() {
        return workflowModel;
    }

    public void setWorkflowModel(final SimpleWorkflowModelDTO workflowModel) {
        this.workflowModel = workflowModel;
    }

    public String getCancelingComment() {
        return cancelingComment;
    }

    public void setCancelingComment(final String cancelingComment) {
        this.cancelingComment = cancelingComment;
    }

    public OmekaListDTO getOmekaCollection() {
        return omekaCollection;
    }

    public void setOmekaCollection(final OmekaListDTO omekaCollection) {
        this.omekaCollection = omekaCollection;
    }

    public OmekaListDTO getOmekaItem() {
        return omekaItem;
    }

    public void setOmekaItem(final OmekaListDTO omekaItem) {
        this.omekaItem = omekaItem;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(final LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(final String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(final LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
