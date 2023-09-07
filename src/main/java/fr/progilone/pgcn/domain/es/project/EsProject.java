package fr.progilone.pgcn.domain.es.project;

import static fr.progilone.pgcn.service.es.EsConstant.*;

import fr.progilone.pgcn.domain.es.library.EsLibrary;
import fr.progilone.pgcn.domain.es.user.EsUser;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.project.Project.ProjectStatus;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Setting;

/**
 * Classe métier permettant de gérer les projets.
 */
@Document(indexName = "#{@environment.getProperty('elasticsearch.index.name')}-project")
@Setting(settingPath = "config/elasticsearch/settings_pgcn.json", shards = 1, replicas = 0)
public class EsProject {

    @Id
    @Field(type = FieldType.Keyword)
    private String identifier;

    /**
     * Bibliothèque principale
     */
    @Field(type = FieldType.Object)
    private EsLibrary library;

    @Field(type = FieldType.Object)
    private EsUser provider;

    /**
     * Nom
     */
    @MultiField(mainField = @Field(type = FieldType.Text),
                otherFields = {@InnerField(type = FieldType.Keyword, suffix = SUBFIELD_RAW),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_CI_AI, analyzer = ANALYZER_CI_AI, searchAnalyzer = ANALYZER_CI_AI),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_CI_AS, analyzer = ANALYZER_CI_AS, searchAnalyzer = ANALYZER_CI_AS),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_PHRASE, analyzer = ANALYZER_PHRASE, searchAnalyzer = ANALYZER_PHRASE)})
    private String name;

    /**
     * Date de début
     */
    @Field(type = FieldType.Date)
    private LocalDate startDate;

    /**
     * Date de fin prévisionnelle
     */
    @Field(type = FieldType.Date)
    private LocalDate forecastEndDate;

    /**
     * Date de fin réelle
     */
    @Field(type = FieldType.Date)
    private LocalDate realEndDate;

    /**
     * Actif
     */
    @Field(type = FieldType.Boolean)
    private boolean active;

    /**
     * statut
     */
    @Field(type = FieldType.Keyword)
    private ProjectStatus status;

    /**
     * Bibliothèques associées
     */
    @Field(type = FieldType.Object)
    private Set<EsLibrary> associatedLibraries;

    /**
     * Utilisateurs rattachés
     */
    @Field(type = FieldType.Object)
    private Set<EsUser> associatedUsers;

    public static EsProject from(final Project project) {
        final EsProject esProject = new EsProject();
        esProject.setIdentifier(project.getIdentifier());
        esProject.setName(project.getName());
        if (project.getLibrary() != null) {
            esProject.setLibrary(EsLibrary.from(project.getLibrary()));
        }
        if (project.getProvider() != null) {
            esProject.setProvider(EsUser.from(project.getProvider()));
        }
        esProject.setStartDate(project.getStartDate());
        esProject.setForecastEndDate(project.getForecastEndDate());
        esProject.setRealEndDate(project.getRealEndDate());
        esProject.setActive(project.isActive());
        esProject.setStatus(project.getStatus());
        esProject.setAssociatedLibraries(project.getAssociatedLibraries().stream().map(EsLibrary::from).collect(Collectors.toSet()));
        esProject.setAssociatedUsers(project.getAssociatedUsers().stream().map(EsUser::from).collect(Collectors.toSet()));
        return esProject;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public EsLibrary getLibrary() {
        return library;
    }

    public void setLibrary(final EsLibrary library) {
        this.library = library;
    }

    public EsUser getProvider() {
        return provider;
    }

    public void setProvider(final EsUser provider) {
        this.provider = provider;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
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

    public Set<EsLibrary> getAssociatedLibraries() {
        return associatedLibraries;
    }

    public void setAssociatedLibraries(final Set<EsLibrary> associatedLibraries) {
        this.associatedLibraries = associatedLibraries;
    }

    public Set<EsUser> getAssociatedUsers() {
        return associatedUsers;
    }

    public void setAssociatedUsers(final Set<EsUser> associatedUsers) {
        this.associatedUsers = associatedUsers;
    }

}
