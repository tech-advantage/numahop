package fr.progilone.pgcn.service.document;

import fr.progilone.pgcn.domain.checkconfiguration.CheckConfiguration;
import fr.progilone.pgcn.domain.delivery.DeliveredDocument;
import fr.progilone.pgcn.domain.document.Check;
import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.document.DigitalDocument.DigitalDocumentStatus;
import fr.progilone.pgcn.domain.document.DocPage;
import fr.progilone.pgcn.domain.document.DocPage.PageStatus;
import fr.progilone.pgcn.domain.document.GlobalCheck;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReport;
import fr.progilone.pgcn.domain.dto.document.DocPageErrorsDTO;
import fr.progilone.pgcn.domain.dto.sample.SampleDTO;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.repository.delivery.DeliveryRepository;
import fr.progilone.pgcn.repository.document.CheckRepository;
import fr.progilone.pgcn.repository.document.DigitalDocumentRepository;
import fr.progilone.pgcn.repository.document.DocPageRepository;
import fr.progilone.pgcn.repository.document.GlobalCheckRepository;
import fr.progilone.pgcn.service.document.conditionreport.ConditionReportService;
import fr.progilone.pgcn.service.lot.LotService;
import fr.progilone.pgcn.service.sample.SampleService;
import fr.progilone.pgcn.service.util.transaction.TransactionService;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by lebouchp on 10/02/2017.
 */
@Service
public class CheckService {

    private static final Logger LOG = LoggerFactory.getLogger(CheckService.class);

    private final CheckRepository checkRepository;
    private final GlobalCheckRepository globalCheckRepository;
    private final DigitalDocumentRepository digitalDocumentRepository;
    private final DocPageRepository docPageRepository;
    private final LotService lotService;
    private final SampleService sampleService;
    private final DigitalDocumentService digitalDocumentService;
    private final DeliveryRepository deliveryRepository;
    private final TransactionService transactionService;

    private final ConditionReportService condReportService;

    @Autowired
    public CheckService(final CheckRepository checkRepository,
                        final GlobalCheckRepository globalCheckRepository,
                        final DigitalDocumentRepository digitalDocumentRepository,
                        final DocPageRepository docPageRepository,
                        final LotService lotService,
                        final SampleService sampleService,
                        final DigitalDocumentService digitalDocumentService,
                        final DeliveryRepository deliveryRepository,
                        final TransactionService transactionService,
                        final ConditionReportService condReportService) {
        this.checkRepository = checkRepository;
        this.globalCheckRepository = globalCheckRepository;
        this.digitalDocumentRepository = digitalDocumentRepository;
        this.docPageRepository = docPageRepository;
        this.lotService = lotService;
        this.sampleService = sampleService;
        this.digitalDocumentService = digitalDocumentService;
        this.deliveryRepository = deliveryRepository;
        this.transactionService = transactionService;
        this.condReportService = condReportService;
    }

    /**
     * Récupération des valeurs d'erreur
     *
     * @return
     */
    public Set<Check.ErrorLabel> getErrors() {
        return new HashSet<>(Arrays.asList(Check.ErrorLabel.values()));
    }

    @Transactional
    public Check save(final Check check) {
        return checkRepository.save(check);
    }

    public synchronized DocErrorReport setChecks(final String identifier,
                                                 final DocPageErrorsDTO errors,
                                                 final Integer pageNumber,
                                                 final boolean sampling,
                                                 final String deliveryId) {

        return transactionService.executeInNewTransactionWithReturn(() -> {
            final DocPage docPage = sampling ? digitalDocumentService.getPage(identifier, pageNumber)
                                             : digitalDocumentService.getPageByOrder(identifier, pageNumber);

            // On supprime les erreurs
            checkRepository.deleteAll(docPage.getChecks());
            docPage.getChecks().clear();

            for (final Check.ErrorLabel error : errors.getFailedChecks()) {
                final Check check = new Check();
                check.setErrorType(error.getType());
                check.setErrorLabel(error);
                check.setPage(docPage);
                docPage.getChecks().add(check);
                docPage.setStatus(PageStatus.REJECTED);
            }
            docPage.setCheckNotes(errors.getCheckNotes());
            // Champs de la table des matieres
            docPage.getMaster().ifPresent(master -> {
                master.setTypeToc(errors.getTypeToc());
                master.setOrderToc(errors.getOrderToc());
                master.setTitleToc(errors.getTitleToc());
            });
            if (errors.getFailedChecks().isEmpty()) {
                docPage.setStatus(PageStatus.VALIDATED);
            }

            docPageRepository.save(docPage);

            final DeliveredDocument doc = deliveryRepository.getOneWithDigitalDoc(deliveryId, identifier);
            if (DigitalDocumentStatus.CHECKING != doc.getStatus()) {
                doc.setStatus(DigitalDocumentStatus.CHECKING);
                doc.getDigitalDocument().setStatus(DigitalDocumentStatus.CHECKING);
            }
            return generateErrorReport(identifier);

        });
    }

