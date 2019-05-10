package fr.progilone.pgcn.service.util.transaction;

import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

/**
 * Service permettant de gérer des transactions à la main. Deux types de transactions sont possibles :
 * <ul>
 * <li>Stateless : ce type est destiné aux imports de masse. Les entitées hibernate ne sont pas attachées à la session
 * (attention aux LazyInitialisationException !) ce qui permet de gagner un temps phénoménal dans les imports.</li>
 * <li>Normal : ce type est destiné à tous les autres cas...</li>
 * </ul>
 * Il est possible de mixer les transactions (par exemple démarrer une stateless, puis faire une normale pour un
 * traitement particulier et continuer la stateless ensuite)
 *
 * @author David
 */
@Service
public class TransactionService {

    private final PlatformTransactionManager txManager;
    private final LocalContainerEntityManagerFactoryBean entityManagerFactory;
    private final EntityManager entityManager;

    public TransactionService(final PlatformTransactionManager txManager,
                              final LocalContainerEntityManagerFactoryBean entityManagerFactory,
                              final EntityManager entityManager) {
        this.txManager = txManager;
        this.entityManagerFactory = entityManagerFactory;
        this.entityManager = entityManager;
    }

    public TransactionStatus startTransaction(final boolean readonly) {
        final DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        def.setReadOnly(readonly);
        return txManager.getTransaction(def);
    }

    public void commitTransaction(final TransactionStatus status) {
        txManager.commit(status);
        entityManager.clear();
    }

    public void rollbackTransaction(final TransactionStatus status) {
        if (!status.isCompleted()) {
            txManager.rollback(status);
        }
    }

    public void flushSession(final TransactionStatus status) {
        status.flush();
        entityManager.clear();
    }

    /**
     * Crée une nouvelle session stateless et démarre une nouvelle transaction. Attention à bien fermer la session après
     * l'avoir utilisée !
     *
     * @return StatelessSession
     */
    public StatelessSession startStatelessTransaction() {
        return startStatelessTransaction(null);
    }

    /**
     * Utilise la session passée en paramètre et démarre une nouvelle transaction. Si la session est <code>null</code>,
     * une nouvelle est créee. Attention à bien fermer la session après l'avoir utilisée !
     *
     * @param session
     * @return StatelessSession
     */
    public StatelessSession startStatelessTransaction(StatelessSession session) {
        if (session == null || !session.isOpen()) {
            session = ((SessionFactory) entityManagerFactory.getNativeEntityManagerFactory()).openStatelessSession();
        }
        session.beginTransaction();
        return session;
    }

    /**
     * Commite la transaction contenue dans la session.
     *
     * @param session
     */
    public void commitStatelessTransaction(final StatelessSession session) {
        session.getTransaction().commit();
    }

    /**
     * Rollback la transaction contenue dans la session.
     *
     * @param session
     */
    public void rollbackStatelessTransaction(final StatelessSession session) {
        if (session.getTransaction().getStatus() == org.hibernate.resource.transaction.spi.TransactionStatus.ACTIVE) {
            session.getTransaction().rollback();
        }
    }

    /**
     * Ferme la session
     *
     * @param session
     */
    public void closeStatelessSession(final StatelessSession session) {
        session.close();
    }

    /**
     * Exécute une méthode dans une nouvelle transaction
     */
    @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = Exception.class)
    public void executeInNewTransaction(final Runnable runnable) {
        runnable.run();
    }

    /**
     * Exécute une méthode dans une nouvelle transaction non readonly
     */
    @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = Exception.class)
    public <T> T executeInNewTransactionWithReturn(final RunnableWithReturn<T> runnable) {
        return runnable.run();
    }

    /**
     * Exécute une méthode dans une nouvelle transaction non readonly
     */
    @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = Exception.class)
    public <T> T executeInNewTransactionWithReturnAndException(final RunnableWithReturnAndException<T> runnable) throws Exception {
        return runnable.run();
    }

    /**
     * Exécute une méthode dans une nouvelle transaction asynchrone
     */
    @Async
    @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = Exception.class)
    public void executeInNewTransactionAsync(final Runnable runnable) {
        executeInNewTransaction(runnable);
    }

    @FunctionalInterface
    public interface RunnableWithReturn<T> {

        public abstract T run();
    }

    @FunctionalInterface
    public interface RunnableWithReturnAndException<T> {

        public abstract T run() throws Exception;
    }
}
