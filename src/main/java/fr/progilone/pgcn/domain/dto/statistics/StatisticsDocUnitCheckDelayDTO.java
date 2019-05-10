package fr.progilone.pgcn.domain.dto.statistics;

public class StatisticsDocUnitCheckDelayDTO {

    private String projectIdentifier;
    private String projectName;

    private String lotIdentifier;
    private String lotLabel;

    private String deliveryIdentifier;
    private String deliveryLabel;

    private long minRemainingCheckDelay;    // j
    private long maxCheckDelay; // j

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

    public long getMinRemainingCheckDelay() {
        return minRemainingCheckDelay;
    }

    public void setMinRemainingCheckDelay(final long minRemainingCheckDelay) {
        this.minRemainingCheckDelay = minRemainingCheckDelay;
    }

    public long getMaxCheckDelay() {
        return maxCheckDelay;
    }

    public void setMaxCheckDelay(final long maxCheckDelay) {
        this.maxCheckDelay = maxCheckDelay;
    }
}
