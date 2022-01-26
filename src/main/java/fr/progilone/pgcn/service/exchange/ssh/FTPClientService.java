package fr.progilone.pgcn.service.exchange.ssh;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.progilone.pgcn.exception.PgcnTechnicalException;

/**
 * Service de connexion FTP
 */

@Service
public class FTPClientService {

    private static final Logger LOG = LoggerFactory.getLogger(FTPClientService.class);

    @Autowired
    public FTPClientService() {

    }

    /**
     * Recopie le fichier ou le répertoire (et son contenu) via FTP
     *
     * @param localSource
     *            fichier ou dossier à copier
     * @throws PgcnTechnicalException
     */
    public void ftpPut(final String adress,
                       final String port,
                       final String login,
                       final String password,
                       final String targetDir,
                       final Path localSource) throws PgcnTechnicalException, IOException {
        final FTPSClient ftpClient = new FTPSClient("TLS");

        if (localSource != null && Files.exists(localSource)) {
            try {
                ftpClient.connect(adress, Integer.parseInt(port));
                LOG.debug("Initialisation de la connexion FTP pour {}@{}:{}", login, adress, port);
                final boolean success = ftpClient.login(login, password);

                if (!success) {
                    LOG.debug("Echec de la connexion FTP pour {}@{}:{}", login, adress, port);
                    throw new PgcnTechnicalException();
                }

                // PAssive mode
                ftpClient.enterLocalPassiveMode();
                // binary type
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                // Set protection buffer size
                ftpClient.execPBSZ(0);
                // Set data channel protection to private
                ftpClient.execPROT("P");

                LOG.debug("Envoi de {} vers {}@{}:{}:{}",
                          localSource.toAbsolutePath().toString(),
                          login,
                          adress,
                          port,
                          targetDir);

                // Positionnement sur le répertoire cible
                ftpClient.changeWorkingDirectory(targetDir);

                LOG.debug("Début du transfert vers le répertoire distant {}", targetDir);
                putPathRecursively(localSource.toFile(), ftpClient);

            } catch (final IOException e) {
                LOG.error("Echec de la connexion FTP pour {}@{}:{}", login, adress, port);
                throw new PgcnTechnicalException(e);
            } finally {
                LOG.debug("Fin du transfert vers le répertoire distant {}", targetDir);
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } else {
            LOG.error("Le fichier n'existe pas - Le transfert n'a pas pu être effectué");
            throw new PgcnTechnicalException();
        }
    }

    /**
     * Copie récursive d'un répertoire
     *
     */
    private void putPathRecursively(final File localSource, final FTPSClient ftpclient) {
        final String targetName = localSource.getName();

        // Répertoire: création, ouverture et recopie du contenu
        if (localSource.isDirectory()) {
            try {
                LOG.debug("Création du répertoire distant {}", targetName);
                // Création du répertoire distant
                ftpclient.makeDirectory(targetName);
                ftpclient.changeWorkingDirectory(targetName);
                // Appel récursif sur le contenu du répertoire
                final String[] list = localSource.list();
                if (list != null) {
                    for (final String subSource : list) {
                        putPathRecursively(new File(localSource, subSource), ftpclient);
                    }
                }
                // On revient au répertoire précédent
                ftpclient.changeToParentDirectory();

            } catch (final IOException e) {
                LOG.error("Une erreur s'est produite lors du traitement du répertoire {}: {}", localSource.getAbsolutePath(), e.getMessage());
                LOG.error(e.getMessage(), e);
            }
        }
        // Fichier: transfert dans le répertoire distant courant
        else if (localSource.isFile()) {
            // Copie du fichier
            try (final FileInputStream in = new FileInputStream(localSource)) {
                LOG.trace("Envoi de {} vers {}", localSource.getAbsolutePath(), targetName);

                // Envoi du fichier
                ftpclient.storeFile(targetName, in);
                // Log reponse de storeFile
                LOG.trace(ftpclient.getReplyString());

                // Changement des droits
                ftpclient.sendSiteCommand("chmod " + "755 " + targetName);

            } catch (final IOException e) {
                LOG.error("Une erreur s'est produite lors de la copie du fichier {} vers {}",
                          localSource.getAbsolutePath(),
                          targetName);
                LOG.error(e.getMessage(), e);
            }
        }
    }
}
