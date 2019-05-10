package fr.progilone.pgcn.service.exchange.internetarchive.mapper;

import fr.progilone.pgcn.domain.document.ArchiveCollection;
import fr.progilone.pgcn.domain.document.ArchiveHeader;
import fr.progilone.pgcn.domain.document.ArchiveItem;
import fr.progilone.pgcn.domain.document.ArchiveSubject;
import fr.progilone.pgcn.service.exchange.internetarchive.InternetArchiveItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Mapper
public interface ArchiveItemMapper {

	ArchiveItemMapper INSTANCE = Mappers.getMapper(ArchiveItemMapper.class);

    InternetArchiveItemDTO archiveItemToInternetArchiveItemDTO(ArchiveItem item);

    default List<String> archiveSubjectToString(Set<ArchiveSubject> archiveSubject) {
        return archiveSubject.stream().map(ArchiveSubject::getValue).collect(Collectors.toList());
    }
    default List<String> archiveCollectionToString(Set<ArchiveCollection> archiveCollection) {
        return archiveCollection.stream().map(ArchiveCollection::getValue).collect(Collectors.toList());
    }
    default List<InternetArchiveItemDTO.CustomHeader> archiveHeaderToCustomHeader(Set<ArchiveHeader> archiveHeaders) {
        List<InternetArchiveItemDTO.CustomHeader> customHeaders = new ArrayList<>();
        for(ArchiveHeader archiveHeader : archiveHeaders) {
            InternetArchiveItemDTO.CustomHeader customHeader = new InternetArchiveItemDTO.CustomHeader();
            customHeader.setValue(archiveHeader.getValue());
            customHeader.setType(archiveHeader.getType());
            customHeaders.add(customHeader);
        }
        return customHeaders;
    }
}
