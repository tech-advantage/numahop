package fr.progilone.pgcn.web.rest.administration.z3950;

import fr.progilone.pgcn.domain.administration.exchange.z3950.Z3950Server;
import fr.progilone.pgcn.domain.dto.administration.z3950.Z3950ServerDTO;
import fr.progilone.pgcn.service.administration.z3950.Z3950ServerService;
import fr.progilone.pgcn.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class Z3950ServerControllerTest {

    @Mock
    private Z3950ServerService z3950ServerService;

    private MockMvc restZ3950ServerMockMvc;

    @Before
    public void setUp() throws Exception {
        final Z3950ServerController controller = new Z3950ServerController(z3950ServerService);
        this.restZ3950ServerMockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testCreate() throws Exception {
        final Z3950Server z3950Server = getZ3950Server("SERVER-001");
        when(z3950ServerService.save(any(Z3950Server.class))).thenReturn(z3950Server);
        this.restZ3950ServerMockMvc.perform(post("/api/rest/z3950Server").contentType(TestUtil.APPLICATION_JSON_UTF8)
                                                                         .content(TestUtil.convertObjectToJsonBytes(z3950Server)))
                                   .andExpect(status().isCreated())
                                   .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                                   .andExpect(jsonPath("identifier").value(z3950Server.getIdentifier()))
                                   .andExpect(jsonPath("name").value(z3950Server.getName()));

    }

    @Test
    public void getById() throws Exception {
        final Z3950Server z3950Server = getZ3950Server("ABCD-1234");
        when(z3950ServerService.findOne(z3950Server.getIdentifier())).thenReturn(z3950Server);

        this.restZ3950ServerMockMvc.perform(get("/api/rest/z3950Server/{id}", z3950Server.getIdentifier()).accept(TestUtil.APPLICATION_JSON_UTF8))
                                   .andExpect(status().isOk())
                                   .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                                   .andExpect(jsonPath("identifier").value(z3950Server.getIdentifier()));
    }

    @Test
    public void testGetById_notFound() throws Exception {
        final String identifier = "AAA";
        when(z3950ServerService.findOne(identifier)).thenReturn(null);

        this.restZ3950ServerMockMvc.perform(get("/api/rest/z3950Server/{identifier}", identifier).accept(TestUtil.APPLICATION_JSON_UTF8))
                                   .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdate() throws Exception {
        final Z3950Server z3950Server = getZ3950Server("ABCD-1234");

        // prepare update
        final String identifier = z3950Server.getIdentifier();
        final Z3950Server savedServer = getZ3950Server("ABCD-1234");
        savedServer.setName("New Name");

        when(z3950ServerService.save(z3950Server)).thenReturn(savedServer);

        // test update
        this.restZ3950ServerMockMvc.perform(post("/api/rest/z3950Server/" + identifier).contentType(TestUtil.APPLICATION_JSON_UTF8)
                                                                                       .content(TestUtil.convertObjectToJsonBytes(savedServer)))
                                   .andExpect(status().isOk())
                                   .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                                   .andExpect(jsonPath("identifier").value(savedServer.getIdentifier()))
                                   .andExpect(jsonPath("name").value(savedServer.getName()));
    }

    @Test
    public void testFindAll() throws Exception {
        final List<Z3950Server> servers = new ArrayList<>();
        servers.add(getZ3950Server("ABCD-1234"));
        when(z3950ServerService.findAll()).thenReturn(servers);

        this.restZ3950ServerMockMvc.perform(get("/api/rest/z3950Server").accept(TestUtil.APPLICATION_JSON_UTF8))
                                   .andExpect(status().isOk())
                                   .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                                   .andExpect(jsonPath("$[0].identifier").value(servers.get(0).getIdentifier()));
    }

    @Test
    public void testFindAllDTO() throws Exception {
        Z3950ServerDTO dto = new Z3950ServerDTO();
        dto.setIdentifier("ABCD-1234");
        final List<Z3950ServerDTO> servers = Collections.singletonList(dto);

        when(z3950ServerService.findAllDTO()).thenReturn(servers);

        this.restZ3950ServerMockMvc.perform(get("/api/rest/z3950Server").param("dto", "true")
                                                                        .accept(TestUtil.APPLICATION_JSON_UTF8))
                                   .andExpect(status().isOk())
                                   .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                                   .andExpect(jsonPath("$[0].identifier").value(servers.get(0).getIdentifier()));
    }

    @Test
    public void testDelete() throws Exception {
        final Z3950Server z3950Server = getZ3950Server("SERVER-001");
        final String identifier = z3950Server.getIdentifier();

        this.restZ3950ServerMockMvc.perform(delete("/api/rest/z3950Server/{id}", identifier).contentType(TestUtil.APPLICATION_JSON_UTF8))
                                   .andExpect(status().isOk());

        verify(z3950ServerService).delete(identifier);
    }

    private Z3950Server getZ3950Server(final String identifier) {
        final Z3950Server z3950Server = new Z3950Server();
        z3950Server.setIdentifier(identifier);
        z3950Server.setName("name");
        z3950Server.setVersion(0);
        return z3950Server;
    }

}
