package fr.progilone.pgcn.domain.workflow;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

/**
 * Permet de générer des {@link DocUnitState} à réaliser par
 * un {@link User} appartenant à un {@link WorkflowGroup}
 *
 * @author jbrunet
 *         Créé le 12 juil. 2017
 */
@Entity
@Table(name = WorkflowModelState.TABLE_NAME)
public class WorkflowModelState extends AbstractDomainObject {

    public static final String TABLE_NAME = "workflow_model_state";

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "`group`")
    private WorkflowGroup group;

    /**
     * Durée maximale pour réaliser la tâche depuis son début
     */
    @Column(name = "limit_duration")
    private Duration limitDuration;

    @Column(name = "`key`", nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkflowStateKey key;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkflowModelStateType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model", nullable = false)
    private WorkflowModel model;

    @OneToMany(mappedBy = "modelState", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private final Set<DocUnitState> instances = new HashSet<>();

    public WorkflowModel getModel() {
        return model;
    }

    public void setModel(WorkflowModel model) {
        this.model = model;
    }

    public Duration getLimitDuration() {
        return limitDuration;
    }

    public void setLimitDuration(Duration limitDuration) {
        this.limitDuration = limitDuration;
    }

    public WorkflowStateKey getKey() {
        return key;
    }

    public void setKey(WorkflowStateKey key) {
        this.key = key;
    }

    public WorkflowModelStateType getType() {
        return type;
    }

    public void setType(WorkflowModelStateType type) {
        this.type = type;
    }

    public WorkflowGroup getGroup() {
        return group;
    }

    public void setGroup(WorkflowGroup group) {
        this.group = group;
    }

    public Set<DocUnitState> getInstances() {
        return instances;
    }
}
