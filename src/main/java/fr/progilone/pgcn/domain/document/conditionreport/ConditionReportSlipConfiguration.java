package fr.progilone.pgcn.domain.document.conditionreport;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.library.Library;

@Entity
@Table(name = ConditionReportSlipConfiguration.TABLE_NAME)
public class ConditionReportSlipConfiguration extends AbstractDomainObject {

    public static final String TABLE_NAME = "doc_condreport_slip_config";

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library")
    private Library library;
    
    @Column(name = "pgcn_id")
    private boolean pgcnId;
    
    @Column(name = "title")
    private boolean title;
    
    @Column(name = "nb_pages")
    private boolean nbPages;
    
    @Column(name = "global_report")
    private boolean globalReport;

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    public boolean isPgcnId() {
        return pgcnId;
    }

    public void setPgcnId(boolean pgcnId) {
        this.pgcnId = pgcnId;
    }

    public boolean isTitle() {
        return title;
    }

    public void setTitle(boolean title) {
        this.title = title;
    }

    public boolean isNbPages() {
        return nbPages;
    }

    public void setNbPages(boolean nbPages) {
        this.nbPages = nbPages;
    }

    public boolean isGlobalReport() {
        return globalReport;
    }

    public void setGlobalReport(boolean globalReport) {
        this.globalReport = globalReport;
    }
    
    
}
