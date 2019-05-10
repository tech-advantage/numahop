package fr.progilone.pgcn.domain.checkconfiguration;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonSubTypes;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.library.Library;

/**
 * Classe métier permettant de gérer la configuration des contrôles.
 */
@Entity
@Table(name = CheckConfiguration.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "configurationCheck", value = CheckConfiguration.class)})
public class CheckConfiguration extends AbstractDomainObject {

    public static final String TABLE_NAME = "conf_configuration_check";

    @Column(name = "label")
    private String label;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library")
    private Library library;

    @Column(name = "major_error_rate")
    private Double majorErrorRate;

    @Column(name = "minor_error_rate")
    private Double minorErrorRate;

    @Column(name = "sample_rate")
    private Double sampleRate;
    
    @Column(name = "sample_mode")
    private String sampleMode;
    
    @Column(name = "separators")
    private String separators;
    
    /**
     * Liste des regles de contrôles automatiques associés
     */
    @OneToMany(mappedBy = "checkConfiguration", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final List<AutomaticCheckRule> automaticCheckRules = new ArrayList<>();

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    public Double getMajorErrorRate() {
        return majorErrorRate;
    }

    public void setMajorErrorRate(Double majorErrorRate) {
        this.majorErrorRate = majorErrorRate;
    }

    public Double getMinorErrorRate() {
        return minorErrorRate;
    }

    public void setMinorErrorRate(Double minorErrorRate) {
        this.minorErrorRate = minorErrorRate;
    }

    public Double getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(Double sampleRate) {
        this.sampleRate = sampleRate;
    }

    public String getSampleMode() {
        return sampleMode;
    }

    public void setSampleMode(String sampleMode) {
        this.sampleMode = sampleMode;
    }

    public String getSeparators() {
        return separators;
    }

    public void setSeparators(String separators) {
        this.separators = separators;
    }

    public List<AutomaticCheckRule> getAutomaticCheckRules() {
        return automaticCheckRules;
    }
    
    public void setAutomaticCheckRules(List<AutomaticCheckRule> rules) {
        this.automaticCheckRules.clear();
        if (rules != null) {
            rules.forEach(this::addAutomaticCheckRule);
        }
    }

    public void addAutomaticCheckRule(AutomaticCheckRule rule) {
        if (rule != null) {
            this.automaticCheckRules.add(rule);
            rule.setCheckConfiguration(this);
        }
    }

}
