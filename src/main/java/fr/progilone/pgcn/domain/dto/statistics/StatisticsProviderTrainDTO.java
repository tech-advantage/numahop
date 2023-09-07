package fr.progilone.pgcn.domain.dto.statistics;

import com.google.common.collect.Ordering;
import com.opencsv.bean.CsvBindByName;
import fr.progilone.pgcn.domain.train.Train;
import jakarta.annotation.Nullable;
import java.time.LocalDate;

public class StatisticsProviderTrainDTO implements Comparable<StatisticsProviderTrainDTO> {

    private static final Ordering<StatisticsProviderTrainDTO> orderDto;

    static {
        Ordering<StatisticsProviderTrainDTO> orderLib = Ordering.natural().nullsFirst().onResultOf(StatisticsProviderTrainDTO::getLibraryName);
        Ordering<StatisticsProviderTrainDTO> orderProject = Ordering.natural().nullsFirst().onResultOf(StatisticsProviderTrainDTO::getProjectName);
        Ordering<StatisticsProviderTrainDTO> orderTrain = Ordering.natural().nullsFirst().onResultOf(StatisticsProviderTrainDTO::getTrainLabel);
        orderDto = orderLib.compound(orderProject).compound(orderTrain);
    }

    private String libraryIdentifier;

    @CsvBindByName(column = "1. Bibliothèque")
    private String libraryName;

    private String projectIdentifier;

    @CsvBindByName(column = "2. Projet")
    private String projectName;

    private String trainIdentifier;

    @CsvBindByName(column = "3. Train")
    private String trainLabel;

    @CsvBindByName(column = "4. Statut")
    private Train.TrainStatus status;

    @CsvBindByName(column = "5. Date d'envoi")
    private LocalDate sendingDate;

    @CsvBindByName(column = "6. Date de retour")
    private LocalDate returnDate;

    @CsvBindByName(column = "7. Durée(j)")
    private Long duration;

    @CsvBindByName(column = " 8. Nb documents physiques")
    private long nbDoc;

    @CsvBindByName(column = "9. Valeur d'assurance")
    private Double insurance;

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

    public String getTrainIdentifier() {
        return trainIdentifier;
    }

    public void setTrainIdentifier(final String trainIdentifier) {
        this.trainIdentifier = trainIdentifier;
    }

    public String getTrainLabel() {
        return trainLabel;
    }

    public void setTrainLabel(final String trainLabel) {
        this.trainLabel = trainLabel;
    }

    public Train.TrainStatus getStatus() {
        return status;
    }

    public void setStatus(final Train.TrainStatus status) {
        this.status = status;
    }

    public LocalDate getSendingDate() {
        return sendingDate;
    }

    public void setSendingDate(final LocalDate sendingDate) {
        this.sendingDate = sendingDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(final LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(final Long duration) {
        this.duration = duration;
    }

    public long getNbDoc() {
        return nbDoc;
    }

    public void setNbDoc(final long nbDoc) {
        this.nbDoc = nbDoc;
    }

    public Double getInsurance() {
        return insurance;
    }

    public void setInsurance(final Double insurance) {
        this.insurance = insurance;
    }

    @Override
    public int compareTo(@Nullable final StatisticsProviderTrainDTO o) {
        return orderDto.compare(this, o);
    }
}
