package fr.progilone.pgcn.repository.help;

import fr.progilone.pgcn.domain.help.HelpPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HelpPageRepository extends JpaRepository<HelpPage, String>, HelpPageRepositoryCustom {

    @Query("select distinct(hp.module) from HelpPage hp")
    List<String> findAllModules();

    @Query("select hp from HelpPage hp left join fetch hp.parent where hp.identifier = ?1")
    HelpPage findOneWithParent(String id);

}
