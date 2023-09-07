package fr.progilone.pgcn.domain.es.lot;

import static fr.progilone.pgcn.service.es.EsConstant.*;

import fr.progilone.pgcn.domain.es.user.EsUser;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.lot.Lot.LotStatus;
import fr.progilone.pgcn.domain.lot.Lot.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Setting;

/**
 * Classe métier permettant de gérer les bibliothèques.
 */
@Document(indexName = "#{@environment.getProperty('elasticsearch.index.name')}-lot")
@Setting(settingPath = "config/elasticsearch/settings_pgcn.json", shards = 1, replicas = 0)
public class EsLot {

    @Id
    @Field(type = FieldType.Keyword)
    private String identifier;

    @Field(type = FieldType.Keyword)
    private String libraryId;

    /**
     * Libellé
     */
    @MultiField(mainField = @Field(type = FieldType.Text),
                otherFields = {@InnerField(type = FieldType.Keyword, suffix = SUBFIELD_RAW),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_CI_AI, analyzer = ANALYZER_CI_AI, searchAnalyzer = ANALYZER_CI_AI),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_CI_AS, analyzer = ANALYZER_CI_AS, searchAnalyzer = ANALYZER_CI_AS),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_PHRASE, analyzer = ANALYZER_PHRASE, searchAnalyzer = ANALYZER_PHRASE)})
    private String label;

    @Field(type = FieldType.Object)
    private EsUser provider;

    /**
     * Type attendu
     */
    @Field(type = FieldType.Keyword)
    private Type type;

    /**
     * Etat
     */
    @Field(type = FieldType.Boolean)
    private boolean active;

    /**
     * statut
     */
    @Field(type = FieldType.Keyword)
    private LotStatus status;

    /**
     * Format des fichiers numériques
     */
    @Field(type = FieldType.Keyword)
    private String requiredFormat;

    public static EsLot from(final Lot lot) {
        final EsLot esLot = new EsLot();
        esLot.setIdentifier(lot.getIdentifier());
        if (lot.getProject() != null && lot.getProject().getLibrary() != null) {
            esLot.setLibraryId(lot.getProject().getLibrary().getIdentifier());
        }
        esLot.setLabel(lot.getLabel());
        if (lot.getProvider() != null) {
            esLot.setProvider(EsUser.from(lot.getProvider()));
        }
        esLot.setType(lot.getType());
        esLot.setActive(lot.isActive());
        esLot.setStatus(lot.getStatus());
        esLot.setRequiredFormat(lot.getRequiredFormat());
        return esLot;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(final String libraryId) {
        this.libraryId = libraryId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public EsUser getProvider() {
        return provider;
    }

    public void setProvider(final EsUser provider) {
        this.provider = provider;
    }

    public Type getType() {
        return type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public LotStatus getStatus() {
        return status;
    }

    public void setStatus(final LotStatus status) {
        this.status = status;
    }

    public String getRequiredFormat() {
        return requiredFormat;
    }

    public void setRequiredFormat(final String requiredFormat) {
        this.requiredFormat = requiredFormat;
    }

}
