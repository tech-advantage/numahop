package fr.progilone.pgcn.repository.document;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import fr.progilone.pgcn.domain.document.DocUnit;

public interface DocUnitRepositoryCustom {

    /**
     * Recherche rapide d'unités documentaires
     *
     * @return
     */
    Page<DocUnit> search(String search,
                         boolean hasDigitalDocuments,
                         boolean active,
                         boolean archived,
                         boolean nonArchived,
                         boolean archivable,
                         boolean nonArchivable,
                         boolean distributed,
                         boolean nonDistributed,
                         boolean diffusable,
                         boolean nonDiffusable,
                         List<String> libraries,
                         List<String> projects,
                         List<String> lots,
                         List<String> trains,
                         List<String> statuses,
                         LocalDate lastModifiedDateFrom,
                         LocalDate lastModifiedDateTo,
                         LocalDate createdDateFrom,
                         LocalDate createdDateTo,
                         List<String> identifiers,
                         @PageableDefault(size = Integer.MAX_VALUE) final Pageable pageable);

    /**
     * Recherche d'unités documentaires correspondant à l'un des identifiants, pour un état donné et par rapport à une unité documentaire de référence
     *
     * @param reference
     * @param identifiers
     * @param state
     * @return
     */
    List<DocUnit> searchDuplicates(final DocUnit reference, final List<String> identifiers, final DocUnit.State... state);
    
    /**
     * Recherche d'unite documentaire complete.
     * 
     * @param identifier
     * @return
     */
    DocUnit findOneWithAllDependencies(String identifier);
}
