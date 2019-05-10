package fr.progilone.pgcn.service.util.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionStatus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Cette classe parcourt un iterateur, effectue un job pour chaque élément, et committe sa transaction tous les 100 éléments.
 * On peut suivre la progression du job grâce à progressJob.
 */
public class TransactionalJobRunner<T> {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionalJobRunner.class);

    private final TransactionService transactionService;

    private long commit = 100L; // Committe tous les x éléments
    private int maxThreads = 1; // spécifie le nombre de thread utilisés par le traitement

    /**
     * Job à exécuter
     */
    private Predicate<T> job;
    /**
     * Permet de suivre la progression du job
     */
    private Consumer<Long> progressJob = lg -> {
    };

    public TransactionalJobRunner(final TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public TransactionalJobRunner<T> setCommit(final long commit) {
        this.commit = commit;
        return this;
    }

    public TransactionalJobRunner<T> setMaxThreads(final int maxThreads) {
        this.maxThreads = maxThreads;
        return this;
    }

    /**
     * Traitement d'une notice
     *
     * @param job le job retourne true si il est réalisé avec succès
     * @return
     */
    public TransactionalJobRunner<T> forEach(final Predicate<T> job) {
        this.job = job;
        return this;
    }

    /**
     * Consumer permettant de suivre la progression du job
     *
     * @param progressJob
     * @return
     */
    public TransactionalJobRunner<T> onProgress(final Consumer<Long> progressJob) {
        this.progressJob = progressJob;
        return this;
    }

    /**
     * Traitement de l'itérateur
     *
     * @param iterator
     */
    public void process(final Iterator<T> iterator) {
        final AtomicLong nbErrors = new AtomicLong(0);    // Nombre de transactions en échec
        final AtomicLong nbSuccess = new AtomicLong(0);   // Nombre de transactions réussies

        // Notification initiale
        progressJob.accept(nbSuccess.get());

        final ExecutorService executorService = Executors.newFixedThreadPool(maxThreads);

        final List<Future<?>> futures = new ArrayList<>();
        for (int nbThread = 0; nbThread < maxThreads; nbThread++) {
            futures.add(executorService.submit(() -> {
                TransactionStatus transactionStatus = startTransaction();
                try {
                    long currentNbSuccess = 0;
                    long currentNbErrors = 0;
                    T record = getNext(iterator);
                    while (record != null) {
                        // Exécution du job
                        final boolean result = job.test(record);
                        if (result) {
                            currentNbSuccess++;
                        } else {
                            currentNbErrors++;
                        }

                        // Gestion de la transaction sous-jacente
                        if (currentNbSuccess > 0 && currentNbSuccess % commit == 0) {
                            commit(transactionStatus, nbSuccess.addAndGet(currentNbSuccess), nbErrors.addAndGet(currentNbErrors), false);
                            transactionStatus = startTransaction();
                            currentNbSuccess = 0;
                            currentNbErrors = 0;
                        }
                        record = getNext(iterator);
                    }
                    commit(transactionStatus, nbSuccess.addAndGet(currentNbSuccess), nbErrors.addAndGet(currentNbErrors), true);
                }
                // Rollback transaction
                catch (final RuntimeException e) {
                    transactionService.rollbackTransaction(transactionStatus);
                    LOG.error(e.getMessage(), e);
                    throw e;
                }
            }));
        }
        futures.forEach(f -> {
            try {
                f.get();
            } catch (InterruptedException | ExecutionException e) {
                LOG.error("Problème d'exécution multithreading : {}", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });
    }

    private synchronized T getNext(final Iterator<T> iterator) {
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    /**
     * Fermeture de la transaction sous-jacente
     */
    private void commit(final TransactionStatus transactionStatus, final long nbSuccess, final long nbError, final boolean forceLog) {
        transactionService.commitTransaction(transactionStatus);
        if (forceLog || nbSuccess % 1000 == 0) {
            LOG.debug("{} enregistrements traités avec succès - {} enregistrements en erreur", nbSuccess, nbError);
        }
        progressJob.accept(nbSuccess);
    }

    private TransactionStatus startTransaction() {
        return transactionService.startTransaction(false);
    }
}
