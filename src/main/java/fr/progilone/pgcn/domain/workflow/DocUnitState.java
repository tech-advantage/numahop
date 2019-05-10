package fr.progilone.pgcn.domain.workflow;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.common.base.MoreObjects;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.user.User;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "`key`")
@Table(name = DocUnitState.TABLE_NAME)
public abstract class DocUnitState extends AbstractDomainObject {

    public static final String TABLE_NAME = "doc_workflow_state";
    protected static final String SYSTEM_USER = "system";

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkflowStateStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "model_state", nullable = false)
    private WorkflowModelState modelState;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "workflow", nullable = false)
    private DocUnitWorkflow workflow;

    /**
     * Date de début effective de la tache
     */
    @Column(name = "start_date")
    private LocalDateTime startDate;
    /**
     * Date de fin effective de la tache (réalisation)
     */
    @Column(name = "end_date")
    private LocalDateTime endDate;

    /**
     * Date à partir de laquelle la tâche est considérée comme "en retard"
     * calculée à partir du début (startDate) de la tâche
     * et du temps imparti {@link WorkflowModelState#getLimitDuration()}
     */
    @Column(name = "due_date")
    private LocalDateTime dueDate;

    /**
     * Champ non modifiable pour requêter sur le discriminator
     */
    @Column(name = "`key`", insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private WorkflowStateKey discriminator;

    /**
     * Utilisateur qui complète la tâche (username pour ne pas bloquer les utilisateurs)
     * {@link User#getLogin()}
     */
    @Column
    private String user;

    public WorkflowModelState getModelState() {
        return modelState;
    }

    public WorkflowStateStatus getStatus() {
        return status;
    }

    public void setStatus(final WorkflowStateStatus status) {
        this.status = status;
    }

    public void setModelState(final WorkflowModelState modelState) {
        this.modelState = modelState;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(final LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    public DocUnitWorkflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(final DocUnitWorkflow workflow) {
        this.workflow = workflow;
    }

    public WorkflowStateKey getDiscriminator() {
        return discriminator;
    }

    public void setDiscriminator(final WorkflowStateKey discriminator) {
        this.discriminator = discriminator;
    }

    /**
     * Retourne vrai si l'étape ne s'est pas encore finie
     *
     * @return
     */
    public boolean isFutureOrCurrentState() {
        return WorkflowStateStatus.NOT_STARTED.equals(status) || WorkflowStateStatus.PENDING.equals(status) || WorkflowStateStatus.TO_SKIP.equals(
            status) || WorkflowStateStatus.TO_WAIT.equals(status) || WorkflowStateStatus.WAITING.equals(status);
    }

    /**
     * Retourne vrai si l'étape est en cours
     *
     * @return
     */
    public boolean isCurrentState() {
        return WorkflowStateStatus.PENDING.equals(status)
               || WorkflowStateStatus.WAITING.equals(status)
               || WorkflowStateStatus.WAITING_NEXT_COMPLETED.equals(status);
    }

    /**
     * Récupération de la clé
     *
     * @return
     */
    public abstract WorkflowStateKey getKey();

    /**
     * Traitement de {@link DocUnitState} du {@link DocUnitWorkflow} par {@link User}
     *
     * @param user
     */
    public abstract void process(User user);

    /**
     * Rejet de {@link DocUnitState} du {@link DocUnitWorkflow} par {@link User}
     *
     * @param user
     */
    public abstract void reject(User user);

    /**
     * Récupération des étapes suivantes
     *
     * @return
     */
    protected abstract List<DocUnitState> getNextStates();

    /**
     * Initialisation de l'étape
     *
     * @param startDate
     *         (si null alors {@link LocalDateTime#now()})
     * @param endDate
     * @param status
     *         (si non précisé, calculé automatiquement)
     */
    public void initializeState(LocalDateTime startDate, final LocalDateTime endDate, final WorkflowStateStatus status) {
        if (isDone()) {
            return;
        }
        if (startDate == null) {
            startDate = LocalDateTime.now();
        }
        setStartDate(startDate);

        if (endDate != null) {
            setEndDate(endDate);
        }

        if (status != null) {
            setStatus(status);
            if (WorkflowStateStatus.FINISHED.equals(status)
                || WorkflowStateStatus.FAILED.equals(status)
                || WorkflowStateStatus.CANCELED.equals(status)
                || WorkflowStateStatus.SKIPPED.equals(status)) {
                setUser(SYSTEM_USER);
                setEndDate(startDate);
            }
        } else {
            if (getModelState() != null) {
                if (getModelState().getLimitDuration() != null) {
                    setDueDate(startDate.plus(getModelState().getLimitDuration()));
                }
                switch (getModelState().getType()) {
                    case REQUIRED:
                        setStatus(WorkflowStateStatus.PENDING);
                        break;
                    case TO_SKIP:
                        setStatus(WorkflowStateStatus.SKIPPED);
                        setUser(SYSTEM_USER);
                        setEndDate(startDate);
                        // On continue d'initialiser si il n'y a pas d'étape en attente ou juste la validation de notice
                        final List<DocUnitState> currentStates = getWorkflow().getCurrentStates();
                        if(currentStates.isEmpty() 
                                || (currentStates.size() == 1 
                                        && WorkflowStateKey.VALIDATION_NOTICES == currentStates.get(0).getKey())) {
                            for (final DocUnitState state : getNextStates()) {
                                state.initializeState(startDate, null, null);
                            }
                        }
                        break;
                    case TO_WAIT:
                        // Cas spécial de l'étape de NUMERISATION_EN_ATTENTE qui est validé ultérieurement
                        if (WorkflowStateKey.NUMERISATION_EN_ATTENTE.equals(getKey())) {
                            setStatus(WorkflowStateStatus.WAITING_NEXT_COMPLETED);
                            // On démarre la prochaine étape
                            for (final DocUnitState state : getNextStates()) {
                                state.initializeState(startDate, null, null);
                            }
                        } else {
                            setStatus(WorkflowStateStatus.WAITING);
                        }

                        break;
                    default:
                        setStatus(WorkflowStateStatus.NOT_STARTED);
                        break;

                }
            }
        }

        // Cas de l'étape de fin
        if(WorkflowStateKey.CLOTURE_DOCUMENT.equals(getKey())) {
            setStatus(WorkflowStateStatus.FINISHED);
            setUser(SYSTEM_USER);
            setEndDate(startDate);
        }
    }

    /**
     * Retourne vrai si la tâche s'est déjà déroulée
     *
     * @return
     */
    public boolean isDone() {
        return endDate != null;
    }

    /**
     * Retourne vrai si la tâche est en cours
     *
     * @return
     */
    public boolean isRunning() {
        return startDate != null && endDate == null;
    }

    /**
     * Retourne vrai si la tâche a été ignorée.
     *
     * @return
     */
    public boolean isSkipped() {
        return WorkflowStateStatus.SKIPPED.equals(status);
    }

    
    /**
     * Retourne vrai si la tâche a été ignorée ou annulée.
     *
     * @return
     */
    public boolean isSkippedOrCanceled() {
        return WorkflowStateStatus.CANCELED.equals(status) || WorkflowStateStatus.SKIPPED.equals(status);
    }

    /**
     * Retourne vrai si la tâche est en attente system
     *
     * @return
     */
    public boolean isWaiting() {
        return startDate != null && endDate == null 
                && (WorkflowStateStatus.WAITING.equals(status) || WorkflowStateStatus.PENDING.equals(status));
    }

    /**
     * Retourne vrai si la tâche s'est déjà déroulée et a été validée
     *
     * @return
     */
    public boolean isValidated() {
        return isDone() && WorkflowStateStatus.FINISHED.equals(status);
    }
    
    public boolean isRejected() {
        return isDone() && WorkflowStateStatus.FAILED.equals(status);
    }

    /**
     * Mise à jour de la date de réalisation de la tâche
     */
    protected void processEndDate() {
        setEndDate(LocalDateTime.now());
    }

    /**
     * Inscription de l'utilisateur
     *
     * @param user
     */
    protected void processUser(final User user) {
        if (user != null) {
            setUser(user.getLogin());
        } else {
            setUser(SYSTEM_USER);
        }
    }

    /**
     * Mise à jour du statut
     */
    protected void processStatus() {
        // Normalement géré à l'initialisation
        if (WorkflowStateStatus.TO_SKIP.equals(status)) {
            setStatus(WorkflowStateStatus.SKIPPED);
        } else {
            setStatus(WorkflowStateStatus.FINISHED);
        }
    }

    /**
     * Echec de la tâche
     */
    protected void failStatus() {
        setStatus(WorkflowStateStatus.FAILED);
    }

    /**
     * Retire les states inexistantes
     * @param states
     */
    protected void cleanNullStates(final List<DocUnitState> states) {
        if(states != null) {
            states.removeIf(Objects::isNull);
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .omitNullValues()
                          .add("key", getKey())
                          .add("status", status)
                          .add("started", startDate)
                          .add("ended", endDate)
                          .add("User", user)
                          .toString();
    }
}
