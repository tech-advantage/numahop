package fr.progilone.pgcn.service.administration.z3950;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Sort;

import fr.progilone.pgcn.domain.administration.exchange.z3950.Z3950Server;
import fr.progilone.pgcn.domain.dto.administration.z3950.Z3950ServerDTO;
import fr.progilone.pgcn.repository.administration.z3950.Z3950ServerRepository;

@RunWith(MockitoJUnitRunner.class)
public class Z3950ServerServiceTest {

    @Mock
    private Z3950ServerRepository z3950ServerRepository;

    private Z3950ServerService service;

    @Before
    public void setUp() {
        service = new Z3950ServerService(z3950ServerRepository);
    }

    @Test
    public void testDelete() {
        final String identifier = "SERVER-001";
        service.delete(identifier);
        verify(z3950ServerRepository).delete(identifier);
    }

    @Test
    public void testFindOne() {
        final Z3950Server z3950Server = new Z3950Server();
        final String identifier = "123";
        when(z3950ServerRepository.findOne(identifier)).thenReturn(z3950Server);

        final Z3950Server actual = service.findOne(identifier);
        assertSame(z3950Server, actual);
    }

    @Test
    public void testFindAllDTO() {
        final Z3950Server server = new Z3950Server();
        server.setIdentifier("identifier");
        server.setName("name");
        when(z3950ServerRepository.findAll(any(Sort.class))).thenReturn(Collections.singletonList(server));

        final List<Z3950ServerDTO> actual = service.findAllDTO();
        assertEquals(1, actual.size());
        final Z3950ServerDTO dto = actual.get(0);
        assertEquals(server.getIdentifier(), dto.getIdentifier());
        assertEquals(server.getName(), dto.getName());
    }

    @Test
    public void testFindAll() {
        final List<Z3950Server> servers = new ArrayList<>();
        final Z3950Server server = new Z3950Server();
        server.setIdentifier("identifier");
        server.setName("name");
        servers.add(server);
        when(z3950ServerRepository.findAll()).thenReturn(servers);

        final List<Z3950Server> actual = service.findAll();
        assertSame(servers, actual);
    }
}
