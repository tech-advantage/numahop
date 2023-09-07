package fr.progilone.pgcn.repository.lot.helper;

import fr.progilone.pgcn.domain.lot.Lot;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;

public class LotSearchBuilder {

    private String search;
    private List<String> libraries;
    private List<String> projects;
    private boolean active;
    private List<Lot.LotStatus> lotStatuses;
    private List<String> providers;
    private Integer docNumber;
    private List<String> fileFormats;
    private LocalDate lastDlvFrom;
    private LocalDate lastDlvTo;
    private List<String> identifiers;

    public Optional<String> getSearch() {
        return Optional.ofNullable(search);
    }

    public Optional<List<String>> getLibraries() {
        return ofEmptiable(libraries);
    }

    public Optional<List<String>> getProjects() {
        return ofEmptiable(projects);
    }

    public boolean isActive() {
        return active;
    }

    public Optional<List<Lot.LotStatus>> getLotStatuses() {
        return ofEmptiable(lotStatuses);
    }

    public Optional<List<String>> getProviders() {
        return ofEmptiable(providers);
    }

    public Optional<Integer> getDocNumber() {
        return Optional.ofNullable(docNumber);
    }

    public Optional<List<String>> getFileFormats() {
        return ofEmptiable(fileFormats);
    }

    public Optional<LocalDate> getLastDlvFrom() {
        return Optional.ofNullable(lastDlvFrom);
    }

    public Optional<LocalDate> getLastDlvTo() {
        return Optional.ofNullable(lastDlvTo);
    }

    public Optional<List<String>> getIdentifiers() {
        return ofEmptiable(identifiers);
    }

    public LotSearchBuilder setSearch(final String search) {
        this.search = search;
        return this;
    }

    public LotSearchBuilder setLibraries(final List<String> libraries) {
        this.libraries = libraries;
        return this;
    }

    public LotSearchBuilder setProjects(final List<String> projects) {
        this.projects = projects;
        return this;
    }

    public LotSearchBuilder setActive(final boolean active) {
        this.active = active;
        return this;
    }

    public LotSearchBuilder setLotStatuses(final List<Lot.LotStatus> lotStatuses) {
        this.lotStatuses = lotStatuses;
        return this;
    }

    public LotSearchBuilder setProviders(final List<String> providers) {
        this.providers = providers;
        return this;
    }

    public LotSearchBuilder setDocNumber(final Integer docNumber) {
        this.docNumber = docNumber;
        return this;
    }

    public LotSearchBuilder setFileFormats(final List<String> fileFormats) {
        this.fileFormats = fileFormats;
        return this;
    }

    public LotSearchBuilder setLastDlvFrom(final LocalDate lastDlvFrom) {
        this.lastDlvFrom = lastDlvFrom;
        return this;
    }

    public LotSearchBuilder setLastDlvTo(final LocalDate lastDlvTo) {
        this.lastDlvTo = lastDlvTo;
        return this;
    }

    public LotSearchBuilder setIdentifiers(final List<String> identifiers) {
        this.identifiers = identifiers;
        return this;
    }

    private static <T> Optional<List<T>> ofEmptiable(final List<T> list) {
        return CollectionUtils.isEmpty(list) ? Optional.empty()
                                             : Optional.of(list);
    }
}
