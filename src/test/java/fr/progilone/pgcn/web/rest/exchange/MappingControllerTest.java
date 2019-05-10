package fr.progilone.pgcn.web.rest.exchange;

import fr.progilone.pgcn.domain.dto.exchange.MappingDTO;
import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;
import fr.progilone.pgcn.domain.exchange.Mapping;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.service.exchange.ExchangeMappingService;
import fr.progilone.pgcn.service.exchange.MappingService;
import fr.progilone.pgcn.util.TestConverterFactory;
import fr.progilone.pgcn.util.TestUtil;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

import static fr.progilone.pgcn.util.SecurityRequestPostProcessors.*;
import static fr.progilone.pgcn.web.rest.exchange.security.AuthorizationConstants.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by Sebastien on 23/11/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class MappingControllerTest {

    @Mock
    private ExchangeMappingService exchangeMappingService;
    @Mock
    private MappingService mappingService;
    @Mock
    private LibraryAccesssHelper libraryAccesssHelper;

    private MockMvc restMockMvc;

    private final RequestPostProcessor allPermissions = roles(MAP_HAB0, MAP_HAB1, MAP_HAB2);

    @Before
    public void setUp() throws Exception {
        final MappingController controller = new MappingController(exchangeMappingService, mappingService, libraryAccesssHelper);

        final FormattingConversionService convService = new DefaultFormattingConversionService();
        convService.addConverter(String.class, Library.class, TestConverterFactory.getConverter(Library.class));
        this.restMockMvc = MockMvcBuilders.standaloneSetup(controller).setConversionService(convService).build();

        when(libraryAccesssHelper.checkLibrary(any(HttpServletRequest.class), any(), any(), anyVararg())).thenReturn(true);
        when(libraryAccesssHelper.checkLibrary(any(HttpServletRequest.class), any(Library.class), anyString())).thenReturn(true);
        when(libraryAccesssHelper.filterObjectsByLibrary(any(HttpServletRequest.class),
                                                         anyCollectionOf(MappingDTO.class),
                                                         any(),
                                                         anyVararg())).thenAnswer(new ReturnsArgumentAt(1));
    }

    @Test
    public void testCreate() throws Exception {
        final Mapping mapping = getMapping("ABCD-1234");
        when(mappingService.save(any(Mapping.class))).thenReturn(mapping);

        this.restMockMvc.perform(post("/api/rest/mapping").contentType(TestUtil.APPLICATION_JSON_UTF8)
                                                          .content(TestUtil.convertObjectToJsonBytes(mapping))
                                                          .with(allPermissions))
                        .andExpect(status().isCreated())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
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

        // test delete
        this.restMockMvc.perform(delete("/api/rest/mapping/{id}", identifier).contentType(TestUtil.APPLICATION_JSON_UTF8).with(allPermissions))
                        .andExpect(status().isOk());

        verify(mappingService).delete(identifier);
    }

    @Test
    public void testFindByType() throws Exception {
        final Set<MappingDTO> mappings = new HashSet<>();
        mappings.add(getSimpleMappingDto("ABCD-1236"));

        when(mappingService.findByType(Mapping.Type.MARC)).thenReturn(mappings);

        // test findAllActive
        this.restMockMvc.perform(get("/api/rest/mapping").accept(TestUtil.APPLICATION_JSON_UTF8).param("type", "MARC").with(allPermissions))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("$[0].identifier").value(mappings.iterator().next().getIdentifier()));
    }

    @Test
    public void testFindByLibrary() throws Exception {
        final Set<MappingDTO> stats = new HashSet<>();
        final MappingDTO mapping = getSimpleMappingDto("ABCD-1237");
        stats.add(mapping);
        Library library = new Library();
        library.setIdentifier(mapping.getLibrary().getIdentifier());

        when(mappingService.findByLibrary(library)).thenReturn(stats);

        // test findAllActive
        this.restMockMvc.perform(get("/api/rest/mapping").param("library", mapping.getLibrary().getIdentifier())
                                                         .accept(TestUtil.APPLICATION_JSON_UTF8)
                                                         .with(allPermissions))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("$[0].identifier").value(mapping.getIdentifier()));
    }

    @Test
    public void getById() throws Exception {
        final Mapping mapping = getMapping("ABCD-1238");
        when(mappingService.findOne(mapping.getIdentifier())).thenReturn(mapping);

        // test findAllActive
        this.restMockMvc.perform(get("/api/rest/mapping/{id}", mapping.getIdentifier()).accept(TestUtil.APPLICATION_JSON_UTF8).with(allPermissions))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("identifier").value(mapping.getIdentifier()));
    }

    @Test
    public void testGetById_notFound() throws Exception {
        final String identifier = "AAA";
        when(mappingService.findOne(identifier)).thenReturn(null);

        this.restMockMvc.perform(get("/api/rest/mapping/{identifier}", identifier).accept(TestUtil.APPLICATION_JSON_UTF8).with(allPermissions))
                        .andExpect(status().isNotFound());
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

        // test update
        this.restMockMvc.perform(post("/api/rest/mapping/" + identifier).contentType(TestUtil.APPLICATION_JSON_UTF8)
                                                                        .content(TestUtil.convertObjectToJsonBytes(mapping))
                                                                        .with(allPermissions))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
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
