package fr.progilone.pgcn.web.rest.lot.security;

import fr.progilone.pgcn.web.util.AuthorizationManager;

/**
 * Habilitations du module lot (LOT)
 *
 *
 * @author jbrunet
 */
@AuthorizationManager.Init
public final class AuthorizationConstants {
    /**
     * Habilitation à la gestion des lots : créer
     */
    public static final String LOT_HAB0 = "LOT-HAB0";
    /**
     * Habilitation à la gestion des lots : modifier
     */
    public static final String LOT_HAB1 = "LOT-HAB1";
    /**
     * Habilitation à la gestion des lots : supprimer
     */
    public static final String LOT_HAB2 = "LOT-HAB2";
    /**
     * Habilitation à la gestion des lots : visualiser
     */
    public static final String LOT_HAB3 = "LOT-HAB3";
    /**
     * Habilitation à la gestion des lots : Contrôler après réception et comptage (physique ou numérique) : valider, rejeter…
     */
    public static final String LOT_HAB4 = "LOT-HAB4";
    /**
     * Habilitation à la gestion des lots : exporter la liste / une fiche au format CSV
     */
    public static final String LOT_HAB5 = "LOT-HAB5";
    /**
     * Habilitation à la gestion des lots : imprimer la liste / une fiche
     */
    public static final String LOT_HAB6 = "LOT-HAB6";

    // ne pas oublier de placer l'annotation @AuthorizationManager.Init sur la classe pour initialiser les relations entre autorisations
    static {
        AuthorizationManager.setRequirements(AuthorizationConstants.LOT_HAB0, AuthorizationConstants.LOT_HAB3);
        AuthorizationManager.setRequirements(AuthorizationConstants.LOT_HAB1, AuthorizationConstants.LOT_HAB3);
        AuthorizationManager.setRequirements(AuthorizationConstants.LOT_HAB2, AuthorizationConstants.LOT_HAB3);
        AuthorizationManager.setRequirements(AuthorizationConstants.LOT_HAB4, AuthorizationConstants.LOT_HAB3);
        AuthorizationManager.setRequirements(AuthorizationConstants.LOT_HAB5, AuthorizationConstants.LOT_HAB3);
        AuthorizationManager.setRequirements(AuthorizationConstants.LOT_HAB6, AuthorizationConstants.LOT_HAB3);
    }

    private AuthorizationConstants() {
    }
}
