package fr.progilone.pgcn.repository.user;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fr.progilone.pgcn.domain.library.QLibrary;
import fr.progilone.pgcn.domain.user.QUser;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.repository.util.QueryDSLBuilderUtils;
import fr.progilone.pgcn.security.SecurityUtils;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public UserRepositoryImpl(final JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<User> search(final String search,
                             final String initiale,
                             final boolean active,
                             final boolean filterProviders,
                             final List<String> libraries,
                             final List<User.Category> categories,
                             final List<String> roles,
                             final Pageable pageable) {

        final QUser user = QUser.user;
        final BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.isNotBlank(search)) {
            final BooleanExpression nameFilter = user.login.containsIgnoreCase(search).or(user.surname.containsIgnoreCase(search).or(user.firstname.containsIgnoreCase(search)));
            builder.andAnyOf(nameFilter);
        }
        // Filter initiale
        QueryDSLBuilderUtils.addFilterForInitiale(builder, initiale, user.surname);
        builder.and(user.superuser.isFalse());

        // active
        if (active) {
            builder.and(user.active.eq(true));
        }
        if (CollectionUtils.isNotEmpty(libraries)) {
            final BooleanExpression sitesFilter = user.library.identifier.in(libraries);
            builder.and(sitesFilter);
        }
        if (CollectionUtils.isNotEmpty(categories)) {
            final BooleanExpression categoryFilter = user.category.in(categories);
            builder.and(categoryFilter);
        }
        if (CollectionUtils.isNotEmpty(roles)) {
            final BooleanExpression rolesFilter = user.role.identifier.in(roles);
            builder.and(rolesFilter);
        }
        // On ne remonte pas les prestataires, sauf l'utilisateur courant
        if (filterProviders) {
            BooleanExpression filterPresta = user.category.ne(User.Category.PROVIDER);

            final String currentUserId = SecurityUtils.getCurrentUserId();
            if (currentUserId != null) {
                filterPresta = filterPresta.or(user.identifier.eq(currentUserId));
            }
            builder.and(filterPresta);
        }

        final JPAQuery<User> baseQuery = queryFactory.selectDistinct(user)
                                                     .from(user)
                                                     .leftJoin(user.library)
                                                     .fetchJoin()
                                                     .leftJoin(user.role)
                                                     .fetchJoin()
                                                     .where(builder)
                                                     .orderBy(user.surname.asc())
                                                     .orderBy(user.firstname.asc());

        if (pageable != null) {
            baseQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
        }

        final long total = queryFactory.select(user.countDistinct()).from(user).leftJoin(user.library).leftJoin(user.role).where(builder).fetchOne();

        return new PageImpl<>(baseQuery.fetch(), pageable, total);
    }

    @Override
    public List<Object[]> getUsersGroupByLibrary(final List<String> libraries) {
        final QUser qUser = QUser.user;
        final QLibrary qLibrary = QLibrary.library;
        final BooleanBuilder builder = new BooleanBuilder();

        // Bibliothèques
        if (CollectionUtils.isNotEmpty(libraries)) {
            builder.and(qLibrary.identifier.in(libraries));
        }
        builder.and(qLibrary.superuser.isFalse());

        return queryFactory.select(qLibrary.identifier, qLibrary.name, qUser.countDistinct())
                           .from(qUser)
                           .innerJoin(qUser.library, qLibrary)
                           .where(builder.getValue())
                           .groupBy(qLibrary.identifier, qLibrary.name)
                           .stream()
                           .map(Tuple::toArray)
                           .collect(Collectors.toList());
    }
}
