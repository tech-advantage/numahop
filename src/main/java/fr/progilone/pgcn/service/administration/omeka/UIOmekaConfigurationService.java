package fr.progilone.pgcn.service.administration.omeka;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.administration.omeka.OmekaConfiguration;
import fr.progilone.pgcn.domain.administration.omeka.OmekaList;
import fr.progilone.pgcn.domain.dto.administration.omeka.OmekaConfigurationDTO;
import fr.progilone.pgcn.domain.dto.administration.omeka.OmekaListDTO;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.administration.mapper.OmekaConfigurationMapper;
import fr.progilone.pgcn.service.administration.mapper.OmekaListMapper;
import fr.progilone.pgcn.service.library.LibraryService;

@Service
public class UIOmekaConfigurationService {
    
    private final OmekaConfigurationService omekaConfigurationService;
    private final LibraryService libraryService;

    @Autowired
    public UIOmekaConfigurationService(final OmekaConfigurationService omekaConfigurationService,
                                       final LibraryService libraryService) {
        this.omekaConfigurationService = omekaConfigurationService;
        this.libraryService = libraryService;
    }

    @Transactional
    public OmekaConfigurationDTO getById(final String confId) {
        
        final OmekaConfiguration conf = omekaConfigurationService.findOne(confId);
        return OmekaConfigurationMapper.INSTANCE.confOmekaToDto(conf);
    }
    
    @Transactional
    public OmekaConfigurationDTO create(final OmekaConfigurationDTO dto) throws PgcnTechnicalException {
        fusionLists(dto);
        final OmekaConfiguration saved = omekaConfigurationService.save(OmekaConfigurationMapper.INSTANCE.dtoToConfOmeka(dto));
        return OmekaConfigurationMapper.INSTANCE.confOmekaToDto(saved);
    }
    
    @Transactional
    public OmekaConfigurationDTO update(final OmekaConfigurationDTO dto) throws PgcnTechnicalException {
        
        // Charge existant
        final OmekaConfiguration conf = omekaConfigurationService.findOne(dto.getIdentifier());   
        fusionLists(dto);
        updateProperties(dto, conf);
        return OmekaConfigurationMapper.INSTANCE.confOmekaToDto(omekaConfigurationService.save(conf));
    }
    
    /**
     * 
     * @param dto
     */
    private void fusionLists(final OmekaConfigurationDTO dto) {
        if (dto.getOmekaLists() == null){
            dto.setOmekaLists(new ArrayList<>());
        } else {
            dto.getOmekaLists().clear();
        }
        if (dto.getOmekaCollections() != null) {
            final List<OmekaListDTO> colls = dto.getOmekaCollections().stream()
                                            .filter(ol-> ol.getName() != null)
                                            .collect(Collectors.toList());
            dto.getOmekaLists().addAll(colls);
        }
        if (dto.getOmekaItems() != null) {
            final List<OmekaListDTO> itms = dto.getOmekaItems().stream()
                                            .filter(ol-> ol.getName() != null)
                                            .collect(Collectors.toList());
            dto.getOmekaLists().addAll(itms);
        }  
    }
    
    /**
     * MAJ des propriétés 
     * @param src
     * @param dest
     */
    private void updateProperties(final OmekaConfigurationDTO srcDto, final OmekaConfiguration dest) {
        dest.setActive(srcDto.isActive());
        dest.setLabel(srcDto.getLabel());
        
        if (! StringUtils.equals(srcDto.getLibrary().getIdentifier(), dest.getLibrary().getIdentifier())) {
            dest.setLibrary(libraryService.findOne(srcDto.getLibrary().getIdentifier()));
        }
        dest.setStorageServer(srcDto.getStorageServer());
        dest.setPort(srcDto.getPort());
        dest.setLogin(srcDto.getLogin());
        dest.setPassword(srcDto.getPassword());
        dest.setAddress(srcDto.getAddress());
        dest.setAccessUrl(srcDto.getAccessUrl());
        dest.setMailCsv(srcDto.getMailCsv());
        
        dest.setExportMets(srcDto.isExportMets());
        dest.setExportMaster(srcDto.isExportMaster());
        dest.setExportView(srcDto.isExportView());
        dest.setExportThumb(srcDto.isExportThumb());
        dest.setExportPdf(srcDto.isExportPdf());
        
        
        // Liste de collections et d'items
        final List<OmekaList> neueList = OmekaListMapper.INSTANCE.dtosToObjs(srcDto.getOmekaLists());
        final List<String> toDel = dest.getOmekaLists().stream().filter(ol -> !neueList.contains(ol))
                                                                   .map(ol -> ol.getIdentifier())
                                                                   .collect(Collectors.toList());
        // to remove
        final List<OmekaList> copy = dest.getOmekaLists().stream().collect(Collectors.toList());
        copy.forEach(ol -> {
            if (toDel.contains(ol.getIdentifier())) {
                dest.getOmekaLists().remove(ol);
            } 
        });
        // to maj
        dest.getOmekaLists().forEach(ol -> {
            ol.setConfOmeka(dest);
            final OmekaList nouv = neueList.stream()
                                    .filter(nl -> StringUtils.equals(nl.getIdentifier(), ol.getIdentifier()))
                                    .findFirst().orElse(null);
                    ol.setName(nouv.getName());  
        });
        // to add
        neueList.forEach(ol -> {
            ol.setConfOmeka(dest);
            if (!dest.getOmekaLists().contains(ol)) {
                dest.getOmekaLists().add(ol);
            }
        });
    }
    
}
