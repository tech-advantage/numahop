package fr.progilone.pgcn.domain.dto.statistics;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Ordering;
import com.opencsv.bean.CsvBindByName;
import fr.progilone.pgcn.domain.project.Project;

public class StatisticsProgressDTO implements Comparable<StatisticsProgressDTO> {

    private static final Ordering<StatisticsProgressDTO> ORDER_DTO;

    static {
        final Ordering<StatisticsProgressDTO> orderLib = Ordering.natural().nullsFirst().onResultOf(StatisticsProgressDTO::getLibraryName);
        final Ordering<StatisticsProgressDTO> orderPj = Ordering.natural().nullsFirst().onResultOf(StatisticsProgressDTO::getProjectName);
        final Ordering<StatisticsProgressDTO> orderLot = Ordering.natural().nullsFirst().onResultOf(StatisticsProgressDTO::getLotLabel);

        ORDER_DTO = orderLib.compound(orderPj).compound(orderLot);
    }

    private String libraryIdentifier;
    @CsvBindByName(column = "01.Bibliothèque")
    private String libraryName;

    private String projectIdentifier;
    @CsvBindByName(column = "02.Projet")
    private String projectName;
    @CsvBindByName(column = "03.Statut du projet")
    private Project.ProjectStatus projectStatus;

    private String lotIdentifier;
    @CsvBindByName(column = "04.Lot")
    private String lotLabel;

    @CsvBindByName(column = "05. Nombre de lots du projet")
    private long nbLots;
    @CsvBindByName(column = "06. Nombre de documents du projet / lot")
    private long nbDocUnits;
    @CsvBindByName(column = "07. Nombre de documents numérisés")
    private long nbDigitalDocs;
    @CsvBindByName(column = "08. % de documents numérisés")
    private double pctDigitalDocs;
    @CsvBindByName(column = "09. Nombre de documents contrôlés (livraison)")
    private long nbDlvControlled;
    @CsvBindByName(column = "10. % de documents contrôlés (livraison)")
    private double pctDlvControlled;
    @CsvBindByName(column = "11. Nombre de documents validés (livraison)")
    private long nbDlvValidated;
    @CsvBindByName(column = "12. % de documents validés (livraison)")
    private double pctDlvValidated;
    @CsvBindByName(column = "13. Nombre de documents rejetés (livraison)")
    private long nbDlvRejected;
    @CsvBindByName(column = "14. % de documents rejetés (livraison)")
    private double pctDlvRejected;
    @CsvBindByName(column = "15. Nombre moyen de livraisons d'un document")
    private double avgDocDlv;
    @CsvBindByName(column = "16. Nombre de livraisons du projet / lot")
    private long nbDlv;
    @CsvBindByName(column = "17. Nombre de documents validés (workflow)")
    private long nbWorkflowValidated;
    @CsvBindByName(column = "18. % de documents validés (workflow)")
    private double pctWorkflowValidated;
    @CsvBindByName(column = "19. Nombre de documents diffusables")
    private long nbDocDistributable;
    @CsvBindByName(column = "20. % de documents diffusables")
    private double pctDocDistributable;
    @CsvBindByName(column = "21. Nombre de documents diffusés")
    private long nbDocDistributed;
    @CsvBindByName(column = "22. % de documents diffusés")
    private double pctDocDistributed;
    @CsvBindByName(column = "23. Nombre de documents archivables")
    private long nbDocArchivable;
    @CsvBindByName(column = "24. % de documents archivables")
    private double pctDocArchivable;
    @CsvBindByName(column = "25. Nombre de documents archivés")
    private long nbDocArchived;
    @CsvBindByName(column = "26. % de documents archivés")
    private double pctDocArchived;

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

