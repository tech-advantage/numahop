package fr.progilone.pgcn.repository.document;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fr.progilone.pgcn.domain.document.DocCheckHistory;
import fr.progilone.pgcn.domain.document.QDocCheckHistory;
import java.time.LocalDate;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

public class DocCheckHistoryRepositoryImpl implements DocCheckHistoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public DocCheckHistoryRepositoryImpl(final JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<DocCheckHistory> findDocCheckHistories(final List<String> libraries,
                                                       final List<String> projects,
                                                       final List<String> lots,
                                                       final List<String> deliveries,
                                                       final LocalDate fromDate,
                                                       final LocalDate toDate) {

        final QDocCheckHistory qDocCheckHistory = QDocCheckHistory.docCheckHistory;

        final BooleanBuilder builder = new BooleanBuilder();

        // Droits d'accès
        // QueryDSLBuilderUtils.addAccessFilters(builder, qDocCheckHistory.library, qDocUnit.project, libraries, null);

        // Libraries
        if (CollectionUtils.isNotEmpty(libraries)) {
            builder.and(qDocCheckHistory.libraryId.in(libraries));
        }
        // Projets
        if (CollectionUtils.isNotEmpty(projects)) {
            builder.and(qDocCheckHistory.projectId.in(projects));
        }
        // Lots
        if (CollectionUtils.isNotEmpty(lots)) {
            builder.and(qDocCheckHistory.lotId.in(lots));
        }
        // Livraisons
        if (CollectionUtils.isNotEmpty(deliveries)) {
            builder.and(qDocCheckHistory.deliveryId.in(deliveries));
        }

        if (fromDate != null) {
            builder.and(qDocCheckHistory.startCheckDate.after(fromDate.atStartOfDay()));
        }
        if (toDate != null) {
            builder.and(qDocCheckHistory.endCheckDate.before(toDate.plusDays(1).atStartOfDay()));
        }

        return queryFactory.selectDistinct(qDocCheckHistory).from(qDocCheckHistory).where(builder.getValue()).fetch();
    }

}
