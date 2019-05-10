package fr.progilone.pgcn.repository.user;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.Tuple;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.BooleanExpression;
import fr.progilone.pgcn.domain.dto.user.SimpleUserDTO;
import fr.progilone.pgcn.domain.library.QLibrary;
import fr.progilone.pgcn.domain.user.QUser;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.repository.util.QueryDSLBuilderUtils;
import fr.progilone.pgcn.security.SecurityUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserRepositoryImpl implements UserRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    /**
     * récupère tous les usagers sous forme allégée
     *
     * @param fromDate
     *         date à partir de laquelle on veut faire un delta des modifications
     * @return liste contenant l'ensemble des SimpleUserDTO
     */
    @Override
    public List<SimpleUserDTO> findAllSimpleDTO(final Optional<Date> fromDate) {
        String q = "select distinct u from User u ";
        if (fromDate.isPresent()) {
            // on sélectionne tous ceux modifiés depuis la date
            q += " and b.lastModifiedDate > :lastModifiedDate ";
        }

        final TypedQuery<User> query = em.createQuery(q, User.class); // NOSONAR : Non il n'y a pas de possiblité d'injection SQL...
        if (fromDate.isPresent()) {
            query.setParameter("lastModifiedDate", fromDate.get());
        }

        final List<User> queryResult = query.getResultList();
        final SimpleUserDTO.Builder builder = new SimpleUserDTO.Builder();

        return queryResult.stream()
                          .map(result -> builder.reinit()
                                                .setFirstname(result.getFirstname())
                                                .setSurname(result.getSurname())
                                                .setFullname(result.getFullName())
                                                .build())
                          .collect(Collectors.toList());
    }

    @Override
    public Page<User> search(String search,
                             String initiale,
                             boolean active,
                             boolean filterProviders,
                             List<String> libraries,
                             List<User.Category> categories,
                             List<String> roles,
                             Pageable pageable) {

        final QUser user = QUser.user;
        final BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.isNotBlank(search)) {
            final BooleanExpression nameFilter =
                user.login.containsIgnoreCase(search).or(user.surname.containsIgnoreCase(search).or(user.firstname.containsIgnoreCase(search)));
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

        final JPQLQuery baseQuery = new JPAQuery(em);
        final JPQLQuery countQuery = new JPAQuery(em);

        if (pageable != null) {
            baseQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
        }

        final List<String> usersIdentifiers = countQuery.from(user)
                                                        .leftJoin(user.library)
                                                        .leftJoin(user.role)
                                                        .groupBy(user.identifier)
                                                        .where(builder.getValue())
                                                        .distinct()
                                                        .list(user.identifier);
        final long total = usersIdentifiers.size();

        final List<User> result = baseQuery.from(user)
                                           .leftJoin(user.library)
                                           .fetch()
                                           .leftJoin(user.role)
                                           .fetch()
                                           .where(builder.getValue())
                                           .orderBy(user.surname.asc())
                                           .orderBy(user.firstname.asc())
                                           .distinct()
                                           .list(user);
        return new PageImpl<>(result, pageable, total);
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

        return new JPAQuery(em).from(qUser)
                               .innerJoin(qUser.library, qLibrary)
                               .where(builder.getValue())
                               .groupBy(qLibrary.identifier, qLibrary.name)
                               .list(qLibrary.identifier, qLibrary.name, qUser.countDistinct())
                               .stream()
                               .map(Tuple::toArray)
                               .collect(Collectors.toList());
    }
}
