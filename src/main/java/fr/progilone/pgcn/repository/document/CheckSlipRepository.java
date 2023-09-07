package fr.progilone.pgcn.repository.document;

import fr.progilone.pgcn.domain.document.CheckSlip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CheckSlipRepository extends JpaRepository<CheckSlip, String> {

    @Query("select distinct cs " + "from CheckSlip cs "
           + "join fetch cs.slipLines "
           + "where cs.identifier = ?1")
    CheckSlip findOneWithDep(String id);

}
