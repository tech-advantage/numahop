package fr.progilone.pgcn.repository.storage;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.domain.storage.StoredFile;

public interface BinaryRepository extends JpaRepository<StoredFile, String> {
    
    StoredFile getOneByPageIdentifierAndFileFormat(String page, ViewsFormatConfiguration.FileFormat format);
    
    int countByPageDigest(String pageDigest);
    
    @Query("select sf from StoredFile sf "
            + "left join fetch sf.page p "
            + "where p.identifier in ?1 "
            + "and sf.fileFormat = ?2 "
            + "and p.number != null "
            + "order by p.number")
     List<StoredFile> getAllByPageIdentifiersAndFileFormat(List<String> pageIdentifier, ViewsFormatConfiguration.FileFormat format);
    
    
    @Query("select sf from StoredFile sf "
            + "left join fetch sf.page p "
            + "left join fetch p.digitalDocument "
            + "where p.identifier in ?1 "
            + "and sf.fileFormat = ?2 "
            + "and p.number != null "
            + "order by p.number")
     List<StoredFile> getAllWithDocByPageIdentifiersAndFileFormat(List<String> pageIdentifier, ViewsFormatConfiguration.FileFormat format);
	
    
    
}
