package fr.progilone.pgcn.domain.exchange;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.library.Library;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Ensemble de règle de mapping, pour un type de donnée et une bibliothèque donnés
 * <p>
 * Created by Sebastien on 23/11/2016.
 */
@Entity
@Table(name = Mapping.TABLE_NAME)
@Audited
@AuditTable(value = Mapping.AUDIT_TABLE_NAME)
public class Mapping extends AbstractDomainObject {

    public static final String TABLE_NAME = "exc_mapping";
    public static final String AUDIT_TABLE_NAME = "aud_exc_mapping";

    /**
     * Types de mapping
     */
    public enum Type {
        EAD,
        DC,
        DCQ,
        MARC
    }

    /**
     * Libellé de cet ensemble de règles de mapping
     */
    @Column(name = "label", nullable = false)
    private String label;

    /**
     * Bibliothèque utilisant cet ensemble de règles
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "library")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Library library;

    /**
     * Type de mapping
     */
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    /**
     * Expression, par défaut, permettant de faire le lien entre des unités parent / enfant
     */
    @Column(name = "join_expression", columnDefinition = "text")
    private String joinExpression;

    @OneToMany(mappedBy = "mapping", fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
    private final List<MappingRule> rules = new ArrayList<>();

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(final Library library) {
        this.library = library;
    }

    public Type getType() {
        return type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public String getJoinExpression() {
        return joinExpression;
    }

    public void setJoinExpression(final String joinExpression) {
        this.joinExpression = joinExpression;
    }

    public List<MappingRule> getRules() {
        return rules;
    }

    public void setRules(final List<MappingRule> rules) {
        this.rules.clear();
        if (rules != null) {
            rules.forEach(this::addRule);
        }
    }

    public void addRule(final MappingRule rule) {
        if (rule != null) {
            this.rules.add(rule);
            rule.setMapping(this);
        }
    }

    @Override
    public String toString() {
        String str = "Mapping{" + "label='" + label + '\'';
        if (library != null) {
            str += ", library=" + library.getName();
        }
        str += ", type=" + type + '}';
        return str;
    }
}
