package fr.progilone.pgcn.domain.document;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.delivery.DeliveredDocument;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = CheckSlip.TABLE_NAME)
public class CheckSlip extends AbstractDomainObject {

    public static final String TABLE_NAME = "check_slip";

    /**
     * Liste des documents concernés.
     */
    @OneToMany(mappedBy = "checkSlip", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final Set<DeliveredDocument> documents = new HashSet<>();

    @OneToMany(mappedBy = "checkSlip", orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final Set<CheckSlipLine> slipLines = new LinkedHashSet<>();

    @Column(name = "lot_label")
    private String lotLabel;

    @Column(name = "deposit_date")
    private LocalDate depositDate;

    @Column(name = "uncompleted")
    private boolean uncompleted;

    public String getLotLabel() {
        return lotLabel;
    }

    public void setLotLabel(String lotLabel) {
        this.lotLabel = lotLabel;
    }

    public LocalDate getDepositDate() {
        return depositDate;
    }

    public void setDepositDate(LocalDate depositDate) {
        this.depositDate = depositDate;
    }

    public Set<DeliveredDocument> getDocuments() {
        return documents;
    }

    public void addDocument(DeliveredDocument document) {
        this.documents.add(document);
    }

    public Set<CheckSlipLine> getSlipLines() {
        return slipLines;
    }

    public void addSlipLine(CheckSlipLine line) {
        this.slipLines.add(line);
    }

    public boolean isUncompleted() {
        return uncompleted;
    }

    public void setUncompleted(boolean uncompleted) {
        this.uncompleted = uncompleted;
    }

    public void deleteAllSlipLines() {
        this.slipLines.clear();
    }

    public void deleteAllDocuments() {
        this.documents.clear();
    }
}
