package fr.progilone.pgcn.web.rest.administration;

import fr.progilone.pgcn.domain.administration.SftpConfiguration;
import fr.progilone.pgcn.domain.dto.administration.SftpConfigurationDTO;
import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.service.administration.CinesPACService;
import fr.progilone.pgcn.service.administration.SftpConfigurationService;
import fr.progilone.pgcn.service.exchange.ssh.SftpService;
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
import java.util.Optional;
import java.util.Set;

import static fr.progilone.pgcn.util.SecurityRequestPostProcessors.*;
import static fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by Sebastien on 30/12/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class SftpConfigurationControllerTest {

    @Mock
    private LibraryAccesssHelper libraryAccesssHelper;
    @Mock
    private SftpConfigurationService sftpConfigurationService;
    @Mock
    private SftpService sftpService;
    @Mock
    private CinesPACService cinesPACService;

    private MockMvc restMockMvc;

    private final RequestPostProcessor allPermissions = roles(SFTP_HAB0, SFTP_HAB1, SFTP_HAB2);

    @Before
    public void setUp() throws Exception {
        final SftpConfigurationController controller = new SftpConfigurationController(sftpConfigurationService, sftpService, libraryAccesssHelper, cinesPACService);

        final FormattingConversionService convService = new DefaultFormattingConversionService();
        convService.addConverter(String.class, Library.class, TestConverterFactory.getConverter(Library.class));
        this.restMockMvc = MockMvcBuilders.standaloneSetup(controller).setConversionService(convService).build();

        when(libraryAccesssHelper.checkLibrary(any(HttpServletRequest.class), any(), any(), anyVararg())).thenReturn(true);
        when(libraryAccesssHelper.checkLibrary(any(HttpServletRequest.class), any(Library.class))).thenReturn(true);
        when(libraryAccesssHelper.filterObjectsByLibrary(any(HttpServletRequest.class), anyCollectionOf(SftpConfiguration.class), any())).thenAnswer(
            new ReturnsArgumentAt(1));
    }

    @Test
    public void testCreate() throws Exception {
        final SftpConfiguration configurationSftp = getConfigurationSftp("ABCD-1234");
        when(sftpConfigurationService.save(any(SftpConfiguration.class))).thenReturn(configurationSftp);

        this.restMockMvc.perform(post("/api/rest/conf_sftp").contentType(TestUtil.APPLICATION_JSON_UTF8)
                                                            .content(TestUtil.convertObjectToJsonBytes(configurationSftp))
                                                            .with(allPermissions))
                        .andExpect(status().isCreated())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("identifier").value(configurationSftp.getIdentifier()))
                        .andExpect(jsonPath("label").value(configurationSftp.getLabel()))
                        .andExpect(jsonPath("library.identifier").value(configurationSftp.getLibrary().getIdentifier()));
    }

    @Test
    public void testDelete() throws Exception {
        final SftpConfiguration configurationSftp = getConfigurationSftp("ABCD-1235");
        final String identifier = configurationSftp.getIdentifier();

        when(sftpConfigurationService.findOne(configurationSftp.getIdentifier())).thenReturn(configurationSftp);

        // test delete
        this.restMockMvc.perform(delete("/api/rest/conf_sftp/{id}", identifier).contentType(TestUtil.APPLICATION_JSON_UTF8).with(allPermissions))
                        .andExpect(status().isOk());

        verify(sftpConfigurationService).delete(identifier);
    }

    @Test
    public void testFindAll() throws Exception {
        final Set<SftpConfigurationDTO> configurationSftps = new HashSet<>();
        configurationSftps.add(getSimpleMappingDto("ABCD-1236"));

        when(sftpConfigurationService.findAllDto(null)).thenReturn(configurationSftps);

        // test findAllActive
        this.restMockMvc.perform(get("/api/rest/conf_sftp").accept(TestUtil.APPLICATION_JSON_UTF8).with(allPermissions))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("$[0].identifier").value(configurationSftps.iterator().next().getIdentifier()));
    }

    @Test
    public void testFindByLibrary() throws Exception {
        final Set<SftpConfigurationDTO> stats = new HashSet<>();
        final SftpConfigurationDTO configurationSftp = getSimpleMappingDto("ABCD-1237");
        stats.add(configurationSftp);
        Library library = new Library();
        library.setIdentifier(configurationSftp.getLibrary().getIdentifier());

        when(sftpConfigurationService.findDtoByLibrary(library, null)).thenReturn(stats);

        // test findAllActive
        this.restMockMvc.perform(get("/api/rest/conf_sftp").param("library", configurationSftp.getLibrary().getIdentifier())
                                                           .accept(TestUtil.APPLICATION_JSON_UTF8)
                                                           .with(allPermissions))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("$[0].identifier").value(configurationSftp.getIdentifier()));
    }

    @Test
    public void getById() throws Exception {
        final SftpConfiguration configurationSftp = getConfigurationSftp("ABCD-1238");
        when(sftpConfigurationService.findOne(configurationSftp.getIdentifier())).thenReturn(configurationSftp) // ok
                                                                                 .thenReturn(null);             // ko

        // test ok
        this.restMockMvc.perform(get("/api/rest/conf_sftp/{id}", configurationSftp.getIdentifier()).accept(TestUtil.APPLICATION_JSON_UTF8)
                                                                                                   .with(allPermissions))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("identifier").value(configurationSftp.getIdentifier()));

        // test ko
        this.restMockMvc.perform(get("/api/rest/conf_sftp/{identifier}", configurationSftp.getIdentifier()).accept(TestUtil.APPLICATION_JSON_UTF8)
                                                                                                           .with(allPermissions))
                        .andExpect(status().isNotFound());
    }

    @Test
    public void testInitConf() throws Exception {
        final SftpConfiguration configurationSftp = getConfigurationSftp("ABCD-1239");
        final String message = "connection failed";

        when(sftpConfigurationService.findOne(configurationSftp.getIdentifier())).thenReturn(configurationSftp);
        when(sftpService.initConnection(configurationSftp)).thenReturn(Optional.empty())        // ok
                                                           .thenReturn(Optional.of(message));   // ko

        // Test ok
        this.restMockMvc.perform(get("/api/rest/conf_sftp/{id}", configurationSftp.getIdentifier()).param("init", "true")
                                                                                                   .accept(TestUtil.APPLICATION_JSON_UTF8)
                                                                                                   .with(allPermissions)).andExpect(status().isOk());

        // Test ko
        this.restMockMvc.perform(get("/api/rest/conf_sftp/{id}", configurationSftp.getIdentifier()).param("init", "true")
                                                                                                   .accept(TestUtil.APPLICATION_JSON_UTF8)
                                                                                                   .with(allPermissions))
                        .andExpect(status().isExpectationFailed())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("message").value(message));
    }

    @Test
    public void testUpdate() throws Exception {
        final SftpConfiguration configurationSftp = getConfigurationSftp("ABCD-1239");

        // prepare update
        final String identifier = configurationSftp.getIdentifier();
        final SftpConfiguration savedStat = getConfigurationSftp("ABCD-1239");
        savedStat.setLabel("New label");

        when(sftpConfigurationService.findOne(configurationSftp.getIdentifier())).thenReturn(configurationSftp);
        when(sftpConfigurationService.save(configurationSftp)).thenReturn(savedStat);

        // test update
        this.restMockMvc.perform(post("/api/rest/conf_sftp/" + identifier).contentType(TestUtil.APPLICATION_JSON_UTF8)
                                                                          .content(TestUtil.convertObjectToJsonBytes(configurationSftp))
                                                                          .with(allPermissions))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("identifier").value(savedStat.getIdentifier()))
                        .andExpect(jsonPath("label").value(savedStat.getLabel()))
                        .andExpect(jsonPath("library.identifier").value(savedStat.getLibrary().getIdentifier()));
    }

    private SftpConfiguration getConfigurationSftp(final String identifier) {
        final Library library = new Library();
        library.setIdentifier("LIBRARY-001");

        final SftpConfiguration configurationSftp = new SftpConfiguration();
        configurationSftp.setIdentifier(identifier);
        configurationSftp.setLabel("Chou-fleur");
        configurationSftp.setLibrary(library);
        return configurationSftp;
    }

    private SftpConfigurationDTO getSimpleMappingDto(final String identifier) {
        final SftpConfigurationDTO dto = new SftpConfigurationDTO();
        dto.setIdentifier(identifier);
        dto.setLabel("Chou-fleur");
        final SimpleLibraryDTO lib = new SimpleLibraryDTO.Builder().setIdentifier("LIBRARY-001").build();
        dto.setLibrary(lib);
        return dto;
    }
}
