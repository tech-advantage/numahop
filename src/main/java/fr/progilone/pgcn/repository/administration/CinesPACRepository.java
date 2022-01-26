package fr.progilone.pgcn.repository.administration;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.progilone.pgcn.domain.administration.CinesPAC;

/**
 *
 * @author jbrunet
 * Créé le 19 avr. 2017
 */
public interface CinesPACRepository extends JpaRepository<CinesPAC, String> {

    @Query("select distinct pac " +
        "from CinesPAC pac " +
        "left join pac.confPac conf " +
        "left join conf.library lib " +
        "where lib.identifier = ?1")
    List<CinesPAC> findAllForLibrary(String identifier);
    
    @Query("select pac from CinesPAC pac where pac.confPac.identifier = ?1")
    List<CinesPAC> findAllByConfPac(String confPac);

    @Query("select pac " +
           "from CinesPAC pac " +
           "left join pac.confPac conf " +
           "left join conf.library lib " +
           "where pac.name = ?1 " +
            "and lib.identifier = ?2")
    CinesPAC findByNameAndLibrary(String name, String library);
}
