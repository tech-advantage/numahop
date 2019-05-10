package fr.progilone.pgcn.domain.workflow;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.service.util.NumahopCollectors;

/**
 * Un Modèle de workflow
 * Permet d'instancier des {@link DocUnitWorkflow}
 * est formé par des {@link WorkflowModelState}
 * 
 * @author jbrunet
 * Créé le 12 juil. 2017
 */
@Entity
@Table(name = WorkflowModel.TABLE_NAME)
public class WorkflowModel extends AbstractDomainObject {
    
    public static final String TABLE_NAME = "workflow_model";
    
    @Column
    private String name;
    
    @Column(columnDefinition = "text")
    private String description;
    
    /**
     * {@link Library} qui possède le workflow model
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library", nullable = false)
    private Library library;
    
    @OneToMany(mappedBy = "model", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private final Set<DocUnitWorkflow> instances = new HashSet<>();

    @OneToMany(mappedBy = "model", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private final Set<WorkflowModelState> modelStates = new HashSet<>();
    
    @Column
    private boolean active;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<WorkflowModelState> getModelStates() {
        return modelStates;
    }

    public void setModelStates(Set<WorkflowModelState> states) {
        this.modelStates.clear();
        if(states != null) {
            states.forEach(this::addModelState);
        }
    }
    
    public void addModelState(WorkflowModelState state) {
        if(state != null) {
            modelStates.add(state);
        }
    }

    public Set<DocUnitWorkflow> getInstances() {
        return instances;
    }
    
    public void setInstances(List<DocUnitWorkflow> instances) {
        this.instances.clear();
        if(instances != null) {
            instances.forEach(this::addInstance);
        }
    }
    
    public void addInstance(DocUnitWorkflow instance) {
        if(instance != null) {
            instances.add(instance);
        }
    }

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
    /**
     * Retourne un modèle d'étape en fonction de sa {@link WorkflowStateKey}
     * @param key
     * @return
     */
    public WorkflowModelState getModelStateByKey(WorkflowStateKey key) {
        return modelStates.stream()
                .filter(state -> state.getKey().equals(key))
                .collect(NumahopCollectors.singletonCollector());
    }
}
