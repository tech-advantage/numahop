package fr.progilone.pgcn.domain.es.document;

import static fr.progilone.pgcn.service.es.EsConstant.*;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.es.library.EsLibrary;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

public class EsBibliographicRecord {

    @Field(type = FieldType.Keyword)
    private String identifier;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime createdDate;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime lastModifiedDate;

    @MultiField(mainField = @Field(type = FieldType.Text),
                otherFields = {@InnerField(type = FieldType.Keyword, suffix = SUBFIELD_RAW),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_CI_AI, analyzer = ANALYZER_CI_AI, searchAnalyzer = ANALYZER_CI_AI),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_CI_AS, analyzer = ANALYZER_CI_AS, searchAnalyzer = ANALYZER_CI_AS),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_PHRASE, analyzer = ANALYZER_PHRASE, searchAnalyzer = ANALYZER_PHRASE)})
    private String title;

    /**
     * URL vers le SIGB
     */
    @Field(type = FieldType.Keyword)
    private String sigb;

    /**
     * identifiant SUDOC
     */
    @Field(type = FieldType.Keyword)
    private String sudoc;

    /**
     * identifiant Calames
     */
    @Field(type = FieldType.Keyword)
    private String calames;

    /**
     * URL document électronique
     */
    @Field(type = FieldType.Keyword)
    private String docElectronique;

    /**
     * Liste des propriétés de la notice (DC, DCq, Custom)
     */
    @Field(type = FieldType.Nested)
    private Set<EsDocProperty> properties;

    /**
     * Bibliothèque de rattachement
     */
    @Field(type = FieldType.Object)
    private EsLibrary library;

    public static EsBibliographicRecord from(final BibliographicRecord record) {
        final EsBibliographicRecord esRecord = new EsBibliographicRecord();
        esRecord.setIdentifier(record.getIdentifier());
        esRecord.setCreatedDate(record.getCreatedDate());
        esRecord.setLastModifiedDate(Stream.concat(Stream.of(record.getLastModifiedDate()), record.getProperties().stream().map(AbstractDomainObject::getLastModifiedDate))
                                           .max(LocalDateTime::compareTo)
                                           .orElse(null));
        esRecord.setTitle(record.getTitle());
        esRecord.setSigb(record.getSigb());
        esRecord.setSudoc(record.getSudoc());
        esRecord.setCalames(record.getCalames());
        esRecord.setDocElectronique(record.getDocElectronique());
        esRecord.setLibrary(EsLibrary.from(record.getLibrary()));
        esRecord.setProperties(record.getProperties().stream().map(EsDocProperty::from).collect(Collectors.toSet()));
        return esRecord;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(final LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(final LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getSigb() {
        return sigb;
    }

    public void setSigb(final String sigb) {
        this.sigb = sigb;
    }

    public String getSudoc() {
        return sudoc;
    }

    public void setSudoc(final String sudoc) {
        this.sudoc = sudoc;
    }

    public String getCalames() {
        return calames;
    }

    public void setCalames(final String calames) {
        this.calames = calames;
    }

    public String getDocElectronique() {
        return docElectronique;
    }

    public void setDocElectronique(final String docElectronique) {
        this.docElectronique = docElectronique;
    }

    public Set<EsDocProperty> getProperties() {
        return properties;
    }

    public void setProperties(final Set<EsDocProperty> properties) {
        this.properties = properties;
    }

    public EsLibrary getLibrary() {
        return library;
    }

    public void setLibrary(final EsLibrary library) {
        this.library = library;
    }
}
