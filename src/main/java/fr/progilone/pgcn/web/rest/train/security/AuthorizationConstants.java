package fr.progilone.pgcn.web.rest.train.security;

import fr.progilone.pgcn.web.util.AuthorizationManager;

/**
 * Habilitations du module train (TRA)
 */
@AuthorizationManager.Init
public final class AuthorizationConstants {
    /**
     * Habilitation à la gestion des trains : créer
     */
    public static final String TRA_HAB0 = "TRA-HAB0";
    /**
     * Habilitation à la gestion des trains : modifier
     */
    public static final String TRA_HAB1 = "TRA-HAB1";
    /**
     * Habilitation à la gestion des trains : supprimer
     */
    public static final String TRA_HAB2 = "TRA-HAB2";
    /**
     * Habilitation à la gestion des trains : visualiser
     */
    public static final String TRA_HAB3 = "TRA-HAB3";
    /**
     * Habilitation à la gestion des trains : Contrôler après réception et comptage (physique ou numérique) : valider, rejeter…
     */
    public static final String TRA_HAB4 = "TRA-HAB4";
    /**
     * Habilitation à la gestion des trains : exporter la liste / une fiche au format CSV
     */
    public static final String TRA_HAB5 = "TRA-HAB5";
    /**
     * Habilitation à la gestion des trains : imprimer la liste / une fiche
     */
    public static final String TRA_HAB6 = "TRA-HAB6";

    static {
        AuthorizationManager.setRequirements(AuthorizationConstants.TRA_HAB0, AuthorizationConstants.TRA_HAB3);
        AuthorizationManager.setRequirements(AuthorizationConstants.TRA_HAB1, AuthorizationConstants.TRA_HAB3);
        AuthorizationManager.setRequirements(AuthorizationConstants.TRA_HAB2, AuthorizationConstants.TRA_HAB3);
        AuthorizationManager.setRequirements(AuthorizationConstants.TRA_HAB4, AuthorizationConstants.TRA_HAB3);
        AuthorizationManager.setRequirements(AuthorizationConstants.TRA_HAB5, AuthorizationConstants.TRA_HAB3);
        AuthorizationManager.setRequirements(AuthorizationConstants.TRA_HAB6, AuthorizationConstants.TRA_HAB3);
    }

    private AuthorizationConstants() {
    }
}
