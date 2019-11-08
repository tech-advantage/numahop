package fr.progilone.pgcn.domain.exchange;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.library.Library;

@Entity
@Table(name = CSVMapping.TABLE_NAME)
public class CSVMapping extends AbstractDomainObject {

    public static final String TABLE_NAME = "exc_csv_mapping";

    @Column(name = "label", nullable = false)
    private String label;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "library")
    private Library library;

    @OneToMany(mappedBy = "mapping", fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
    @OrderBy("rank ASC")
    private final List<CSVMappingRule> rules = new ArrayList<>();

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

    /**
     * Expression, par défaut, permettant de faire le lien entre des unités parent / enfant
     */
    @Column(name = "join_expression", columnDefinition = "text")
    private String joinExpression;

    public String getJoinExpression() {
        return joinExpression;
    }

    public void setJoinExpression(final String joinExpression) {
        this.joinExpression = joinExpression;
    }

    public List<CSVMappingRule> getRules() {
        return rules;
    }

    public void setRules(final List<CSVMappingRule> rules) {
        this.rules.clear();
        if (rules != null) {
            rules.forEach(this::addRule);
        }
    }

    public void addRule(final CSVMappingRule rule) {
        if (rule != null) {
            this.rules.add(rule);
            rule.setMapping(this);
        }
    }

    @Override
    public String toString() {
        String str = "CSVMapping{" + "label='" + label + '\'';
        if (library != null) {
            str += ", library=" + library.getName();
        }
        return str;
    }
}
