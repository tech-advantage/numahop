package fr.progilone.pgcn.web.rest.exchange;

import static fr.progilone.pgcn.util.SecurityRequestPostProcessors.roles;
import static fr.progilone.pgcn.web.rest.exchange.security.AuthorizationConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.progilone.pgcn.domain.dto.exchange.MappingDTO;
import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;
import fr.progilone.pgcn.domain.exchange.Mapping;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.service.exchange.ExchangeMappingService;
import fr.progilone.pgcn.service.exchange.MappingService;
import fr.progilone.pgcn.util.TestConverterFactory;
import fr.progilone.pgcn.util.TestUtil;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Created by Sebastien on 23/11/2016.
 */
@ExtendWith(MockitoExtension.class)
public class MappingControllerTest {

    @Mock
    private ExchangeMappingService exchangeMappingService;
    @Mock
    private MappingService mappingService;
    @Mock
    private LibraryAccesssHelper libraryAccesssHelper;

    private MockMvc restMockMvc;

    private final RequestPostProcessor allPermissions = roles(MAP_HAB0, MAP_HAB1, MAP_HAB2);

    @BeforeEach
    public void setUp() throws Exception {
        final MappingController controller = new MappingController(exchangeMappingService, mappingService, libraryAccesssHelper);

        final FormattingConversionService convService = new DefaultFormattingConversionService();
        convService.addConverter(String.class, Library.class, TestConverterFactory.getConverter(Library.class));
        this.restMockMvc = MockMvcBuilders.standaloneSetup(controller).setConversionService(convService).build();
    }

    @Test
    public void testCreate() throws Exception {
        final Mapping mapping = getMapping("ABCD-1234");
        when(mappingService.save(any(Mapping.class))).thenReturn(mapping);
        when(libraryAccesssHelper.checkLibrary(any(HttpServletRequest.class), any(), any(), any())).thenReturn(true);

        this.restMockMvc.perform(post("/api/rest/mapping").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(mapping)).with(allPermissions))
                        .andExpect(status().isCreated())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("identifier").value(mapping.getIdentifier()))
                        .andExpect(jsonPath("label").value(mapping.getLabel()))
                        .andExpect(jsonPath("library.identifier").value(mapping.getLibrary().getIdentifier()))
                        .andExpect(jsonPath("type").value(mapping.getType().name()));
    }

    @Test
    public void testDelete() throws Exception {
        final Mapping mapping = getMapping("ABCD-1235");
        final String identifier = mapping.getIdentifier();

        when(mappingService.findOne(mapping.getIdentifier())).thenReturn(mapping);
        when(libraryAccesssHelper.checkLibrary(any(HttpServletRequest.class), any(), any(), any())).thenReturn(true);

        // test delete
        this.restMockMvc.perform(delete("/api/rest/mapping/{id}", identifier).contentType(MediaType.APPLICATION_JSON).with(allPermissions)).andExpect(status().isOk());

        verify(mappingService).delete(identifier);
    }

    @Test
    public void testFindByType() throws Exception {
        final Set<MappingDTO> mappings = new HashSet<>();
        mappings.add(getSimpleMappingDto("ABCD-1236"));

        when(libraryAccesssHelper.filterObjectsByLibrary(any(HttpServletRequest.class), any(), any(), any())).thenAnswer(new ReturnsArgumentAt(1));

        when(mappingService.findByType(Mapping.Type.MARC)).thenReturn(mappings);

        // test findAllActive
        this.restMockMvc.perform(get("/api/rest/mapping").accept(MediaType.APPLICATION_JSON).param("type", "MARC").with(allPermissions))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$[0].identifier").value(mappings.iterator().next().getIdentifier()));
    }

    @Test
    @Disabled
    public void testFindByLibrary() throws Exception {
        final Set<MappingDTO> stats = new HashSet<>();
        final MappingDTO mapping = getSimpleMappingDto("ABCD-1237");
        stats.add(mapping);
        final Library library = new Library();
        library.setIdentifier(mapping.getLibrary().getIdentifier());

        when(mappingService.findByLibrary(library)).thenReturn(stats);

        // test findAllActive
        this.restMockMvc.perform(get("/api/rest/mapping").param("library", mapping.getLibrary().getIdentifier()).accept(MediaType.APPLICATION_JSON).with(allPermissions))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$[0].identifier").value(mapping.getIdentifier()));
    }

    @Test
    public void getById() throws Exception {
        final Mapping mapping = getMapping("ABCD-1238");
        when(mappingService.findOne(mapping.getIdentifier())).thenReturn(mapping);
        when(libraryAccesssHelper.checkLibrary(any(HttpServletRequest.class), any(), any(), any())).thenReturn(true);

        // test findAllActive
        this.restMockMvc.perform(get("/api/rest/mapping/{id}", mapping.getIdentifier()).accept(MediaType.APPLICATION_JSON).with(allPermissions))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("identifier").value(mapping.getIdentifier()));
    }

    @Test
    public void testGetById_notFound() throws Exception {
        final String identifier = "AAA";
        when(mappingService.findOne(identifier)).thenReturn(null);

        this.restMockMvc.perform(get("/api/rest/mapping/{identifier}", identifier).accept(MediaType.APPLICATION_JSON).with(allPermissions)).andExpect(status().isNotFound());
    }

    @Test
    public void testUpdate() throws Exception {
        final Mapping mapping = getMapping("ABCD-1239");

        // prepare update
        final String identifier = mapping.getIdentifier();
        final Mapping savedStat = getMapping("ABCD-1239");
        savedStat.setLabel("New label");

        when(mappingService.findOne(mapping.getIdentifier())).thenReturn(mapping);
        when(mappingService.save(mapping)).thenReturn(savedStat);
        when(libraryAccesssHelper.checkLibrary(any(HttpServletRequest.class), any(), any(), any())).thenReturn(true);

        // test update
        this.restMockMvc.perform(post("/api/rest/mapping/" + identifier).contentType(MediaType.APPLICATION_JSON)
                                                                        .content(TestUtil.convertObjectToJsonBytes(mapping))
                                                                        .with(allPermissions))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("identifier").value(savedStat.getIdentifier()))
                        .andExpect(jsonPath("label").value(savedStat.getLabel()))
                        .andExpect(jsonPath("library.identifier").value(savedStat.getLibrary().getIdentifier()))
                        .andExpect(jsonPath("type").value(savedStat.getType().name()));
    }

    private Mapping getMapping(final String identifier) {
        final Library library = new Library();
        library.setIdentifier("LIBRARY-001");

        final Mapping mapping = new Mapping();
        mapping.setIdentifier(identifier);
        mapping.setLabel("Chou-fleur");
        mapping.setLibrary(library);
        mapping.setType(Mapping.Type.MARC);
        return mapping;
    }

    private MappingDTO getSimpleMappingDto(final String identifier) {
        final MappingDTO dto = new MappingDTO();
        dto.setIdentifier(identifier);
        dto.setLabel("Chou-fleur");
        final SimpleLibraryDTO lib = new SimpleLibraryDTO.Builder().setIdentifier("LIBRARY-001").build();
        dto.setLibrary(lib);
        dto.setType(Mapping.Type.MARC.name());
        return dto;
    }

}
