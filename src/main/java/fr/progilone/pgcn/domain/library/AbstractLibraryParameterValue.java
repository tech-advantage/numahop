package fr.progilone.pgcn.domain.library;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import jakarta.persistence.*;

/**
 * Valeur pour un paramètre système
 *
 * @author jbrunet
 *         Créé le 23 févr. 2017
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@JsonSubTypes({@JsonSubTypes.Type(name = "lib_parameter_value_cines", value = LibraryParameterValueCines.class)})
public abstract class AbstractLibraryParameterValue extends AbstractDomainObject {

    /**
     * Type de paramètre
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "parameter")
    private LibraryParameter parameter;

    public LibraryParameter getParameter() {
        return parameter;
    }

    public void setParameter(LibraryParameter parameter) {
        this.parameter = parameter;
    }

}
