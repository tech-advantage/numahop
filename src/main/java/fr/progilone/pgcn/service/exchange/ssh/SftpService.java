package fr.progilone.pgcn.service.exchange.ssh;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;
import java.util.Vector;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.HostKey;
import com.jcraft.jsch.HostKeyRepository;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import fr.progilone.pgcn.domain.administration.SftpConfiguration;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.util.CryptoService;

/**
 * Created by Sébastien on 28/12/2016.
 */
@Service
public class SftpService {

    private static final Logger LOG = LoggerFactory.getLogger(SftpService.class);

    private final CryptoService cryptoService;

    /**
     * Fichier contenant les RSA key fingerprint des serveurs sur lesquels se connecter<br/>
     * <ul>
     * <li>RSA key fingerprint: <code> ssh -oHostKeyAlgorithms='ssh-rsa' user@server</code></li>
     * <li>Noms des serveurs non hachés: fichier <code> .ssh/config</code>, propriété <code> HashKnownHosts no</code></li>
     * </ul>
     *
     * @see 'man sshd, rubrique SSH_KNOWN_HOSTS'
     */
    @Value("${export.ssh.knownHosts}")
    private String knownHosts;

    @Value("${export.sftp.privateKey}")
    private String privateKey;

    /**
     * Vérification de la clé du serveur yes / no<br/>
     * - yes (conseillé): les connexions aux serveurs absents du fichier knownHosts sont rejetées
     * - no:  toutes les connexions sont autorisées; si un fichier knownHosts est renseignés, il est alimenté automatiquement avec les nouvelles connexions
     */
    @Value("${export.ssh.strictHostKeyChecking}")
    private String strictHostKeyChecking;

    private final JSch jSch;

    @Autowired
    public SftpService(final CryptoService cryptoService) {
        this.cryptoService = cryptoService;
        this.jSch = new JSch();
    }

