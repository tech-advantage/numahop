package fr.progilone.pgcn.web.rest.delivery.security;

import fr.progilone.pgcn.web.util.AuthorizationManager;

/**
 * Habilitations du module Livraison (DEL)
 */
@AuthorizationManager.Init
public final class AuthorizationConstants {

    /**
     * Habilitation à la gestion des livraisons : lecture
     */
    public static final String DEL_HAB0 = "DEL-HAB0";
    /**
     * Habilitation à la gestion des livraisons : création
     */
    public static final String DEL_HAB1 = "DEL-HAB1";
    /**
     * Habilitation à la gestion des livraisons : modification restreinte (prestataires)
     */
    public static final String DEL_HAB2 = "DEL-HAB2";
    /**
     * Habilitation à la gestion des livraisons : suppression
     */
    public static final String DEL_HAB3 = "DEL-HAB3";
    /**
     * Habilitation à la gestion des livraisons : édition du bordereau de livraison
     */
    public static final String DEL_HAB4 = "DEL-HAB4";
    /**
     * Habilitation à la gestion des livraisons : pré-livraison, livraison
     */
    public static final String DEL_HAB5 = "DEL-HAB5";
    /**
     * Habilitation à la gestion des livraisons : pré-livraison, livraison + création d'UD
     */
    public static final String DEL_HAB5_2 = "DEL-HAB5-2";
    /**
     * Habilitation à la gestion des livraisons : export CSV
     */
    public static final String DEL_HAB6 = "DEL-HAB6";
    /**
     * Habilitation à la gestion des livraisons : impression
     */
    public static final String DEL_HAB7 = "DEL-HAB7";
    /**
     * Habilitation à la gestion des livraisons : modification
     */
    public static final String DEL_HAB8 = "DEL-HAB8";

    static {
        AuthorizationManager.setRequirements(AuthorizationConstants.DEL_HAB1, AuthorizationConstants.DEL_HAB0);
        AuthorizationManager.setRequirements(AuthorizationConstants.DEL_HAB2, AuthorizationConstants.DEL_HAB0);
        AuthorizationManager.setRequirements(AuthorizationConstants.DEL_HAB3, AuthorizationConstants.DEL_HAB0);
        AuthorizationManager.setRequirements(AuthorizationConstants.DEL_HAB4, AuthorizationConstants.DEL_HAB0);
        AuthorizationManager.setRequirements(AuthorizationConstants.DEL_HAB5, AuthorizationConstants.DEL_HAB0);
        AuthorizationManager.setRequirements(AuthorizationConstants.DEL_HAB5_2, AuthorizationConstants.DEL_HAB0);
        AuthorizationManager.setRequirements(AuthorizationConstants.DEL_HAB6, AuthorizationConstants.DEL_HAB0);
        AuthorizationManager.setRequirements(AuthorizationConstants.DEL_HAB7, AuthorizationConstants.DEL_HAB0);
        AuthorizationManager.setRequirements(AuthorizationConstants.DEL_HAB8, AuthorizationConstants.DEL_HAB2);
    }

    private AuthorizationConstants() {
    }
}
