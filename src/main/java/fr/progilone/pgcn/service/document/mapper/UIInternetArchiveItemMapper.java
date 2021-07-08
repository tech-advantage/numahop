package fr.progilone.pgcn.service.document.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.progilone.pgcn.domain.document.ArchiveCollection;
import fr.progilone.pgcn.domain.document.ArchiveContributor;
import fr.progilone.pgcn.domain.document.ArchiveCoverage;
import fr.progilone.pgcn.domain.document.ArchiveCreator;
import fr.progilone.pgcn.domain.document.ArchiveHeader;
import fr.progilone.pgcn.domain.document.ArchiveItem;
import fr.progilone.pgcn.domain.document.ArchiveLanguage;
import fr.progilone.pgcn.domain.document.ArchiveSubject;
import fr.progilone.pgcn.service.exchange.internetarchive.InternetArchiveItemDTO;

@Service
public class UIInternetArchiveItemMapper {

    @Autowired
    public UIInternetArchiveItemMapper() {
    }

    public void mapInto(final InternetArchiveItemDTO itemDTO, final ArchiveItem item) {
        item.setArchiveIdentifier(itemDTO.getArchiveIdentifier());
        item.setCredits(itemDTO.getCredits());
        item.setDate(itemDTO.getDate());
        item.setDescription(itemDTO.getDescription());
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
        item.getContributors().clear();
        item.getCreators().clear();
        item.getLanguages().clear();

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
        for (final String coverage : itemDTO.getCoverages()) {
            final ArchiveCoverage archiveCoverage = createCoverage(coverage);
            item.getCoverages().add(archiveCoverage);
            archiveCoverage.setItem(item);
        }
        for (final String contributor : itemDTO.getContributors()) {
            final ArchiveContributor archiveContributor = createContributor(contributor);
            item.getContributors().add(archiveContributor);
            archiveContributor.setItem(item);
        }
        for (final String creator : itemDTO.getCreators()) {
            final ArchiveCreator archiveCreator = createCreator(creator);
            item.getCreators().add(archiveCreator);
            archiveCreator.setItem(item);
        }
        for (final String language : itemDTO.getLanguages()) {
            final ArchiveLanguage archiveLanguage = createLanguage(language);
            item.getLanguages().add(archiveLanguage);
            archiveLanguage.setItem(item);
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

    private ArchiveCoverage createCoverage(final String dto) {
        final ArchiveCoverage coverage = new ArchiveCoverage();
        coverage.setValue(dto);
        return coverage;
    }

    private ArchiveContributor createContributor(final String dto) {
        final ArchiveContributor contributor = new ArchiveContributor();
        contributor.setValue(dto);
        return contributor;
    }

    private ArchiveCreator createCreator(final String dto) {
        final ArchiveCreator creator = new ArchiveCreator();
        creator.setValue(dto);
        return creator;
    }

    private ArchiveLanguage createLanguage(final String dto) {
        final ArchiveLanguage language = new ArchiveLanguage();
        language.setValue(dto);
        return language;
    }
}
