package fr.progilone.pgcn.repository.document;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.collections4.CollectionUtils;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.impl.JPAQuery;

import fr.progilone.pgcn.domain.document.DocCheckHistory;
import fr.progilone.pgcn.domain.document.QDocCheckHistory;

public class DocCheckHistoryRepositoryImpl implements DocCheckHistoryRepositoryCustom {

    @PersistenceContext
    private EntityManager em;
    
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
        //QueryDSLBuilderUtils.addAccessFilters(builder, qDocCheckHistory.library, qDocUnit.project, libraries, null);

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
        
        final JPAQuery query = new JPAQuery(em).from(qDocCheckHistory).fetchAll().where(builder.getValue());

        return query.distinct().list(qDocCheckHistory);
    }
    
    

}