    /**
     * Initialisation de la configuration SSH
     */
    @PostConstruct
    public void init() {
        try {
            LOG.debug("Initialisation de JSch...");

            // Host keys
            if (StringUtils.isNoneBlank(knownHosts)) {
                jSch.setKnownHosts(knownHosts);

                if (LOG.isDebugEnabled()) {
                    logKnownHosts();
                }
            }
        } catch (final JSchException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * Initialisation d'une nouvelle connexion:
     * <ul>
     * <li>Tentative de connexion au serveur onfiguré</li>
     * <li>Ajout du fingerprint du serveur dans le fichier known_hosts</li>
     * </ul>
     *
     * @param conf
     * @return message d'erreur ou empty si tout s'est bien passé
     */
    public Optional<String> initConnection(final SftpConfiguration conf) {
        Session session = null;
        try {
            LOG.debug("Initialisation de la configuration SFTP {}", conf);
            session = openSession(conf, true);
            return Optional.empty();

        } catch (final JSchException | PgcnTechnicalException e) {
            LOG.error(e.getMessage());
            return Optional.of(e.getMessage());
        } finally {
            if (session != null) {
                session.disconnect();
            }
        }
    }

    /**
     * Recopie le fichier ou le répertoire (et son contenu) via SFTP
     *
     * @param localSource
     * @throws PgcnTechnicalException
     */
    public void sftpPut(final SftpConfiguration conf, final Path localSource) throws PgcnTechnicalException {
        Session session = null;
        ChannelSftp channelSftp = null;
        if (localSource != null) {
            try {
                session = openSession(conf);
                channelSftp = getChannelSftp(session);
                LOG.debug("Envoi de {} vers {}@{}:{}:{}",
                          localSource.toAbsolutePath().toString(),
                          session.getUserName(),
                          session.getHost(),
                          session.getPort(),
                          conf.getTargetDir());

                // Positionnement sur le répertoire cible
                channelSftp.cd(conf.getTargetDir());
                // Si le répertoire existe déjà, on le supprime
                final boolean pathExists = checkPath(localSource, channelSftp);
                if (pathExists) {
                    final Path fileName = localSource.getFileName();
                    if (fileName != null) {
                        LOG.warn("Le répertoire distant {} existe déjà, le transfert est annulé", fileName.toString());
                    } else {
                        LOG.warn("Le répertoire distant {} existe déjà, le transfert est annulé", localSource.toString());
                    }
                }
                // Transfert des fichiers récursivement
                else {
                    LOG.debug("Début du transfert vers le répertoire distant {}", channelSftp.pwd());
                    putPathRecursively(localSource.toFile(), channelSftp);
                }

            } catch (final JSchException | SftpException e) {
                throw new PgcnTechnicalException(e);
            } finally {
                LOG.debug("Transfert de {} terminé; fermeture de la connexion SFTP vers {}@{}:{}",
                          localSource.toAbsolutePath().toString(),
                          conf.getUsername(),
                          conf.getHost(),
                          conf.getPort());
                if (channelSftp != null) {
                    channelSftp.exit();
                }
                if (session != null) {
                    session.disconnect();
                }
            }
        }
    }

    /**
     * Vérifie l'existence du fichier / répertoire local dans le répertoire distant courant
     *
     * @param localSource
     * @param channelSftp
     * @return
     */
    private boolean checkPath(final Path localSource, final ChannelSftp channelSftp) throws SftpException {
        final Vector<ChannelSftp.LsEntry> ls = channelSftp.ls(".");
        return ls.stream().anyMatch(e -> StringUtils.equals(e.getFilename(), localSource.getFileName().toString()));
    }

    /**
     * Copie récursive d'un répertoire
     *
     * @param localSource
     *         fichier ou répertoire à transférer
     * @param channelSftp
     *         connexion SFTP établie
     */
    private void putPathRecursively(final File localSource, final ChannelSftp channelSftp) {
        final String targetName = localSource.getName();

        // Répertoire: création, ouverture et recopie du contenu
        if (localSource.isDirectory()) {
            try {
                LOG.debug("Création du répertoire distant {} ({})", targetName, channelSftp.pwd());
                // Création du répertoire distant
                channelSftp.mkdir(targetName);
                channelSftp.chmod(0770, targetName);    // permission en octal
                // Ouverture du répertoire distant
                channelSftp.cd(targetName);
                // Appel récursif sur le contenu du répertoire
                final String[] list = localSource.list();
                if (list != null) {
                    for (final String subSource : list) {
                        putPathRecursively(new File(localSource, subSource), channelSftp);
                    }
                }
                // On revient au répertoire précédent
                channelSftp.cd("..");

            } catch (final SftpException e) {
                LOG.error("Une erreur s'est produite lors du traitement du répertoire {}: {}", localSource.getAbsolutePath(), e.getMessage());
                LOG.error(e.getMessage(), e);
            }
        }
        // Fichier: transfert dans le répertoire distant courant
        else if (localSource.isFile()) {
            // Copie du fichier
            try (final InputStream in = new FileInputStream(localSource)) {
                LOG.debug("Envoi de {} vers {} ({})", localSource.getAbsolutePath(), targetName, channelSftp.pwd());
                channelSftp.put(in, targetName);
                channelSftp.chmod(0755, targetName);    // permission en octal

            } catch (final IOException | SftpException e) {
                LOG.error("Une erreur s'est produite lors de la copie du fichier {} vers {}: {}",
                          localSource.getAbsolutePath(),
                          targetName,
                          e.getMessage());
                LOG.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Ouverture d'une session SSH
     *
     * @return
     * @throws JSchException
     */
    private Session openSession(final SftpConfiguration conf, final boolean disableKeyCheck) throws JSchException, PgcnTechnicalException {
        LOG.debug("Ouverture d'une session distante sur {}@{}:{}, StrictHostKeyChecking = {}",
                  conf.getUsername(),
                  conf.getHost(),
                  conf.getPort(),
                  !disableKeyCheck);
        final Session session = jSch.getSession(conf.getUsername(), conf.getHost(), conf.getPort());
        if (conf.getPassword() != null) {
            session.setPassword(cryptoService.decrypt(conf.getPassword()));
        }

        // Private key
        jSch.addIdentity(privateKey);

        // StrictHostKeyChecking
        if (disableKeyCheck) {
            final Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
        }
        session.connect();
        return session;
    }

    /**
     * Ouverture d'une session SSH
     *
     * @param conf
     * @return
     * @throws JSchException
     * @throws PgcnTechnicalException
     */
    private Session openSession(final SftpConfiguration conf) throws JSchException, PgcnTechnicalException {
        final boolean disableKeyCheck = StringUtils.equals(strictHostKeyChecking, "no");
        return openSession(conf, disableKeyCheck);
    }

    /**
     * Ouverture d'une connexion SFTP
     *
     * @param session
     * @return
     * @throws JSchException
     */
    private ChannelSftp getChannelSftp(final Session session) throws JSchException {
        LOG.debug("Initialisation de la connexion SFTP pour {}@{}:{}", session.getUserName(), session.getHost(), session.getPort());
        final Channel channel = session.openChannel("sftp");
        channel.connect();
        return (ChannelSftp) channel;
    }

    /**
     * Log les hôtes récupérés du fichier known_hosts
     */
    private void logKnownHosts() {
        final HostKeyRepository hkr = jSch.getHostKeyRepository();
        final HostKey[] hks = hkr.getHostKey();

        if (hks != null) {
            final StringBuilder msg = new StringBuilder();
            msg.append("Host keys du fichier: ").append(hkr.getKnownHostsRepositoryID());

            for (final HostKey hk : hks) {
                msg.append('\n')
                   .append("\tHost: ")
                   .append(hk.getHost())
                   .append(", Type: ")
                   .append(hk.getType())
                   .append(", FingerPrint: ")
                   .append(hk.getFingerPrint(jSch));
            }
            LOG.debug(msg.toString());
        }
    }
}
