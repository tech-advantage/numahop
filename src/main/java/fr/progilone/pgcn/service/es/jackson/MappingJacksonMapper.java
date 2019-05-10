package fr.progilone.pgcn.service.es.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.progilone.pgcn.domain.exchange.Mapping;
import fr.progilone.pgcn.domain.exchange.MappingRule;

import java.io.IOException;

/**
 * Mapper json pour les mappings et règles de mapping
 */
public class MappingJacksonMapper {

    private final ObjectMapper objectMapper;

    public MappingJacksonMapper() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID, true);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);    // 1970-01-01T00:00:00.000+0000

        final SimpleModule module = new SimpleModule();
        module.addSerializer(Mapping.class, new MappingSerializer());
        module.addSerializer(MappingRule.class, new MappingRuleSerializer());
        module.addDeserializer(MappingRule.class, new MappingRuleDeserializer());

        objectMapper.registerModule(module)
                    // LocalDate, ...
                    .registerModule(new JavaTimeModule());
    }

    /**
     * Mapping Mapping -> String
     *
     * @param mapping
     * @return
     * @throws IOException
     */
    public String mapToString(final Mapping mapping) throws IOException {
        return objectMapper.writeValueAsString(mapping);
    }

    /**
     * Mapping String -> Mapping
     *
     * @param source
     * @return
     * @throws IOException
     */
    public Mapping mapToMapping(final String source) throws IOException {
        return objectMapper.readValue(source, Mapping.class);
    }
}
