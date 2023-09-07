package fr.progilone.pgcn.service.util.transaction;

import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.exception.message.PgcnError;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.hibernate.StatelessSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.TransactionStatus;

/**
 * Cette classe parcourt un iterateur, effectue un job pour chaque élément, et committe sa transaction tous les 100 éléments.
 * On peut suivre la progression du job grâce à progressJob.
 */
public class TransactionalJobRunner<T> {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionalJobRunner.class);

    private final TransactionService transactionService;
    private final Iterator<T> iterator;   // Itérateur sur les objets à traiter
    private final int totalElements;

    private String elementName = "enregistrements";
    private long commit = 100L; // Committe tous les x éléments
    private long flush = 0L;  // Flush tous les x éléments
    private long groupSize = 0L;  // Regroupe les éléments en collections de x éléments avant de les traiter
    private int maxThreads = 1; // spécifie le nombre de threads utilisés par le traitement
    private boolean readOnly = false; // si les transactions doivent être readonly
    private long nbBeforeLog = 100L; // Nombre d'éléments en dessous duquel les logs sont générés seulement en trace
    private long durationBetweenLogs = 5000l; // Nombre de millisecondes minimum entre 2 logs

    /**
     * Lorsqu'une exception est lancée par le job:
     * - true: Le traitement s'arrête, l'exception est relancée
     * - false: Le traitement se poursuit, un rollback est fait sur la transaction en cours
     */
    private boolean failOnException = true;
    private boolean logExceptions = true;
    private boolean stateless = false;

    /**
     * Job à exécuter
     */
    private Job<T> job;

    /**
     * Job à exécuter pour les groupes
     */
    private Job<Collection<T>> groupJob;

    /**
     * Jobs à exécuter après que tous les jobs soient terminés, chacun dans une transaction
     */
    private final List<PostJob> postJobs = new ArrayList<>();

    /**
     * Suivi de la progression du job
     */
    private Consumer<Long> progressJob = lg -> {
    };

    /**
     * Permet de regrouper des enregistrements pour qu'ils soient traités par le même job (donc séquentiellement) pour éviter les problèmes de
     * concurence d'accès entre ces enregistrements.
     */
    private GroupExtractor<T> groupExtractor;

    /**
     * Stocke les affectations entre un group et un job qui le traite.
     */
    private final Map<String, String> groupAssignment = new HashMap<>();

    /**
     * Stocke les enregistrements à traiter ultérieurement par un autre job.
     */
    private final Map<String, List<T>> toBeHandled = new HashMap<>();

    public TransactionalJobRunner(final Collection<T> collection, final TransactionService transactionService) {
        this.iterator = collection.iterator();
        this.totalElements = collection.size();
        this.transactionService = transactionService;
    }

    public TransactionalJobRunner(final Iterator<T> iterator, final TransactionService transactionService) {
        this.iterator = iterator;
        this.totalElements = 0;
        this.transactionService = transactionService;
    }

    public TransactionalJobRunner(final Stream<T> stream, final TransactionService transactionService) {
        this.iterator = stream.iterator();
        this.totalElements = 0;
        this.transactionService = transactionService;
    }

    // Committe tous les commit éléments
    public TransactionalJobRunner<T> setCommit(final long commit) {
        this.commit = commit;
        return this;
    }

    public TransactionalJobRunner<T> setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
        return this;
    }

    public TransactionalJobRunner<T> setFlush(final long flush) {
        this.flush = flush;
        return this;
    }

    public TransactionalJobRunner<T> setNbBeforeLog(final long nbBeforeLog) {
        this.nbBeforeLog = nbBeforeLog;
        return this;
    }

    public TransactionalJobRunner<T> setDurationBetweenLogs(final long durationBetweenLogs) {
        this.durationBetweenLogs = durationBetweenLogs;
        return this;
    }

    public TransactionalJobRunner<T> autoSetMaxThreads() {
        this.maxThreads = Runtime.getRuntime().availableProcessors();
        return this;
    }

    public TransactionalJobRunner<T> setFailOnException(final boolean failOnException) {
        this.failOnException = failOnException;
        return this;
    }

    public TransactionalJobRunner<T> setLogExceptions(final boolean logExceptions) {
        this.logExceptions = logExceptions;
        return this;
    }

    public TransactionalJobRunner<T> setStateless(final boolean stateless) {
        this.stateless = stateless;
        return this;
    }

    public TransactionalJobRunner<T> setElementName(final String elementName) {
        this.elementName = elementName;
        return this;
    }

    public TransactionalJobRunner<T> setGroupExtractor(final GroupExtractor<T> groupExtractor) {
        this.groupExtractor = groupExtractor;
        return this;
    }

    /**
     * Traitement d'une notice
     *
     * @param job
     *            le job retourne true si il est réalisé avec succès
     */
    public TransactionalJobRunner<T> forEach(final Job<T> job) {
        this.job = job;
        return this;
    }

    /**
     * Traitement d'un groupe
     *
     * @param groupJob
     *            le job retourne true si il est réalisé avec succès
     */
    public TransactionalJobRunner<T> forEachGroup(final int groupSize, final Job<Collection<T>> groupJob) {
        this.groupJob = groupJob;
        this.groupSize = groupSize;
        return this;
    }

    public TransactionalJobRunner<T> addPostJob(final PostJob job) {
        this.postJobs.add(job);
        return this;
    }

    /**
     * Consumer permettant de suivre la progression du job
     */
    public TransactionalJobRunner<T> onProgress(final Consumer<Long> progressJob) {
        if (progressJob != null) {
            this.progressJob = progressJob;
        }
        return this;
    }

    /**
     * Lancement du traitement
     */
    public void process() {
        final AtomicBoolean stopAllJobs = new AtomicBoolean(false);
        final AtomicLong nbErrors = new AtomicLong(0);    // Nombre de transactions en échec
        final AtomicLong nbSuccess = new AtomicLong(0);   // Nombre de transactions réussies
        final long start = System.currentTimeMillis();
        final AtomicLong lastLogTime = new AtomicLong(start);

        final ThreadPoolTaskExecutor taskExecutor = transactionService.getTaskExecutor();
        final Collection<Future<Object>> futures = new ArrayList<>(maxThreads);

        // Notification initiale
        progressJob.accept(nbSuccess.get());

        // Initialisation des traitements
        for (int nbThread = 0; nbThread < maxThreads; nbThread++) {
            futures.add(taskExecutor.submit(Executors.callable(() -> {
                final String jobId = UUID.randomUUID().toString();
                final TransactionManager t = new TransactionManager();
                t.startTransaction();
                long currentNbSuccess = 0;
                long currentNbErrors = 0;

                try {
                    if (this.groupSize > 0 && this.groupJob != null) {
                        Collection<T> list = getNextGroup(jobId);
                        while (!list.isEmpty() && !stopAllJobs.get()) {
                            try {
                                // Exécution du job
                                final boolean result = groupJob.run(list);
                                if (result) {
                                    currentNbSuccess += list.size();
                                } else {
                                    currentNbErrors += list.size();
                                }

                                // Gestion de la transaction sous-jacente
                                if (currentNbSuccess > 0 && currentNbSuccess % commit == 0) {
                                    lastLogTime.set(t.commit(nbSuccess.addAndGet(currentNbSuccess), nbErrors.addAndGet(currentNbErrors), start, lastLogTime.get(), false));
                                    t.startTransaction();

                                    currentNbSuccess = 0;
                                    currentNbErrors = 0;
                                } else if (flush > 0 && currentNbSuccess > 0
                                           && currentNbSuccess % flush == 0) {
                                    // Vidage de la session
                                    t.flush();
                                }
                            } catch (final Throwable e) {
                                handleException(nbErrors, nbSuccess, t, currentNbSuccess, currentNbErrors + list.size(), stopAllJobs, e);
                                currentNbSuccess = 0;
                                currentNbErrors = 0;
                            }
                            list = getNextGroup(jobId);
                        }
                        lastLogTime.set(t.commit(nbSuccess.addAndGet(currentNbSuccess), nbErrors.addAndGet(currentNbErrors), start, lastLogTime.get(), true));
                    } else {
                        T record = getNext(jobId);
                        while (record != null && !stopAllJobs.get()) {
                            try {
                                // Exécution du job
                                final boolean result = job.run(record);
                                if (result) {
                                    currentNbSuccess++;
                                } else {
                                    currentNbErrors++;
                                }

                                // Gestion de la transaction sous-jacente
                                if (currentNbSuccess > 0 && currentNbSuccess % commit == 0) {
                                    lastLogTime.set(t.commit(nbSuccess.addAndGet(currentNbSuccess), nbErrors.addAndGet(currentNbErrors), start, lastLogTime.get(), false));
                                    t.startTransaction();

                                    currentNbSuccess = 0;
                                    currentNbErrors = 0;
                                } else if (flush > 0 && currentNbSuccess > 0
                                           && currentNbSuccess % flush == 0) {
                                    // Vidage de la session
                                    t.flush();
                                }
                            } catch (final Throwable e) {
                                handleException(nbErrors, nbSuccess, t, currentNbSuccess, ++currentNbErrors, stopAllJobs, e);
                                currentNbSuccess = 0;
                                currentNbErrors = 0;
                            }
                            record = getNext(jobId);
                        }
                        lastLogTime.set(t.commit(nbSuccess.addAndGet(currentNbSuccess), nbErrors.addAndGet(currentNbErrors), start, lastLogTime.get(), true));
                    }
                } finally {
                    // Normalement ici il ne devrait pas y avoir de transaction ouverte mais bon on ne sait jamais...
                    t.rollback();
                    t.closeSession();
                }
            })));
        }
        // Lancement des traitements
        try {
            LOG.trace("Attente de la fin de {} traitement(s)", futures.size());

            // Vérification du résultat des traitements: future.get() lance une ExecutionException si le job a donné lieu à une exception
            for (final Future<Object> future : futures) {
                future.get();
            }

            if (!postJobs.isEmpty()) {
                LOG.debug("Lancement de {} actions post-traitement", postJobs.size());
                final long startPostJobs = System.currentTimeMillis();

                final TransactionManager t = new TransactionManager();

                postJobs.forEach(j -> {
                    t.startTransaction();
                    try {
                        j.run();
                        lastLogTime.set(t.commit(1, 0, startPostJobs, lastLogTime.get(), true));
                    } catch (final Throwable e) {
                        LOG.error(e.getMessage(), e);
                        t.rollback();
                    } finally {
                        t.closeSession();
                    }
                });
            }

        } catch (final InterruptedException e) {
            LOG.warn("Thread interrompu : {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        } catch (final Throwable e) {
            LOG.error("Problème d'exécution multithreading : {}", e.getMessage(), e);
            throw new PgcnException(new PgcnError.Builder().setMessage("Problème d'exécution multithreading").build(), e);

        } finally {
            final long elapsedTime = System.currentTimeMillis() - start;
            LOG.trace("{} traitement(s) terminé(s) en {} ms", futures.size(), elapsedTime);
        }
    }

    private void handleException(final AtomicLong nbErrors,
                                 final AtomicLong nbSuccess,
                                 final TransactionManager t,
                                 final long currentNbSuccess,
                                 final long currentNbErrors,
                                 final AtomicBoolean stopAllJobs,
                                 final Throwable e) {
        // Rollback transaction
        t.rollback();
        nbSuccess.addAndGet(currentNbSuccess);
        nbErrors.addAndGet(currentNbErrors);

        if (failOnException) {
            stopAllJobs.set(true);
            throw new IllegalStateException(e);
        } else {
            if (logExceptions) {
                LOG.warn(e.getMessage(), e);
            } else {
                LOG.trace(e.getMessage(), e);
            }
            t.startTransaction();
        }
    }

    /**
     * Récupération de l'élément à traiter suivant
     *
     * @return
     */
    private T getNext(final String jobId) {
        synchronized (iterator) {
            if (toBeHandled.containsKey(jobId)) {
                final List<T> list = toBeHandled.get(jobId);
                if (CollectionUtils.isNotEmpty(list)) {
                    return list.remove(0);
                }
            }
            while (iterator.hasNext()) {
                try {
                    final T next = iterator.next();
                    if (groupExtractor == null) {
                        return next;
                    } else {
                        final String group = groupExtractor.extractGroup(next);
                        if (groupAssignment.containsKey(group)) {
                            final String assignedJob = groupAssignment.get(group);
                            if (!jobId.equals(assignedJob)) {
                                toBeHandled.computeIfAbsent(assignedJob, k -> new ArrayList<>()).add(next);
                            } else {
                                return next;
                            }
                        } else {
                            groupAssignment.put(group, jobId);
                            return next;
                        }
                    }
                } catch (final Throwable e) {
                    if (failOnException) {
                        throw e;
                    } else {
                        if (logExceptions) {
                            LOG.warn(e.getMessage(), e);
                        } else {
                            LOG.trace(e.getMessage(), e);
                        }
                    }
                }
            }
            return null;
        }
    }

    /**
     * Récupération de l'élément à traiter suivant
     *
     * @return
     */
    private Collection<T> getNextGroup(final String jobId) {
        final Collection<T> list = new ArrayList<>();
        T record;
        do {
            record = getNext(jobId);
            if (record != null) {
                list.add(record);
            }
        } while (record != null && list.size() < this.groupSize);
        return list;
    }

    private class TransactionManager {

        private StatelessSession statelessSession;
        private TransactionStatus status;

        /**
         * Ouverture de la transaction sous-jacente
         */
        public void startTransaction() {
            if (stateless) {
                statelessSession = transactionService.startStatelessTransaction(statelessSession);
            } else {
                status = transactionService.startTransaction(readOnly);
            }
        }

        /**
         * Commit de la transaction sous-jacente
         */
        public long commit(final long nbSuccess, final long nbError, final long start, final long lastLogTime, final boolean forceLog) {
            if (stateless) {
                transactionService.commitStatelessTransaction(statelessSession);
            } else {
                transactionService.commitTransaction(status);
            }
            long result = lastLogTime;
            final long currentTime = System.currentTimeMillis();
            final long elapsedTimeSinceLastLog = currentTime - lastLogTime;
            if (elapsedTimeSinceLastLog >= durationBetweenLogs || forceLog) {
                result = currentTime;
                final long elapsedTime = currentTime - start;
                if (totalElements > 0) {
                    if (nbSuccess + nbError < nbBeforeLog) {
                        LOG.trace("{} {} traités avec succès - {} erreur - {}% ({})",
                                  nbSuccess,
                                  elementName,
                                  nbError,
                                  ((nbSuccess + nbError) * 100) / totalElements,
                                  DurationFormatUtils.formatDurationHMS(elapsedTime));
                    } else {
                        LOG.debug("{} {} traités avec succès - {} erreur - {}% ({})",
                                  nbSuccess,
                                  elementName,
                                  nbError,
                                  ((nbSuccess + nbError) * 100) / totalElements,
                                  DurationFormatUtils.formatDurationHMS(elapsedTime));
                    }
                } else {
                    if (nbSuccess + nbError < nbBeforeLog) {
                        LOG.trace("{} {} traités avec succès - {} erreur ({})", nbSuccess, elementName, nbError, DurationFormatUtils.formatDurationHMS(elapsedTime));
                    } else {
                        LOG.debug("{} {} traités avec succès - {} erreur ({})", nbSuccess, elementName, nbError, DurationFormatUtils.formatDurationHMS(elapsedTime));
                    }
                }
            }
            progressJob.accept(nbSuccess);
            return result;
        }

        /**
         * Rollback de la transaction sous-jacente
         */
        public void rollback() {
            if (stateless) {
                transactionService.rollbackStatelessTransaction(statelessSession);
            } else if (status != null && !status.isCompleted()) {
                transactionService.rollbackTransaction(status);
            }
        }

        /**
         * Fermeture de la transaction sous-jacente
         */
        public void closeSession() {
            if (stateless) {
                transactionService.closeStatelessSession(statelessSession);
            }
        }

        /**
         * Flush de la session sous-jacente
         */
        public void flush() {
            if (!stateless) {
                transactionService.flushSession(status);
            }
        }
    }

    @FunctionalInterface
    public interface Job<T> {

        boolean run(T t) throws Exception;
    }

    @FunctionalInterface
    public interface PostJob {

        void run();
    }

    @FunctionalInterface
    public interface GroupExtractor<T> {

        String extractGroup(T record);
    }
}
