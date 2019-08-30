package fr.progilone.pgcn.domain.dto.statistics;

import javax.annotation.Nullable;

import com.google.common.collect.Ordering;
import com.opencsv.bean.CsvBindByName;

public class StatisticsDocUnitAverageDTO implements Comparable<StatisticsDocUnitAverageDTO> {

    private static final Ordering<StatisticsDocUnitAverageDTO> orderDto;

    static {
        final Ordering<StatisticsDocUnitAverageDTO> orderPj = Ordering.natural().nullsFirst().onResultOf(StatisticsDocUnitAverageDTO::getProjectName);
        final Ordering<StatisticsDocUnitAverageDTO> orderLot = Ordering.natural().nullsFirst().onResultOf(StatisticsDocUnitAverageDTO::getLotLabel);
        final Ordering<StatisticsDocUnitAverageDTO> orderDlv = Ordering.natural().nullsFirst().onResultOf(StatisticsDocUnitAverageDTO::getDeliveryLabel);
        orderDto = orderPj.compound(orderLot).compound(orderDlv);
    }

    private String projectIdentifier;

    @CsvBindByName(column = "1. Projet")
    private String projectName;

    private String lotIdentifier;

    @CsvBindByName(column = "2. Lot")
    private String lotLabel;

    private String deliveryIdentifier;

    @CsvBindByName(column = "3. Livraison")
    private String deliveryLabel;
    
    /**
     * Nbre total de documents
     */
    @CsvBindByName(column = "4. Nombre total de documents")
    private int nbDocs;

    /**
     * Nombre total de pages
     */
    @CsvBindByName(column = "5. Nombre total de pages")
    private int avgTotalPages;
    
    /**
     * Poids total des documents livrés
     */
    @CsvBindByName(column = "6. Poids total des documents")
    private long lengthDocs;
    
    /**
     * Taux de rejet (% de docs qui ont été rejetés au moins une fois)
     */
    @CsvBindByName(column = "7. Taux de rejet")
    private double rejectRatio;

    /**
     * Temps moyen de contrôle, en secondes
     */
    @CsvBindByName(column = "8. Temps moyen de contrôle")
    private long avgDurControl;

    /**
     * Temps moyen de livraison et de relivraison, en secondes
     */
    @CsvBindByName(column = "9. Temps moyen de (re)livraison")
    private long avgDurDelivery;

    /**
     * Durée moyenne d'un workflow, en secondes
     */
    @CsvBindByName(column = "10. Durée moyenne d'un workflow")
    private long avgDurWorkflow;

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

    public String getDeliveryIdentifier() {
        return deliveryIdentifier;
    }

    public void setDeliveryIdentifier(final String deliveryIdentifier) {
        this.deliveryIdentifier = deliveryIdentifier;
    }

    public String getDeliveryLabel() {
        return deliveryLabel;
    }

    public void setDeliveryLabel(final String deliveryLabel) {
        this.deliveryLabel = deliveryLabel;
    }

    public int getNbDocs() {
        return nbDocs;
    }

    public void setNbDocs(final int nbDocs) {
        this.nbDocs = nbDocs;
    }

    public int getAvgTotalPages() {
        return avgTotalPages;
    }

    public void setAvgTotalPages(final int avgTotalPages) {
        this.avgTotalPages = avgTotalPages;
    }

    public long getLengthDocs() {
        return lengthDocs;
    }

    public void setLengthDocs(final long lengthDocs) {
        this.lengthDocs = lengthDocs;
    }

    public double getRejectRatio() {
        return rejectRatio;
    }

    public void setRejectRatio(final double rejectRatio) {
        this.rejectRatio = rejectRatio;
    }

    public long getAvgDurControl() {
        return avgDurControl;
    }

    public void setAvgDurControl(final long avgDurControl) {
        this.avgDurControl = avgDurControl;
    }

    public long getAvgDurDelivery() {
        return avgDurDelivery;
    }

    public void setAvgDurDelivery(final long avgDurDelivery) {
        this.avgDurDelivery = avgDurDelivery;
    }

    public long getAvgDurWorkflow() {
        return avgDurWorkflow;
    }

    public void setAvgDurWorkflow(final long avgDurWorkflow) {
        this.avgDurWorkflow = avgDurWorkflow;
    }

    @Override
    public int compareTo(@Nullable final StatisticsDocUnitAverageDTO o) {
        return orderDto.compare(this, o);
    }
}