    public Project.ProjectStatus getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(final Project.ProjectStatus projectStatus) {
        this.projectStatus = projectStatus;
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

    public long getNbLots() {
        return nbLots;
    }

    public void setNbLots(final long nbLots) {
        this.nbLots = nbLots;
    }

    public long getNbDocUnits() {
        return nbDocUnits;
    }

    public void setNbDocUnits(final long nbDocUnits) {
        this.nbDocUnits = nbDocUnits;
    }

    public long getNbDigitalDocs() {
        return nbDigitalDocs;
    }

    public void setNbDigitalDocs(final long nbDigitalDocs) {
        this.nbDigitalDocs = nbDigitalDocs;
    }

    public double getPctDigitalDocs() {
        return pctDigitalDocs;
    }

    public void setPctDigitalDocs(final double pctDigitalDocs) {
        this.pctDigitalDocs = pctDigitalDocs;
    }

    public long getNbDlvControlled() {
        return nbDlvControlled;
    }

    public void setNbDlvControlled(final long nbDlvControlled) {
        this.nbDlvControlled = nbDlvControlled;
    }

    public double getPctDlvControlled() {
        return pctDlvControlled;
    }

    public void setPctDlvControlled(final double pctDlvControlled) {
        this.pctDlvControlled = pctDlvControlled;
    }

    public long getNbDlvValidated() {
        return nbDlvValidated;
    }

    public void setNbDlvValidated(final long nbDlvValidated) {
        this.nbDlvValidated = nbDlvValidated;
    }

    public double getPctDlvValidated() {
        return pctDlvValidated;
    }

    public void setPctDlvValidated(final double pctDlvValidated) {
        this.pctDlvValidated = pctDlvValidated;
    }

    public long getNbDlvRejected() {
        return nbDlvRejected;
    }

    public void setNbDlvRejected(final long nbDlvRejected) {
        this.nbDlvRejected = nbDlvRejected;
    }

    public double getPctDlvRejected() {
        return pctDlvRejected;
    }

    public void setPctDlvRejected(final double pctDlvRejected) {
        this.pctDlvRejected = pctDlvRejected;
    }

    public double getAvgDocDlv() {
        return avgDocDlv;
    }

    public void setAvgDocDlv(final double avgDocDlv) {
        this.avgDocDlv = avgDocDlv;
    }

    public long getNbDlv() {
        return nbDlv;
    }

    public void setNbDlv(final long nbDlv) {
        this.nbDlv = nbDlv;
    }

    public long getNbWorkflowValidated() {
        return nbWorkflowValidated;
    }

    public void setNbWorkflowValidated(final long nbWorkflowValidated) {
        this.nbWorkflowValidated = nbWorkflowValidated;
    }

    public double getPctWorkflowValidated() {
        return pctWorkflowValidated;
    }

    public void setPctWorkflowValidated(final double pctWorkflowValidated) {
        this.pctWorkflowValidated = pctWorkflowValidated;
    }

    public long getNbDocDistributable() {
        return nbDocDistributable;
    }

    public void setNbDocDistributable(final long nbDocDistributable) {
        this.nbDocDistributable = nbDocDistributable;
    }

    public double getPctDocDistributable() {
        return pctDocDistributable;
    }

    public void setPctDocDistributable(final double pctDocDistributable) {
        this.pctDocDistributable = pctDocDistributable;
    }

    public long getNbDocDistributed() {
        return nbDocDistributed;
    }

    public void setNbDocDistributed(final long nbDocDistributed) {
        this.nbDocDistributed = nbDocDistributed;
    }

    public double getPctDocDistributed() {
        return pctDocDistributed;
    }

    public void setPctDocDistributed(final double pctDocDistributed) {
        this.pctDocDistributed = pctDocDistributed;
    }

    public long getNbDocArchivable() {
        return nbDocArchivable;
    }

    public void setNbDocArchivable(final long nbDocArchivable) {
        this.nbDocArchivable = nbDocArchivable;
    }

    public double getPctDocArchivable() {
        return pctDocArchivable;
    }

    public void setPctDocArchivable(final double pctDocArchivable) {
        this.pctDocArchivable = pctDocArchivable;
    }

    public long getNbDocArchived() {
        return nbDocArchived;
    }

    public void setNbDocArchived(final long nbDocArchived) {
        this.nbDocArchived = nbDocArchived;
    }

    public double getPctDocArchived() {
        return pctDocArchived;
    }

    public void setPctDocArchived(final double pctDocArchived) {
        this.pctDocArchived = pctDocArchived;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("libraryIdentifier", libraryIdentifier)
                          .add("libraryName", libraryName)
                          .add("projectIdentifier", projectIdentifier)
                          .add("projectName", projectName)
                          .add("lotIdentifier", lotIdentifier)
                          .add("lotLabel", lotLabel)
                          .toString();
    }

    @Override
    public int compareTo(final StatisticsProgressDTO o) {
        return ORDER_DTO.compare(this, o);
    }
}
