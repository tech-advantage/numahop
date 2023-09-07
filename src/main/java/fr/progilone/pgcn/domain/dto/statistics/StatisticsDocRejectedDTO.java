package fr.progilone.pgcn.domain.dto.statistics;

import com.google.common.collect.Ordering;
import com.opencsv.bean.CsvBindByName;
import java.time.LocalDate;

public class StatisticsDocRejectedDTO implements Comparable<StatisticsDocRejectedDTO> {

    private static final Ordering<StatisticsDocRejectedDTO> ORDER_DTO;

    static {
        final Ordering<StatisticsDocRejectedDTO> orderLib = Ordering.natural().nullsFirst().onResultOf(StatisticsDocRejectedDTO::getLibraryName);
        final Ordering<StatisticsDocRejectedDTO> orderPj = Ordering.natural().nullsFirst().onResultOf(StatisticsDocRejectedDTO::getProjectName);
        final Ordering<StatisticsDocRejectedDTO> orderLot = Ordering.natural().nullsFirst().onResultOf(StatisticsDocRejectedDTO::getLotLabel);
        final Ordering<StatisticsDocRejectedDTO> orderDate = Ordering.natural().nullsFirst().onResultOf(StatisticsDocRejectedDTO::getImportDate);

        ORDER_DTO = orderLib.compound(orderPj).compound(orderLot).compound(orderDate);
    }

    private String libraryIdentifier;
    @CsvBindByName(column = "1. Bibliothèque")
    private String libraryName;

    private String projectIdentifier;
    @CsvBindByName(column = "2. Projet")
    private String projectName;

    private String lotIdentifier;
    @CsvBindByName(column = "3. Lot")
    private String lotLabel;

    @CsvBindByName(column = "4. Date d'import")
    private LocalDate importDate;

    private String providerIdentifier;
    @CsvBindByName(column = "5. Prestataire: login")
    private String providerLogin;
    @CsvBindByName(column = "6. Prestataire: nom")
    private String providerFullName;

    @CsvBindByName(column = "7. Nombre de documents rejetés")
    private long nbDocRejected;
    @CsvBindByName(column = "8. Nombre total de documents")
    private long nbDocTotal;
    @CsvBindByName(column = "9. % de documents rejetés")
    private double pctDocRejected;

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

    public String getProjectIdentifier() {
        return projectIdentifier;
    }

    public void setProjectIdentifier(final String projectIdentifier) {
        this.projectIdentifier = projectIdentifier;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(final String projectName) {
        this.projectName = projectName;
    }

    public String getLotIdentifier() {
        return lotIdentifier;
    }

    public void setLotIdentifier(final String lotIdentifier) {
        this.lotIdentifier = lotIdentifier;
    }

    public String getLotLabel() {
        return lotLabel;
    }

    public void setLotLabel(final String lotLabel) {
        this.lotLabel = lotLabel;
    }

    public LocalDate getImportDate() {
        return importDate;
    }

    public void setImportDate(final LocalDate importDate) {
        this.importDate = importDate;
    }

    public String getProviderIdentifier() {
        return providerIdentifier;
    }

    public void setProviderIdentifier(final String providerIdentifier) {
        this.providerIdentifier = providerIdentifier;
    }

    public String getProviderLogin() {
        return providerLogin;
    }

    public void setProviderLogin(final String providerLogin) {
        this.providerLogin = providerLogin;
    }

    public String getProviderFullName() {
        return providerFullName;
    }

    public void setProviderFullName(final String providerFullName) {
        this.providerFullName = providerFullName;
    }

    public long getNbDocRejected() {
        return nbDocRejected;
    }

    public void setNbDocRejected(final long nbDocRejected) {
        this.nbDocRejected = nbDocRejected;
    }

    public long getNbDocTotal() {
        return nbDocTotal;
    }

    public void setNbDocTotal(final long nbDocTotal) {
        this.nbDocTotal = nbDocTotal;
    }

    public double getPctDocRejected() {
        return pctDocRejected;
    }

    public void setPctDocRejected(final double pctDocRejected) {
        this.pctDocRejected = pctDocRejected;
    }

    @Override
    public int compareTo(final StatisticsDocRejectedDTO o) {
        return ORDER_DTO.compare(this, o);
    }
}
