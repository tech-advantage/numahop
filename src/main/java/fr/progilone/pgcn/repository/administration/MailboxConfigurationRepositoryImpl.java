package fr.progilone.pgcn.repository.administration;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fr.progilone.pgcn.domain.administration.MailboxConfiguration;
import fr.progilone.pgcn.domain.administration.QMailboxConfiguration;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class MailboxConfigurationRepositoryImpl implements MailboxConfigurationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MailboxConfigurationRepositoryImpl(final JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<MailboxConfiguration> search(final String search, final List<String> libraries, final boolean active) {

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

        return queryFactory.selectDistinct(qConf).from(qConf).where(builder).orderBy(qConf.label.asc()).fetch();
    }
}
