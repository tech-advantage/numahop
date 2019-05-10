package fr.progilone.pgcn.repository.train;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.Tuple;
import com.mysema.query.jpa.JPASubQuery;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.BooleanExpression;
import fr.progilone.pgcn.domain.document.QDocUnit;
import fr.progilone.pgcn.domain.document.QPhysicalDocument;
import fr.progilone.pgcn.domain.document.conditionreport.QConditionReport;
import fr.progilone.pgcn.domain.document.conditionreport.QConditionReportDetail;
import fr.progilone.pgcn.domain.dto.train.SimpleTrainDTO;
import fr.progilone.pgcn.domain.library.QLibrary;
import fr.progilone.pgcn.domain.project.QProject;
import fr.progilone.pgcn.domain.train.QTrain;
import fr.progilone.pgcn.domain.train.Train;
import fr.progilone.pgcn.repository.util.QueryDSLBuilderUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class TrainRepositoryImpl implements TrainRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Object[]> getTrainGroupByStatus(final List<String> libraries, final List<String> projects) {
        final QTrain qTrain = QTrain.train;
        final QLibrary qLibrary = QLibrary.library;
        final QProject qProject = QProject.project;

        final BooleanBuilder builder = new BooleanBuilder();
        // provider
        QueryDSLBuilderUtils.addAccessFilters(builder, qLibrary, qProject, libraries, null);

        // projets
        if (projects != null && !projects.isEmpty()) {
            final BooleanExpression sitesFilter = qTrain.project.identifier.in(projects);
            builder.and(sitesFilter);
        }

        // query
        return new JPAQuery(em).from(qTrain)
                               .leftJoin(qTrain.project, qProject)
                               .leftJoin(qProject.library, qLibrary)
                               .where(builder)
                               .groupBy(qTrain.status)
                               .list(qTrain.status, qTrain.identifier.countDistinct())
                               .stream()
                               .map(Tuple::toArray)
                               .collect(Collectors.toList());
    }

    @Override
    public Page<Train> search(String search,
                              List<String> libraries,
                              List<String> projects,
                              boolean active,
                              List<Train.TrainStatus> statuses,
                              LocalDate providerSendingDateFrom,
                              LocalDate providerSendingDateTo,
                              LocalDate returnDateFrom,
                              LocalDate returnDateTo,
                              Integer docNumber,
                              Pageable pageable) {

        final QTrain qTrain = QTrain.train;
        final QLibrary qLibrary = QLibrary.library;
        final QProject qProject = QProject.project;
        final BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.isNotBlank(search)) {
            final BooleanExpression nameFilter = qTrain.label.containsIgnoreCase(search);
            builder.andAnyOf(nameFilter);
        }

        // active
        if (active) {
            builder.and(qTrain.active.eq(true));
        }
        // provider
        QueryDSLBuilderUtils.addAccessFilters(builder, qLibrary, qProject, libraries, null);

        // projets
        if (CollectionUtils.isNotEmpty(projects)) {
            final BooleanExpression sitesFilter = qTrain.project.identifier.in(projects);
            builder.and(sitesFilter);
        }
        // statuts
        if (CollectionUtils.isNotEmpty(statuses)) {
            final BooleanExpression categoryFilter = qTrain.status.in(statuses);
            builder.and(categoryFilter);
        }
        // Date d'envoi
        if (providerSendingDateFrom != null) {
            final BooleanExpression providerSendingDateFromFilter = qTrain.providerSendingDate.goe(providerSendingDateFrom);
            builder.and(providerSendingDateFromFilter);
        }
        if (providerSendingDateTo != null) {
            final BooleanExpression providerSendingDateToFilter = qTrain.providerSendingDate.loe(providerSendingDateTo);
            builder.and(providerSendingDateToFilter);
        }
        // Date de retour
        if (returnDateFrom != null) {
            final BooleanExpression returnDateFromFilter = qTrain.returnDate.goe(returnDateFrom);
            builder.and(returnDateFromFilter);
        }
        if (returnDateTo != null) {
            final BooleanExpression returnDateToFilter = qTrain.returnDate.loe(returnDateTo);
            builder.and(returnDateToFilter);
        }
        // docNumber
        if (docNumber != null) {
            builder.and(qTrain.physicalDocuments.size().eq(docNumber));
        }

        final JPQLQuery baseQuery = new JPAQuery(em);
        final JPQLQuery countQuery = new JPAQuery(em);

        if (pageable != null) {
            baseQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
        }

        final List<String> trainsIdentifiers = countQuery.from(qTrain)
                                                         .leftJoin(qTrain.project, qProject)
                                                         .leftJoin(qProject.library, qLibrary)
                                                         .groupBy(qTrain.identifier)
                                                         .where(builder.getValue())
                                                         .distinct()
                                                         .list(qTrain.identifier);
        final long total = trainsIdentifiers.size();

        final List<Train> result = baseQuery.from(qTrain)
                                            .leftJoin(qTrain.project, qProject)
                                            .leftJoin(qProject.library, qLibrary)
                                            .where(builder.getValue())
                                            .distinct()
                                            .orderBy(qTrain.label.asc())
                                            .list(qTrain);
        return new PageImpl<>(result, pageable, total);
    }

    @Override
    public List<Train> findAll(final List<String> libraries,
                               final List<String> projects,
                               final List<String> trains,
                               final List<Train.TrainStatus> status,
                               final LocalDate sendFrom,
                               final LocalDate sendTo,
                               final LocalDate returnFrom,
                               final LocalDate returnTo,
                               final Double insuranceFrom,
                               final Double insuranceTo) {

        final QTrain qTrain = QTrain.train;
        final QLibrary qLibrary = QLibrary.library;
        final QProject qProject = QProject.project;
        final BooleanBuilder builder = new BooleanBuilder();

        // active
        builder.and(qTrain.active.eq(true));

        // accès / lib, projet
        QueryDSLBuilderUtils.addAccessFilters(builder, qLibrary, qProject, libraries, null);

        // projets
        if (CollectionUtils.isNotEmpty(projects)) {
            builder.and(qTrain.project.identifier.in(projects));
        }
        // trains
        if (CollectionUtils.isNotEmpty(trains)) {
            builder.and(qTrain.identifier.in(trains));
        }
        // statuts
        if (CollectionUtils.isNotEmpty(status)) {
            builder.and(qTrain.status.in(status));
        }
        // Date d'envoi
        if (sendFrom != null) {
            builder.and(qTrain.providerSendingDate.goe(sendFrom));
        }
        if (sendTo != null) {
            builder.and(qTrain.providerSendingDate.loe(sendTo));
        }
        // Date de retour
        if (returnFrom != null) {
            builder.and(qTrain.returnDate.goe(returnFrom));
        }
        if (returnTo != null) {
            builder.and(qTrain.returnDate.loe(returnTo));
        }
        // Valeur d'assurance
        if (insuranceFrom != null || insuranceTo != null) {
            final QConditionReportDetail qConditionReportDetail = QConditionReportDetail.conditionReportDetail;
            final QConditionReportDetail qConditionReportDetailMax = new QConditionReportDetail("qConditionReportDetailMax");
            final QConditionReport qConditionReport = QConditionReport.conditionReport;
            final QDocUnit qDocUnit = QDocUnit.docUnit;
            final QPhysicalDocument qPhysicalDocument = QPhysicalDocument.physicalDocument;

            final BooleanBuilder condreportBuilder = new BooleanBuilder();
            // dernier constat d'état
            condreportBuilder.and(qConditionReportDetail.position.eq(new JPASubQuery().from(qConditionReportDetailMax)
                                                                                      .where(qConditionReportDetailMax.report.eq(
                                                                                          qConditionReportDetail.report))
                                                                                      .unique(qConditionReportDetailMax.position.max())));
            // lien avec le train
            condreportBuilder.and(qPhysicalDocument.train.eq(qTrain));

            final BooleanBuilder condreportHavingBuilder = new BooleanBuilder();
            // valeur d'assurance
            if (insuranceFrom != null) {
                condreportHavingBuilder.and(qConditionReportDetail.insurance.sum().goe(insuranceFrom));
            }
            if (insuranceTo != null) {
                condreportHavingBuilder.and(qConditionReportDetail.insurance.sum().loe(insuranceTo));
            }

            builder.and(new JPASubQuery().from(qConditionReportDetail)
                                         .innerJoin(qConditionReportDetail.report, qConditionReport)
                                         .innerJoin(qConditionReport.docUnit, qDocUnit)
                                         .innerJoin(qDocUnit.physicalDocuments, qPhysicalDocument)
                                         .where(condreportBuilder)
                                         .groupBy(qPhysicalDocument.train)
                                         .having(condreportHavingBuilder)
                                         .exists());
        }

        return new JPAQuery(em).from(qTrain)
                               .leftJoin(qTrain.project, qProject)
                               .leftJoin(qProject.library, qLibrary)
                               .where(builder.getValue())
                               .distinct()
                               .list(qTrain);
    }

    /**
     * récupère les trains attachés aux projets.
     *
     * @param projectIds
     * @return
     */
    public List<SimpleTrainDTO> findAllIdentifiersInProjectIds(final Iterable<String> projectIds) {

        String q = "select t.identifier, t.label from Train t inner join t.project p where p.identifier in :projectIds ";
        final TypedQuery<Object[]> query = em.createQuery(q, Object[].class); // NOSONAR : Non il n'y a pas de possiblité d'injection SQL...
        query.setParameter("projectIds", projectIds);

        final List<Object[]> queryResult = query.getResultList();
        final SimpleTrainDTO.Builder builder = new SimpleTrainDTO.Builder();

        return queryResult.stream()
                          .map(result -> builder.reinit().setIdentifier((String) result[0]).setLabel((String) result[1]).build())
                          .collect(Collectors.toList());
    }
}
