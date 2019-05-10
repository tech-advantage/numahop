package fr.progilone.pgcn.repository.es;

import fr.progilone.pgcn.domain.exchange.internetarchive.InternetArchiveReport;

import java.util.Collection;

public interface EsInternetArchiveReportRepositoryCustom {

    /**
     * Indexation des notices, en spécifiant l'index
     *
     * @param index
     * @param reports
     */
    void index(final String index, final Collection<InternetArchiveReport> reports);

    /**
     * Indexation (avec gestion de la relation parent/enfant)
     *
     * @param report
     */
    void indexEntity(InternetArchiveReport report);

    /**
     * Indexation (avec gestion de la relation parent/enfant)
     *
     * @param reports
     */
    void indexEntities(Collection<InternetArchiveReport> reports);

    /**
     * Suppression d'une notice de l'index
     *
     * @param report
     */
    void deleteEntity(InternetArchiveReport report);

    /**
     * Suppression d'une collection de notices de l'index
     *
     * @param reports
     */
    void deleteEntities(Collection<InternetArchiveReport> reports);
}
