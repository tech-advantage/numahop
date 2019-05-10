package fr.progilone.pgcn.repository.lot;

import fr.progilone.pgcn.domain.dto.lot.SimpleLotDTO;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.repository.lot.helper.LotSearchBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface LotRepositoryCustom {

    /**
     * récupère les lots attachés aux projets.
     *
     * @param projectIds
     * @return
     */
    List<SimpleLotDTO> findAllIdentifiersInProjectIds(Iterable<String> projectIds);

    List<Object[]> getLotGroupByStatus(List<String> libraries, List<String> projects);

    /**
     * Recherche rapide de lots
     *
     * @param searchBuilder
     * @param pageable
     * @return
     */
    Page<Lot> search(LotSearchBuilder searchBuilder, Pageable pageable);

    List<Lot> findLotsForWidget(LocalDate fromDate, List<String> libraries, List<String> projects, List<Lot.LotStatus> statuses);
}
