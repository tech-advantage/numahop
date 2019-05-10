package fr.progilone.pgcn.service.administration;

import fr.progilone.pgcn.domain.administration.MailboxConfiguration;
import fr.progilone.pgcn.domain.dto.administration.MailboxConfigurationDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.repository.administration.MailboxConfigurationRepository;
import fr.progilone.pgcn.service.administration.mapper.MailboxConfigurationMapper;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
public class MailboxConfigurationServiceTest {

    @Mock
    private MailboxConfigurationRepository mailboxConfigurationRepository;
    @Mock
    private CryptoService cryptoService;

    private MailboxConfigurationService service;

    @Before
    public void setUp() {
        final MailboxConfigurationMapper mapper = MailboxConfigurationMapper.INSTANCE;
        ReflectionTestUtils.setField(mapper, "libraryMapper", LibraryMapper.INSTANCE);
        service = new MailboxConfigurationService(mailboxConfigurationRepository, cryptoService);
    }

    @Test
    public void testFindAll() {
        final Set<MailboxConfiguration> mailboxConfigurations = new HashSet<>();
        final String identifier = "M001";
        mailboxConfigurations.add(getMailConfiguration(identifier));

        when(mailboxConfigurationRepository.findAllWithDependencies()).thenReturn(mailboxConfigurations);

        final List<MailboxConfigurationDTO> actual = service.findAllDto(null);
        assertEquals(1, actual.size());
        assertEquals(identifier, actual.iterator().next().getIdentifier());
    }

    @Test
    public void testSearch() {
        final List<String> libraries = Collections.singletonList("new Library()");
        final List<MailboxConfiguration> mailboxConfigurations = new ArrayList<>();
        final String identifier = "M002";
        mailboxConfigurations.add(getMailConfiguration(identifier));

        when(mailboxConfigurationRepository.search("test", libraries, true)).thenReturn(mailboxConfigurations);

        final List<MailboxConfigurationDTO> actual = service.search("test", libraries, true);
        assertEquals(1, actual.size());
        assertEquals(identifier, actual.iterator().next().getIdentifier());
    }

    @Test
    public void testFindOne() {
        final String id = "MailConfiguration-001";
        final MailboxConfiguration mailboxConfiguration = getMailConfiguration(id);

        when(mailboxConfigurationRepository.findOneWithDependencies(id)).thenReturn(mailboxConfiguration);

        final MailboxConfiguration actual = service.findOne(id);
        assertSame(mailboxConfiguration, actual);
    }

    @Test
    public void testDelete() {
        final String id = "MailConfiguration-001";
        service.delete(id);
        verify(mailboxConfigurationRepository).delete(id);
    }

    @Test
    public void testSave() throws PgcnTechnicalException {
        final MailboxConfiguration mailboxConfiguration = new MailboxConfiguration();
        mailboxConfiguration.setIdentifier("MailConfiguration-001");
        when(mailboxConfigurationRepository.save(any(MailboxConfiguration.class))).then(new ReturnsArgumentAt(0));
        when(mailboxConfigurationRepository.findOneWithDependencies("MailConfiguration-001")).thenReturn(mailboxConfiguration);

        // #1: validation failed
        try {
            service.save(mailboxConfiguration);
            fail("test Save should have failed !");
        } catch (PgcnTechnicalException e) {
            fail("test Save should have failed with PgcnTechnicalException !");
        } catch (PgcnValidationException e) {
            TestUtil.checkPgcnException(e, CONF_SFTP_LABEL_MANDATORY, CONF_SFTP_LIBRARY_MANDATORY);
        }

        // #2 validation ok
        mailboxConfiguration.setLabel("MailConfiguration des monographies");
        final Library lib = new Library();
        lib.setIdentifier("LIB-001");
        mailboxConfiguration.setLibrary(lib);
        MailboxConfiguration actual = service.save(mailboxConfiguration);

        assertEquals(mailboxConfiguration.getIdentifier(), actual.getIdentifier());
        assertNotNull(actual.getLabel());
    }

    private MailboxConfiguration getMailConfiguration(final String identifier) {
        final Library library = new Library();
        library.setIdentifier("LIBRARY-001");

        final MailboxConfiguration mailboxConfiguration = new MailboxConfiguration();
        mailboxConfiguration.setIdentifier(identifier);
        mailboxConfiguration.setLabel("Chou-fleur");
        mailboxConfiguration.setLibrary(library);
        return mailboxConfiguration;
    }
}
