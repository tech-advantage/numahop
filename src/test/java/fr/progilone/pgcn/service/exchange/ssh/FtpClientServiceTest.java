package fr.progilone.pgcn.service.exchange.ssh;

import static org.junit.jupiter.api.Assertions.assertThrows;

import fr.progilone.pgcn.exception.PgcnTechnicalException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@Disabled
@ExtendWith(MockitoExtension.class)
public class FtpClientServiceTest {

    private FTPClientService service;

    @BeforeEach
    public void setUp() throws PgcnTechnicalException {
        service = new FTPClientService();
    }

    /**
     * Envoi d'un dossier
     *
     * @throws PgcnTechnicalException
     */
    @Test
    public void testFtpPut() throws PgcnTechnicalException, IOException {
        final Path source = Paths.get("D:\\Projets\\PGCN\\Exemples");

        service.ftpPut("ftp.progilone.fr", "21", "gjehanno", "", "gjehanno", source);
    }

    /**
     * Echec de connection
     */
    @Test
    public void testFtpPutFailConnection() throws PgcnTechnicalException, IOException {
        assertThrows(PgcnTechnicalException.class, () -> {
            final Path source = Paths.get("D:\\Projets\\PGCN\\Exemples");
            service.ftpPut("ftp.progilone.fr", "21", "gjehanno", "", "gjehanno", source);
        });
    }

    /**
     * Envoi d'un dossier qui n'existe pas
     * Doit renvoyer une exception
     */
    @Test
    public void testFtpPutDoesNotExist() throws PgcnTechnicalException, IOException {
        assertThrows(PgcnTechnicalException.class, () -> {
            final Path source = Paths.get("D:\\Projets\\PGCN\\DoesNotExist");
            service.ftpPut("ftp.progilone.fr", "21", "gjehanno", "", "gjehanno", source);
        });
    }
}
