package fr.progilone.pgcn.domain.library;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import fr.progilone.pgcn.domain.AbstractDomainObject;

/**
 * Classe représentant un paramétrage dédié d'un service pour une bibliothèque
 * 
 * @author jbrunet
 * Créé le 23 févr. 2017
 */
@Entity
@Table(name = LibraryParameter.TABLE_NAME)
@NamedEntityGraph(name = "LibraryParameter.values", attributeNodes = @NamedAttributeNode("values"))
public class LibraryParameter extends AbstractDomainObject {

	public static final String TABLE_NAME = "lib_parameter";
	
	/**
     * Type de paramètre
     */
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private LibraryParameterType type;
    
    /**
     * Bibliothèque rattachée
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "library")
    private Library library;
    
    /**
     * Valeurs liées
     */
    @OneToMany(mappedBy = "parameter", orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final Set<AbstractLibraryParameterValue> values = new HashSet<>();

    
    public LibraryParameterType getType() {
        return type;
    }

    public void setType(LibraryParameterType type) {
        this.type = type;
    }

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    public Set<AbstractLibraryParameterValue> getValues() {
        return values;
    }
    
    public void setValues(final Set<AbstractLibraryParameterValue> values) {
        this.values.clear();
        if (values != null) {
            values.forEach(this::addValue);
        }
    }

    public void addValue(final AbstractLibraryParameterValue value) {
        if (value != null) {
            this.values.add(value);
        }
    }

    public enum LibraryParameterType {
    	CINES_EXPORT /* paramètres pour le Cines */
    }
}
