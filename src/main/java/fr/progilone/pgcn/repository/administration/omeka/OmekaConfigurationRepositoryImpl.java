package fr.progilone.pgcn.repository.administration.omeka;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fr.progilone.pgcn.domain.administration.omeka.OmekaConfiguration;
import fr.progilone.pgcn.domain.administration.omeka.QOmekaConfiguration;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class OmekaConfigurationRepositoryImpl implements OmekaConfigurationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public OmekaConfigurationRepositoryImpl(final JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<OmekaConfiguration> search(final String search, final List<String> libraries, final Boolean omekas, final Pageable pageable) {

        final QOmekaConfiguration configuration = QOmekaConfiguration.omekaConfiguration;

        final BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.isNotBlank(search)) {
            final BooleanExpression nameFilter = configuration.label.containsIgnoreCase(search);
            builder.andAnyOf(nameFilter);
        }
        if (libraries != null && !libraries.isEmpty()) {
            final BooleanExpression libraryFilter = configuration.library.identifier.in(libraries);
            builder.and(libraryFilter);
        }
        if (omekas != null) {
            final BooleanExpression omekasFilter = configuration.omekas.eq(omekas);
            builder.and(omekasFilter);
        }

        final JPAQuery<OmekaConfiguration> baseQuery = queryFactory.selectDistinct(configuration).from(configuration).where(builder);

        final long total = baseQuery.clone().select(configuration.identifier.countDistinct()).fetchOne();

        if (pageable != null) {
            baseQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
        }

        final List<OmekaConfiguration> result = baseQuery.leftJoin(configuration.library).fetchJoin().orderBy(configuration.label.asc()).fetch();

        return new PageImpl<>(result, pageable, total);
    }
}
