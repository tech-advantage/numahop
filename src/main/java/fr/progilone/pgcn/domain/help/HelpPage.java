package fr.progilone.pgcn.domain.help;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = HelpPage.TABLE_NAME)
public class HelpPage extends AbstractDomainObject {

    public static final String TABLE_NAME = "hlp_page";

    /**
     * Le titre de la page
     */
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Rang de la page (pas utilisé pour le moment...)
     */
    @Column(name = "rank", nullable = false)
    private int rank;

    /**
     * Le tag de la page
     */
    @Column(name = "tag", nullable = false)
    private String tag;

    /**
     * Le module de la page
     */
    @Column(name = "module")
    private String module;

    /**
     * Type de page
     */
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private HelpPageType type;

    /**
     * Le contenu de la page
     */
    @Column(name = "content", nullable = true, columnDefinition = "longtext")
    private String content;

    /**
     * La page parent de cette page, null si c'est la page de plus haut niveau (root)
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent", nullable = true, referencedColumnName = "identifier")
    private HelpPage parent;

    /**
     * Liste des pages "enfants" de cette page
     */
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final Set<HelpPage> children = new HashSet<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(final String tag) {
        this.tag = tag;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(final int rank) {
        this.rank = rank;
    }

    public String getModule() {
        return module;
    }

    public void setModule(final String module) {
        this.module = module;
    }

    public HelpPageType getType() {
        return type;
    }

    public void setType(final HelpPageType type) {
        this.type = type;
    }

    public HelpPage getParent() {
        return parent;
    }

    public void setParent(final HelpPage parent) {
        this.parent = parent;
    }

    public Set<HelpPage> getChildren() {
        return children;
    }
}
