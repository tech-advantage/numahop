package fr.progilone.pgcn.repository.project.helper;

import fr.progilone.pgcn.domain.project.Project;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;

public class ProjectSearchBuilder {

    private String search;
    private String initiale;
    private List<Project.ProjectStatus> statuses;
    private List<String> libraries;
    private List<String> providers;
    private List<String> projects;
    private boolean active;
    private LocalDate from;
    private LocalDate to;

    public Optional<String> getSearch() {
        return Optional.ofNullable(search);
    }

    public Optional<String> getInitiale() {
        return Optional.ofNullable(initiale);
    }

    public Optional<List<Project.ProjectStatus>> getStatuses() {
        return ofEmptiable(statuses);
    }

    public Optional<List<String>> getLibraries() {
        return ofEmptiable(libraries);
    }

    public Optional<List<String>> getProviders() {
        return ofEmptiable(providers);
    }

    public Optional<List<String>> getProjects() {
        return ofEmptiable(projects);
    }

    public boolean isActive() {
        return active;
    }

    public Optional<LocalDate> getFrom() {
        return Optional.ofNullable(from);
    }

    public Optional<LocalDate> getTo() {
        return Optional.ofNullable(to);
    }

    public ProjectSearchBuilder setSearch(final String search) {
        this.search = search;
        return this;
    }

    public ProjectSearchBuilder setInitiale(final String initiale) {
        this.initiale = initiale;
        return this;
    }

    public ProjectSearchBuilder setStatuses(final List<Project.ProjectStatus> statuses) {
        this.statuses = statuses;
        return this;
    }

    public ProjectSearchBuilder setLibraries(final List<String> libraries) {
        this.libraries = libraries;
        return this;
    }

    public ProjectSearchBuilder setProviders(final List<String> providers) {
        this.providers = providers;
        return this;
    }

    public ProjectSearchBuilder setProjects(final List<String> projects) {
        this.projects = projects;
        return this;
    }

    public ProjectSearchBuilder setActive(final boolean active) {
        this.active = active;
        return this;
    }

    public ProjectSearchBuilder setFrom(final LocalDate from) {
        this.from = from;
        return this;
    }

    public ProjectSearchBuilder setTo(final LocalDate to) {
        this.to = to;
        return this;
    }

    private static <T> Optional<List<T>> ofEmptiable(final List<T> list) {
        return CollectionUtils.isEmpty(list) ? Optional.empty()
                                             : Optional.ofNullable(list);
    }
}
