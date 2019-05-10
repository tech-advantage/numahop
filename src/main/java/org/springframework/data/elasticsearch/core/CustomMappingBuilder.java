package org.springframework.data.elasticsearch.core;

import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;

/**
 * Petit bricolage pour pouvoir accéder à MappingBuilder.buildMapping qui est restreinte à son package...
 */
public class CustomMappingBuilder {

    private CustomMappingBuilder() {
    }

    public static XContentBuilder buildMapping(Class clazz, String indexType, String idFieldName, String parentType) throws IOException {
        return MappingBuilder.buildMapping(clazz, indexType, idFieldName, parentType);
    }
}
