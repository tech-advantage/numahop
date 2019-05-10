package fr.progilone.pgcn.service.lot.ui;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.audit.AuditLotRevisionDTO;
import fr.progilone.pgcn.domain.dto.lot.LotDTO;
import fr.progilone.pgcn.domain.dto.lot.LotListDTO;
import fr.progilone.pgcn.domain.dto.lot.LotWithConfigRulesDTO;
import fr.progilone.pgcn.domain.dto.lot.SimpleLotDTO;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.lot.Lot.LotStatus;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.util.CustomUserDetails;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.exception.PgcnBusinessException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.security.SecurityUtils;
import fr.progilone.pgcn.service.lot.LotService;
import fr.progilone.pgcn.service.lot.mapper.LotMapper;
import fr.progilone.pgcn.service.lot.mapper.LotWithConfigRulesMapper;
import fr.progilone.pgcn.service.lot.mapper.SimpleLotMapper;
import fr.progilone.pgcn.service.lot.mapper.UILotMapper;
import fr.progilone.pgcn.service.util.transaction.VersionValidationService;
import fr.progilone.pgcn.service.workflow.WorkflowService;
import fr.progilone.pgcn.web.util.AccessHelper;

/**
 * Service dédié à les gestion des vues des lots
 */
@Service
public class UILotService {

    private final LotService lotService;
    private final UILotMapper uiLotMapper;
    private final AccessHelper accessHelper;
    private final WorkflowService workflowService;

    @Autowired
    public UILotService(final LotService lotService, final UILotMapper uiLotMapper, 
                        final AccessHelper accessHelper, final WorkflowService workflowService) {
        this.lotService = lotService;
        this.uiLotMapper = uiLotMapper;
        this.accessHelper = accessHelper;
        this.workflowService = workflowService;
    }

    @Transactional
    public LotDTO create(final LotDTO request) throws PgcnValidationException {
        final Lot lot = new Lot();
        lot.setStatus(LotStatus.CREATED);
        uiLotMapper.mapInto(request, lot);
        try {
            final Lot savedLot = lotService.save(lot);
            final Lot lotWithProperties = lotService.getOne(savedLot.getIdentifier());
            return LotMapper.INSTANCE.lotToLotDTO(lotWithProperties);
        } catch (final PgcnBusinessException e) {
            e.getErrors().forEach(semanthequeError -> request.addError(buildError(semanthequeError.getCode())));
            throw new PgcnValidationException(request);
        }
    }

    /**
     * Mise à jour d'un lot
     *
     * @param request
     *         un objet contenant les informations necessaires à l'enregistrement d'un lot
     * @return le lot nouvellement créée ou mise à jour
     * @throws PgcnValidationException
     */
    @Transactional
    public LotDTO update(final LotDTO request) throws PgcnValidationException {
        final Lot lot = lotService.getOne(request.getIdentifier());

        // Contrôle d'accès concurrents
        VersionValidationService.checkForStateObject(lot, request);

        uiLotMapper.mapInto(request, lot);
        try {
            return LotMapper.INSTANCE.lotToLotDTO(lotService.save(lot));
        } catch (final PgcnBusinessException e) {
            e.getErrors().forEach(semanthequeError -> request.addError(buildError(semanthequeError.getCode())));
            throw new PgcnValidationException(request);
        }
    }

    private PgcnError buildError(final PgcnErrorCode pgcnErrorCode) {
        final PgcnError.Builder builder = new PgcnError.Builder();
        switch (pgcnErrorCode) {
            case LOT_DUPLICATE_LABEL:
                builder.setCode(pgcnErrorCode).setField("label");
                break;
            default:
                break;
        }
        return builder.build();
    }

    @Transactional(readOnly = true)
    public LotDTO getOne(final String id) {
        return LotMapper.INSTANCE.lotToLotDTO(lotService.getOne(id));
    }

    @Transactional(readOnly = true)
    public LotWithConfigRulesDTO getOneWithConfigRules(final String id) {
        return LotWithConfigRulesMapper.INSTANCE.lotToLotWithConfigRulesDTO(lotService.getOneWithConfigRules(id));
    }

    @Transactional(readOnly = true)
    public List<LotDTO> findByIdentifierIn(final List<String> identifiers) {
        final List<Lot> lots = lotService.findByIdentifierIn(identifiers);
        return lots.stream().map(LotMapper.INSTANCE::lotToLotDTO).collect(Collectors.toList());
    }

    @Transactional
    public void delete(final Collection<Lot> lots) throws PgcnBusinessException {
        lotService.delete(lots);
    }

