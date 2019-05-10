package fr.progilone.pgcn.service.ftpconfiguration.mapper;

import fr.progilone.pgcn.domain.dto.ftpconfiguration.FTPConfigurationDTO;
import fr.progilone.pgcn.domain.ftpconfiguration.FTPConfiguration;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.repository.library.LibraryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by lebouchp on 03/02/2017.
 */
@Service
public class UIFTPConfigurationMapper {

    @Autowired
    private LibraryRepository libraryRepository;

    public void mapInto(FTPConfigurationDTO ftpConfigurationDTO, FTPConfiguration ftpConfiguration) {
        ftpConfiguration.setAddress(ftpConfigurationDTO.getAddress());
        ftpConfiguration.setLogin(ftpConfigurationDTO.getLogin());
        ftpConfiguration.setLabel(ftpConfigurationDTO.getLabel());
        ftpConfiguration.setPassword(ftpConfigurationDTO.getPassword());
        ftpConfiguration.setDeliveryFolder(ftpConfigurationDTO.getDeliveryFolder());

        if(ftpConfigurationDTO.getLibrary() != null) {
            Library library = libraryRepository.getOne(ftpConfigurationDTO.getLibrary().getIdentifier());
            ftpConfiguration.setLibrary(library);
        }
    }
}
