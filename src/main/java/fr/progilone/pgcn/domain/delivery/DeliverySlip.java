package fr.progilone.pgcn.domain.delivery;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonSubTypes;

import fr.progilone.pgcn.domain.AbstractDomainObject;

/**
 * Classe représentant un bordereau de livraison
 */
@Entity
@Table(name = DeliverySlip.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "del_slip", value = DeliverySlip.class)})
public class DeliverySlip extends AbstractDomainObject {

    public static final String TABLE_NAME = "del_slip";

    @OneToMany(mappedBy = "slip", orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final Set<DeliverySlipLine> slipLines = new LinkedHashSet<>();

    /**
     * Livraison rattachée
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery")
    private Delivery delivery;

    public Set<DeliverySlipLine> getSlipLines() {
        return slipLines;
    }

    public void addSlipLine(DeliverySlipLine line) {
        this.slipLines.add(line);
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }
}
