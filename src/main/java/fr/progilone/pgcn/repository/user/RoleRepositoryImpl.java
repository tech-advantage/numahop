package fr.progilone.pgcn.repository.user;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fr.progilone.pgcn.domain.user.QAuthorization;
import fr.progilone.pgcn.domain.user.QRole;
import fr.progilone.pgcn.domain.user.Role;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class RoleRepositoryImpl implements RoleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public RoleRepositoryImpl(final JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<Role> search(final String search, final List<String> authorizations) {

        final QRole role = QRole.role;
        final QAuthorization authorization = QAuthorization.authorization;
        final BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.isNotBlank(search)) {
            final BooleanExpression nameFilter = role.label.containsIgnoreCase(search).or(role.identifier.containsIgnoreCase(search));
            builder.andAnyOf(nameFilter);
        }

        if (CollectionUtils.isNotEmpty(authorizations)) {
            final BooleanExpression sitesFilter = authorization.identifier.in(authorizations);
            builder.and(sitesFilter);
        }

        final BooleanExpression excludeSuperRoleFilter = role.identifier.ne("SUPERROLE");
        builder.and(excludeSuperRoleFilter);

        return queryFactory.selectDistinct(role).from(role).leftJoin(role.authorizations, authorization).fetchJoin().where(builder).orderBy(role.label.asc()).fetch();
    }
}
