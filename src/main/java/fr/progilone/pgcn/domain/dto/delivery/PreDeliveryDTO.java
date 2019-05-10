package fr.progilone.pgcn.domain.dto.delivery;

import fr.progilone.pgcn.domain.dto.AbstractDTO;
import fr.progilone.pgcn.domain.dto.document.DigitalDocumentDTO;
import fr.progilone.pgcn.domain.dto.document.PreDeliveryDocumentDTO;

import java.util.HashSet;
import java.util.Set;

/**
 * Classe qui contient les informations à afficher à l'utilisateur pour l'écran de validation pré-livraison.
 */
public class PreDeliveryDTO extends AbstractDTO {
    private Set<PreDeliveryDocumentDTO> documents = new HashSet<>();
    private Set<DigitalDocumentDTO> lockedDigitalDocuments = new HashSet<>();

    public PreDeliveryDTO(Set<PreDeliveryDocumentDTO> documents) {
        this.documents = documents;
    }
    public PreDeliveryDTO(){}

    public Set<PreDeliveryDocumentDTO> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<PreDeliveryDocumentDTO> documents) {
        this.documents.clear();
        if(documents != null) {
            documents.forEach(this::addDocument);
        }
    }
    
    public void addDocument(PreDeliveryDocumentDTO doc) {
        if(doc != null) {
            this.documents.add(doc);
        }
    }
    
    public Set<DigitalDocumentDTO> getLockedDigitalDocuments() {
        return lockedDigitalDocuments;
    }
    public void setLockedDigitalDocuments(Set<DigitalDocumentDTO> digitalDocumentsLocked) {
        this.lockedDigitalDocuments.clear();
        if(digitalDocumentsLocked != null) {
            digitalDocumentsLocked.forEach(this::addLockedDigitalDocument);
        }
    }
    
    public void addLockedDigitalDocument(DigitalDocumentDTO digitalDocument) {
        if(digitalDocument != null) {
            this.lockedDigitalDocuments.add(digitalDocument);
        }
    }
}
