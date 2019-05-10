package fr.progilone.pgcn.repository.delivery.helper;

import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class DeliverySearchBuilder {

    private String search;
    private List<String> libraries;
    private List<String> projects;
    private List<String> lots;
    private List<String> deliveries;
    private List<String> providers;
    private List<Delivery.DeliveryStatus> status;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String docUnitPgcnId;
    private List<WorkflowStateKey> docUnitStates;

    public Optional<String> getSearch() {
        return Optional.ofNullable(search);
    }

    public Optional<List<String>> getLibraries() {
        return ofEmptiable(libraries);
    }

    public Optional<List<String>> getProjects() {
        return ofEmptiable(projects);
    }

    public Optional<List<String>> getLots() {
        return ofEmptiable(lots);
    }

    public Optional<List<String>> getDeliveries() {
        return ofEmptiable(deliveries);
    }

    public Optional<List<String>> getProviders() {
        return ofEmptiable(providers);
    }

    public Optional<List<Delivery.DeliveryStatus>> getStatus() {
        return ofEmptiable(status);
    }

    public Optional<LocalDate> getDateFrom() {
        return Optional.ofNullable(dateFrom);
    }

    public Optional<LocalDate> getDateTo() {
        return Optional.ofNullable(dateTo);
    }

    public Optional<String> getDocUnitPgcnId() {
        return Optional.ofNullable(docUnitPgcnId);
    }

    public Optional<List<WorkflowStateKey>> getDocUnitStates() {
        return ofEmptiable(docUnitStates);
    }

    public DeliverySearchBuilder setSearch(final String search) {
        this.search = search;
        return this;
    }

    public DeliverySearchBuilder setLibraries(final List<String> libraries) {
        this.libraries = libraries;
        return this;
    }

    public DeliverySearchBuilder setProjects(final List<String> projects) {
        this.projects = projects;
        return this;
    }

    public DeliverySearchBuilder setLots(final List<String> lots) {
        this.lots = lots;
        return this;
    }

    public DeliverySearchBuilder setDeliveries(final List<String> deliveries) {
        this.deliveries = deliveries;
        return this;
    }

    public DeliverySearchBuilder setProviders(final List<String> providers) {
        this.providers = providers;
        return this;
    }

    public DeliverySearchBuilder setStatus(final List<Delivery.DeliveryStatus> status) {
        this.status = status;
        return this;
    }

    public DeliverySearchBuilder setDateFrom(final LocalDate dateFrom) {
        this.dateFrom = dateFrom;
        return this;
    }

    public DeliverySearchBuilder setDateTo(final LocalDate dateTo) {
        this.dateTo = dateTo;
        return this;
    }

    public DeliverySearchBuilder setDocUnitPgcnId(final String docUnitPgcnId) {
        this.docUnitPgcnId = docUnitPgcnId;
        return this;
    }

    public DeliverySearchBuilder setDocUnitStates(final List<WorkflowStateKey> docUnitStates) {
        this.docUnitStates = docUnitStates;
        return this;
    }

    private static <T> Optional<List<T>> ofEmptiable(final List<T> list) {
        return CollectionUtils.isEmpty(list) ? Optional.empty() : Optional.of(list);
    }
}
