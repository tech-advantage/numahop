package fr.progilone.pgcn.repository.document;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.progilone.pgcn.domain.document.CheckSlip;

public interface CheckSlipRepository extends JpaRepository<CheckSlip, String> {

    @Query("select distinct cs "
            + "from CheckSlip cs "
            + "join fetch cs.slipLines "
           + "where cs.identifier = ?1")
    CheckSlip findOneWithDep(String id);
    
}