    private DocErrorReport generateErrorReport(final String identifier) {

        final DigitalDocument digitalDocument = digitalDocumentRepository.getOneWithCheckConfiguration(identifier);
        final int countMinorErrors = docPageRepository.countDocPageWithMinorErrors(identifier);
        final int countMajorErrors = docPageRepository.countDocPageWithMajorErrors(identifier);
        final double[] errorRates = computeErrorRates(identifier, countMinorErrors, countMajorErrors);

        digitalDocument.setMinorErrorRate(errorRates[0]);
        digitalDocument.setMajorErrorRate(errorRates[1]);

        final Lot lot = digitalDocument.getDocUnit().getLot();
        final CheckConfiguration checkConfiguration = lotService.getActiveCheckConfiguration(lot);
        final DocErrorReport der = new DocErrorReport();
        der.setNbMinorErrors(countMinorErrors);
        der.setNbMajorErrors(countMajorErrors);
        der.minorErrorRateExceeded = errorRates[0] > checkConfiguration.getMinorErrorRate();
        der.majorErrorRateExceeded = errorRates[1] > checkConfiguration.getMajorErrorRate();
        return der;
    }

    private DocErrorReport generateErrorReportForSample(final String identifier) {

        final SampleDTO sample = sampleService.getOne(identifier);
        final DigitalDocument digitalDocument = digitalDocumentRepository.getOneWithCheckConfiguration(sample.getDocuments().iterator().next().getIdentifier());

        final int countMinorErrors = docPageRepository.countDocPageWithMinorErrorsForSample(identifier);
        final int countMajorErrors = docPageRepository.countDocPageWithMajorErrorsForSample(identifier);
        final double[] errorRates = {(double) countMinorErrors / sample.getPages().size(),
                                     (double) countMajorErrors / sample.getPages().size()};

        final Lot lot = digitalDocument.getDocUnit().getLot();
        final CheckConfiguration checkConfiguration = lotService.getActiveCheckConfiguration(lot);
        final DocErrorReport der = new DocErrorReport();
        der.setNbMinorErrors(countMinorErrors);
        der.setNbMajorErrors(countMajorErrors);
        der.minorErrorRateExceeded = errorRates[0] > checkConfiguration.getMinorErrorRate();
        der.majorErrorRateExceeded = errorRates[1] > checkConfiguration.getMajorErrorRate();
        return der;
    }

    @Transactional
    public void setGlobalChecks(final String identifier, final DocPageErrorsDTO errors) {

        if (docErrorsHasNotChanged(identifier, errors)) {
            // on touche à rien
            return;
        }

        // on nettoie ...
        cleanChecks(identifier);
        // on recharge les checks
        final DigitalDocument cleanedDoc = digitalDocumentRepository.getOneWithChecks(identifier);
        // puis on recree les nveaux checks/globalCheck
        for (final Check.ErrorLabel error : errors.getFailedChecks()) {
            final GlobalCheck check = new GlobalCheck();
            check.setErrorType(error.getType());
            check.setErrorLabel(error);
            check.setDigitalDocument(cleanedDoc);
            cleanedDoc.getChecks().add(check);

            cleanedDoc.getPages().stream().forEach(p -> {
                final Check chk = new Check();
                chk.setErrorType(error.getType());
                chk.setErrorLabel(error);
                chk.setPage(p);
                p.getChecks().add(chk);
                docPageRepository.save(p);
            });
        }
        cleanedDoc.setCheckNotes(errors.getCheckNotes());
        digitalDocumentRepository.save(cleanedDoc);
    }

    /**
     * Suppression des checks/globalCheck precedents.
     *
     * @param identifier
     * @return
     */
    private DigitalDocument cleanChecks(final String identifier) {

        final DigitalDocument digitalDocument = digitalDocumentRepository.getOneWithChecks(identifier);
        final List<Check.ErrorLabel> errLabels = digitalDocument.getChecks().stream().map(GlobalCheck::getErrorLabel).collect(Collectors.toList());

        // on fait en 2 etapes sinon ça merdoie...
        digitalDocument.getPages().forEach(p -> {
            final List<Check> pgChecks = new ArrayList<>(p.getChecks());
            pgChecks.stream().filter(chk -> errLabels.contains(chk.getErrorLabel())).forEach(chk -> {
                p.getChecks().remove(chk);
                checkRepository.delete(chk);
            });
        });

        for (final GlobalCheck globCheck : digitalDocument.getChecks()) {
            globalCheckRepository.delete(globCheck);
        }
        digitalDocument.getChecks().clear();
        return digitalDocumentRepository.save(digitalDocument);
    }

