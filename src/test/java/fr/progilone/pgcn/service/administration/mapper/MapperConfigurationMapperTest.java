package fr.progilone.pgcn.service.administration.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.progilone.pgcn.domain.administration.MailboxConfiguration;
import fr.progilone.pgcn.domain.dto.administration.MailboxConfigurationDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.service.library.mapper.SimpleLibraryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Created by Sebastien on 07/12/2016.
 */
public class MapperConfigurationMapperTest {

    private MailboxConfigurationMapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = MailboxConfigurationMapper.INSTANCE;
        ReflectionTestUtils.setField(mapper, "simpleLibraryMapper", SimpleLibraryMapper.INSTANCE);
    }

    @Test
    public void shouldConfigurationSftpToDto() {
        final Library library = new Library();
        library.setIdentifier("LIB-001");

        final MailboxConfiguration conf = new MailboxConfiguration();
        conf.setIdentifier("CONF-001");
        conf.setLabel("Config de test");
        conf.setLibrary(library);
        conf.setVersion(10);

        conf.addProperty("mail.store.protocol", "imaps");
        conf.addProperty("mail.imaps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        conf.addProperty("mail.imaps.socketFactory.fallback", "false");

        final MailboxConfigurationDTO actual = mapper.mailboxToDto(conf);

        assertNotNull(actual);
        assertEquals(conf.getIdentifier(), actual.getIdentifier());
        assertEquals(3, actual.getProperties().size());
    }
}
