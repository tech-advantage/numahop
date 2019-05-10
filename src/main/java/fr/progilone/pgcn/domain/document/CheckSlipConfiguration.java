package fr.progilone.pgcn.domain.document;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.library.Library;

/**
 * Classe métier permettant de gérer la configuration des bordereaux de livraison.
 */
@Entity
@Table(name = CheckSlipConfiguration.TABLE_NAME)
public class CheckSlipConfiguration extends AbstractDomainObject {

    public static final String TABLE_NAME = "doc_check_slip_config";

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library")
    private Library library;

    @Column(name = "pgcn_id")
    private boolean pgcnId;
    @Column(name = "title")
    private boolean title;
    @Column(name = "state")
    private boolean state;
    @Column(name = "errors")
    private boolean errs;
    @Column(name = "nb_pages")
    private boolean nbPages;
    @Column(name = "nb_pages_tobill")
    private boolean nbPagesToBill;

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

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public boolean isErrs() {
        return errs;
    }

    public void setErrors(boolean errs) {
        this.errs = errs;
    }

    public boolean isNbPages() {
        return nbPages;
    }

    public void setNbPages(boolean nbPages) {
        this.nbPages = nbPages;
    }

    public boolean isNbPagesToBill() {
        return nbPagesToBill;
    }

    public void setNbPagesToBill(boolean nbPagesToBill) {
        this.nbPagesToBill = nbPagesToBill;
    }

    
}
