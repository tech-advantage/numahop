package fr.progilone.pgcn.domain.delivery;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.library.Library;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Classe métier permettant de gérer la configuration des bordereaux de livraison.
 */
@Entity
@Table(name = DeliverySlipConfiguration.TABLE_NAME)
public class DeliverySlipConfiguration extends AbstractDomainObject {

    public static final String TABLE_NAME = "del_slip_config";

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library")
    private Library library;

    @Column(name = "pgcn_id")
    private boolean pgcnId;
    @Column(name = "lot")
    private boolean lot;
    @Column(name = "train")
    private boolean train;
    @Column(name = "radical")
    private boolean radical;
    @Column(name = "title")
    private boolean title;
    @Column(name = "nb_pages")
    private boolean nbPages;
    @Column(name = "date")
    private boolean date;

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    public boolean getPgcnId() {
        return pgcnId;
    }

    public void setPgcnId(boolean pgcnId) {
        this.pgcnId = pgcnId;
    }

    public boolean getLot() {
        return lot;
    }

    public void setLot(boolean lot) {
        this.lot = lot;
    }

    public boolean getTrain() {
        return train;
    }

    public void setTrain(boolean train) {
        this.train = train;
    }

    public boolean getRadical() {
        return radical;
    }

    public void setRadical(boolean radical) {
        this.radical = radical;
    }

    public boolean getTitle() {
        return title;
    }

    public void setTitle(boolean title) {
        this.title = title;
    }

    public boolean getNbPages() {
        return nbPages;
    }

    public void setNbPages(boolean nbPages) {
        this.nbPages = nbPages;
    }

    public boolean getDate() {
        return date;
    }

    public void setDate(boolean date) {
        this.date = date;
    }

}
