package fr.progilone.pgcn.domain.dto.statistics;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import fr.progilone.pgcn.domain.dto.AbstractDTO;
import fr.progilone.pgcn.domain.dto.document.SimpleDocUnitDTO;
import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;
import fr.progilone.pgcn.domain.dto.user.SimpleUserDTO;

/**
 * DTO de statistiques : projet
 * @author jbrunet
 * Créé le 9 mai 2017
 */
public class StatisticsProjectDTO extends AbstractDTO {

    private String identifier;
    private SimpleLibraryDTO library;
    private String name;
    private String description;
    private Boolean active;
    private LocalDate startDate;
    private LocalDate forecastEndDate;
    private LocalDate realEndDate;
    private String status;
    private Set<SimpleDocUnitDTO> docUnits;
    private SimpleUserDTO provider;
    private List<SimpleUserDTO> otherProviders;

    public StatisticsProjectDTO() {}

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public final String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public SimpleLibraryDTO getLibrary() {
        return library;
    }

    public void setLibrary(SimpleLibraryDTO library) {
        this.library = library;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getForecastEndDate() {
        return forecastEndDate;
    }

    public void setForecastEndDate(LocalDate forecastEndDate) {
        this.forecastEndDate = forecastEndDate;
    }

    public LocalDate getRealEndDate() {
        return realEndDate;
    }

    public void setRealEndDate(LocalDate realEndDate) {
        this.realEndDate = realEndDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<SimpleDocUnitDTO> getDocUnits() {
        return docUnits;
    }

    public void setDocUnits(Set<SimpleDocUnitDTO> docUnits) {
        this.docUnits = docUnits;
    }

    public SimpleUserDTO getProvider() {
        return provider;
    }

    public void setProvider(SimpleUserDTO provider) {
        this.provider = provider;
    }

    public List<SimpleUserDTO> getOtherProviders() {
        return otherProviders;
    }

    public void setOtherProviders(List<SimpleUserDTO> otherProviders) {
        this.otherProviders = otherProviders;
    }

    public void addOtherProvider(SimpleUserDTO provider) {
        this.otherProviders.add(provider);
    }
}
