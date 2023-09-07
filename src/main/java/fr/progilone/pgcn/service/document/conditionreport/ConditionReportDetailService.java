package fr.progilone.pgcn.service.document.conditionreport;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReport;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail;
import fr.progilone.pgcn.domain.document.conditionreport.Description;
import fr.progilone.pgcn.domain.document.conditionreport.PropertyConfiguration;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.document.conditionreport.ConditionReportDetailRepository;
import fr.progilone.pgcn.security.SecurityUtils;
import fr.progilone.pgcn.service.user.UserService;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConditionReportDetailService {

    private final ConditionReportDetailRepository conditionReportDetailRepository;
    private final PropertyConfigurationService propertyConfigurationService;
    private final UserService userService;

    @Autowired
    public ConditionReportDetailService(final ConditionReportDetailRepository conditionReportDetailRepository,
                                        final PropertyConfigurationService propertyConfigurationService,
                                        final UserService userService) {
        this.conditionReportDetailRepository = conditionReportDetailRepository;
        this.propertyConfigurationService = propertyConfigurationService;
        this.userService = userService;
    }

    /**
     * Création d'une nouvelle étape
     */
    @Transactional
    public ConditionReportDetail create(final ConditionReportDetail.Type type, final ConditionReport report) throws PgcnValidationException {
        final ConditionReportDetail detail = getNewDetail(type, report);
        return conditionReportDetailRepository.save(detail);
    }

    /**
     * Création d'une nouvelle étape du constat d'état à partir d'une étape précédente
     */
    @Transactional
    public ConditionReportDetail create(final ConditionReportDetail.Type type, final String fromDetailId) throws PgcnValidationException {
        // Recherche du constat détaillé source
        final ConditionReportDetail fromDetail = conditionReportDetailRepository.findByIdentifier(fromDetailId);
        if (fromDetail == null) {
            return null;
        }

        final ConditionReportDetail detail = getNewDetail(type, fromDetail.getReport());

        detail.setAdditionnalDesc(fromDetail.getAdditionnalDesc());
        detail.setBindingDesc(fromDetail.getBindingDesc());
        detail.setBodyDesc(fromDetail.getBodyDesc());
        detail.setDim1(fromDetail.getDim1());
        detail.setDim2(fromDetail.getDim2());
        detail.setDim3(fromDetail.getDim3());
        detail.setInsurance(fromDetail.getInsurance());

        detail.setNbViewAdditionnal(fromDetail.getNbViewAdditionnal());
        detail.setNbViewBinding(fromDetail.getNbViewBinding());
        detail.setNbViewBody(fromDetail.getNbViewBody());

        // Copie des descriptions
        fromDetail.getDescriptions().stream().map(d -> {
            final Description newD = new Description();
            newD.setComment(d.getComment());
            newD.setProperty(d.getProperty());
            newD.setValue(d.getValue());
            return newD;
        }).forEach(detail::addDescription);

        final ConditionReportDetail savedDetail = conditionReportDetailRepository.save(detail);
        return findByIdentifier(savedDetail.getIdentifier());
    }

    /**
     * Création d'un nouveau détail du constat d'état
     *
     * @param position
     *            position du détail parmi les détails du constat d'état
     */
    public ConditionReportDetail getNewDetail(final ConditionReportDetail.Type type, final ConditionReport report, final int position) {
        final ConditionReportDetail detail = new ConditionReportDetail();
        detail.setType(type);
        detail.setReport(report);
        detail.setDate(LocalDate.now());
        detail.setPosition(position);

        // rédacteur bibliothèque
        final String currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId != null) {
            final User currentUser = userService.findByIdentifier(currentUserId);
            if (currentUser != null) {
                if (User.Category.PROVIDER == currentUser.getCategory()) {
                    detail.setProvWriterName(currentUser.getLogin());
                    detail.setProvWriterFunction(currentUser.getFunction());
                } else {
                    detail.setLibWriterName(currentUser.getLogin());
                    detail.setLibWriterFunction(currentUser.getFunction());
                }
            }
        }
        return detail;
    }

    private ConditionReportDetail getNewDetail(final ConditionReportDetail.Type type, final ConditionReport report) {
        return getNewDetail(type, report, getNextPosition(report));
    }

    /**
     * Création d'un nouveau détail du constat d'état
     *
     * @param position
     *            position du détail parmi les détails du constat d'état
     */
    public ConditionReportDetail getNewDetailWithoutWriters(final ConditionReportDetail.Type type, final ConditionReport report, final int position) {
        final ConditionReportDetail detail = new ConditionReportDetail();
        detail.setType(type);
        detail.setReport(report);
        detail.setDate(LocalDate.now());
        detail.setPosition(position);
        return detail;
    }

    /**
     * Recherche d'une étape particulière du constat d'état
     */
    @Transactional(readOnly = true)
    public ConditionReportDetail findByIdentifier(final String identifier) {
        return conditionReportDetailRepository.findByIdentifier(identifier);
    }

    /**
     * Recherche des étapes d'un constat d'état.
     */
    @Transactional(readOnly = true)
    public List<ConditionReportDetail> findByConditionReport(final String reportId) {
        return conditionReportDetailRepository.findByConditionReportIdentifier(reportId);
    }

    /**
     * Recherche des étapes avec descriptions d'un constat d'état.
     */
    @Transactional(readOnly = true)
    public List<ConditionReportDetail> findWithDescriptionsByCondReportIdentifier(final String reportId) {
        return conditionReportDetailRepository.findWithDescriptionsByCondReportIdentifier(reportId);
    }

    /**
     * Recherche de l'unité documentaire d'un constat d'état
     */
    @Transactional(readOnly = true)
    public DocUnit findDocUnitByIdentifier(final String detailId) {
        return conditionReportDetailRepository.findDocUnitByIdentifier(detailId);
    }

    /**
     * Recherche du parent du détail de constat d'état
     */
    @Transactional(readOnly = true)
    public ConditionReport findParentByIdentifier(final String detailId) {
        return conditionReportDetailRepository.findParentByIdentifier(detailId);
    }

    /**
     * Suppression d'une étape particulière du constat d'état
     */
    @Transactional
    public void delete(final String identifier) throws PgcnValidationException {
        conditionReportDetailRepository.findById(identifier).ifPresent(detail -> {
            final String reportId = detail.getReport().getIdentifier();

            validateDeletion(detail);
            conditionReportDetailRepository.delete(detail);

            // màj des positions des autres détails
            final AtomicInteger counter = new AtomicInteger();
            final List<ConditionReportDetail> details = conditionReportDetailRepository.findByConditionReportIdentifier(reportId)
                                                                                       .stream()
                                                                                       .sorted(Comparator.comparing(ConditionReportDetail::getPosition))
                                                                                       .peek(d -> {
                                                                                           d.setPosition(counter.getAndIncrement());
                                                                                       })
                                                                                       .collect(Collectors.toList());
            if (!details.isEmpty()) {
                conditionReportDetailRepository.saveAll(details);
            }
        });
    }

    /**
     * Sauvegarde d'une étape de constat d'état
     */
    @Transactional
    public ConditionReportDetail save(final ConditionReportDetail detail) throws PgcnValidationException {
        return save(detail, true);
    }

    /**
     * Sauvegarde d'une étape de constat d'état
     */
    @Transactional
    public ConditionReportDetail save(final ConditionReportDetail detail, final boolean validateDetail) throws PgcnValidationException {
        updateBeforeSave(detail);

        if (validateDetail) {
            final DocUnit docUnit = conditionReportDetailRepository.findDocUnitByIdentifier(detail.getIdentifier());
            final List<PropertyConfiguration> confs = propertyConfigurationService.findByLibrary(docUnit.getLibrary());
            final PgcnList<PgcnError> pgcnErrors = validateSave(detail, confs);

            if (!pgcnErrors.isEmpty()) {
                throw new PgcnValidationException(detail, pgcnErrors);
            }
        }
        final ConditionReportDetail savedDetail = conditionReportDetailRepository.save(detail);
        return findByIdentifier(savedDetail.getIdentifier());
    }

    /**
     * Suppression des valeurs vides
     */
    private void updateBeforeSave(final ConditionReportDetail detail) {
        detail.getDescriptions().removeIf(Description::isEmpty);
        detail.getDescriptions().forEach(d -> d.setDetail(detail));
    }

    private void validateDeletion(final ConditionReportDetail detail) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        // la 1e étape n'est pas suppressible
        if (detail.getType() == ConditionReportDetail.Type.LIBRARY_LEAVING) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.CONDREPORT_DETAIL_MANDATORY).build());
        }

        if (!errors.isEmpty()) {
            detail.setErrors(errors);
            throw new PgcnValidationException(detail, errors);
        }
    }

    @Transactional
    public ConditionReportDetail updateProvWriter(final ConditionReportDetail value) {
        final ConditionReportDetail detail = conditionReportDetailRepository.findById(value.getIdentifier()).orElseThrow();
        detail.setProvWriterName(value.getProvWriterName());
        detail.setProvWriterFunction(value.getProvWriterFunction());
        return conditionReportDetailRepository.save(detail);
    }

    @Transactional(readOnly = true)
    public PgcnList<PgcnError> validateSave(final ConditionReportDetail detail, final List<PropertyConfiguration> confs) {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        // Aucune description n'est renseignée
        if (detail.getDescriptions().isEmpty() && StringUtils.isBlank(detail.getAdditionnalDesc())
            && StringUtils.isBlank(detail.getBindingDesc())
            && StringUtils.isBlank(detail.getBodyDesc())
            && detail.getDim1() == null
            && detail.getDim2() == null
            && detail.getDim3() == null
            && detail.getInsurance() == null
            && detail.getNbViewTotal() == 0) {

            errors.add(builder.reinit().setCode(PgcnErrorCode.CONDREPORT_DETAIL_EMPTY).build());
        }

        if (CollectionUtils.isNotEmpty(confs)) {
            // descriptions obligatoires
            confs.stream()
                 .filter(p -> p.getDescProperty() != null && p.isRequired())
                 .filter(p -> detail.getDescriptions()
                                    .stream()
                                    .noneMatch(desc -> StringUtils.equals(desc.getProperty().getIdentifier(), p.getDescProperty().getIdentifier()) && !desc.isEmpty()))
                 .map(PropertyConfiguration::getDescProperty)
                 .forEach(p -> errors.add(builder.reinit().setCode(PgcnErrorCode.CONDREPORT_DETAIL_DESC_EMPTY).setField(p.getLabel()).build()));

            // descriptions internes obligatoires
            confs.stream().filter(p -> {
                final PropertyConfiguration.InternalProperty internalProperty = p.getInternalProperty();
                if (internalProperty != null && p.isRequired()) {
                    switch (internalProperty) {
                        case BINDING_DESC:
                            return StringUtils.isBlank(detail.getBindingDesc());
                        case BODY_DESC:
                            return StringUtils.isBlank(detail.getBodyDesc());
                        case DIMENSION:
                            return (detail.getDim1() == null || detail.getDim1() == 0) && (detail.getDim2() == null || detail.getDim2() == 0)
                                   && (detail.getDim3() == null || detail.getDim3() == 0);
                    }
                }
                return false;
            })
                 .map(PropertyConfiguration::getInternalProperty)
                 .forEach(p -> errors.add(builder.reinit().setCode(PgcnErrorCode.CONDREPORT_DETAIL_DESC_EMPTY).setField(p.name()).build()));
        }

        if (!errors.isEmpty()) {
            detail.setErrors(errors);
        }
        return errors;
    }

    @Transactional
    public List<ConditionReportDetail> findByIdentifierIn(final List<String> ids) {
        return conditionReportDetailRepository.findByIdentifierIn(ids);
    }

    /**
     * @param report
     * @return position du prochain détail du constat d'état passé en paramètre
     */
    private int getNextPosition(final ConditionReport report) {
        final Integer pos = conditionReportDetailRepository.getMaxPositionByConditionReportIdentifier(report.getIdentifier());
        return pos != null ? pos + 1
                           : 0;
    }

    public Optional<ConditionReportDetail> getLatest(final ConditionReport report) {
        return report.getDetails().stream().max(Comparator.comparing(ConditionReportDetail::getPosition));
    }

    public Optional<ConditionReportDetail> getLastDetailByDocUnitId(final String docUnitId) {
        final List<ConditionReportDetail> details = conditionReportDetailRepository.findByDocUnitId(docUnitId);
        return details.stream().max(Comparator.comparing(ConditionReportDetail::getPosition));
    }
}