    @Transactional(readOnly = true)
    public List<LotListDTO> findAllActiveDTO() {
        final List<Lot> lots = lotService.findAllByActive(true);
        return filterLots(lots).stream().map(LotMapper.INSTANCE::lotToLotListDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LotListDTO> findAllActiveForDelivery() {
        final List<Lot> lots = lotService.findAllByActiveForDelivery(true);
        final List<Lot> availables = 
                lots.stream().filter(lot -> checkLotIsAvailableForDeliv(lot)).collect(Collectors.toList());
                    
        return filterLots(availables).stream().map(LotMapper.INSTANCE::lotToLotListDTO).collect(Collectors.toList());
    }
       
    /**
     * Vérifie que le lot est dispo pour une livraison.
     * 
     * - lots physiques en cours (déjà filtrés par la requete)
     * - lots digitaux créés
     * - lots digitaux en cours avec un doc workflow en attente de relivraison  
     * 
     * @param lot
     * @return
     */
    private boolean checkLotIsAvailableForDeliv(final Lot lot) {
        
        boolean isAvailable = false;
        if (Lot.Type.PHYSICAL ==  lot.getType()) {
            isAvailable = true;
        } else if (Lot.Type.DIGITAL ==  lot.getType()) {
            if (LotStatus.CREATED == lot.getStatus()) {
                isAvailable = true;
            } else if (LotStatus.ONGOING == lot.getStatus()) {
                    for (final DocUnit du : lot.getDocUnits()) {
                        if (workflowService.isStateRunning(du, WorkflowStateKey.RELIVRAISON_DOCUMENT_EN_COURS)) {
                            isAvailable = true;
                            break;
                        }
                    }
            }
        }
        return isAvailable;
    }
    
    

    @Transactional(readOnly = true)
    public List<LotListDTO> findAllActiveForMultiLotsDelivery() {
        final List<Lot> lots = lotService.findAllActiveForMultiLotsDelivery(true);
        return filterLots(lots).stream().map(LotMapper.INSTANCE::lotToLotListDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Collection<LotListDTO> findAllByLibraryIn(final List<String> libraries) {
        final List<Lot> lots = lotService.findAllByLibraryIn(libraries);
        return lots.stream().map(LotMapper.INSTANCE::lotToLotListDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Collection<LotListDTO> findAllByProjectIn(final List<String> projects) {
        final List<Lot> lots = lotService.findAllByProjectIn(projects);
        return lots.stream().map(LotMapper.INSTANCE::lotToLotListDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LotDTO> findAllForProject(final String projectId) {
        final List<Lot> lots = lotService.findAllByProjectId(projectId);
        return lots.stream().map(LotMapper.INSTANCE::lotToLotDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LotDTO> findAllForDocUnitByProject(final String projectId) {
        final List<Lot> lots = lotService.findAllForDocUnitByProjectId(projectId);
        return lots.stream().map(LotMapper.INSTANCE::lotToLotDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SimpleLotDTO> findAllSimpleForProject(final String id) {
        final List<Lot> lots = lotService.findAllByProjectId(id);
        return lots.stream().map(SimpleLotMapper.INSTANCE::lotToSimpleLotDTO).collect(Collectors.toList());
    }

    /**
     * Filtre la liste de lots accessible au prestataire.
     *
     * @param lotsIn
     * @return
     */
    private List<Lot> filterLots(final List<Lot> lotsIn) {
        final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        if (currentUser != null && User.Category.PROVIDER.equals(currentUser.getCategory())) {
            return lotsIn.stream()
                         .filter(lot -> lot.getProvider() != null && StringUtils.equals(lot.getProvider().getIdentifier(),
                                                                                        currentUser.getIdentifier()))
                         .collect(Collectors.toList());
        } else {
            return lotsIn;
        }
    }

    @Transactional(readOnly = true)
    public Page<SimpleLotDTO> search(final String search,
                                     final List<String> libraries,
                                     final List<String> projects,
                                     final boolean active,
                                     final List<LotStatus> lotStatuses,
                                     final Integer docNumber,
                                     final List<String> fileFormats,
                                     final List<String> identifiers,
                                     final Integer page,
                                     final Integer size) {
        final Page<Lot> lots =
            lotService.search(search, libraries, projects, active, lotStatuses, null, docNumber, fileFormats, identifiers, page, size);
        return lots.map(SimpleLotMapper.INSTANCE::lotToSimpleLotDTO);
    }

    @Transactional(readOnly = true)
    public List<AuditLotRevisionDTO> getLotsForWidget(final LocalDate fromDate,
                                                      final List<String> libraries,
                                                      final List<String> projects,
                                                      final List<Lot.LotStatus> status) {

        final List<Lot> lots = lotService.findLotsForWidget(fromDate, libraries, projects, status);
        final List<AuditLotRevisionDTO> revs = new ArrayList<>();

        lots.stream().filter(lot -> accessHelper.checkLot(lot.getIdentifier())).forEach(lot -> {
            final AuditLotRevisionDTO dto = new AuditLotRevisionDTO();
            dto.setIdentifier(lot.getIdentifier());
            dto.setLabel(lot.getLabel());
            dto.setStatus(lot.getStatus());
            dto.setTimestamp(lot.getCreatedDate().toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli());
            revs.add(dto);
        });
        return revs;
    }

    @Transactional
    public void setProjectAndLot(final List<String> lotIds, final String project) {
        lotService.setProject(lotIds, project);
    }
}
