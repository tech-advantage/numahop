package fr.progilone.pgcn.repository.administration.omeka;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.progilone.pgcn.domain.administration.omeka.OmekaList;

/**
 * @author jbrunet
 * Créé le 19 avr. 2017
 */
public interface OmekaListRepository extends JpaRepository<OmekaList, String> {

    @Query("select distinct ol "
           + "from OmekaList ol "
           + "left join ol.confOmeka conf "
           + "left join conf.library lib "
           + "where lib.identifier = ?1 and ol.type =?2 ")
    List<OmekaList> findAllByLibraryAndType(String identifier, OmekaList.ListType type);
    
    
}
