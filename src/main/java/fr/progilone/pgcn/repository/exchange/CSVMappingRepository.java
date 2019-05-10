package fr.progilone.pgcn.repository.exchange;

import fr.progilone.pgcn.domain.exchange.CSVMapping;
import fr.progilone.pgcn.domain.library.Library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

/**
 * Created by Sebastien on 23/11/2016.
 */
public interface CSVMappingRepository extends JpaRepository<CSVMapping, String> {

    @Query("select m "
           + "from CSVMapping m "
           + "left join fetch m.library "
           + "left join fetch m.rules r "
           + "where m.identifier = ?1")
    CSVMapping findOneWithRules(String identifier);

    @Query("select distinct m "
           + "from CSVMapping m "
           + "left join fetch m.library "
           + "left join fetch m.rules r ")
    Set<CSVMapping> findAllWithRules();

    @Query("select m "
           + "from CSVMapping m "
           + "left join fetch m.library l "
           + "left join fetch m.rules r "
           + "where r.docUnitField = 'pgcnId'")
    Set<CSVMapping> findAllUsableWithRules();

    @Query("select m "
           + "from CSVMapping m "
           + "join fetch m.library l "
           + "left join fetch m.rules r "
           + "where l = ?1")
    Set<CSVMapping> findByLibraryWithRules(Library library);

    Set<CSVMapping> findByLibrary(Library library);

    @Query("select m "
           + "from CSVMapping m "
           + "join fetch m.library l "
           + "left join fetch m.rules r "
           + "where l = ?1 "
           + "and r.docUnitField = 'pgcnId'")
    Set<CSVMapping> findUsableByLibrary(Library library);
}
