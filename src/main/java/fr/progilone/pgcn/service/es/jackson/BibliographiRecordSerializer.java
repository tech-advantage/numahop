package fr.progilone.pgcn.service.es.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.BibliographicRecord_;

import java.io.IOException;

public class BibliographiRecordSerializer extends StdSerializer<BibliographicRecord> {

    public BibliographiRecordSerializer() {
        super(BibliographicRecord.class);
    }

    @Override
    public void serialize(final BibliographicRecord record, final JsonGenerator jgen, final SerializerProvider serializerProvider) throws
                                                                                                                                   IOException {
        jgen.writeStartObject();
        jgen.writeStringField(BibliographicRecord_.identifier.getName(), record.getIdentifier());
        jgen.writeStringField(BibliographicRecord_.docUnitId.getName(), record.getDocUnitId());
        jgen.writeObjectField(BibliographicRecord_.createdDate.getName(), record.getCreatedDate());
        jgen.writeObjectField(BibliographicRecord_.lastModifiedDate.getName(), record.getGeneralLastModifiedDate());
        jgen.writeStringField(BibliographicRecord_.title.getName(), record.getTitle());
        jgen.writeStringField(BibliographicRecord_.sigb.getName(), record.getSigb());
        jgen.writeStringField(BibliographicRecord_.sudoc.getName(), record.getSudoc());
        jgen.writeStringField(BibliographicRecord_.calames.getName(), record.getCalames());
        jgen.writeStringField(BibliographicRecord_.docElectronique.getName(), record.getDocElectronique());
        jgen.writeObjectField(BibliographicRecord_.library.getName(), record.getLibrary());
        jgen.writeObjectField(BibliographicRecord_.properties.getName(), record.getProperties());
        jgen.writeEndObject();
    }
}
