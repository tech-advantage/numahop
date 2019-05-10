package fr.progilone.pgcn.repository.user;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.BooleanExpression;
import fr.progilone.pgcn.domain.user.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class RoleRepositoryImpl implements RoleRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Role> search(String search,
                             List<String> authorizations) {

        final QRole role = QRole.role;
        final QAuthorization authorization = QAuthorization.authorization;
        final BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.isNotBlank(search)) {
            final BooleanExpression nameFilter =
                role.label.containsIgnoreCase(search).or(role.identifier.containsIgnoreCase(search));
            builder.andAnyOf(nameFilter);
        }

        if (CollectionUtils.isNotEmpty(authorizations)) {
            final BooleanExpression sitesFilter = authorization.identifier.in(authorizations);
            builder.and(sitesFilter);
        }

        final BooleanExpression excludeSuperRoleFilter = role.identifier.ne("SUPERROLE");
        builder.and(excludeSuperRoleFilter);

        final JPQLQuery baseQuery = new JPAQuery(em);

        final List<Role> result = baseQuery.from(role)
                                           .leftJoin(role.authorizations, authorization)
                                           .fetch()
                                           .where(builder.getValue())
                                           .orderBy(role.label.asc())
                                           .distinct()
                                           .list(role);
        return result;
    }
}
