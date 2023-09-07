package fr.progilone.pgcn.repository.exportftpconfiguration;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fr.progilone.pgcn.domain.exportftpconfiguration.ExportFTPConfiguration;
import fr.progilone.pgcn.domain.exportftpconfiguration.QExportFTPConfiguration;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class ExportFTPConfigurationRepositoryImpl implements ExportFTPConfigurationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ExportFTPConfigurationRepositoryImpl(final JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<ExportFTPConfiguration> search(final String search, final List<String> libraries, final Pageable pageable) {

        final QExportFTPConfiguration configuration = QExportFTPConfiguration.exportFTPConfiguration;

        final BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.isNotBlank(search)) {
            final BooleanExpression nameFilter = configuration.label.containsIgnoreCase(search);
            builder.andAnyOf(nameFilter);
        }
        if (libraries != null && !libraries.isEmpty()) {
            final BooleanExpression libraryFilter = configuration.library.identifier.in(libraries);
            builder.and(libraryFilter);
        }

        final JPAQuery<ExportFTPConfiguration> baseQuery = queryFactory.selectDistinct(configuration)
                                                                       .from(configuration)
                                                                       .leftJoin(configuration.library)
                                                                       .fetchJoin()
                                                                       .where(builder.getValue())
                                                                       .orderBy(configuration.label.asc());

        if (pageable != null) {
            baseQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
        }

        final long total = queryFactory.select(configuration.countDistinct()).from(configuration).leftJoin(configuration.library).where(builder.getValue()).fetchOne();

        return new PageImpl<>(baseQuery.fetch(), pageable, total);
    }

}
