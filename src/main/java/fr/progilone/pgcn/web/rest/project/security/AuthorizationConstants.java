package fr.progilone.pgcn.web.rest.project.security;

import fr.progilone.pgcn.web.util.AuthorizationManager;

/**
 * Habilitations du module Projet (PROJ)
 *
 * @author jbrunet
 */
@AuthorizationManager.Init
public final class AuthorizationConstants {

    /**
     * Habilitation à la gestion des projets: créer
     */
    public static final String PROJ_HAB0 = "PROJ-HAB0";
    /**
     * Habilitation à la gestion des projets: Modifier
     */
    public static final String PROJ_HAB1 = "PROJ-HAB1";
    /**
     * Habilitation à la gestion des projets: Ajouter des utilisateurs
     */
    public static final String PROJ_HAB2 = "PROJ-HAB2";
    /**
     * Habilitation à la gestion des projets: Ajouter des bibliothèques
     */
    public static final String PROJ_HAB3 = "PROJ-HAB3";
    /**
     * Habilitation à la gestion des projets: Supprimer
     */
    public static final String PROJ_HAB4 = "PROJ-HAB4";
    /**
     * Habilitation à la gestion des projets: Suspendre
     */
    public static final String PROJ_HAB5 = "PROJ-HAB5";
    /**
     * Habilitation à la gestion des projets: Annuler
     */
    public static final String PROJ_HAB6 = "PROJ-HAB6";
    /**
     * Habilitation à la gestion des projets: Visualiser
     */
    public static final String PROJ_HAB7 = "PROJ-HAB7";
    /**
     * Habilitation à la gestion des projets: Exporter la liste / une fiche au format CSV
     */
    public static final String PROJ_HAB8 = "PROJ-HAB8";
    /**
     * Habilitation à la gestion des projets: Imprimer la liste / une fiche
     */
    public static final String PROJ_HAB9 = "PROJ-HAB9";
    /**
     * Habilitation à la gestion des projets: Scinder un projet en deux
     */
    public static final String PROJ_HAB10 = "PROJ-HAB10";
    /**
     * Habilitation à la gestion des projets: Fusionner des projets
     */
    public static final String PROJ_HAB11 = "PROJ-HAB11";

    // ne pas oublier de placer l'annotation @AuthorizationManager.Init sur la classe pour initialiser les relations entre autorisations
    static {
        AuthorizationManager.setRequirements(PROJ_HAB0, PROJ_HAB7);
        AuthorizationManager.setRequirements(PROJ_HAB1, PROJ_HAB7);
        AuthorizationManager.setRequirements(PROJ_HAB2, PROJ_HAB7);
        AuthorizationManager.setRequirements(PROJ_HAB3, PROJ_HAB7);
        AuthorizationManager.setRequirements(PROJ_HAB4, PROJ_HAB7);
        AuthorizationManager.setRequirements(PROJ_HAB5, PROJ_HAB1);
        AuthorizationManager.setRequirements(PROJ_HAB6, PROJ_HAB1);
        AuthorizationManager.setRequirements(PROJ_HAB8, PROJ_HAB7);
        AuthorizationManager.setRequirements(PROJ_HAB9, PROJ_HAB7);
        AuthorizationManager.setRequirements(PROJ_HAB10, PROJ_HAB1);
        AuthorizationManager.setRequirements(PROJ_HAB11, PROJ_HAB1);
    }

    private AuthorizationConstants() {
    }
}
