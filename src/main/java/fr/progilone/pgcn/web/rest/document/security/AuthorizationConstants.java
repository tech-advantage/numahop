package fr.progilone.pgcn.web.rest.document.security;

import fr.progilone.pgcn.web.util.AuthorizationManager;

/**
 * Habilitations du module Document (DOC)
 *
 * @author jbrunet
 */
@AuthorizationManager.Init
public final class AuthorizationConstants {

    /**
     * Accès aux données des autres bibliothèques, en lecture seule
     */
    public static final String DOCUMENT_LIB = "DOCUMENT-LIB";

    /**
     * Habilitation à la gestion des unités documentaires : lecture
     */
    public static final String DOC_UNIT_HAB0 = "DOC-UNIT-HAB0";
    /**
     * Habilitation à la gestion des unités documentaires : création
     */
    public static final String DOC_UNIT_HAB1 = "DOC-UNIT-HAB1";
    /**
     * Habilitation à la gestion des unités documentaires : modification d'une unité doc
     */
    public static final String DOC_UNIT_HAB2 = "DOC-UNIT-HAB2";
    /**
     * Habilitation à la gestion des unités documentaires : suppression
     */
    public static final String DOC_UNIT_HAB3 = "DOC-UNIT-HAB3";
    /**
     * Habilitation à la gestion des unités documentaires : export
     */
    public static final String DOC_UNIT_HAB4 = "DOC-UNIT-HAB4";
    /**
     * Habilitation à la gestion des propriété personnalisées
     */
    public static final String DOC_UNIT_HAB5 = "DOC-UNIT-HAB5";
    /**
     * Habilitation à la gestion des constats d'état: lecture
     */
    public static final String COND_REPORT_HAB0 = "COND-REPORT-HAB0";
    /**
     * Habilitation à la gestion des constats d'état: création
     */
    public static final String COND_REPORT_HAB1 = "COND-REPORT-HAB1";
    /**
     * Habilitation à la gestion des constats d'état: modification
     */
    public static final String COND_REPORT_HAB2 = "COND-REPORT-HAB2";
    /**
     * Habilitation à la gestion des constats d'état: suppression
     */
    public static final String COND_REPORT_HAB3 = "COND-REPORT-HAB3";
    /**
     * Habilitation à la gestion des constats d'état: export
     */
    public static final String COND_REPORT_HAB4 = "COND-REPORT-HAB4";
    /**
     * Habilitation à la gestion des constats d'état: configuration par défaut pour chaque bibliothèque
     */
    public static final String COND_REPORT_HAB5 = "COND-REPORT-HAB5";
    /**
     * Habilitation à la gestion des constats d'état: administration
     */
    public static final String COND_REPORT_HAB6 = "COND-REPORT-HAB6";

    // ne pas oublier de placer l'annotation @AuthorizationManager.Init sur la classe pour initialiser les relations entre autorisations
    static {
        AuthorizationManager.setRequirements(DOC_UNIT_HAB1, DOC_UNIT_HAB0);
        AuthorizationManager.setRequirements(DOC_UNIT_HAB2, DOC_UNIT_HAB0);
        AuthorizationManager.setRequirements(DOC_UNIT_HAB3, DOC_UNIT_HAB0);
        AuthorizationManager.setRequirements(DOC_UNIT_HAB4, DOC_UNIT_HAB0);

        AuthorizationManager.setRequirements(COND_REPORT_HAB1, COND_REPORT_HAB0);
        AuthorizationManager.setRequirements(COND_REPORT_HAB2, COND_REPORT_HAB0);
        AuthorizationManager.setRequirements(COND_REPORT_HAB3, COND_REPORT_HAB0);
        AuthorizationManager.setRequirements(COND_REPORT_HAB4, COND_REPORT_HAB0);
    }

    private AuthorizationConstants() {
    }
}
