package fr.progilone.pgcn.service.document.mapper;

import fr.progilone.pgcn.domain.document.PhysicalDocument;
import fr.progilone.pgcn.domain.dto.document.PhysicalDocumentDTO;
import fr.progilone.pgcn.service.train.TrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UIPhysicalDocumentMapper {

    @Autowired
    private TrainService trainService;

    public void mapInto(final PhysicalDocumentDTO docDTO, final PhysicalDocument doc) {
        doc.setDigitalId(docDTO.getDigitalId());
        doc.setTotalPage(docDTO.getTotalPage());
        doc.setName(docDTO.getName());
        if (docDTO.getTrain() != null) {
            doc.setTrain(trainService.getOne(docDTO.getTrain().getIdentifier()));
        }
    }
}
