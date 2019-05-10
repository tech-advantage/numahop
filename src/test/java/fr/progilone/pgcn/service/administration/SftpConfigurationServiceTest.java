package fr.progilone.pgcn.service.administration;

import fr.progilone.pgcn.domain.administration.SftpConfiguration;
import fr.progilone.pgcn.domain.dto.administration.SftpConfigurationDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.repository.administration.SftpConfigurationRepository;
import fr.progilone.pgcn.service.administration.mapper.SftpConfigurationMapper;
import fr.progilone.pgcn.service.library.mapper.LibraryMapper;
import fr.progilone.pgcn.service.util.CryptoService;
import fr.progilone.pgcn.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.Set;

import static fr.progilone.pgcn.exception.message.PgcnErrorCode.CONF_SFTP_LABEL_MANDATORY;
import static fr.progilone.pgcn.exception.message.PgcnErrorCode.CONF_SFTP_LIBRARY_MANDATORY;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Sebastien on 30/12/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class SftpConfigurationServiceTest {

    @Mock
    private SftpConfigurationRepository sftpConfigurationRepository;
    @Mock
    private CryptoService cryptoService;

    private SftpConfigurationService service;

    @Before
    public void setUp() {
        final SftpConfigurationMapper mapper = SftpConfigurationMapper.INSTANCE;
        ReflectionTestUtils.setField(mapper, "libraryMapper", LibraryMapper.INSTANCE);
        service = new SftpConfigurationService(sftpConfigurationRepository, cryptoService);
    }

    @Test
    public void testFindAll() {
        final Set<SftpConfiguration> configurationSftps = new HashSet<>();
        final String identifier = "M001";
        configurationSftps.add(getConfigurationSftp(identifier));

        when(sftpConfigurationRepository.findAllWithDependencies()).thenReturn(configurationSftps);

        final Set<SftpConfigurationDTO> actual = service.findAllDto(null);
        assertEquals(1, actual.size());
        assertEquals(identifier, actual.iterator().next().getIdentifier());
    }

    @Test
    public void testFindByLibrary() {
        final Library library = new Library();
        final Set<SftpConfiguration> configurationSftps = new HashSet<>();
        final String identifier = "M002";
        configurationSftps.add(getConfigurationSftp(identifier));

        when(sftpConfigurationRepository.findByLibrary(library)).thenReturn(configurationSftps);

        final Set<SftpConfigurationDTO> actual = service.findDtoByLibrary(library, null);
        assertEquals(1, actual.size());
        assertEquals(identifier, actual.iterator().next().getIdentifier());
    }

    @Test
    public void testFindOne() {
        final String id = "ConfigurationSftp-001";
        final SftpConfiguration configurationSftp = getConfigurationSftp(id);

        when(sftpConfigurationRepository.findOneWithDependencies(id)).thenReturn(configurationSftp);

        final SftpConfiguration actual = service.findOne(id);
        assertSame(configurationSftp, actual);
    }

    @Test
    public void testDelete() {
        final String id = "ConfigurationSftp-001";
        service.delete(id);
        verify(sftpConfigurationRepository).delete(id);
    }

    @Test
    public void testSave() throws PgcnTechnicalException {
        final SftpConfiguration configurationSftp = new SftpConfiguration();
        configurationSftp.setIdentifier("ConfigurationSftp-001");
        when(sftpConfigurationRepository.save(any(SftpConfiguration.class))).then(new ReturnsArgumentAt(0));
        when(sftpConfigurationRepository.findOneWithDependencies("ConfigurationSftp-001")).thenReturn(configurationSftp);

        // #1: validation failed
        try {
            service.save(configurationSftp);
            fail("test Save should have failed !");
        } catch (PgcnTechnicalException e) {
            fail("test Save should have failed with PgcnTechnicalException !");
        } catch (PgcnValidationException e) {
            TestUtil.checkPgcnException(e, CONF_SFTP_LABEL_MANDATORY, CONF_SFTP_LIBRARY_MANDATORY);
        }

        // #2 validation ok
        configurationSftp.setLabel("ConfigurationSftp des monographies");
        final Library lib = new Library();
        lib.setIdentifier("LIB-001");
        configurationSftp.setLibrary(lib);
        SftpConfiguration actual = service.save(configurationSftp);

        assertEquals(configurationSftp.getIdentifier(), actual.getIdentifier());
        assertNotNull(actual.getLabel());
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
}
