package fr.progilone.pgcn.service.train.mapper;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.progilone.pgcn.domain.dto.train.TrainDTO;
import fr.progilone.pgcn.domain.train.Train;
import fr.progilone.pgcn.repository.document.PhysicalDocumentRepository;
import fr.progilone.pgcn.repository.project.ProjectRepository;

@Service
public class UITrainMapper {


    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private PhysicalDocumentRepository physicalDocumentRepository;

    public UITrainMapper() {
    }

    public void mapInto(final TrainDTO trainDTO, final Train train) {
        train.setIdentifier(trainDTO.getIdentifier());
        train.setLabel(trainDTO.getLabel());
        train.setActive(trainDTO.getActive());
        train.setDescription(trainDTO.getDescription());
        train.setStatus(trainDTO.getStatus());
        if (trainDTO.getProject() != null) {
            train.setProject(projectRepository.getOne(trainDTO.getProject().getIdentifier()));
        }
        if (trainDTO.getPhysicalDocuments() != null) {
            train.setPhysicalDocuments(trainDTO.getPhysicalDocuments()
                                           .stream()
                                           .filter(documentDTO -> documentDTO.getIdentifier() != null)
                                           .map(documentDTO -> physicalDocumentRepository.findByIdentifier(documentDTO.getIdentifier()))
                                               .collect(Collectors.toSet()));
        }
        train.setProviderSendingDate(trainDTO.getProviderSendingDate());
        train.setReturnDate(trainDTO.getReturnDate());
    }
}
