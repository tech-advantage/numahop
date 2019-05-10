package fr.progilone.pgcn.domain.delivery;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonSubTypes;

import fr.progilone.pgcn.domain.AbstractDomainObject;

/**
 * Classe représentant une ligne d'un bordereau de livraison
 */
@Entity
@Table(name = DeliverySlipLine.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "del_slip_line", value = DeliverySlipLine.class)})
public class DeliverySlipLine extends AbstractDomainObject {

    public static final String TABLE_NAME = "del_slip_line";

    @Column(name = "pgcn_id", columnDefinition = "text")
    private String pgcnId;
    @Column(name = "lot", columnDefinition = "text")
    private String lot;
    @Column(name = "train", columnDefinition = "text")
    private String train;
    @Column(name = "radical", columnDefinition = "text")
    private String radical;
    @Column(name = "title", columnDefinition = "text")
    private String title;
    @Column(name = "nb_pages", columnDefinition = "text")
    private String nbPages;
    @Column(name = "date", columnDefinition = "text")
    private String date;

    /**
     * Bordereau rattaché
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "slip")
    private DeliverySlip slip;

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public String getPgcnId() {
        return pgcnId;
    }

    public void setPgcnId(String pgcnId) {
        this.pgcnId = pgcnId;
    }

    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public String getTrain() {
        return train;
    }

    public void setTrain(String train) {
        this.train = train;
    }

    public String getRadical() {
        return radical;
    }

    public void setRadical(String radical) {
        this.radical = radical;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNbPages() {
        return nbPages;
    }

    public void setNbPages(String nbPages) {
        this.nbPages = nbPages;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public DeliverySlip getSlip() {
        return slip;
    }

    public void setSlip(DeliverySlip slip) {
        this.slip = slip;
    }
}
