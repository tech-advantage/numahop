package fr.progilone.pgcn.domain.dto.delivery;

import java.util.HashSet;
import java.util.Set;

import fr.progilone.pgcn.domain.dto.AbstractDTO;
import fr.progilone.pgcn.domain.dto.document.DigitalDocumentDTO;
import fr.progilone.pgcn.domain.dto.document.PhysicalDocumentDTO;
import fr.progilone.pgcn.domain.dto.document.PreDeliveryDocumentDTO;

/**
 * Classe qui contient les informations à afficher à l'utilisateur pour l'écran de validation pré-livraison.
 */
public class PreDeliveryDTO extends AbstractDTO {
    private Set<PreDeliveryDocumentDTO> documents = new HashSet<>();
    private final Set<DigitalDocumentDTO> lockedDigitalDocuments = new HashSet<>();
    private Set<PhysicalDocumentDTO> undeliveredDocuments = new HashSet<>();

    public PreDeliveryDTO(final Set<PreDeliveryDocumentDTO> documents) {
        this.documents = documents;
    }
    public PreDeliveryDTO(){}

    public Set<PreDeliveryDocumentDTO> getDocuments() {
        return documents;
    }

    public void setDocuments(final Set<PreDeliveryDocumentDTO> documents) {
        this.documents.clear();
        if(documents != null) {
            documents.forEach(this::addDocument);
        }
    }
    
    public void addDocument(final PreDeliveryDocumentDTO doc) {
        if(doc != null) {
            this.documents.add(doc);
        }
    }
    
    public Set<DigitalDocumentDTO> getLockedDigitalDocuments() {
        return lockedDigitalDocuments;
    }
    public void setLockedDigitalDocuments(final Set<DigitalDocumentDTO> digitalDocumentsLocked) {
        this.lockedDigitalDocuments.clear();
        if(digitalDocumentsLocked != null) {
            digitalDocumentsLocked.forEach(this::addLockedDigitalDocument);
        }
    }
    
    public void addLockedDigitalDocument(final DigitalDocumentDTO digitalDocument) {
        if(digitalDocument != null) {
            this.lockedDigitalDocuments.add(digitalDocument);
        }
    }

    public Set<PhysicalDocumentDTO> getUndeliveredDocuments() {
        return undeliveredDocuments;
    }

    public void setUndeliveredDocuments(final Set<PhysicalDocumentDTO> undeliveredDocuments) {
        this.undeliveredDocuments = undeliveredDocuments;
    }

    public void addUndeliveredDigitalDocument(final PhysicalDocumentDTO physicalDocument) {
        if (physicalDocument != null) {
            this.undeliveredDocuments.add(physicalDocument);
        }
    }
}
