package fr.progilone.pgcn.repository.project;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.expr.BooleanExpression;
import fr.progilone.pgcn.domain.library.QLibrary;
import fr.progilone.pgcn.domain.lot.QLot;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.project.QProject;
import fr.progilone.pgcn.domain.user.QUser;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.util.CustomUserDetails;
import fr.progilone.pgcn.repository.project.helper.ProjectSearchBuilder;
import fr.progilone.pgcn.repository.util.QueryDSLBuilderUtils;
import fr.progilone.pgcn.security.SecurityUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProjectRepositoryImpl implements ProjectRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<Project> search(final ProjectSearchBuilder searchBuilder, final Pageable pageable) {

        final QLibrary qAssociatedLibrary = QLibrary.library;
        final QLot qLot = QLot.lot;
        final QProject qProject = QProject.project;
        final QUser qAssociatedUser = QUser.user;

        final BooleanBuilder builder = new BooleanBuilder();

        // Nom du projet
        searchBuilder.getSearch().ifPresent(search -> {
            builder.andAnyOf(qProject.name.containsIgnoreCase(search));
        });
        // Initiale
        QueryDSLBuilderUtils.addFilterForInitiale(builder, searchBuilder.getInitiale().orElse(null), qProject.name);

        // active: true => filtrage des projets actifs, false => pas de filtrage
        if (searchBuilder.isActive()) {
            builder.and(qProject.active.eq(true));
        }
        // Statuts
        searchBuilder.getStatuses().ifPresent(statuses -> {
            builder.and(qProject.status.in(statuses));
        });
        // Prestataires
        searchBuilder.getProviders().ifPresent(providers -> {
            builder.and(qProject.provider.identifier.in(providers));
        });
        // Projets
        searchBuilder.getProjects().ifPresent(projects -> {
            builder.and(qProject.identifier.in(projects));
        });
        // Droits d'accès
        final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        if (currentUser != null) {
            // Prestataires
            if (currentUser.getCategory() == User.Category.PROVIDER) {
                final BooleanExpression providerFilter =
                    // prestataire sur le projet
                    qProject.provider.identifier.eq(currentUser.getIdentifier())
                                                // prestataire sur un lot du projet
                                                .or(qLot.provider.identifier.eq(currentUser.getIdentifier()));
                builder.and(providerFilter);
            }
            // Bibliothèques & intervenants
            searchBuilder.getLibraries().ifPresent(libraries -> {
                builder.and(
                    // bibliothèque du projet
                    qProject.library.identifier.in(libraries)
                                               // bibliothèque partenaire
                                               .or(qAssociatedLibrary.identifier.in(libraries)
                                                                                // pas d'intervenant défini
                                                                                .and(qAssociatedUser.isNull()
                                                                                                    // l'utilisateur fait partie des intervenants
                                                                                                    .or(qAssociatedUser.identifier.contains(
                                                                                                        currentUser.getIdentifier())))));
            });
        }
        // Intervalle de dates
        searchBuilder.getFrom().ifPresent(from -> {
            builder.and(new BooleanBuilder().or(qProject.realEndDate.isNull()).or(qProject.realEndDate.after(from)));
        });
        searchBuilder.getTo().ifPresent(to -> {
            builder.and(new BooleanBuilder().or(qProject.startDate.isNull()).or(qProject.startDate.before(to)));
        });

        final JPQLQuery baseQuery = new JPAQuery(em);
        final JPQLQuery countQuery = new JPAQuery(em);

        if (pageable != null) {
            baseQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
            applySorting(pageable.getSort(), baseQuery, qProject);
        }

        final List<String> projectsIdentifiers = countQuery.from(qProject)
                                                           .leftJoin(qProject.library)
                                                           .leftJoin(qProject.lots, qLot)
                                                           .leftJoin(qProject.associatedLibraries, qAssociatedLibrary)
                                                           .leftJoin(qProject.associatedUsers, qAssociatedUser)
                                                           .groupBy(qProject.identifier)
                                                           .where(builder.getValue())
                                                           .distinct()
                                                           .list(qProject.identifier);
        final List<Project> result = baseQuery.from(qProject)
                                              .leftJoin(qProject.library)
                                              .fetch()
                                              .leftJoin(qProject.lots, qLot)
                                              .leftJoin(qProject.associatedLibraries, qAssociatedLibrary)
                                              .leftJoin(qProject.associatedUsers, qAssociatedUser)
                                              .where(builder.getValue())
                                              .orderBy(qProject.name.asc())
                                              .distinct()
                                              .list(qProject);

        final long total = projectsIdentifiers.size();
        return new PageImpl<>(result, pageable, total);
    }

    protected JPQLQuery applySorting(final Sort sort, final JPQLQuery query, final QProject qProject) {
        if (sort == null) {
            return query;
        }
        final List<OrderSpecifier> orders = new ArrayList<>();

        for (final Sort.Order order : sort) {
            final Order qOrder = order.isAscending() ? Order.ASC : Order.DESC;
            if ("name".equals(order.getProperty())) {
                orders.add(new OrderSpecifier<>(qOrder, qProject.name));
            }
            if ("total".equals(order.getProperty())) {
                orders.add(new OrderSpecifier<>(qOrder, qProject.docUnits.size()));
            }
            if ("status".equals(order.getProperty())) {
                orders.add(new OrderSpecifier<>(qOrder, qProject.status));
            }
        }
        return query.orderBy(orders.toArray(new OrderSpecifier[0]));
    }

    @Override
    public List<Project> findProjectsForWidget(final LocalDate fromDate, final List<String> libraries, final List<Project.ProjectStatus> statuses) {

        final QProject qProject = QProject.project;
        final QLibrary qLibrary = QLibrary.library;
        final BooleanBuilder builder = new BooleanBuilder();
        if (CollectionUtils.isNotEmpty(libraries)) {
            builder.and(qLibrary.identifier.in(libraries));
        }
        if (CollectionUtils.isNotEmpty(statuses)) {
            builder.and(qProject.status.in(statuses));
        }
        if (fromDate != null) {
            builder.and(qProject.startDate.after(fromDate));
        }
        // Requête
        return new JPAQuery(em).from(qProject).leftJoin(qProject.library, qLibrary).where(builder.getValue()).list(qProject);
    }
}
