package fr.progilone.pgcn.repository.library;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.domain.checkconfiguration.CheckConfiguration;
import fr.progilone.pgcn.domain.exportftpconfiguration.ExportFTPConfiguration;
import fr.progilone.pgcn.domain.ftpconfiguration.FTPConfiguration;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.ocrlangconfiguration.OcrLangConfiguration;
import fr.progilone.pgcn.domain.user.Role;

public interface LibraryRepository extends JpaRepository<Library, String>, LibraryRepositoryCustom {

    @Query("from Library l "
           + "where l.name = ?1")
    Library findByName(String name);

    @Query("select distinct l "
           + "from Library l "
           + "where l.identifier in ?1 ")
    List<Library> findByIdentifierIn(Iterable<String> ids, Sort sort);

    @Query("from Library l "
           + "left join fetch l.address "
           + "left join fetch l.platforms "
           + "left join fetch l.activeFTPConfiguration "
           + "left join fetch l.activeOcrLangConfiguration "
           + "where l.identifier = ?1")
    Library findOneWithDependencies(String identifier);

    @Query("select distinct l "
           + "from Library l "
           + "where l.superuser is false "
           + "and l.active is ?1")
    List<Library> findAllByActive(boolean active);

    
    @Query("from Library lib "
            + "left join fetch lib.activeOcrLangConfiguration conf "
            + "left join fetch conf.activatedOcrLanguages langs "
            + "left join fetch langs.ocrLanguage "
            + "where lib.identifier = ?1 ")
    Library findOneWithActifsOcrLanguages(String libraryId);
    

    Long countByActiveFTPConfiguration(FTPConfiguration conf);
    
    Long countByActiveExportFTPConfiguration(ExportFTPConfiguration conf);

    Long countByActiveCheckConfiguration(CheckConfiguration conf);
    
    Long countByActiveOcrLangConfiguration(OcrLangConfiguration conf);
    
    Long countByActiveFormatConfiguration(ViewsFormatConfiguration conf);

    Long countByDefaultRole(Role role);
}
