package fr.progilone.pgcn.web.rest.ocrlangconfiguration.security;

import fr.progilone.pgcn.web.util.AuthorizationManager;

@AuthorizationManager.Init
public final class AuthorizationConstants {

    /**
     * Habilitation configuration des langages OCR: lecture
     */
    public static final String OCR_LANG_HAB0 = "OCR-LANG-HAB0";

    /**
     * Habilitation configuration des langages OCR: créer / modifier
     */
    public static final String OCR_LANG_HAB1 = "OCR-LANG-HAB1";

    /**
     * Habilitation configuration des langages OCR: supprimer
     */
    public static final String OCR_LANG_HAB2 = "OCR-LANG-HAB2";

    // ne pas oublier de placer l'annotation @AuthorizationManager.Init sur la classe pour initialiser les relations entre autorisations
    static {
        AuthorizationManager.setRequirements(OCR_LANG_HAB1, OCR_LANG_HAB0);
        AuthorizationManager.setRequirements(OCR_LANG_HAB2, OCR_LANG_HAB0);
    }

    private AuthorizationConstants() {
    }
}
