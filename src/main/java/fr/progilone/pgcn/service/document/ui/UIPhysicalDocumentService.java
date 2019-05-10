package fr.progilone.pgcn.service.document.ui;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.document.PhysicalDocument;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReport;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail;
import fr.progilone.pgcn.domain.document.conditionreport.Description;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty;
import fr.progilone.pgcn.domain.dto.document.ListPhysicalDocumentDTO;
import fr.progilone.pgcn.domain.dto.document.PhysicalDocumentDTO;
import fr.progilone.pgcn.service.document.PhysicalDocumentService;
import fr.progilone.pgcn.service.document.conditionreport.ConditionReportDetailService;
import fr.progilone.pgcn.service.document.conditionreport.ConditionReportService;
import fr.progilone.pgcn.service.document.mapper.PhysicalDocumentMapper;
import fr.progilone.pgcn.service.document.mapper.UIPhysicalDocumentMapper;
import fr.progilone.pgcn.service.util.transaction.VersionValidationService;

@Service
public class UIPhysicalDocumentService {

    @Autowired
    private PhysicalDocumentService physicalDocumentService;
    @Autowired
    private UIPhysicalDocumentMapper uiPhysicalDocumentMapper;
    @Autowired
    private ConditionReportService reportService;
    @Autowired
    private ConditionReportDetailService reportDetailService;

    @Transactional(readOnly = true)
    public PhysicalDocumentDTO findByIdentifier(final String identifier) {
        final PhysicalDocument physDoc = physicalDocumentService.findByIdentifier(identifier);
        return PhysicalDocumentMapper.INSTANCE.physicalDocumentToPhysicalDocumentDTO(physDoc);
    }

    @Transactional(readOnly = true)
    public List<ListPhysicalDocumentDTO> findByDocUnitIds(final List<String> docUnitIds) {
        
        final List<PhysicalDocument> physicalDocuments = physicalDocumentService.findByDocUnitIdentifiers(docUnitIds);
        final List<ListPhysicalDocumentDTO> physicalDtos = getListeDtosWithSum(physicalDocuments);
        return physicalDtos;
        
    }
    
    @Transactional(readOnly = true)
    public List<ListPhysicalDocumentDTO> findByTrainIdentifier(final String trainId) {
        final List<PhysicalDocument> physicalDocuments = physicalDocumentService.findByTrainIdentifier(trainId);
        final List<ListPhysicalDocumentDTO> physicalDtos = getListeDtosWithSum(physicalDocuments);
        return physicalDtos;
    }
    
    /**
     * 
     * @param docs
     * @return
     */
    private List<ListPhysicalDocumentDTO> getListeDtosWithSum(final List<PhysicalDocument> docs) {
        
        final List<ListPhysicalDocumentDTO> physicalDtos = docs.stream()
                .map(PhysicalDocumentMapper.INSTANCE::physicalDocumentToListPhysicalDocumentDTO)
                .collect(Collectors.toList());

        final DecimalFormat df = new DecimalFormat("#.##");
        Double somInsurance = 0.0;
        Integer maxDim1=0, maxDim2=0,maxDim3=0;
        int somNbDocs = 0;
        
        for (final ListPhysicalDocumentDTO pdto : physicalDtos) {
            final ConditionReport report = reportService.findByDocUnit(pdto.getDocUnit().getIdentifier());
            if (report != null) {
                final Optional<ConditionReportDetail> detail = reportDetailService.getLatest(report);
                if (detail.isPresent()) {
                    final Integer dim1 = detail.get().getDim1()!=null ? detail.get().getDim1() : 0;
                    final Integer dim2 = detail.get().getDim2()!=null ? detail.get().getDim2() : 0;
                    final Integer dim3 = detail.get().getDim3()!=null ? detail.get().getDim3() : 0;
                    maxDim1 = dim1 > maxDim1 ? dim1 : maxDim1;
                    maxDim2 = dim2 > maxDim2 ? dim2 : maxDim2;
                    maxDim3 = dim3 > maxDim3 ? dim3 : maxDim3;
                    final Integer nbPg = detail.get().getNbViewTotal();
                    pdto.setTotalPage(nbPg);
                    somNbDocs += nbPg;
                    
                    pdto.setReportDetailDim(String.valueOf(dim1)
                                     .concat(" x ")
                                     .concat(String.valueOf(dim2))
                                     .concat(" x ")
                                     .concat(String.valueOf(dim3)));
                    final Double insurVal = detail.get().getInsurance() == null ? 0.0 : detail.get().getInsurance();
                    somInsurance += insurVal;
                    pdto.setReportDetailInsurance(String.valueOf(df.format(insurVal)));
                    final Optional<Description> description = detail.get().getDescriptions().stream()
                                            .filter(desc-> DescriptionProperty.Type.VIGILANCE == desc.getProperty().getType()
                                                        && "MAX_ANGLE".equals(desc.getProperty().getCode()))
                                            .findFirst();
                    if (description.isPresent()){
                        pdto.setReportDetailOperture(description.get().getValue().getLabel());
                    } 
                }
            }
        }
        // Objet contenant uniquement les valeurs sommées à afficher #1318
        final ListPhysicalDocumentDTO somDto = new ListPhysicalDocumentDTO();
        somDto.setReportDetailDim(String.valueOf(maxDim1)
              .concat(" x ")
              .concat(String.valueOf(maxDim2))
              .concat(" x ")
              .concat(String.valueOf(maxDim3)));
        somDto.setReportDetailInsurance(String.valueOf(df.format(somInsurance)));
        somDto.setTotalPage(somNbDocs);
        physicalDtos.add(somDto);
        
        return physicalDtos;
    }

    @Transactional
    public PhysicalDocumentDTO update(final PhysicalDocumentDTO doc) {
        final PhysicalDocument pd = physicalDocumentService.findByIdentifier(doc.getIdentifier());
        // Contrôle d'accès concurrents
        VersionValidationService.checkForStateObject(pd, doc);

        uiPhysicalDocumentMapper.mapInto(doc, pd);
        final PhysicalDocument savedDoc = physicalDocumentService.save(pd);

        return findByIdentifier(savedDoc.getIdentifier());
    }
}
