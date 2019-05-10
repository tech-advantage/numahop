package fr.progilone.pgcn.repository.exchange;

import fr.progilone.pgcn.domain.exchange.Mapping;
import fr.progilone.pgcn.domain.library.Library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

/**
 * Created by Sebastien on 23/11/2016.
 */
public interface MappingRepository extends JpaRepository<Mapping, String> {

    @Query("select m "
           + "from Mapping m "
           + "left join fetch m.library "
           + "left join fetch m.rules r "
           + "left join fetch r.property "
           + "where m.identifier = ?1")
    Mapping findOneWithRules(String identifier);

    @Query("select distinct m "
           + "from Mapping m "
           + "left join fetch m.library "
           + "left join fetch m.rules r "
           + "left join fetch r.property "
           + "where m.type = ?1")
    Set<Mapping> findByTypeWithRules(final Mapping.Type type);

    @Query("select distinct m "
           + "from Mapping m "
           + "left join fetch m.library l "
           + "left join fetch m.rules r "
           + "left join fetch r.property "
           + "where m.type = ?1 "
           + "and l = ?2")
    Set<Mapping> findByTypeAndLibraryWithRules(final Mapping.Type type, final Library library);

    @Query("select m "
           + "from Mapping m "
           + "left join fetch m.library l "
           + "left join fetch m.rules r "
           + "left join fetch r.property "
           + "where r.docUnitField = 'pgcnId'")
    Set<Mapping> findAllUsableWithRules();

    @Query("select m "
           + "from Mapping m "
           + "join fetch m.library l "
           + "left join fetch m.rules r "
           + "left join fetch r.property "
           + "where l = ?1")
    Set<Mapping> findByLibraryWithRules(Library library);

    Set<Mapping> findByLibrary(Library library);

    @Query("select m "
           + "from Mapping m "
           + "join fetch m.library l "
           + "left join fetch m.rules r "
           + "left join fetch r.property "
           + "where l = ?1 "
           + "and r.docUnitField = 'pgcnId'")
    Set<Mapping> findUsableByLibrary(Library library);
}
