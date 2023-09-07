/**
 *
 */
package fr.progilone.pgcn.domain.es.document;

import fr.progilone.pgcn.domain.document.PhysicalDocument;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

public class EsPhysicalDocument {

    @Field(type = FieldType.Keyword)
    private String identifier;

    @Field(type = FieldType.Integer)
    private Integer totalPage;

    @Field(type = FieldType.Keyword)
    private String digitalId;

    public static EsPhysicalDocument from(final PhysicalDocument document) {
        final EsPhysicalDocument esDocument = new EsPhysicalDocument();
        esDocument.setIdentifier(document.getIdentifier());
        esDocument.setDigitalId(document.getDigitalId());
        esDocument.setTotalPage(document.getTotalPage());
        return esDocument;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(final Integer totalPage) {
        this.totalPage = totalPage;
    }

    public String getDigitalId() {
        return digitalId;
    }

    public void setDigitalId(final String digitalId) {
        this.digitalId = digitalId;
    }

}
