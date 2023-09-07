package fr.progilone.pgcn.service.exchange.oaipmh;

import fr.progilone.pgcn.domain.jaxb.oaidc.OaiDcType;
import fr.progilone.pgcn.domain.jaxb.oaipmh.OAIPMHtype;
import fr.progilone.pgcn.domain.jaxb.oaipmh.ResumptionTokenType;
import java.util.Iterator;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

/**
 * Itérateur sur les OAI_DC récupérés sur un service OAI-PMH
 */
final class OaiPmhRecordIterator implements Iterator<OaiDcType> {

    private final OaiPmhService oaiPmhService;
    private final OaiPmhRequest request;

    private Iterator<OaiDcType> innerIt;
    private ResumptionTokenType token;

    public OaiPmhRecordIterator(final OaiPmhService oaiPmhService, final OaiPmhRequest request) {
        this.oaiPmhService = oaiPmhService;
        this.request = request;
    }

    @Override
    public synchronized boolean hasNext() {
        return token == null || StringUtils.isNotBlank(token.getValue())
               || innerIt.hasNext();
    }

    @Override
    public synchronized OaiDcType next() {
        // Chargement de la prochaine page de résultats
        if (innerIt == null || !innerIt.hasNext()) {
            final Optional<OAIPMHtype> oaiPmhOpt = oaiPmhService.listRecords(request.getBaseUrl(),
                                                                             request.getMetadataPrefix(),
                                                                             request.getFrom(),
                                                                             request.getTo(),
                                                                             request.getSet(),
                                                                             token != null ? token.getValue()
                                                                                           : null);

            if (!oaiPmhOpt.isPresent()) {
                return null;
            }
            readResponse(oaiPmhOpt.get());
        }
        // Lecture de l'itérateur
        return innerIt.hasNext() ? innerIt.next()
                                 : null;
    }

    private void readResponse(final OAIPMHtype oaiPmh) {
        innerIt = oaiPmh.getListRecords().getRecord().stream().filter(rec -> rec.getMetadata() != null).map(rec -> (OaiDcType) rec.getMetadata().getAny()).iterator();
        token = oaiPmh.getListRecords().getResumptionToken();
    }
}
