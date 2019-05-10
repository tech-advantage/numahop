package fr.progilone.pgcn.repository.library;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.BooleanExpression;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.library.QLibrary;
import fr.progilone.pgcn.repository.util.QueryDSLBuilderUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class LibraryRepositoryImpl implements LibraryRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<Library> search(String search, List<String> libraries, String initiale, List<String> institutions, boolean isActive, Pageable pageable) {

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

        final JPQLQuery baseQuery = new JPAQuery(em);
        final JPQLQuery countQuery = new JPAQuery(em);

        if (pageable != null) {
            baseQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
        }

        final List<String> librariesIdentifiers = countQuery.from(library)
                                                            .groupBy(library.identifier)
                                                            .where(builder.getValue())
                                                            .distinct()
                                                            .list(library.identifier);
        final long total = librariesIdentifiers.size();

        final List<Library> result = baseQuery.from(library)
                                              .where(builder.getValue())
                                              .orderBy(library.name.asc())
                                              .distinct()
                                              .list(library);
        return new PageImpl<>(result, pageable, total);
    }
}
