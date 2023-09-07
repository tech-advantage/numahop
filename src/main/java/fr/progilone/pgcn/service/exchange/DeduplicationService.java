package fr.progilone.pgcn.service.exchange;

import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocProperty;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.repository.document.DocUnitRepository;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service assurant la recherche de doublon
 * <p>
 * Created by Sébastien on 20/12/2016.
 */
@Service
public class DeduplicationService {

    private final DocUnitRepository docUnitRepository;

    @Autowired
    public DeduplicationService(final DocUnitRepository docUnitRepository) {
        this.docUnitRepository = docUnitRepository;
    }

    /**
     * Recherche de doublon par rapport à l'unité documentaire docUnit
     *
     * @param docUnit
     * @return
     */
    public Collection<DocUnit> lookupDuplicates(final DocUnit docUnit) {
        final Set<DocUnit> duplicates = new HashSet<>();
        // UD doublons sur le PGCN Id
        if (docUnit.getPgcnId() != null) {
            final DocUnit duplPgcnId = docUnitRepository.getOneByPgcnIdAndState(docUnit.getPgcnId(), DocUnit.State.AVAILABLE);

            if (duplPgcnId != null) {
                duplicates.add(duplPgcnId);
            }
        }
        // UD doublon sur l'identifiant de la notice
        docUnit.getRecords().stream().map(bib -> lookupDuplicates(docUnit, bib)).flatMap(Collection::stream).forEach(duplicates::add);
        return duplicates;
    }

    /**
     * Recherche des UD doublon en se basant sur le champ DC Identifier des notices
     *
     * @param docUnit
     * @param bib
     * @return
     */
    private List<DocUnit> lookupDuplicates(final DocUnit docUnit, final BibliographicRecord bib) {
        final List<String> identifiers = bib.getProperties()
                                            .stream()
                                            .filter(prop -> StringUtils.equals(prop.getType().getIdentifier(), "identifier"))
                                            .map(DocProperty::getValue)
                                            .collect(Collectors.toList());
        // Recherche des doublons déjà importés
        return docUnitRepository.searchDuplicates(docUnit, identifiers, DocUnit.State.AVAILABLE);
    }
}
