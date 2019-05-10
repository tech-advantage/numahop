package fr.progilone.pgcn.domain.dto.statistics;

import com.google.common.collect.Ordering;
import com.opencsv.bean.CsvBindByName;

import javax.annotation.Nullable;

public class WorkflowUserProgressDTO implements Comparable<WorkflowUserProgressDTO> {

    private static final Ordering<WorkflowUserProgressDTO> orderDto;

    static {
        Ordering<WorkflowUserProgressDTO> orderLib = Ordering.natural().nullsFirst().onResultOf(WorkflowUserProgressDTO::getLibraryName);
        Ordering<WorkflowUserProgressDTO> orderUser = Ordering.natural().nullsFirst().onResultOf(WorkflowUserProgressDTO::getUserFullName);
        orderDto = orderLib.compound(orderUser);
    }

    /* Bibliothèque */
    private String libraryIdentifier;

    @CsvBindByName(column = "1. Bibliothèque")
    private String libraryName;

    /* Utilisatur */
    private String userIdentifier;

    @CsvBindByName(column = "2. Login")
    private String userLogin;

    @CsvBindByName(column = "3. Nom")
    private String userFullName;

    /**
     * Nombre d'UD contrôlées
     */
    @CsvBindByName(column = "4. Nombre d'UD")
    private long nbDocUnit;

    /**
     * Nombre moyen de pages
     */
    @CsvBindByName(column = "5. Nombre moyen de pages")
    private int avgTotalPages;

    /**
     * Délai moyen de contrôle, secondes
     */
    @CsvBindByName(column = "6. Délai moyen de contrôle")
    private long avgDuration;

    public String getLibraryIdentifier() {
        return libraryIdentifier;
    }

    public void setLibraryIdentifier(final String libraryIdentifier) {
        this.libraryIdentifier = libraryIdentifier;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(final String libraryName) {
        this.libraryName = libraryName;
    }

    public String getUserIdentifier() {
        return userIdentifier;
    }

    public void setUserIdentifier(final String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(final String userLogin) {
        this.userLogin = userLogin;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(final String userFullName) {
        this.userFullName = userFullName;
    }

    public long getNbDocUnit() {
        return nbDocUnit;
    }

    public void setNbDocUnit(final long nbDocUnit) {
        this.nbDocUnit = nbDocUnit;
    }

    public int getAvgTotalPages() {
        return avgTotalPages;
    }

    public void setAvgTotalPages(final int avgTotalPages) {
        this.avgTotalPages = avgTotalPages;
    }

    public long getAvgDuration() {
        return avgDuration;
    }

    public void setAvgDuration(final long avgDuration) {
        this.avgDuration = avgDuration;
    }

    @Override
    public int compareTo(@Nullable final WorkflowUserProgressDTO o) {
        return orderDto.compare(this, o);
    }
}
