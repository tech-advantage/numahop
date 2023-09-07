package fr.progilone.pgcn.domain.exchange;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

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
     * Champ de {@link fr.progilone.pgcn.domain.document.BibliographicRecord} concerné par cette règle de mapping
     */
    @Column(name = "bib_record_field")
    private String bibRecordField;

    /**
     * Id de la propriété du constat d'état concerné (DescriptionProperty ou attribut du ConditionReportDetail)
     */
    @Column(name = "cond_report")
    private String condReport;

    /**
     * Metadata concernée par cette règle de mapping
     */
    @Column(name = "metadata")
    private String metadata;

    @Column(name = "rank")
    private int rank;

    /**
     * Propriété concernée par cette règle de mapping
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "property")
    private DocPropertyType property;

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
        return "CSVMappingRule{" + "docUnitField='"
               + docUnitField
               + '\''
               + ", csvField="
               + csvField
               + ", "
               + "propertyId="
               + condReport
               + '}';
    }

    public String getCsvField() {
        return csvField;
    }

    public void setCsvField(final String csvField) {
        this.csvField = csvField;
    }

    public String getBibRecordField() {
        return bibRecordField;
    }

    public void setBibRecordField(final String bibRecordField) {
        this.bibRecordField = bibRecordField;
    }

    public String getCondReport() {
        return condReport;
    }

    public void setCondReport(final String propertyId) {
        this.condReport = propertyId;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(final int rank) {
        this.rank = rank;
    }

    public DocPropertyType getProperty() {
        return property;
    }

    public void setProperty(final DocPropertyType property) {
        this.property = property;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
