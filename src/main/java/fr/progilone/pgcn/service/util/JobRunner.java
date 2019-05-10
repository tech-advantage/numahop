package fr.progilone.pgcn.service.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cette classe parcourt un iterateur, effectue un job pour chaque élément.
 * On peut suivre la progression du job grâce à progressJob.
 */
public class JobRunner<T> {

    private static final Logger LOG = LoggerFactory.getLogger(JobRunner.class);

    private final Iterator<T> iterator;   // Itérateur sur les objets à traiter

    private String elementName = "enregistrements";
    private int maxThreads = 1; // spécifie le nombre de threads utilisés par le traitement

    /**
     * Lorsqu'une exception est lancée par le job:
     * - true: Le traitement s'arrête, l'exception est relancée
     * - false: Le traitement se poursuit, un rollback est fait sur la transaction en cours
     */
    private boolean failOnException = true;

    /**
     * Job à exécuter
     */
    private ImportJob<T> job;

    /**
     * Suivi de la progression du job
     */
    private Consumer<Long> progressJob = lg -> {
    };

    public JobRunner(final Iterator<T> iterator) {
        this.iterator = iterator;
    }

    /**
     * Définit le nombre max de thread comme étant le nombre de proc - 2, et au minimum 1
     *
     * @return
     */
    public JobRunner<T> autoSetMaxThreads() {
        final int nbProc = Runtime.getRuntime().availableProcessors();
        this.maxThreads = nbProc > 2 ? nbProc - 2 : 1;
        return this;
    }

    public JobRunner<T> setMaxThreads(final int maxThreads) {
        this.maxThreads = maxThreads;
        return this;
    }

    public JobRunner<T> setFailOnException(final boolean failOnException) {
        this.failOnException = failOnException;
        return this;
    }

    public JobRunner<T> setElementName(final String elementName) {
        this.elementName = elementName;
        return this;
    }

    /**
     * Traitement d'une notice
     *
     * @param job
     *            le job retourne true si il est réalisé avec succès
     */
    public JobRunner<T> forEach(final ImportJob<T> job) {
        this.job = job;
        return this;
    }

    /**
     * Consumer permettant de suivre la progression du job
     */
    public JobRunner<T> onProgress(final Consumer<Long> progressJob) {
        this.progressJob = progressJob;
        return this;
    }

    /**
     * Lancement du traitement
     */
    public void process() {
        final AtomicLong nbErrors = new AtomicLong(0);    // Nombre de transactions en échec
        final AtomicLong nbSuccess = new AtomicLong(0);   // Nombre de transactions réussies
        final LocalDateTime start = LocalDateTime.now();

        final ExecutorService executorService = Executors.newFixedThreadPool(maxThreads);
        final Collection<Callable<Object>> jobs = new ArrayList<>(maxThreads);

        // Notification initiale
        progressJob.accept(nbSuccess.get());

        // Initialisation des traitements
        for (int nbThread = 0; nbThread < maxThreads; nbThread++) {
            jobs.add(Executors.callable(() -> {
                try {
                    T record = getNext();
                    while (record != null) {
                        // Exécution du job
                        final boolean result = job.run(record);
                        if (result) {
                            nbSuccess.incrementAndGet();
                        } else {
                            nbErrors.incrementAndGet();
                            LOG.info("{} {} non traités", nbSuccess, elementName, nbErrors, elementName);
                        }

                        LOG.debug("{} {} traités avec succès - {} {} non traités", nbSuccess, elementName, nbErrors, elementName);
                        record = getNext();
                    }

                } catch (final Throwable e) {
                    // Rollback transaction
                    LOG.error(e.getMessage(), e);

                    if (failOnException) {
                        throw e;
                    }

                }
            }));
        }
        // Lancement des traitements
        try {
            LOG.info("Lancement de {} traitement(s)", jobs.size());
            final List<Future<Object>> futures = executorService.invokeAll(jobs);

            // Vérification du résultat des traitements: future.get() lance une ExecutionException si le job a donné lieu à une exception
            for (final Future<Object> future : futures) {
                future.get();
            }

        } catch (final Throwable e) {
            LOG.error("Problème d'exécution multithreading : {}", e.getMessage(), e);
            throw new RuntimeException(e);

        } finally {
            executorService.shutdown();
            final Duration duration = Duration.between(start, LocalDateTime.now());
            LOG.info("{} traitement(s) terminé(s) en {} secondes", jobs.size(), duration.getSeconds());
            LOG.debug("Statut du service d'exécution: {}", executorService.toString());
        }
    }

    /**
     * Récupération de l'élément à traiter suivant
     *
     * @return
     */
    private T getNext() {
        synchronized (iterator) {
            if (iterator.hasNext()) {
                return iterator.next();
            }
            return null;
        }
    }

    @FunctionalInterface
    public interface ImportJob<T> {

        boolean run(T t);
    }

}
