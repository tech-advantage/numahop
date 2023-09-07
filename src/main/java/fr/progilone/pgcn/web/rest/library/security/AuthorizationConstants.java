package fr.progilone.pgcn.web.rest.library.security;

import fr.progilone.pgcn.web.util.AuthorizationManager;

/**
 * Habilitations du module Bibliothèque (LIB)
 *
 *
 * @author jbrunet
 */
@AuthorizationManager.Init
public final class AuthorizationConstants {

    /**
     * Habilitation à la gestion des bibliothèques: créer
     */
    public static final String LIB_HAB1 = "LIB-HAB1";
    /**
     * Habilitation à la gestion des bibliothèques: Modifier
     */
    public static final String LIB_HAB2 = "LIB-HAB2";
    /**
     * Habilitation à la gestion des bibliothèques: Supprimer
     */
    public static final String LIB_HAB3 = "LIB-HAB3";
    /**
     * Habilitation à la gestion des bibliothèques: Désactiver
     */
    public static final String LIB_HAB4 = "LIB-HAB4";
    /**
     * Habilitation à la gestion des bibliothèques: Visualiser (hors données de production)
     */
    public static final String LIB_HAB5 = "LIB-HAB5";
    /**
     * Habilitation à la gestion des bibliothèques: Visualiser les utilisateurs
     */
    public static final String LIB_HAB6 = "LIB-HAB6";
    /**
     * Habilitation à la gestion des bibliothèques: Visualiser les projets et les données de production
     */
    public static final String LIB_HAB7 = "LIB-HAB7";
    /**
     * Habilitation à la gestion des bibliothèques: Exporter la liste / une fiche au format CSV
     */
    public static final String LIB_HAB8 = "LIB-HAB8";
    /**
     * Habilitation à la gestion des bibliothèques: Imprime la liste / une fiche
     */
    public static final String LIB_HAB9 = "LIB-HAB9";

    // ne pas oublier de placer l'annotation @AuthorizationManager.Init sur la classe pour initialiser les relations entre autorisations
    static {
        AuthorizationManager.setRequirements(AuthorizationConstants.LIB_HAB1, AuthorizationConstants.LIB_HAB5);
        AuthorizationManager.setRequirements(AuthorizationConstants.LIB_HAB2, AuthorizationConstants.LIB_HAB5);
        AuthorizationManager.setRequirements(AuthorizationConstants.LIB_HAB3, AuthorizationConstants.LIB_HAB5);
        AuthorizationManager.setRequirements(AuthorizationConstants.LIB_HAB4, AuthorizationConstants.LIB_HAB5);
        AuthorizationManager.setRequirements(AuthorizationConstants.LIB_HAB6, AuthorizationConstants.LIB_HAB5);
        AuthorizationManager.setRequirements(AuthorizationConstants.LIB_HAB7, AuthorizationConstants.LIB_HAB5, AuthorizationConstants.LIB_HAB6);
        AuthorizationManager.setRequirements(AuthorizationConstants.LIB_HAB8, AuthorizationConstants.LIB_HAB5);
        AuthorizationManager.setRequirements(AuthorizationConstants.LIB_HAB9, AuthorizationConstants.LIB_HAB5);
    }

    private AuthorizationConstants() {
    }
}
