package fr.progilone.pgcn.web.rest.exchange.security;

import fr.progilone.pgcn.web.util.AuthorizationManager;

/**
 * Habilitations du module Import/Export (EXC)
 */
@AuthorizationManager.Init
public final class AuthorizationConstants {

    /**
     * Habilitation import/export: lecture
     */
    public static final String EXC_HAB0 = "EXC-HAB0";

    /**
     * Habilitation import/export: suppression
     */
    public static final String EXC_HAB1 = "EXC-HAB1";

    /**
     * Habilitation import/export: exécution d'un import
     */
    public static final String EXC_HAB2 = "EXC-HAB2";

    /**
     * Habilitation import/export: recherche Z39.50 sur des serveurs distants
     */
    public static final String EXC_HAB3 = "EXC-HAB3";

    /**
     * Administration de templates
     */
    public static final String TPL_HAB0 = "TPL-HAB0";

    /**
     * Habilitation mapping: lecture
     */
    public static final String MAP_HAB0 = "MAP-HAB0";

    /**
     * Habilitation mapping: création / modification
     */
    public static final String MAP_HAB1 = "MAP-HAB1";

    /**
     * Habilitation mapping: suppression
     */
    public static final String MAP_HAB2 = "MAP-HAB2";

    /**
     * Habilitation export Internet Archive
     */
    public static final String EXPORT_INTERNET_ARCHIVE_HAB0 = "EXPORT-INTERNET-ARCHIVE-HAB0";

    /**
     * Habilitation export Omeka
     */
    public static final String EXPORT_DIFFUSION_OMEKA_HAB0 = "EXPORT-DIFFUSION-OMEKA-HAB0";

    /**
     * Habilitation export bibliothèque numérique
     */
    public static final String EXPORT_DIFFUSION_DIGITAL_LIBRARY_HAB0 = "EXPORT-DIFFUSION-DIGITAL-LIBRARY-HAB0";

    // ne pas oublier de placer l'annotation @AuthorizationManager.Init sur la classe pour initialiser les relations entre autorisations
    static {
        AuthorizationManager.setRequirements(AuthorizationConstants.EXC_HAB1, AuthorizationConstants.EXC_HAB0);
        AuthorizationManager.setRequirements(AuthorizationConstants.EXC_HAB2, AuthorizationConstants.EXC_HAB0);
        AuthorizationManager.setRequirements(AuthorizationConstants.MAP_HAB1, AuthorizationConstants.MAP_HAB0);
        AuthorizationManager.setRequirements(AuthorizationConstants.MAP_HAB2, AuthorizationConstants.MAP_HAB0);
    }

    private AuthorizationConstants() {
    }
}
