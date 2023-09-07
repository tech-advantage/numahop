package fr.progilone.pgcn.service.administration.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.progilone.pgcn.domain.administration.SftpConfiguration;
import fr.progilone.pgcn.domain.dto.administration.SftpConfigurationDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.service.library.mapper.SimpleLibraryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Created by Sebastien on 07/12/2016.
 */
public class SftpConfigurationMapperTest {

    private SftpConfigurationMapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = SftpConfigurationMapper.INSTANCE;
        ReflectionTestUtils.setField(mapper, "simpleLibraryMapper", SimpleLibraryMapper.INSTANCE);
    }

    @Test
    public void shouldConfigurationSftpToDto() {
        final Library library = new Library();
        library.setIdentifier("LIB-001");

        final SftpConfiguration mapping = new SftpConfiguration();
        mapping.setIdentifier("MAPPING-001");
        mapping.setLabel("Mapping de test");
        mapping.setLibrary(library);
        mapping.setVersion(10);

        final SftpConfigurationDTO actual = mapper.configurationSftpToDto(mapping);

        assertNotNull(actual);
        assertEquals(mapping.getIdentifier(), actual.getIdentifier());
    }
}
