package fr.progilone.pgcn.repository.es;

import fr.progilone.pgcn.domain.exchange.cines.CinesReport;

import java.util.Collection;

public interface EsCinesReportRepositoryCustom {

    /**
     * Indexation des notices, en spécifiant l'index
     *
     * @param index
     * @param reports
     */
    void index(final String index, final Collection<CinesReport> reports);

    /**
     * Indexation (avec gestion de la relation parent/enfant)
     *
     * @param report
     */
    void indexEntity(CinesReport report);

    /**
     * Indexation (avec gestion de la relation parent/enfant)
     *
     * @param reports
     */
    void indexEntities(Collection<CinesReport> reports);

    /**
     * Suppression d'une notice de l'index
     *
     * @param report
     */
    void deleteEntity(CinesReport report);

    /**
     * Suppression d'une collection de notices de l'index
     *
     * @param reports
     */
    void deleteEntities(Collection<CinesReport> reports);
}
