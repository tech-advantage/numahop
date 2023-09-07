package fr.progilone.pgcn.service.multilotsdelivery.ui;

import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.dto.delivery.SimpleDeliveryLotDTO;
import fr.progilone.pgcn.domain.dto.document.PreDeliveryDocumentDTO;
import fr.progilone.pgcn.domain.dto.document.PreDeliveryLockedDocsDTO;
import fr.progilone.pgcn.domain.dto.lot.SimpleLotForDeliveryDTO;
import fr.progilone.pgcn.domain.dto.multilotsdelivery.MultiLotsDeliveryDTO;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.multilotsdelivery.MultiLotsDelivery;
import fr.progilone.pgcn.domain.util.CustomUserDetails;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.security.SecurityUtils;
import fr.progilone.pgcn.service.delivery.DeliveryAsyncService;
import fr.progilone.pgcn.service.delivery.DeliveryProcessResults;
import fr.progilone.pgcn.service.delivery.DeliveryProcessService;
import fr.progilone.pgcn.service.es.EsDeliveryService;
import fr.progilone.pgcn.service.lot.mapper.LotMapper;
import fr.progilone.pgcn.service.multilotsdelivery.MultiLotsDeliveryService;
import fr.progilone.pgcn.service.multilotsdelivery.mapper.MultiLotsDeliveryMapper;
import fr.progilone.pgcn.service.multilotsdelivery.mapper.UIMultiLotsDeliveryMapper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UIMultiLotsDeliveryService {

    private static final Logger LOG = LoggerFactory.getLogger(UIMultiLotsDeliveryService.class);

    private final MultiLotsDeliveryService multiService;
    private final UIMultiLotsDeliveryMapper uiMapper;
    private final DeliveryProcessService deliveryProcessService;
    private final DeliveryAsyncService deliveryAsyncService;
    private final EsDeliveryService esDeliveryService;

    @Autowired
    public UIMultiLotsDeliveryService(final MultiLotsDeliveryService multiService,
                                      final UIMultiLotsDeliveryMapper uiMapper,
                                      final DeliveryProcessService deliveryProcessService,
                                      final DeliveryAsyncService deliveryAsyncService,
                                      final EsDeliveryService esDeliveryService) {
        this.multiService = multiService;
        this.uiMapper = uiMapper;
        this.deliveryProcessService = deliveryProcessService;
        this.deliveryAsyncService = deliveryAsyncService;
        this.esDeliveryService = esDeliveryService;
    }

    /**
     * Recherche paramétrée
     *
     * @param search
     * @param projects
     * @param lots
     * @param status
     * @param dateFrom
     * @param dateTo
     * @param page
     * @param size
     * @return
     */
    public Page<MultiLotsDeliveryDTO> search(final String search,
                                             final List<String> libraries,
                                             final List<String> projects,
                                             final List<String> lots,
                                             final List<String> providers,
                                             final List<Delivery.DeliveryStatus> status,
                                             final LocalDate dateFrom,
                                             final LocalDate dateTo,
                                             final Integer page,
                                             final Integer size) {

        final Page<MultiLotsDelivery> multiDeliveries = multiService.search(search, libraries, projects, lots, providers, status, dateFrom, dateTo, page, size);
        return multiDeliveries.map(MultiLotsDeliveryMapper.INSTANCE::objToDto);
    }

    @Transactional(readOnly = true)
    public MultiLotsDelivery getOne(final String id) {

        return multiService.getOne(id);
    }

    @Transactional(readOnly = true)
    public MultiLotsDeliveryDTO findOneByIdWithDeliveries(final String id) throws PgcnValidationException {

        final MultiLotsDelivery multiDeliv = multiService.findOneByIdWithDeliveries(id);
        return MultiLotsDeliveryMapper.INSTANCE.objToDto(multiDeliv);
    }

    @Transactional
    public MultiLotsDeliveryDTO create(final MultiLotsDeliveryDTO request) throws PgcnValidationException {
        validate(request);
        final MultiLotsDelivery multiDeliv = new MultiLotsDelivery();
        uiMapper.mapInto(request, multiDeliv);

        final MultiLotsDelivery savedMulti = multiService.save(multiDeliv);
        return MultiLotsDeliveryMapper.INSTANCE.objToDto(savedMulti);
    }

    @Transactional
    public MultiLotsDeliveryDTO update(final MultiLotsDeliveryDTO request) throws PgcnValidationException {
        validate(request);
        final MultiLotsDelivery multiDeliv = multiService.getOne(request.getIdentifier());
        uiMapper.mapInto(request, multiDeliv);

        final MultiLotsDelivery savedMulti = multiService.save(multiDeliv);
        return MultiLotsDeliveryMapper.INSTANCE.objToDto(savedMulti);
    }

    @Transactional
    public void delete(final String identifier) {
        multiService.delete(identifier);
    }

    /**
     * Validation du multiLotsDelivery DTO avant le merge
     */
    private PgcnList<PgcnError> validate(final MultiLotsDeliveryDTO dto) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        // TODO : Le label est unique PAR LIBRARY
        // if (StringUtils.isNotBlank(dto.getLabel())) {
        // final Delivery duplicate = deliveryService.findOneByLabel(dto.getLabel());
        //
        // if (duplicate != null && (dto.getIdentifier() == null || !duplicate.getIdentifier().equalsIgnoreCase(dto.getIdentifier()))) {
        // errors.add(builder.reinit().setCode(PgcnErrorCode.DELIVERY_DUPLICATE_LABEL).setField("label").build());
        // }
        // }

        // Au moins 1 lot obligatoire.
        if (CollectionUtils.isEmpty(dto.getLots())) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.DELIVERY_LOT_MANDATORY).setField("lot").build());
        }

        // Retour
        if (!errors.isEmpty()) {
            dto.setErrors(errors);
            throw new PgcnValidationException(dto, errors);
        }
        return errors;
    }

    /**
     *
     *
     * @param multiDelivery
     * @param lockedDocs
     * @param metaDatas
     * @param prefixToExclude
     * @throws PgcnTechnicalException
     */
    public void deliver(final MultiLotsDeliveryDTO multiDelivery,
                        final List<PreDeliveryLockedDocsDTO> lockedDocs,
                        final List<PreDeliveryDocumentDTO> metaDatas,
                        final List<String> prefixToExclude) throws PgcnTechnicalException {

        final Map<String, DeliveryProcessResults> allProcessElems = new HashMap<>();
        final List<String> deliveryIds = multiDelivery.getDeliveries().stream().map(SimpleDeliveryLotDTO::getIdentifier).collect(Collectors.toList());

        final CustomUserDetails user = SecurityUtils.getCurrentUser();
        final String libraryId = user != null ? user.getLibraryId()
                                              : null;

        // Synchrone
        for (final SimpleDeliveryLotDTO delivery : multiDelivery.getDeliveries()) {

            final Optional<PreDeliveryLockedDocsDTO> lockedIds = lockedDocs.stream().filter(o -> StringUtils.equals(delivery.getLabel(), o.getDeliveryLabel())).findFirst();
            final List<String> lockedForDeliv;
            if (lockedIds.isPresent()) {
                lockedForDeliv = lockedIds.get().getLockedDocsIdentifiers();
            } else {
                lockedForDeliv = new ArrayList<>();
            }

            final DeliveryProcessResults processElement = deliveryProcessService.deliver(delivery.getIdentifier(), lockedForDeliv, prefixToExclude, metaDatas, libraryId);
            allProcessElems.put(delivery.getIdentifier(), processElement);
            LOG.debug("LIVRAISON MULTI : Preparation pour la livraison {} [{}]", delivery.getLabel(), delivery.getIdentifier());
        }

        // Asynchrone
        for (final String identifier : deliveryIds) {
            esDeliveryService.indexAsync(identifier);
            LOG.debug("LIVRAISON MULTI : lancement process asynchrone - delivery {}", identifier);
            deliveryAsyncService.processDelivery(identifier, allProcessElems.get(identifier));
        }

    }

    @Transactional(readOnly = true)
    public List<SimpleLotForDeliveryDTO> findLotsByTrainIdentifier(final String trainId) {

        final List<Lot> lots = multiService.findLotsByTrainIdentifier(trainId);
        return lots.stream().map(LotMapper.INSTANCE::lotToSimpleLotForDeliveryDTO).collect(Collectors.toList());
    }

}
