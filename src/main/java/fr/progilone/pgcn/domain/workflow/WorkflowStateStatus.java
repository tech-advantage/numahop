package fr.progilone.pgcn.domain.workflow;

public enum WorkflowStateStatus {
    NOT_STARTED, // Tâche non commencée
    PENDING,     // Tâche en cours
    FINISHED,    // Tâche accomplie
    CANCELED,    // Tâche annulée
    FAILED,      // Tâche échouée
    TO_WAIT,     // Tâche qui sera à attendre
    WAITING,     // En attente d'action système
    WAITING_NEXT_COMPLETED, // En attente de la complétion de la prochaine étape
    TO_SKIP,     // Tâche qui ne sera pas accomplie (optionnelle uniquement)
    SKIPPED;     // Tâche optionnelle qui a été passée
}
