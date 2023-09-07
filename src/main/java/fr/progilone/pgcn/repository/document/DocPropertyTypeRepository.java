package fr.progilone.pgcn.repository.document;

import fr.progilone.pgcn.domain.document.DocPropertyType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by Sebastien on 06/12/2016.
 */
public interface DocPropertyTypeRepository extends JpaRepository<DocPropertyType, String> {

    @Query("select max(t.rank) " + "from DocPropertyType t "
           + "where t.superType = ?1")
    Integer findCurrentRankForPropertyType(DocPropertyType.DocPropertySuperType superType);

    List<DocPropertyType> findAllBySuperType(DocPropertyType.DocPropertySuperType superType);

    List<DocPropertyType> findAllBySuperTypeIn(List<DocPropertyType.DocPropertySuperType> superTypes);

    @Query("select t from DocPropertyType t " + "left join fetch t.docProperties dp "
           + "left join fetch dp.record "
           + "left join fetch dp.type "
           + "where t.identifier = ?1")
    DocPropertyType findOneWithDependencies(String identifier);
}
