package fr.progilone.pgcn.repository.administration.viewsformat;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fr.progilone.pgcn.domain.administration.viewsformat.QViewsFormatConfiguration;
import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class ViewsFormatConfigurationRepositoryImpl implements ViewsFormatConfigurationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ViewsFormatConfigurationRepositoryImpl(final JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<ViewsFormatConfiguration> search(final String search, final List<String> libraries, final Pageable pageable) {

        final QViewsFormatConfiguration configuration = QViewsFormatConfiguration.viewsFormatConfiguration;
        final BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.isNotBlank(search)) {
            final BooleanExpression nameFilter = configuration.label.containsIgnoreCase(search);
            builder.andAnyOf(nameFilter);
        }
        if (libraries != null && !libraries.isEmpty()) {
            final BooleanExpression libraryFilter = configuration.library.identifier.in(libraries);
            builder.and(libraryFilter);
        }

        final JPAQuery<ViewsFormatConfiguration> baseQuery = queryFactory.selectDistinct(configuration).from(configuration).where(builder);

        final long total = baseQuery.clone().select(configuration.identifier.countDistinct()).fetchOne();

        if (pageable != null) {
            baseQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
        }

        final List<ViewsFormatConfiguration> result = baseQuery.leftJoin(configuration.library).fetchJoin().orderBy(configuration.label.asc()).fetch();

        return new PageImpl<>(result, pageable, total);
    }

}
