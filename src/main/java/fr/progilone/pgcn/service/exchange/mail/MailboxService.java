package fr.progilone.pgcn.service.exchange.mail;

import fr.progilone.pgcn.domain.administration.MailboxConfiguration;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.util.CryptoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.util.Properties;
import java.util.function.Consumer;

/**
 * Created by Sébastien on 03/01/2017.
 */
@Service
public class MailboxService {

    private static final Logger LOG = LoggerFactory.getLogger(MailboxService.class);

    private final CryptoService cryptoService;

    @Autowired
    public MailboxService(final CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    /**
     * Téléchargement des messages à partir de la configuration conf
     *
     * @param conf
     * @return
     * @throws PgcnTechnicalException
     */
    public void readMailbox(MailboxConfiguration conf, Consumer<Message[]> messageConsumer) throws PgcnTechnicalException {
        LOG.debug("Vérification des emails à partir de la configuration {}", conf);

        // Set configuration
        final Properties props = new Properties();
        conf.getProperties().forEach(p -> props.setProperty(p.getName(), p.getValue()));

        Store store = null;
        try {
            // Ouverture d'une session sur le serveur mail
            final Session session = Session.getDefaultInstance(props, null);
            store = session.getStore();
            store.connect(conf.getHost(), conf.getPort(), conf.getUsername(), cryptoService.decrypt(conf.getPassword()));

            // Ouverture du répertoire distant
            final Folder folder = conf.getInbox() != null ? store.getFolder(conf.getInbox()) : store.getDefaultFolder();
            LOG.debug("Ouverture du répertoire {}", folder.getURLName());
            folder.open(Folder.READ_WRITE);

            // Lecture des messages
            final Message[] messages;
            try {
                // messages = folder.getMessages();
                messages = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
                LOG.debug("{} nouveaux emails trouvés dans le répertoire {}", messages.length, conf.getInbox());

                final FetchProfile fp = new FetchProfile();
                fp.add(FetchProfile.Item.ENVELOPE);
                fp.add(FetchProfile.Item.CONTENT_INFO);
                folder.fetch(messages, fp);

                messageConsumer.accept(messages);

            } finally {
                folder.close(false);
            }

        } catch (MessagingException e) {
            throw new PgcnTechnicalException(e);
        } finally {
            if (store != null) {
                try {
                    store.close();
                } catch (MessagingException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }
}
