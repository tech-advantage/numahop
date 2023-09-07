package fr.progilone.pgcn.domain.dto.statistics.csv;

import com.google.common.collect.Ordering;
import com.opencsv.bean.CsvBindByName;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import jakarta.annotation.Nullable;

public class WorkflowDeliveryProgressCsvDTO implements Comparable<WorkflowDeliveryProgressCsvDTO> {

    private static final Ordering<WorkflowDeliveryProgressCsvDTO> ORDER_DTO;

    static {
        final Ordering<WorkflowDeliveryProgressCsvDTO> orderLib = Ordering.natural().nullsFirst().onResultOf(WorkflowDeliveryProgressCsvDTO::getLibraryName);
        final Ordering<WorkflowDeliveryProgressCsvDTO> orderPj = Ordering.natural().nullsFirst().onResultOf(WorkflowDeliveryProgressCsvDTO::getProjectName);
        final Ordering<WorkflowDeliveryProgressCsvDTO> orderLot = Ordering.natural().nullsFirst().onResultOf(WorkflowDeliveryProgressCsvDTO::getLotLabel);
        final Ordering<WorkflowDeliveryProgressCsvDTO> orderDlv = Ordering.natural().nullsFirst().onResultOf(WorkflowDeliveryProgressCsvDTO::getDeliveryLabel);

        ORDER_DTO = orderLib.compound(orderPj).compound(orderLot).compound(orderDlv);
    }

    @CsvBindByName(column = "1.Bibliothèque")
    private String libraryName;
    @CsvBindByName(column = "2.Projet")
    private String projectName;
    @CsvBindByName(column = "3.Lot")
    private String lotLabel;
    @CsvBindByName(column = "4.Livraison")
    private String deliveryLabel;
    @CsvBindByName(column = "5.Étape")
    private WorkflowStateKey key;
    @CsvBindByName(column = "6.Nombre d'UD")
    private long count = 0L;

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(final String libraryName) {
        this.libraryName = libraryName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(final String projectName) {
        this.projectName = projectName;
    }

    public String getLotLabel() {
        return lotLabel;
    }

    public void setLotLabel(final String lotLabel) {
        this.lotLabel = lotLabel;
    }

    public String getDeliveryLabel() {
        return deliveryLabel;
    }

    public void setDeliveryLabel(final String deliveryLabel) {
        this.deliveryLabel = deliveryLabel;
    }

    public WorkflowStateKey getKey() {
        return key;
    }

    public void setKey(final WorkflowStateKey key) {
        this.key = key;
    }

    public long getCount() {
        return count;
    }

    public void setCount(final long count) {
        this.count = count;
    }

    @Override
    public int compareTo(@Nullable final WorkflowDeliveryProgressCsvDTO o) {
        return ORDER_DTO.compare(this, o);
    }
}
