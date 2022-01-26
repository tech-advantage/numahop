package fr.progilone.pgcn.repository.administration;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.progilone.pgcn.domain.administration.InternetArchiveCollection;

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

    @Query("select iac "
           + "from InternetArchiveCollection iac "
           + "left join iac.confIa conf "
           + "left join conf.library lib "
           + "where iac.name = ?1 "
           + "and lib.identifier = ?2")
    InternetArchiveCollection findByNameAndLibrary(String name, String library);
}
