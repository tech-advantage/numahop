package fr.progilone.pgcn.domain.workflow;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * Un groupe de {@link User} qui a la responsabilité d'une étape de workflow
 *
 * @author jbrunet
 *         Créé le 12 juil. 2017
 */
@Entity
@Table(name = WorkflowGroup.TABLE_NAME)
public class WorkflowGroup extends AbstractDomainObject {

    public static final String TABLE_NAME = "workflow_group";
    public static final String TABLE_NAME_GROUP_USER = "workflow_group_user";

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {})
    @JoinTable(name = TABLE_NAME_GROUP_USER,
               joinColumns = {@JoinColumn(name = "workflow_group", referencedColumnName = "identifier")},
               inverseJoinColumns = {@JoinColumn(name = "workflow_user", referencedColumnName = "identifier")})
    private final Set<User> users = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "group")
    private final Set<WorkflowModelState> states = new HashSet<>();

    /**
     * {@link Library} qui possède le groupe
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library", nullable = false)
    private Library library;

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(final Set<User> users) {
        this.users.clear();
        if (users != null) {
            users.forEach(this::addUser);
        }
    }

    public void addUser(final User user) {
        if (user != null) {
            this.users.add(user);
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Set<WorkflowModelState> getStates() {
        return states;
    }

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

}
