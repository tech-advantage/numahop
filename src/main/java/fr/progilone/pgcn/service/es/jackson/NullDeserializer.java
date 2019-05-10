package fr.progilone.pgcn.service.es.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

/**
 * Deserializer JSON qui retourne tout le temps null
 *
 * @param <T>
 */
public class NullDeserializer<T> extends StdDeserializer<T> {

    public NullDeserializer(final Class<?> vc) {
        super(vc);
    }

    @Override
    public T deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        return null;
    }
}
