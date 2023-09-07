package fr.progilone.pgcn.repository.train;

import fr.progilone.pgcn.domain.dto.train.SimpleTrainDTO;
import fr.progilone.pgcn.domain.train.Train;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TrainRepositoryCustom {

    /**
     * Recherche rapide de trains
     *
     * @param search
     * @param projects
     * @param active
     * @param pageable
     * @return
     */
    Page<Train> search(String search,
                       List<String> libraries,
                       List<String> projects,
                       boolean active,
                       List<Train.TrainStatus> statuses,
                       LocalDate providerSendingDateFrom,
                       LocalDate providerSendingDateTo,
                       LocalDate returnDateFrom,
                       LocalDate returnDateTo,
                       Integer docNumber,
                       Pageable pageable);

    /**
     * Recherche de trains, non paginée
     *
     * @param libraries
     * @param projects
     * @param trains
     * @param status
     * @param sendFrom
     * @param sendTo
     * @param returnFrom
     * @param returnTo
     * @param insuranceFrom
     * @param insuranceTo
     * @return
     */
    List<Train> findAll(List<String> libraries,
                        List<String> projects,
                        List<String> trains,
                        List<Train.TrainStatus> status,
                        LocalDate sendFrom,
                        LocalDate sendTo,
                        LocalDate returnFrom,
                        LocalDate returnTo,
                        Double insuranceFrom,
                        Double insuranceTo);

    /**
     * récupère les trains attachés aux projets.
     *
     * @param projectIds
     * @return
     */
    List<SimpleTrainDTO> findAllIdentifiersInProjectIds(Collection<String> projectIds);

    List<Object[]> getTrainGroupByStatus(List<String> libraries, List<String> projects);
}
