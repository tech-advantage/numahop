package fr.progilone.pgcn.repository.workflow.helper;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import fr.progilone.pgcn.domain.administration.QInternetArchiveCollection;
import fr.progilone.pgcn.domain.administration.omeka.QOmekaList;
import fr.progilone.pgcn.domain.delivery.QDeliveredDocument;
import fr.progilone.pgcn.domain.document.QDigitalDocument;
import fr.progilone.pgcn.domain.document.QDocUnit;
import fr.progilone.pgcn.domain.user.QUser;
import fr.progilone.pgcn.domain.workflow.QDocUnitState;
import fr.progilone.pgcn.domain.workflow.QDocUnitWorkflow;
import fr.progilone.pgcn.domain.workflow.QWorkflowModel;
import fr.progilone.pgcn.domain.workflow.WorkflowStateStatus;
import fr.progilone.pgcn.repository.util.QueryDSLBuilderUtils;

public class DocUnitWorkflowHelper {

    private DocUnitWorkflowHelper() {
    }

    public static <T> JPAQuery<T> getFindDocUnitWorkflowQuery(final JPAQuery<T> query,
                                                              final DocUnitWorkflowSearchBuilder searchBuilder,
                                                              final QDocUnit qDocUnit,
                                                              final QDocUnitWorkflow qDocUnitWorkflow,
                                                              final QDocUnitState qDocUnitState) {

        final BooleanBuilder builder = new BooleanBuilder();

        // Droits d'accès
        QueryDSLBuilderUtils.addAccessFilters(builder, qDocUnit.library, qDocUnit.project, searchBuilder.getLibraries().orElse(null), null);
        // Projets
        searchBuilder.getProjects().ifPresent(projects -> builder.and(qDocUnit.project.identifier.in(projects)));
        // Lots
        searchBuilder.getLots().ifPresent(lots -> builder.and(qDocUnit.lot.identifier.in(lots)));
        // Livraisons
        searchBuilder.getDeliveries().ifPresent(deliveries -> {
            final QDeliveredDocument qDeliveredDocument = QDeliveredDocument.deliveredDocument;
            final QDigitalDocument qDigitalDocument = QDigitalDocument.digitalDocument;

            final BooleanBuilder subBuilder = new BooleanBuilder().and(qDeliveredDocument.delivery.identifier.in(deliveries))
                                                                  .and(qDigitalDocument.docUnit.identifier.eq(qDocUnit.identifier));
            final Predicate subQuery = JPAExpressions.select(qDeliveredDocument)
                                                     .from(qDeliveredDocument)
                                                     .innerJoin(qDeliveredDocument.digitalDocument, qDigitalDocument)
                                                     .where(subBuilder)
                                                     .exists();
            builder.and(subQuery);
        });
        // Rôles des intervenants
        searchBuilder.getRoles().ifPresent(roles -> {
            final QUser qUser = QUser.user;
            builder.and(JPAExpressions.select(qUser).from(qUser).where(new BooleanBuilder().and(qUser.role.identifier.in(roles)).and(qUser.login.eq(qDocUnitState.user))).exists());
        });
        // Workflows
        searchBuilder.getWorkflows().ifPresent(workflows -> {
            final QWorkflowModel qWorkflowModel = QWorkflowModel.workflowModel;
            query.innerJoin(qDocUnitWorkflow.model, qWorkflowModel);
            builder.and(qWorkflowModel.identifier.in(workflows));
        });
        // Étapes terminées avec succès
        searchBuilder.getStates().ifPresent(states -> {
            if (searchBuilder.isWithFailedStatuses()) {
                builder.and(qDocUnitState.discriminator.in(states))
                       .and(qDocUnitState.status.eq(WorkflowStateStatus.FINISHED).or(qDocUnitState.status.eq(WorkflowStateStatus.FAILED)));
            } else {
                builder.and(qDocUnitState.discriminator.in(states)).and(qDocUnitState.status.eq(WorkflowStateStatus.FINISHED));
            }

        });
        // Période
        searchBuilder.getFromDate().ifPresent(fromDate -> {
            builder.and(new BooleanBuilder().or(qDocUnitState.endDate.isNull()).or(qDocUnitState.endDate.after(fromDate.atStartOfDay())));
        });
        searchBuilder.getToDate().ifPresent(toDate -> {
            builder.and(new BooleanBuilder().or(qDocUnitState.startDate.isNull()).or(qDocUnitState.startDate.before(toDate.plusDays(1).atStartOfDay())));
        });
        // Type de document
        searchBuilder.getTypes().ifPresent(types -> {
            builder.and(qDocUnit.type.in(types));
        });
        // Collection
        searchBuilder.getCollections().ifPresent(collections -> {
            final QInternetArchiveCollection qIACollection = QInternetArchiveCollection.internetArchiveCollection;
            final QOmekaList qOmekaCollection = QOmekaList.omekaList;
            query.leftJoin(qDocUnit.collectionIA, qIACollection).leftJoin(qDocUnit.omekaCollection, qOmekaCollection);
            builder.and(new BooleanBuilder().or(qIACollection.identifier.in(collections)).or(qOmekaCollection.identifier.in(collections)));
        });
        // Requête
        return query.where(builder.getValue()).distinct();
    }
}
