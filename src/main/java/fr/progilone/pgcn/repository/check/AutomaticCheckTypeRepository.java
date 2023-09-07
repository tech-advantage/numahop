package fr.progilone.pgcn.repository.check;

import fr.progilone.pgcn.domain.check.AutomaticCheckType;
import fr.progilone.pgcn.domain.check.AutomaticCheckType.AutoCheckType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author jbrunet
 *         Créé le 13 févr. 2017
 */
public interface AutomaticCheckTypeRepository extends JpaRepository<AutomaticCheckType, String> {

    List<AutomaticCheckType> findByTypeIn(List<AutoCheckType> types);

    AutomaticCheckType getOneByType(AutoCheckType type);

    @Query("select distinct type " + "from AutomaticCheckType type "
           + "where configurable = true "
           + "order by order asc")
    List<AutomaticCheckType> findAllConfigurable();
}
