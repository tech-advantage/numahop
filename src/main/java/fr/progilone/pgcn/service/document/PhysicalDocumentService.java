package fr.progilone.pgcn.service.document;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.document.PhysicalDocument;
import fr.progilone.pgcn.domain.document.PhysicalDocument.PhysicalDocumentStatus;
import fr.progilone.pgcn.repository.document.PhysicalDocumentRepository;

@Service
public class PhysicalDocumentService {

    private final PhysicalDocumentRepository physicalDocumentRepository;

    @Autowired
    public PhysicalDocumentService(final PhysicalDocumentRepository physicalDocumentRepository) {
        this.physicalDocumentRepository = physicalDocumentRepository;
    }

    @Transactional
    public PhysicalDocument save(final PhysicalDocument doc) {
        setDefaultValues(doc);
        return physicalDocumentRepository.save(doc);
    }

    @Transactional(readOnly = true)
    public PhysicalDocument findByIdentifier(final String identifier) {
        return physicalDocumentRepository.findByIdentifier(identifier);
    }

    @Transactional
    public List<PhysicalDocument> findByTrainIdentifier(final String identifier) {
        return physicalDocumentRepository.findByTrainIdentifier(identifier);
    }
    
    @Transactional
    public List<PhysicalDocument> findByDocUnitIdentifiers(final List<String> identifiers) {
        return physicalDocumentRepository.findByDocUnitIdentifierIn(identifiers);
    }

    @Transactional(readOnly = true)
    public List<PhysicalDocument> findAll() {
        return physicalDocumentRepository.findAll();
    }

    @Transactional
    public void delete(final String identifier) {
        physicalDocumentRepository.delete(identifier);
    }

    private void setDefaultValues(final PhysicalDocument doc) {

        // initialisation du statut
        if (doc.getStatus() == null) {
            doc.setStatus(PhysicalDocumentStatus.CREATED);
        }
    }
}
