package fr.progilone.pgcn.domain.dto.statistics.csv;

import com.google.common.collect.Ordering;
import com.opencsv.bean.CsvBindByName;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.domain.workflow.WorkflowStateStatus;

import javax.annotation.Nullable;

public class WorkflowDocUnitProgressCsvDTO implements Comparable<WorkflowDocUnitProgressCsvDTO> {

    private static final Ordering<WorkflowDocUnitProgressCsvDTO> orderDto;

    static {
        Ordering<WorkflowDocUnitProgressCsvDTO> orderLib = Ordering.natural().nullsFirst().onResultOf(WorkflowDocUnitProgressCsvDTO::getLibraryName);
        Ordering<WorkflowDocUnitProgressCsvDTO> orderPj = Ordering.natural().nullsFirst().onResultOf(WorkflowDocUnitProgressCsvDTO::getProjectName);
        Ordering<WorkflowDocUnitProgressCsvDTO> orderLot = Ordering.natural().nullsFirst().onResultOf(WorkflowDocUnitProgressCsvDTO::getLotLabel);
        Ordering<WorkflowDocUnitProgressCsvDTO> orderUd = Ordering.natural().nullsFirst().onResultOf(WorkflowDocUnitProgressCsvDTO::getDocPgcnId);
        Ordering<WorkflowDocUnitProgressCsvDTO> orderStep = Ordering.natural().nullsFirst().onResultOf(WorkflowDocUnitProgressCsvDTO::getKey);
        orderDto = orderLib.compound(orderPj).compound(orderLot).compound(orderUd).compound(orderStep);
    }

    @CsvBindByName(column = "1. Bibliothèque")
    private String libraryName;
    @CsvBindByName(column = "2. Projet")
    private String projectName;
    @CsvBindByName(column = "3. Lot")
    private String lotLabel;
    @CsvBindByName(column = "4. Pgcn Id")
    private String docPgcnId;
    @CsvBindByName(column = "5. Libellé UD")
    private String docLabel;
    @CsvBindByName(column = "6. Nombre de pages")
    private Integer totalPage;
    @CsvBindByName(column = "7. Étape")
    private WorkflowStateKey key;
    @CsvBindByName(column = "8. Statut")
    private WorkflowStateStatus status;

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

    public String getDocPgcnId() {
        return docPgcnId;
    }

    public void setDocPgcnId(final String docPgcnId) {
        this.docPgcnId = docPgcnId;
    }

    public String getDocLabel() {
        return docLabel;
    }

    public void setDocLabel(final String docLabel) {
        this.docLabel = docLabel;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(final Integer totalPage) {
        this.totalPage = totalPage;
    }

    public WorkflowStateKey getKey() {
        return key;
    }

    public void setKey(final WorkflowStateKey key) {
        this.key = key;
    }

    public WorkflowStateStatus getStatus() {
        return status;
    }

    public void setStatus(final WorkflowStateStatus status) {
        this.status = status;
    }

    @Override
    public int compareTo(@Nullable final WorkflowDocUnitProgressCsvDTO o) {
        return orderDto.compare(this, o);
    }
}
