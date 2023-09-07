package fr.progilone.pgcn.repository.document;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.QBibliographicRecord;
import fr.progilone.pgcn.domain.document.QDocProperty;
import fr.progilone.pgcn.domain.document.QDocUnit;
import fr.progilone.pgcn.domain.document.QPhysicalDocument;
import fr.progilone.pgcn.domain.exchange.cines.QCinesReport;
import fr.progilone.pgcn.domain.exchange.internetarchive.QInternetArchiveReport;
import fr.progilone.pgcn.domain.library.QLibrary;
import fr.progilone.pgcn.domain.lot.QLot;
import fr.progilone.pgcn.domain.project.QProject;
import fr.progilone.pgcn.domain.user.QUser;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.util.CustomUserDetails;
import fr.progilone.pgcn.domain.workflow.QDocUnitState;
import fr.progilone.pgcn.domain.workflow.QDocUnitWorkflow;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.domain.workflow.WorkflowStateStatus;
import fr.progilone.pgcn.repository.util.QueryDSLBuilderUtils;
import fr.progilone.pgcn.security.SecurityUtils;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class DocUnitRepositoryImpl implements DocUnitRepositoryCustom {

    private static final Logger LOG = LoggerFactory.getLogger(DocUnitRepositoryImpl.class);

    private final JPAQueryFactory queryFactory;

    public DocUnitRepositoryImpl(final JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<DocUnit> search(final String search,
                                final boolean hasDigitalDocuments,
                                final boolean active,
                                final boolean archived,
                                final boolean nonArchived,
                                final boolean archivable,
                                final boolean nonArchivable,
                                final boolean distributed,
                                final boolean nonDistributed,
                                final boolean distributable,
                                final boolean nonDistributable,
                                final List<String> libraries,
                                final List<String> projects,
                                final List<String> lots,
                                final List<String> trains,
                                final List<String> statuses,
                                final LocalDate lastModifiedDateFrom,
                                final LocalDate lastModifiedDateTo,
                                final LocalDate createdDateFrom,
                                final LocalDate createdDateTo,
                                final List<String> identifiers,
                                final Pageable pageable) {

        final QDocUnit doc = QDocUnit.docUnit;
        final QProject project = QProject.project;
        final QLot lot = QLot.lot;
        final QDocUnitWorkflow workflow = QDocUnitWorkflow.docUnitWorkflow;
        final QDocUnitState state = QDocUnitState.docUnitState;
        final QLibrary associatedLibrary = QLibrary.library;
        final QUser associatedUser = QUser.user;
        final QLibrary library = doc.library;

        final QCinesReport cinesReport = QCinesReport.cinesReport;
        final QInternetArchiveReport iaReport = QInternetArchiveReport.internetArchiveReport;

        final BooleanBuilder builder = new BooleanBuilder();
        // filtrage des unités documentaires disponibles
        if (active) {
            builder.and(doc.state.eq(DocUnit.State.AVAILABLE));
        }
        if (StringUtils.isNotBlank(search)) {
            final BooleanExpression nameFilter = doc.label.containsIgnoreCase(search).or(doc.pgcnId.containsIgnoreCase(search));
            builder.andAnyOf(nameFilter);
        }
        // available
        if (hasDigitalDocuments) {
            builder.and(doc.digitalDocuments.isEmpty().not());
        }
        // Archivés / non archivés
        if (archived != nonArchived) {
            final JPQLQuery<String> subRes = JPAExpressions.select(cinesReport.docUnit.identifier).from(cinesReport).where(cinesReport.certificate.isNotNull());
            if (archived) {
                builder.and(doc.identifier.in(subRes));
            } else {
                builder.and(doc.identifier.notIn(subRes));
            }
        }
        // Archivables
        if (archivable && !nonArchivable) {
            builder.and(doc.archivable.isTrue());
        } else if (nonArchivable && !archivable) {
            builder.and(doc.archivable.isFalse());
        }
        // Diffusés / non diffusés
        if (distributed != nonDistributed) {
            final JPQLQuery<String> subResIa = JPAExpressions.select(iaReport.docUnit.identifier).from(iaReport).where(iaReport.dateArchived.isNotNull());
            if (distributed) {
                builder.and(doc.identifier.in(subResIa));
            } else {
                builder.and(doc.identifier.notIn(subResIa));
            }
        }
        // Diffusables
        if (distributable && !nonDistributable) {
            builder.and(doc.distributable.isTrue());
        } else if (nonDistributable && !distributable) {
            builder.and(doc.distributable.isFalse());
        }
        if (CollectionUtils.isNotEmpty(projects)) {
            final BooleanExpression projectFilter = project.identifier.in(projects);
            builder.and(projectFilter);
        }
        if (CollectionUtils.isNotEmpty(lots)) {
            final BooleanExpression lotFilter = lot.identifier.in(lots);
            builder.and(lotFilter);
        }
        if (CollectionUtils.isNotEmpty(trains)) {
            final QPhysicalDocument qpd = doc.physicalDocuments.any();
            final BooleanExpression trainFilter = qpd.isNotNull().and(qpd.train.identifier.in(trains));
            builder.and(trainFilter);
        }

        // provider, library
        QueryDSLBuilderUtils.addAccessFilters(builder, library, lot, project, associatedLibrary, associatedUser, libraries, null);

        // Statuts de workflow
        if (CollectionUtils.isNotEmpty(statuses)) {
            final List<WorkflowStateKey> wkfStates = statuses.stream().map(WorkflowStateKey::valueOf).collect(Collectors.toList());

            final BooleanBuilder statusBuilder = new BooleanBuilder();
            if (statuses.contains(WorkflowStateKey.CLOTURE_DOCUMENT.name())) {
                statuses.remove(WorkflowStateKey.CLOTURE_DOCUMENT.name());
                statusBuilder.and(state.discriminator.eq(WorkflowStateKey.CLOTURE_DOCUMENT)).and(state.status.in(WorkflowStateStatus.FINISHED));
                if (CollectionUtils.isNotEmpty(statuses)) {
                    wkfStates.remove(WorkflowStateKey.CLOTURE_DOCUMENT);
                    statusBuilder.or(state.discriminator.in(wkfStates).and(state.startDate.isNotNull().and(state.endDate.isNull())));
                }
            } else {
                final BooleanExpression stateFilter = state.discriminator.in(wkfStates);
                final BooleanExpression statusFilter = state.startDate.isNotNull().and(state.endDate.isNull());
                statusBuilder.and(stateFilter).and(statusFilter);
            }

            builder.and(statusBuilder);
        }
        if (lastModifiedDateFrom != null) {
            final BooleanExpression lastModifiedDateFromFilter = doc.lastModifiedDate.after(lastModifiedDateFrom.atStartOfDay());
            builder.and(lastModifiedDateFromFilter);
        }
        if (lastModifiedDateTo != null) {
            final BooleanExpression lastModifiedDateToFilter = doc.lastModifiedDate.before(lastModifiedDateTo.atTime(LocalTime.MAX));
            builder.and(lastModifiedDateToFilter);
        }
        if (createdDateFrom != null) {
            final BooleanExpression createdDateFromFilter = doc.createdDate.after(createdDateFrom.atStartOfDay());
            builder.and(createdDateFromFilter);
        }
        if (createdDateTo != null) {
            final BooleanExpression createdDateToFilter = doc.createdDate.before(createdDateTo.atTime(LocalTime.MAX));
            builder.and(createdDateToFilter);
        }
        if (CollectionUtils.isNotEmpty(identifiers)) {
            builder.and(doc.identifier.in(identifiers));
        }

        final JPAQuery<DocUnit> baseQuery = queryFactory.selectDistinct(doc)
                                                        .from(doc)
                                                        .leftJoin(doc.library)
                                                        .fetchJoin()
                                                        .leftJoin(doc.project, project)
                                                        .leftJoin(project.associatedLibraries, associatedLibrary)
                                                        .leftJoin(project.associatedUsers, associatedUser)
                                                        .leftJoin(doc.lot, lot)
                                                        .leftJoin(doc.workflow, workflow)
                                                        .leftJoin(workflow.states, state)
                                                        .where(builder.getValue());

        if (pageable != null) {
            baseQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
            applySorting(pageable.getSort(), baseQuery, doc, project, lot);
        }

        final long total = queryFactory.select(doc.countDistinct())
                                       .from(doc)
                                       .leftJoin(doc.library)
                                       .leftJoin(doc.project, project)
                                       .leftJoin(project.associatedLibraries, associatedLibrary)
                                       .leftJoin(project.associatedUsers, associatedUser)
                                       .leftJoin(doc.lot, lot)
                                       .leftJoin(doc.workflow, workflow)
                                       .leftJoin(workflow.states, state)
                                       .where(builder.getValue())
                                       .fetchOne();

        return new PageImpl<>(baseQuery.fetch(), pageable, total);
    }

    @Override
    public List<DocUnit> minSearch(final String search,
                                   final List<String> libraries,
                                   final List<String> projects,
                                   final List<String> lots,
                                   final List<String> trains,
                                   final List<String> statuses) {

        final QDocUnit doc = QDocUnit.docUnit;
        final QProject project = QProject.project;
        final QLot lot = QLot.lot;
        final QDocUnitWorkflow workflow = QDocUnitWorkflow.docUnitWorkflow;
        final QDocUnitState state = QDocUnitState.docUnitState;

        final BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.isNotBlank(search)) {
            final BooleanExpression nameFilter = doc.label.containsIgnoreCase(search).or(doc.pgcnId.containsIgnoreCase(search));
            builder.andAnyOf(nameFilter);
        }
        // available
        // builder.and(doc.digitalDocuments.isEmpty().not());

        if (CollectionUtils.isNotEmpty(libraries)) {
            final BooleanExpression sitesFilter = doc.library.identifier.in(libraries);
            builder.and(sitesFilter);
        }
        if (CollectionUtils.isNotEmpty(projects)) {
            final BooleanExpression projectFilter = project.identifier.in(projects);
            builder.and(projectFilter);
        }
        if (CollectionUtils.isNotEmpty(lots)) {
            final BooleanExpression lotFilter = lot.identifier.in(lots);
            builder.and(lotFilter);
        }

        if (CollectionUtils.isNotEmpty(trains)) {
            final QPhysicalDocument qpd = doc.physicalDocuments.any();
            final BooleanExpression trainFilter = qpd.isNotNull().and(qpd.train.identifier.in(trains));
            builder.and(trainFilter);
        }

        // Statuts de workflow
        if (CollectionUtils.isNotEmpty(statuses)) {
            final List<WorkflowStateKey> wkfStates = statuses.stream().map(WorkflowStateKey::valueOf).collect(Collectors.toList());

            final BooleanBuilder statusBuilder = new BooleanBuilder();
            if (statuses.contains(WorkflowStateKey.CLOTURE_DOCUMENT.name())) {
                statuses.remove(WorkflowStateKey.CLOTURE_DOCUMENT.name());
                statusBuilder.and(state.discriminator.eq(WorkflowStateKey.CLOTURE_DOCUMENT)).and(state.status.in(WorkflowStateStatus.FINISHED));
                if (CollectionUtils.isNotEmpty(statuses)) {
                    wkfStates.remove(WorkflowStateKey.CLOTURE_DOCUMENT);
                    statusBuilder.or(state.discriminator.in(wkfStates).and(state.startDate.isNotNull().and(state.endDate.isNull())));
                }
            } else {
                final BooleanExpression stateFilter = state.discriminator.in(wkfStates);
                final BooleanExpression statusFilter = state.startDate.isNotNull().and(state.endDate.isNull());
                statusBuilder.and(stateFilter).and(statusFilter);
            }

            builder.and(statusBuilder);
        }

        return queryFactory.selectDistinct(doc)
                           .from(doc)
                           .leftJoin(doc.library)
                           .fetchJoin()
                           .leftJoin(doc.project, project)
                           // .leftJoin(project.trains, train)
                           .leftJoin(doc.lot, lot)
                           .leftJoin(doc.workflow, workflow)
                           .leftJoin(workflow.states, state)
                           .where(builder.getValue())
                           .orderBy(doc.label.asc())
                           .orderBy(doc.pgcnId.asc())
                           .fetch();
    }

    @Override
    public Page<DocUnit> searchAllForProject(final String projectId, final Pageable pageable) {

        final QDocUnit doc = QDocUnit.docUnit;
        final QProject project = QProject.project;
        final QLot lot = QLot.lot;

        final BooleanBuilder builder = new BooleanBuilder();
        // filtrage des unités documentaires disponibles
        if (!projectId.isEmpty()) {
            final BooleanExpression projectFilter = project.identifier.eq(projectId);
            builder.and(projectFilter);
        }
        // Droits d'accès
        final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        if (currentUser != null) {
            // Prestataires
            if (currentUser.getCategory() == User.Category.PROVIDER) {
                final BooleanExpression providerFilter =
                // prestataire sur le projet
                                                       project.provider.identifier.eq(currentUser.getIdentifier())
                                                                                  // prestataire sur un lot du projet
                                                                                  .or(lot.provider.identifier.eq(currentUser.getIdentifier()));
                builder.and(providerFilter);
            }
        }

        final JPAQuery<DocUnit> baseQuery = queryFactory.selectDistinct(doc)
                                                        .from(doc)
                                                        .leftJoin(doc.library)
                                                        .fetchJoin()
                                                        .leftJoin(doc.project, project)
                                                        .leftJoin(doc.lot, lot)
                                                        .where(builder.getValue())
                                                        .orderBy(doc.label.asc())
                                                        .orderBy(doc.pgcnId.asc());

        if (pageable != null) {
            baseQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
            applySorting(pageable.getSort(), baseQuery, doc, project, lot);
        }

        final long total = queryFactory.select(doc.count())
                                       .from(doc)
                                       .leftJoin(doc.library)
                                       .leftJoin(doc.project, project)
                                       .leftJoin(doc.lot, lot)
                                       .where(builder.getValue())
                                       .fetchOne();

        return new PageImpl<>(baseQuery.fetch(), pageable, total);
    }

    /**
     * Gère le tri
     *
     * @param sort
     * @param query
     * @param doc
     * @param project
     * @param lot
     * @return
     */
    protected JPAQuery<DocUnit> applySorting(final Sort sort, final JPAQuery<DocUnit> query, final QDocUnit doc, final QProject project, final QLot lot) {

        final List<OrderSpecifier<?>> orders = new ArrayList<>();
        if (sort == null) {
            return query;
        }

        for (final Sort.Order order : sort) {
            final Order qOrder = order.isAscending() ? Order.ASC
                                                     : Order.DESC;

            switch (order.getProperty()) {
                case "pgcnId":
                    orders.add(new OrderSpecifier<>(qOrder, doc.pgcnId));
                    break;
                case "label":
                    orders.add(new OrderSpecifier<>(qOrder, doc.label));
                    break;
                case "project.name":
                    orders.add(new OrderSpecifier<>(qOrder, project.name));
                    break;
                case "lot.label":
                    orders.add(new OrderSpecifier<>(qOrder, lot.label));
                    break;
                case "parent.pgcnId":
                    orders.add(new OrderSpecifier<>(qOrder, doc.parent.pgcnId));
                    break;
                default:
                    LOG.warn("Tri non implémenté: {}", order.getProperty());
                    break;
            }
        }
        OrderSpecifier<?>[] orderArray = new OrderSpecifier[orders.size()];
        orderArray = orders.toArray(orderArray);
        return query.orderBy(orderArray);
    }

    @Override
    public List<DocUnit> searchDuplicates(final DocUnit reference, final List<String> identifiers, final DocUnit.State... state) {
        // pas d'identifiants à rechercher
        if (identifiers.isEmpty()) {
            return Collections.emptyList();
        }

        final QDocUnit qDocUnit = QDocUnit.docUnit;
        final QBibliographicRecord qBibliographicRecord = QBibliographicRecord.bibliographicRecord;
        final QDocProperty qDocProperty = QDocProperty.docProperty;

        // Requête
        final JPAQuery<DocUnit> query = queryFactory.selectDistinct(qDocUnit)
                                                    .from(qDocUnit)
                                                    .innerJoin(qDocUnit.records, qBibliographicRecord)
                                                    .innerJoin(qBibliographicRecord.properties, qDocProperty);

        // Différent de l'unité documentaire de référence
        query.where(qDocUnit.identifier.ne(reference.getIdentifier()));
        // Filtrage sur la bibliothèque
        query.where(qDocUnit.library.eq(reference.getLibrary()));
        // Filtrage sur l'état
        if (state.length > 0) {
            query.where(qDocUnit.state.in(state));
        }
        // Filtrage sur les identifiants
        final BooleanBuilder identifierPredicate = new BooleanBuilder();
        identifiers.stream().map(qDocProperty.value::eq).forEach(identifierPredicate::or);
        query.where(identifierPredicate);

        // Recherche
        return query.fetch();
    }

    public DocUnit findOneWithAllDependencies(final String identifier) {
        return findOneWithAllDependencies(identifier, false);
    }

    @Override
    public DocUnit findOneWithAllDependencies(final String identifier, final boolean initFiles) {

        final QDocUnit qDocUnit = QDocUnit.docUnit;
        final DocUnit doc = queryFactory.select(qDocUnit).from(qDocUnit).where(qDocUnit.identifier.eq(identifier)).fetchOne();
        if (doc != null) {
            Hibernate.initialize(doc.getParent());
            Hibernate.initialize(doc.getLibrary());
            Hibernate.initialize(doc.getWorkflow());
            Hibernate.initialize(doc.getPhysicalDocuments());
            Hibernate.initialize(doc.getDigitalDocuments());
            Hibernate.initialize(doc.getActiveOcrLanguage());

            doc.getDigitalDocuments().forEach(dd -> {
                Hibernate.initialize(dd.getPages());
                if (initFiles) {
                    dd.getPages().forEach(pg -> {
                        Hibernate.initialize(pg.getFiles());
                    });
                }
            });

            Hibernate.initialize(doc.getProject());
            Hibernate.initialize(doc.getLot());

            if (doc.getPlanClassementPAC() == null) {
                if (doc.getLot() != null) {
                    if (doc.getLot().getPlanClassementPAC() == null) {
                        if (doc.getProject() != null) {
                            Hibernate.initialize(doc.getProject().getPlanClassementPAC());
                        }
                    } else {
                        Hibernate.initialize(doc.getLot().getPlanClassementPAC());
                    }
                }
            } else {
                Hibernate.initialize(doc.getPlanClassementPAC());
            }

            Hibernate.initialize(doc.getRecords());
            doc.getRecords().forEach(not -> {
                if (not != null) {
                    Hibernate.initialize(not.getProperties());
                    if (not.getProperties() != null) {
                        not.getProperties().forEach(p -> Hibernate.initialize(p.getType()));
                    }
                }
            });

            if (doc.getExportData() != null) {
                Hibernate.initialize(doc.getExportData().getProperties());
            }

            if (doc.getArchiveItem() != null) {
                Hibernate.initialize(doc.getArchiveItem().getCollections());
                Hibernate.initialize(doc.getArchiveItem().getSubjects());
                Hibernate.initialize(doc.getArchiveItem().getHeaders());
                Hibernate.initialize(doc.getArchiveItem().getCoverages());
                Hibernate.initialize(doc.getArchiveItem().getContributors());
                Hibernate.initialize(doc.getArchiveItem().getCreators());
                Hibernate.initialize(doc.getArchiveItem().getLanguages());
            }
        }
        return doc;
    }

}
