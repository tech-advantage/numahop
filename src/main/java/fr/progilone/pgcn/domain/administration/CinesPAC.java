package fr.progilone.pgcn.domain.administration;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

import fr.progilone.pgcn.domain.AbstractDomainObject;

@Entity
@Table(name = CinesPAC.TABLE_NAME)
public class CinesPAC extends AbstractDomainObject {

    public static final String TABLE_NAME = "conf_classement_pac";

    @Column(name = "name", nullable = false)
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String name;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "conf_pac")
    private SftpConfiguration confPac;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SftpConfiguration getConfPac() {
        return confPac;
    }

    public void setConfPac(SftpConfiguration confPac) {
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
