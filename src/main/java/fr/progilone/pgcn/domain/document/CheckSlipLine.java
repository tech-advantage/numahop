package fr.progilone.pgcn.domain.document;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.progilone.pgcn.domain.AbstractDomainObject;

@Entity
@Table(name = CheckSlipLine.TABLE_NAME)
public class CheckSlipLine extends AbstractDomainObject {

    public static final String TABLE_NAME = "check_slip_line";
    
    
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "check_slip")
    private CheckSlip checkSlip;
    
    @Column(name = "pgcn_id")
    private String pgcnId;
    
    @Column(name = "title")
    private String title;
    
    @Column(name = "status")
    private String status;

    @Column(name = "doc_errors", columnDefinition = "TEXT")
    private String docErrors;
    
    @Column(name = "nb_pages")
    private Integer nbPages;
    
    @Column(name = "nb_pages_to_bill")
    private Integer nbPagesToBill;

    public CheckSlip getCheckSlip() {
        return checkSlip;
    }

    public void setCheckSlip(CheckSlip checkSlip) {
        this.checkSlip = checkSlip;
    }

    public String getPgcnId() {
        return pgcnId;
    }

    public void setPgcnId(String pgcnId) {
        this.pgcnId = pgcnId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDocErrors() {
        return docErrors;
    }

    public void setDocErrors(String docErrors) {
        this.docErrors = docErrors;
    }

    public Integer getNbPages() {
        return nbPages;
    }

    public void setNbPages(Integer nbPages) {
        this.nbPages = nbPages;
    }

    public Integer getNbPagesToBill() {
        return nbPagesToBill;
    }

    public void setNbPagesToBill(Integer nbPagesToBill) {
        this.nbPagesToBill = nbPagesToBill;
    }
    
    
}
