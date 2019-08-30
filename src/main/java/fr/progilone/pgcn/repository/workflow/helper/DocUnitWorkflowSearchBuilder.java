package fr.progilone.pgcn.repository.workflow.helper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;

import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;

/**
 * Aide pour la construction d'une recherche
 */
public final class DocUnitWorkflowSearchBuilder {

    private List<String> libraries;
    private List<String> projects;
    private List<String> lots;
    private List<String> deliveries;
    private List<String> roles;
    private List<String> workflows;
    private List<WorkflowStateKey> states;
    private LocalDate fromDate;
    private LocalDate toDate;
    private List<String> types;
    private List<String> collections;
    
    private boolean withFailedStatuses = false;
    
    

    public Optional<List<String>> getLibraries() {
        return ofEmptiable(libraries);
    }

    public DocUnitWorkflowSearchBuilder setLibraries(final List<String> libraries) {
        this.libraries = libraries;
        return this;
    }

    public Optional<List<String>> getProjects() {
        return ofEmptiable(projects);
    }

    public DocUnitWorkflowSearchBuilder setProjects(final List<String> projects) {
        this.projects = projects;
        return this;
    }

    public Optional<List<String>> getLots() {
        return ofEmptiable(lots);
    }

    public DocUnitWorkflowSearchBuilder setLots(final List<String> lots) {
        this.lots = lots;
        return this;
    }

    public Optional<List<String>> getDeliveries() {
        return ofEmptiable(deliveries);
    }

    public DocUnitWorkflowSearchBuilder setDeliveries(final List<String> deliveries) {
        this.deliveries = deliveries;
        return this;
    }

    public Optional<List<String>> getRoles() {
        return ofEmptiable(roles);
    }

    public DocUnitWorkflowSearchBuilder setRoles(final List<String> roles) {
        this.roles = roles;
        return this;
    }

    public Optional<List<String>> getWorkflows() {
        return ofEmptiable(workflows);
    }

    public DocUnitWorkflowSearchBuilder setWorkflows(final List<String> workflows) {
        this.workflows = workflows;
        return this;
    }

    public Optional<List<WorkflowStateKey>> getStates() {
        return ofEmptiable(states);
    }

    public DocUnitWorkflowSearchBuilder setStates(final List<WorkflowStateKey> states) {
        this.states = states;
        return this;
    }

    public DocUnitWorkflowSearchBuilder addState(final WorkflowStateKey state) {
        if (this.states == null) {
            this.states = new ArrayList<>();
        }
        this.states.add(state);
        return this;
    }

    public Optional<LocalDate> getFromDate() {
        return Optional.ofNullable(fromDate);
    }

    public DocUnitWorkflowSearchBuilder setFromDate(final LocalDate fromDate) {
        this.fromDate = fromDate;
        return this;
    }

    public Optional<LocalDate> getToDate() {
        return Optional.ofNullable(toDate);
    }

    public DocUnitWorkflowSearchBuilder setToDate(final LocalDate toDate) {
        this.toDate = toDate;
        return this;
    }

    public Optional<List<String>> getTypes() {
        return ofEmptiable(types);
    }

    public DocUnitWorkflowSearchBuilder setTypes(final List<String> types) {
        this.types = types;
        return this;
    }

    public Optional<List<String>> getCollections() {
        return ofEmptiable(collections);
    }

    public DocUnitWorkflowSearchBuilder setCollections(final List<String> collections) {
        this.collections = collections;
        return this;
    }

    public boolean isWithFailedStatuses() {
        return this.withFailedStatuses;
    }

    public DocUnitWorkflowSearchBuilder setWithFailedStatuses(final boolean withFailedStatuses) {
        this.withFailedStatuses = withFailedStatuses;
        return this;
    }

    private static <T> Optional<List<T>> ofEmptiable(final List<T> list) {
        return CollectionUtils.isEmpty(list) ? Optional.empty() : Optional.of(list);
    }
}
