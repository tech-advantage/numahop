package fr.progilone.pgcn.service.delivery.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.service.delivery.PrefixedDocuments;
import fr.progilone.pgcn.service.delivery.PrefixedDocumentsDTO;
import fr.progilone.pgcn.service.document.DigitalDocumentService;
import fr.progilone.pgcn.service.document.PhysicalDocumentService;

@Service
public class PrefixedDocumentsMapper {

	public PrefixedDocumentsMapper() {
	    
	}

	@Autowired
	private DigitalDocumentService ddService;
	@Autowired
    private PhysicalDocumentService pdService;

	@Transactional(readOnly = true)
	public PrefixedDocuments mapFromDTO(final PrefixedDocumentsDTO dto) {
	    final PrefixedDocuments result = new PrefixedDocuments();
	    result.setFiles(dto.getFiles());
	    dto.getDigitalDocuments().forEach(dd -> {
	        result.addDigitalDocument(ddService.findOne(dd));
	    });
	    dto.getPhysicalDocuments().forEach(pd -> {
	        result.addPhysicalDocument(pdService.findByIdentifier(pd));
	    });
	    return result;
	}

	public PrefixedDocumentsDTO mapToDTO(final PrefixedDocuments doc) {
	    final PrefixedDocumentsDTO dto = new PrefixedDocumentsDTO();
	    dto.setFiles(doc.getFiles());
	    doc.getDigitalDocuments().forEach(dd -> {
	        dto.addDigitalDocument(dd.getIdentifier());
	    });
	    doc.getPhysicalDocuments().forEach(pd -> {
            dto.addPhysicalDocument(pd.getIdentifier());
        });
	    return dto;
	}
}
