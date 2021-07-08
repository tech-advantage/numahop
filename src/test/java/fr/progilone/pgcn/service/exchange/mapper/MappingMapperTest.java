package fr.progilone.pgcn.service.exchange.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import fr.progilone.pgcn.domain.dto.exchange.MappingDTO;
import fr.progilone.pgcn.domain.exchange.Mapping;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.service.library.mapper.SimpleLibraryMapper;

/**
 * Created by Sebastien on 07/12/2016.
 */
public class MappingMapperTest {

    private MappingMapper mapper;

    @Before
    public void setUp() {
        mapper = MappingMapper.INSTANCE;
        ReflectionTestUtils.setField(mapper, "simpleLibraryMapper", SimpleLibraryMapper.INSTANCE);
    }

    @Test
    public void shouldMapMappingToSimpleMappingDTO() {
        final Library library = new Library();
        library.setIdentifier("LIB-001");

        final Mapping mapping = new Mapping();
        mapping.setIdentifier("MAPPING-001");
        mapping.setLabel("Mapping de test");
        mapping.setLibrary(library);
        mapping.setType(Mapping.Type.MARC);
        mapping.setVersion(10);

        final MappingDTO actual = mapper.mappingToDto(mapping);

        assertNotNull(actual);
        assertEquals(mapping.getIdentifier(), actual.getIdentifier());
    }
}
