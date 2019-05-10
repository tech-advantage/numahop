package fr.progilone.pgcn.domain.audit;

import org.hibernate.envers.RevisionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditingRevisionListener implements RevisionListener {

    private static final Logger LOG = LoggerFactory.getLogger(RevisionListener.class);

    @Override
    public void newRevision(final Object revisionEntity) {
        LOG.debug("AuditingRevisionListener newRevision: {}", revisionEntity);
        final AuditRevision revision = (AuditRevision) revisionEntity;
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            revision.setUsername(auth.getName());
        }
    }
}
