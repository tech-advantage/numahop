package fr.progilone.pgcn.domain.document;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant une fratrie d'unités documentaires
 */
@Entity
@Table(name = DocSibling.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "doc_unit_sibling", value = DocSibling.class)})
public class DocSibling extends AbstractDomainObject {

    public static final String TABLE_NAME = "doc_unit_sibling";

    /**
     * Liste des unités documentaires soeurs
     */
    @OneToMany(mappedBy = "sibling", fetch = FetchType.EAGER)
    private final List<DocUnit> docUnits = new ArrayList<>();

    public List<DocUnit> getDocUnits() {
        return docUnits;
    }

    public void setDocUnits(final List<DocUnit> docUnits) {
        this.docUnits.clear();
        if (docUnits != null) {
            docUnits.forEach(this::addDocUnit);
        }
    }

    private void addDocUnit(final DocUnit docUnit) {
        if (docUnit != null) {
            this.docUnits.add(docUnit);
        }
    }
}
