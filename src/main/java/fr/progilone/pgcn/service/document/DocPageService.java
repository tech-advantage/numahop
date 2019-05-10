package fr.progilone.pgcn.service.document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.document.DocPage;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.repository.document.DocPageRepository;
import fr.progilone.pgcn.service.storage.BinaryStorageManager;

@Service
public class DocPageService {

    @Autowired
    private DocPageRepository docPageRepository;
    @Autowired
    private BinaryStorageManager bm;

    @Transactional
    public DocPage save(final DocPage docPage) {
        return docPageRepository.save(docPage);
    }

    @Transactional
    public void delete(final DocPage docPage, final String libraryId) throws PgcnTechnicalException {
        try {
            bm.deleteAllFilesFromPage(docPage, libraryId);
        } catch (final IOException e) {
            throw new PgcnTechnicalException(e);
        }
        docPageRepository.delete(docPage);
    }

    @Transactional
    public DocPage findOne(final String identifier) {
        return docPageRepository.findOne(identifier);
    }

    @Transactional
    public List<String> getAllPageIdsByDigitalDocumentId(final String identifier) {
        final List<DocPage> dps = docPageRepository.getAllByDigitalDocumentIdentifier(identifier);
        return dps.stream()
                .map(AbstractDomainObject::getIdentifier)
                .collect(Collectors.toList());
    }

    @Transactional
    public Map<String, List<DocPage>> getPagesByProjectId(final String projectId) {
        final List<Object[]> results = docPageRepository.getPagesByProjectIdentifier(projectId);
        final Map<String, List<DocPage>> pages = new HashMap<>();
        results.forEach (res -> {
            final String key = (String)res[0];
            final DocPage pg = (DocPage)res[1];
            if (pages.get(key) == null) {
                final List<DocPage> dps = new ArrayList<>();
                dps.add(pg);
                pages.put((String)res[0], dps);
            } else {
               pages.get(key).add(pg);
            }

        });
        return pages;
    }

    @Transactional
    public Map<String, List<DocPage>> getPagesByLotId(final String lotId) {
        final List<Object[]> results = docPageRepository.getPagesByLotIdentifier(lotId);
        final Map<String, List<DocPage>> pages = new HashMap<>();
        results.forEach (res -> {
            final String key = (String)res[0];
            final DocPage pg = (DocPage)res[1];
            if (pages.get(key) == null) {
                final List<DocPage> dps = new ArrayList<>();
                dps.add(pg);
                pages.put((String)res[0], dps);
            } else {
               pages.get(key).add(pg);
            }

        });
        return pages;
    }

}
