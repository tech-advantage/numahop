package fr.progilone.pgcn.service.administration.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.progilone.pgcn.domain.administration.exchange.z3950.Z3950Server;
import fr.progilone.pgcn.domain.dto.administration.z3950.Z3950ServerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Created by Sebastien on 07/12/2016.
 */
public class Z3950ServerMapperTest {

    private Z3950ServerMapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = Z3950ServerMapper.INSTANCE;
    }

    @Test
    public void shouldMapZ3950ServerToDto() {
        final Z3950Server server = new Z3950Server();
        server.setIdentifier("Z3950-001");
        server.setName("Z3950 de test");
        server.setActive(true);
        server.setVersion(10L);

        final Z3950ServerDTO actual = mapper.z3950ServerToDto(server);

        assertNotNull(actual);
        assertEquals(server.getIdentifier(), actual.getIdentifier());
        assertEquals(server.getName(), actual.getName());
        assertTrue(actual.isActive());
        assertEquals(10L, actual.getVersion());
    }
}
