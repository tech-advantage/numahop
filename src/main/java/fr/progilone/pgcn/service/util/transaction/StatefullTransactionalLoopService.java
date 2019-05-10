package fr.progilone.pgcn.service.util.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import java.util.List;

/**
 * Service permettant d'exécuter une tâche <code>callback</code> sur une liste d'entitées <code>entities</code> dans une transaction commitée tous
 * les <code>nbCommit</code>
 * <p>
 * La session utilisée est statefull
 *
 * @author David
 */
@Service
public class StatefullTransactionalLoopService {

    private static final Logger LOG = LoggerFactory.getLogger(StatefullTransactionalLoopService.class);

    private final TransactionService transactionService;

    @Autowired
    public StatefullTransactionalLoopService(final TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Async
    public <T> void loopAsync(final List<T> entities, final long nbCommit, final boolean readonly, final LoopCallback<T> c) {
        loop(entities, nbCommit, readonly, c);
    }

    public <T> void loop(final List<T> entities, final long nbCommit, final boolean readonly, final LoopCallback<T> c) {
        TransactionStatus status = transactionService.startTransaction(readonly);
        try {
            int nb = 0;
            for (final T e : entities) {
                c.doLoop(e);
                status = saveIfNeeded(++nb, nbCommit, status, readonly);
            }
            transactionService.commitTransaction(status);
        } catch (final RuntimeException e) {
            transactionService.rollbackTransaction(status);
            throw e;
        }
    }

    private TransactionStatus saveIfNeeded(final long nb, final long nbCommit, final TransactionStatus status, final boolean readonly) {
        if (nb % nbCommit == 0) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("{} traités...", nb);
            }
            transactionService.commitTransaction(status);
            return transactionService.startTransaction(readonly);
        }
        return status;
    }

    public interface LoopCallback<T> {
        void doLoop(T entity);
    }

}