    /**
     * @param identifier
     * @param errors
     * @return
     */
    private boolean docErrorsHasNotChanged(final String identifier, final DocPageErrorsDTO errors) {

        final DigitalDocument doc = digitalDocumentRepository.getOneWithChecks(identifier);
        boolean notChanged = true;
        if (!StringUtils.equals(errors.getCheckNotes(), doc.getCheckNotes())) {
            notChanged = false;
        } else if (errors.getFailedChecks().size() == doc.getChecks().size()) {
            for (final GlobalCheck gc : doc.getChecks()) {
                if (!errors.getFailedChecks().contains(gc.getErrorLabel())) {
                    notChanged = false;
                }
            }
        } else {
            notChanged = false;
        }
        return notChanged;
    }

    @Transactional
    public void setGlobalChecksForSampling(final String identifier, final DocPageErrorsDTO errors) {

        final SampleDTO sample = sampleService.getOne(identifier);
        sample.getDocuments().forEach(dd -> {
            setGlobalChecks(dd.getIdentifier(), errors);
        });
    }

    @Transactional(readOnly = true)
    public Set<Check.ErrorLabel> getGlobalChecks(final String id) {
        final DigitalDocument digitalDocument = digitalDocumentRepository.getOneWithChecks(id);
        final Set<GlobalCheck> checks = digitalDocument.getChecks();
        return checks.stream().map(GlobalCheck::getErrorLabel).collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public Set<Check.ErrorLabel> getGlobalChecksForSampling(final String id) {
        final Set<Check.ErrorLabel> checks = new HashSet<>();
        final SampleDTO sample = sampleService.getOne(id);
        sample.getDocuments().forEach(dd -> {
            checks.addAll(getGlobalChecks(dd.getIdentifier()));
        });
        return checks;
    }

    @Transactional(readOnly = true)
    public DocErrorReport getDocumentErrors(final String id) {
        return generateErrorReport(id);
    }

    @Transactional(readOnly = true)
    public DocErrorReport getSampleErrors(final String identifier) {
        return generateErrorReportForSample(identifier);
    }

    /**
     * Structure pour communiquer les taux d'erreur au client.
     */
    public static final class DocErrorReport {

        private int nbMinorErrors;
        private int nbMajorErrors;
        private boolean minorErrorRateExceeded;
        private boolean majorErrorRateExceeded;

        public int getNbMinorErrors() {
            return nbMinorErrors;
        }

        public void setNbMinorErrors(final int nbMinorErrors) {
            this.nbMinorErrors = nbMinorErrors;
        }

        public int getNbMajorErrors() {
            return nbMajorErrors;
        }

        public void setNbMajorErrors(final int nbMajorErrors) {
            this.nbMajorErrors = nbMajorErrors;
        }

        public boolean isMinorErrorRateExceeded() {
            return minorErrorRateExceeded;
        }

        public void setMinorErrorRateExceeded(final boolean minorErrorRateExceeded) {
            this.minorErrorRateExceeded = minorErrorRateExceeded;
        }

        public boolean isMajorErrorRateExceeded() {
            return majorErrorRateExceeded;
        }

        public void setMajorErrorRateExceeded(final boolean majorErrorRateExceeded) {
            this.majorErrorRateExceeded = majorErrorRateExceeded;
        }
    }

    @Transactional
    public double[] computeErrorRates(final String identifier, final int countMinorErrors, final int countMajorErrors) {
        final int countPages = docPageRepository.countDocPageByDigitalDocumentIdentifier(identifier);
        final double[] errRates = {(double) countMinorErrors / countPages,
                                   (double) countMajorErrors / countPages};
        return errRates;
    }

    @Transactional(readOnly = true)
    public Set<Check.ErrorLabel> getChecks(final String id, final Integer pageNumber) {
        final DocPage docPage = digitalDocumentService.getPageByOrder(id, pageNumber);
        final Set<Check> checks = docPage.getChecks();
        return checks.stream().map(Check::getErrorLabel).collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public Set<Check.ErrorLabel> getChecksForSampling(final String idPage) {
        final DocPage docPage = docPageRepository.getOne(idPage);
        final Set<Check> checks = docPage.getChecks();
        return checks.stream().map(Check::getErrorLabel).collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public Set<String> getCondReportSummaryForSamplePage(final String idPage) {
        final DocPage docPage;
        Set<String> summary = new HashSet<>();
        try {
            docPage = docPageRepository.findById(idPage).orElseThrow(Exception::new);
            ConditionReport report = condReportService.findByDocUnit(docPage.getDigitalDocument().getDocUnit().getIdentifier());
            summary = condReportService.getSummary(report);
        } catch (Exception e) {
            LOG.error("Erreur lors de la recuperation du document", e);
        }

        return summary;
    }

    @Transactional(readOnly = true)
    public Check findOne(final String identifier) {
        return checkRepository.findById(identifier).orElse(null);
    }
}
