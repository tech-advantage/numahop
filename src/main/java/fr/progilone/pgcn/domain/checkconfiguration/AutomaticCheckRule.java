package fr.progilone.pgcn.domain.checkconfiguration;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.check.AutomaticCheckType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Classe métier permettant de gérer les contrôles d'une configuration.
 *
 * @author Emmanuel RIZET
 *
 */
@Entity
@Table(name = AutomaticCheckRule.TABLE_NAME)
public class AutomaticCheckRule extends AbstractDomainObject {

    private static final long serialVersionUID = 5090553160924640906L;

    public static final String TABLE_NAME = "conf_automatic_check_rule";

    @Column(name = "active")
    private boolean active;

    @Column(name = "blocking")
    private boolean blocking;

    /**
     * Entités liés
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "check_configuration")
    private CheckConfiguration checkConfiguration;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "check_type")
    private AutomaticCheckType automaticCheckType;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isBlocking() {
        return blocking;
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }

    public CheckConfiguration getCheckConfiguration() {
        return checkConfiguration;
    }

    public void setCheckConfiguration(CheckConfiguration checkConfiguration) {
        this.checkConfiguration = checkConfiguration;
    }

    public AutomaticCheckType getAutomaticCheckType() {
        return automaticCheckType;
    }

    public void setAutomaticCheckType(AutomaticCheckType automaticCheckType) {
        this.automaticCheckType = automaticCheckType;
    }

}
