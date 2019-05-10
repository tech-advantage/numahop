package fr.progilone.pgcn.domain.audit;

import com.mysema.query.annotations.QueryExclude;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@QueryExclude
@Table(name = AuditRevision.TABLE_NAME)
@RevisionEntity(AuditingRevisionListener.class)
public class AuditRevision extends DefaultRevisionEntity {

    public static final String TABLE_NAME = "aud_revision";

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }
}
