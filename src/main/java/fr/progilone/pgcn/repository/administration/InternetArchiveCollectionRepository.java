package fr.progilone.pgcn.repository.administration;

import fr.progilone.pgcn.domain.administration.InternetArchiveCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author jbrunet
 * Créé le 19 avr. 2017
 */
public interface InternetArchiveCollectionRepository extends JpaRepository<InternetArchiveCollection, String> {

    @Query("select distinct iac "
           + "from InternetArchiveCollection iac "
           + "left join iac.confIa conf "
           + "left join conf.library lib")
    List<InternetArchiveCollection> findAllWithDependencies();

    @Query("select distinct iac "
           + "from InternetArchiveCollection iac "
           + "left join iac.confIa conf "
           + "left join conf.library lib "
           + "where lib.identifier in ?1")
    List<InternetArchiveCollection> findAllForLibraries(List<String> libraryIds);
}
