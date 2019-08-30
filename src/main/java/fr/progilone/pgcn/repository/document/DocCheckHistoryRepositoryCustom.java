package fr.progilone.pgcn.repository.document;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import fr.progilone.pgcn.domain.document.DocCheckHistory;

@Repository
public interface DocCheckHistoryRepositoryCustom {
    
    
    List<DocCheckHistory> findDocCheckHistories(List<String> libraries, List<String> projects, List<String> lots, List<String> deliveries, LocalDate fromDate, LocalDate toDate);
    
    
    
}
