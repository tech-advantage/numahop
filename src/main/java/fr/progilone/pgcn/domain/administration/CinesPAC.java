package fr.progilone.pgcn.domain.administration;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = CinesPAC.TABLE_NAME)
public class CinesPAC extends AbstractDomainObject {

    public static final String TABLE_NAME = "conf_classement_pac";

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "conf_pac")
    private SftpConfiguration confPac;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public SftpConfiguration getConfPac() {
        return confPac;
    }

    public void setConfPac(final SftpConfiguration confPac) {
        this.confPac = confPac;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CinesPAC that = (CinesPAC) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
