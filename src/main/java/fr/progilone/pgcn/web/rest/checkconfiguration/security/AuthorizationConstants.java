package fr.progilone.pgcn.web.rest.checkconfiguration.security;

import fr.progilone.pgcn.web.util.AuthorizationManager;

@AuthorizationManager.Init
public final class AuthorizationConstants {

    /**
     * Habilitation configuration des contrôles: lecture
     */
    public static final String CHECK_HAB0 = "CHECK-HAB0";

    /**
     * Habilitation configuration des contrôles: créer / modifier
     */
    public static final String CHECK_HAB1 = "CHECK-HAB1";

    /**
     * Habilitation configuration des contrôles: supprimer
     */
    public static final String CHECK_HAB2 = "CHECK-HAB2";

    /**
     * Habilitation contrôles: visualisation des documents contrôlés et du détail des contrôles effectués
     */
    public static final String CHECK_HAB3 = "CHECK-HAB3";

    /**
     * Habilitation contrôles: action de contrôler des documents
     */
    public static final String CHECK_HAB4 = "CHECK-HAB4";

    // ne pas oublier de placer l'annotation @AuthorizationManager.Init sur la classe pour initialiser les relations entre autorisations
    static {
        AuthorizationManager.setRequirements(CHECK_HAB1, CHECK_HAB0);
        AuthorizationManager.setRequirements(CHECK_HAB2, CHECK_HAB0);
        AuthorizationManager.setRequirements(CHECK_HAB4, CHECK_HAB3);
    }

    private AuthorizationConstants() {
    }
}
