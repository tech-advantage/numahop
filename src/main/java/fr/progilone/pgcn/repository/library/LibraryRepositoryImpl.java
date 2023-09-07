package fr.progilone.pgcn.repository.library;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.library.QLibrary;
import fr.progilone.pgcn.repository.util.QueryDSLBuilderUtils;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class LibraryRepositoryImpl implements LibraryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public LibraryRepositoryImpl(final JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<Library> search(final String search,
                                final List<String> libraries,
                                final String initiale,
                                final List<String> institutions,
                                final boolean isActive,
                                final Pageable pageable) {

        final QLibrary library = QLibrary.library;
        final BooleanBuilder builder = new BooleanBuilder();

        if (CollectionUtils.isNotEmpty(libraries)) {
            final BooleanExpression idFilter = library.identifier.in(libraries);
            builder.and(idFilter);
        }
        if (StringUtils.isNotBlank(search)) {
            final BooleanExpression nameFilter = library.name.containsIgnoreCase(search);
            builder.andAnyOf(nameFilter);
        }
        // Filter initiale
        QueryDSLBuilderUtils.addFilterForInitiale(builder, initiale, library.name);
        builder.and(library.superuser.isFalse());

        if (CollectionUtils.isNotEmpty(institutions)) {
            final BooleanExpression sitesFilter = library.institution.in(institutions);
            builder.and(sitesFilter);
        }
        // remplacer institution par active et recuperer l'information

        if (isActive) {
            builder.and(library.active.eq(true));
        }

        final JPAQuery<Library> baseQuery = queryFactory.selectDistinct(library).from(library).where(builder).orderBy(library.name.asc());

        if (pageable != null) {
            baseQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
        }

        final long total = queryFactory.select(library.countDistinct()).from(library).where(builder).fetchOne();

        return new PageImpl<>(baseQuery.fetch(), pageable, total);
    }
}
