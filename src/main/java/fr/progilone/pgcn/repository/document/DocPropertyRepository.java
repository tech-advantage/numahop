package fr.progilone.pgcn.repository.document;

import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocProperty;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DocPropertyRepository extends JpaRepository<DocProperty, String> {
	@Query("Select max(p.rank) "
			+ "from DocProperty p "
	           + "where p.record = ?1 "
	           + "and p.type = ?2")
	Integer findCurrentRankForProperty(BibliographicRecord record, DocPropertyType type);

    @Query("Select max(p.rank) "
           + "from DocProperty p "
           + "where p.record = ?1")
    Integer findCurrentRankForProperty(BibliographicRecord record);

    Integer countByType(DocPropertyType type);
}
