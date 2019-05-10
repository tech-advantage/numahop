package fr.progilone.pgcn.repository.document;

import fr.progilone.pgcn.domain.document.DocPropertyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Sebastien on 06/12/2016.
 */
public interface DocPropertyTypeRepository extends JpaRepository<DocPropertyType, String> {

    @Query("select max(t.rank) "
           + "from DocPropertyType t "
           + "where t.superType = ?1")
    Integer findCurrentRankForPropertyType(DocPropertyType.DocPropertySuperType superType);

    List<DocPropertyType> findAllBySuperType(DocPropertyType.DocPropertySuperType superType);
}
