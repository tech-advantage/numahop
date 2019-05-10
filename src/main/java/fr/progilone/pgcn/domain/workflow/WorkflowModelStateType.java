package fr.progilone.pgcn.domain.workflow;

/**
 * Indique si l'étape devra être remplie ou passée
 * 
 * @author jbrunet
 * Créé le 12 oct. 2017
 */
public enum WorkflowModelStateType {
    REQUIRED, // Etape à faire
    TO_SKIP,  // Etape non requise (passée)
    TO_WAIT,  // Attente d'action système (numérisation)
    OTHER;    // Indique un autre type d'état (début, fin..)
}
