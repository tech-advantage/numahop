package fr.progilone.pgcn.repository.administration;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.BooleanExpression;
import fr.progilone.pgcn.domain.administration.MailboxConfiguration;
import fr.progilone.pgcn.domain.administration.QMailboxConfiguration;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class MailboxConfigurationRepositoryImpl implements MailboxConfigurationRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<MailboxConfiguration> search(final String search, List<String> libraries, final boolean active) {

        final QMailboxConfiguration qConf = QMailboxConfiguration.mailboxConfiguration;
        final BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.isNotBlank(search)) {
            final BooleanExpression nameFilter = qConf.label.containsIgnoreCase(search);
            builder.andAnyOf(nameFilter);
        }
        if (CollectionUtils.isNotEmpty(libraries)) {
            final BooleanExpression libraryFilter = qConf.library.identifier.in(libraries);
            builder.and(libraryFilter);
        }
        if (active) {
            builder.and(qConf.active.eq(true));
        }

        return new JPAQuery(em).from(qConf).where(builder.getValue()).distinct().orderBy(qConf.label.asc()).list(qConf);
    }
}
