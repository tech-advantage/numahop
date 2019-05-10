package fr.progilone.pgcn.service.document.mapper;

import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.dto.document.DigitalDocumentDTO;
import org.springframework.stereotype.Service;

@Service
public class UIDigitalDocumentMapper {

    public void mapInto(final DigitalDocumentDTO docDTO, final DigitalDocument doc) {
        doc.setDigitalId(docDTO.getDigitalId());
        doc.setCheckNotes(docDTO.getCheckNotes());
        doc.getDocUnit().setDigitizingNotes(docDTO.getDocUnit().getDigitizingNotes());
    }
}
