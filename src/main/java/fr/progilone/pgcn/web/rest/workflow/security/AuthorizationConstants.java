package fr.progilone.pgcn.web.rest.workflow.security;

import fr.progilone.pgcn.web.util.AuthorizationManager;

/**
 * Habilitations pour les workflows
 * 
 * @author jbrunet
 * Créé le 17 juil. 2017
 */
@AuthorizationManager.Init
public final class AuthorizationConstants {
    /**
     * Habilitation à la gestion des workflows: créer
     */
    public static final  String WORKFLOW_HAB1 ="WORKFLOW-HAB1";
    /**
     * Habilitation à la gestion des workflows: Modifier
     */
    public static final  String WORKFLOW_HAB2 ="WORKFLOW-HAB2";
    /**
     * Habilitation à la gestion des workflows: Supprimer
     */
    public static final  String WORKFLOW_HAB3 ="WORKFLOW-HAB3";
    /**
     * Habilitation à la gestion des workflows: Visualisation
     */
    public static final  String WORKFLOW_HAB4 ="WORKFLOW-HAB4";

    // ne pas oublier de placer l'annotation @AuthorizationManager.Init sur la classe pour initialiser les relations entre autorisations
    static {
        AuthorizationManager.setRequirements(AuthorizationConstants.WORKFLOW_HAB1, AuthorizationConstants.WORKFLOW_HAB4);
        AuthorizationManager.setRequirements(AuthorizationConstants.WORKFLOW_HAB2, AuthorizationConstants.WORKFLOW_HAB4);
        AuthorizationManager.setRequirements(AuthorizationConstants.WORKFLOW_HAB3, AuthorizationConstants.WORKFLOW_HAB4);
    }

    private AuthorizationConstants() {
    }
}
