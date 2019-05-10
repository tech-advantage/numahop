package fr.progilone.pgcn.domain.exchange;

import fr.progilone.pgcn.domain.AbstractDomainObject;

import javax.persistence.*;

/**
 * Régle de mapping
 * <p>
 * Created by Sebastien on 23/11/2016.
 */
@Entity
@Table(name = CSVMappingRule.TABLE_NAME)
public class CSVMappingRule extends AbstractDomainObject {

    public static final String TABLE_NAME = "exc_csv_mapping_rule";

    @Column(name = "doc_unit_field")
    private String docUnitField;

    @Column(name = "csv_field")
    private String csvField;

    /**
     * Ensemble de règles de mapping auquel celle-ci appartient
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "mapping", nullable = false)
    private CSVMapping mapping;

    public String getDocUnitField() {
        return docUnitField;
    }

    public void setDocUnitField(final String docUnitField) {
        this.docUnitField = docUnitField;
    }

    public CSVMapping getMapping() {
        return mapping;
    }

    public void setMapping(final CSVMapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public String toString() {
        return "CSVMappingRule{" +
               "docUnitField='" + docUnitField + '\'' +
               ", csvField=" + csvField +
               '}';
    }

    public String getCsvField() {
        return csvField;
    }

    public void setCsvField(String csvField) {
        this.csvField = csvField;
    }
}
