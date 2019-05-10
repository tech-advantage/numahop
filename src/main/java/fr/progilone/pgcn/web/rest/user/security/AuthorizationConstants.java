package fr.progilone.pgcn.web.rest.user.security;

import fr.progilone.pgcn.web.util.AuthorizationManager;

/**
 * Habilitations du module Usager (USER)
 *
 *
 * @author jbrunet
 */
@AuthorizationManager.Init
public final class AuthorizationConstants {
    /**
     * Habilitation à la gestion des utilisateurs : lecture
     */
    public static final String USER_HAB0 = "USER-HAB0";
    /**
     * Habilitation à la gestion des utilisateurs : création
     */
    public static final String USER_HAB1 = "USER-HAB1";
    /**
     * Habilitation à la gestion des utilisateurs : modification d'un utilisateur
     */
    public static final String USER_HAB2 = "USER-HAB2";
    /**
     * Habilitation à la gestion des utilisateurs : suppression
     */
    public static final String USER_HAB3 = "USER-HAB3";
    /**
     * Habilitation à la gestion des utilisateurs : désactivation
     */
    public static final String USER_HAB4 = "USER-HAB4";
    /**
     * Habilitation à la gestion des utilisateurs : export au format CSV
     */
    public static final String USER_HAB5 = "USER-HAB5";
    /**
     * Habilitation à la gestion des utilisateurs : modification de son propre contenu
     */
    public static final String USER_HAB6 = "USER-HAB6";

    /**
     * Habilitation à la gestion des rôles : lecture
     */
    public static final String ROLE_HAB0 = "ROLE-HAB0";
    /**
     * Habilitation à la gestion des rôles : création
     */
    public static final String ROLE_HAB1 = "ROLE-HAB1";
    /**
     * Habilitation à la gestion des rôles : modification d'un rôle
     */
    public static final String ROLE_HAB2 = "ROLE-HAB2";
    /**
     * Habilitation à la gestion des rôles : suppression
     */
    public static final String ROLE_HAB3 = "ROLE-HAB3";
    /**
     * Habilitation à la gestion des rôles : désactivation
     */
    public static final String ROLE_HAB4 = "ROLE-HAB4";
    /**
     * Habilitation à la gestion des rôles : export au format CSV
     */
    public static final String ROLE_HAB5 = "ROLE-HAB5";

    // ne pas oublier de placer l'annotation @AuthorizationManager.Init sur la classe pour initialiser les relations entre autorisations
    static {
        AuthorizationManager.setRequirements(AuthorizationConstants.USER_HAB1, AuthorizationConstants.USER_HAB0);
        AuthorizationManager.setRequirements(AuthorizationConstants.USER_HAB2, AuthorizationConstants.USER_HAB0);
        AuthorizationManager.setRequirements(AuthorizationConstants.USER_HAB3, AuthorizationConstants.USER_HAB0);
        AuthorizationManager.setRequirements(AuthorizationConstants.USER_HAB5, AuthorizationConstants.USER_HAB0);
        AuthorizationManager.setRequirements(AuthorizationConstants.USER_HAB6, AuthorizationConstants.USER_HAB0);
        AuthorizationManager.setRequirements(AuthorizationConstants.USER_HAB4, AuthorizationConstants.USER_HAB0, AuthorizationConstants.USER_HAB2);
        AuthorizationManager.setRequirements(AuthorizationConstants.ROLE_HAB1, AuthorizationConstants.ROLE_HAB0);
        AuthorizationManager.setRequirements(AuthorizationConstants.ROLE_HAB2, AuthorizationConstants.ROLE_HAB0);
        AuthorizationManager.setRequirements(AuthorizationConstants.ROLE_HAB3, AuthorizationConstants.ROLE_HAB0);
        AuthorizationManager.setRequirements(AuthorizationConstants.ROLE_HAB5, AuthorizationConstants.ROLE_HAB0);
        AuthorizationManager.setRequirements(AuthorizationConstants.ROLE_HAB4, AuthorizationConstants.ROLE_HAB0, AuthorizationConstants.ROLE_HAB2);
    }

    private AuthorizationConstants() {
    }
}
