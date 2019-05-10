package fr.progilone.pgcn.service.document.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.progilone.pgcn.domain.document.ArchiveCollection;
import fr.progilone.pgcn.domain.document.ArchiveHeader;
import fr.progilone.pgcn.domain.document.ArchiveItem;
import fr.progilone.pgcn.domain.document.ArchiveSubject;
import fr.progilone.pgcn.service.exchange.internetarchive.InternetArchiveItemDTO;

@Service
public class UIInternetArchiveItemMapper {

    @Autowired
    public UIInternetArchiveItemMapper() {
    }

    public void mapInto(final InternetArchiveItemDTO itemDTO, final ArchiveItem item) {
        item.setArchiveIdentifier(itemDTO.getArchiveIdentifier());
        item.setContributor(itemDTO.getContributor());
        item.setCoverage(itemDTO.getCoverage());
        item.setCreator(itemDTO.getCreator());
        item.setCredits(itemDTO.getCredits());
        item.setDate(itemDTO.getDate());
        item.setDescription(itemDTO.getDescription());
        item.setLanguage(itemDTO.getLanguage());
        item.setLicenseUrl(itemDTO.getLicenseUrl());
        item.setMediatype(itemDTO.getMediatype().name());
        item.setCustomMediatype(itemDTO.getCustomMediatype());
        item.setNotes(itemDTO.getNotes());
        item.setPublisher(itemDTO.getPublisher());
        item.setRights(itemDTO.getRights());
        item.setTitle(itemDTO.getTitle());
        item.setType(itemDTO.getType());
        item.setSource(itemDTO.getSource());

        item.getHeaders().clear();
        item.getSubjects().clear();
        item.getCollections().clear();

        for(final InternetArchiveItemDTO.CustomHeader header : itemDTO.getCustomHeaders()) {
            final ArchiveHeader archiveHeader = createHeader(header);
            item.getHeaders().add(archiveHeader);
            archiveHeader.setItem(item);
        }
        for(final String subject : itemDTO.getSubjects()) {
            final ArchiveSubject archiveSubject = createSubject(subject);
            item.getSubjects().add(archiveSubject);
            archiveSubject.setItem(item);
        }
        for(final String collection : itemDTO.getCollections()) {
            final ArchiveCollection archiveCollection = createCollection(collection);
            item.getCollections().add(archiveCollection);
            archiveCollection.setItem(item);
        }
    }

    private ArchiveHeader createHeader(final InternetArchiveItemDTO.CustomHeader dto) {
        final ArchiveHeader header = new ArchiveHeader();
        header.setValue(dto.getValue());
        header.setType(dto.getType());
        return header;
    }
    private ArchiveSubject createSubject(final String dto) {
        final ArchiveSubject subject = new ArchiveSubject();
        subject.setValue(dto);
        return subject;
    }
    private ArchiveCollection createCollection(final String dto) {
        final ArchiveCollection collection = new ArchiveCollection();
        collection.setValue(dto);
        return collection;
    }
}
