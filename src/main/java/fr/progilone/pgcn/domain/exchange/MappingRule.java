package fr.progilone.pgcn.domain.exchange;

import com.google.common.base.MoreObjects;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Régle de mapping
 * <p>
 * Created by Sebastien on 23/11/2016.
 */
@Entity
@Table(name = MappingRule.TABLE_NAME)
@Audited
@AuditTable(value = MappingRule.AUDIT_TABLE_NAME)
public class MappingRule extends AbstractDomainObject {

    public static final String TABLE_NAME = "exc_mapping_rule";
    public static final String AUDIT_TABLE_NAME = "aud_exc_mapping_rule";

    /**
     * Champ de {@link fr.progilone.pgcn.domain.document.DocUnit} concerné par cette règle de mapping
     */
    @Column(name = "doc_unit_field")
    private String docUnitField;

    /**
     * Champ de {@link fr.progilone.pgcn.domain.document.BibliographicRecord} concerné par cette règle de mapping
     */
    @Column(name = "bib_record_field")
    private String bibRecordField;

    /**
     * Propriété concernée par cette règle de mapping
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "property")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private DocPropertyType property;

    /**
     * Configuration de l'expression
     * (code exécuté une seule fois à l'initialisation de la règle, pour initialiser la règle de formattage)
     */
    @Column(name = "expression_conf", columnDefinition = "text")
    private String expressionConf;

    /**
     * Expression permettant de calculer la valeur mappée
     */
    @Column(name = "expression", columnDefinition = "text")
    private String expression;

    /**
     * Configuration de la condition
     * (code exécuté une seule fois à l'initialisation de la règle, pour initialiser la règle de filtrage)
     */
    @Column(name = "apply_if_conf", columnDefinition = "text")
    private String conditionConf;

    /**
     * Condition déterminant si cette règle de mapping doit être appliquée
     */
    @Column(name = "apply_if", columnDefinition = "text")
    private String condition;

    /**
     * Position de la règle
     */
    @Column(name = "position")
    private Integer position;

    /**
     * Cette règle s'applique par défaut si aucune valeur n'a été définie pour les champs docUnitField, bibRecordField ou property
     */
    @Column(name = "default_rule")
    private boolean defaultRule = false;

    /**
     * Ensemble de règles de mapping auquel celle-ci appartient
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "mapping", nullable = false)
    private Mapping mapping;

    public String getDocUnitField() {
        return docUnitField;
    }

    public void setDocUnitField(final String docUnitField) {
        this.docUnitField = docUnitField;
    }

    public String getBibRecordField() {
        return bibRecordField;
    }

    public void setBibRecordField(final String bibRecordField) {
        this.bibRecordField = bibRecordField;
    }

    public DocPropertyType getProperty() {
        return property;
    }

    public void setProperty(final DocPropertyType property) {
        this.property = property;
    }

    public String getExpressionConf() {
        return expressionConf;
    }

    public void setExpressionConf(final String expressionConf) {
        this.expressionConf = expressionConf;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(final String expression) {
        this.expression = expression;
    }

    public String getConditionConf() {
        return conditionConf;
    }

    public void setConditionConf(final String conditionConf) {
        this.conditionConf = conditionConf;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(final String condition) {
        this.condition = condition;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(final Integer position) {
        this.position = position;
    }

    public boolean isDefaultRule() {
        return defaultRule;
    }

    public void setDefaultRule(final boolean defaultRule) {
        this.defaultRule = defaultRule;
    }

    public Mapping getMapping() {
        return mapping;
    }

    public void setMapping(final Mapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("docUnitField", docUnitField)
                          .add("bibRecordField", bibRecordField)
                          .add("property", property)
                          .add("expressionConf", expressionConf)
                          .add("expression", expression)
                          .add("conditionConf", conditionConf)
                          .add("condition", condition)
                          .add("position", position)
                          .toString();
    }
}
