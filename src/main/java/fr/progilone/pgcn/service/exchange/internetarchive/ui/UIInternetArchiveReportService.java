package fr.progilone.pgcn.service.exchange.internetarchive.ui;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.exchange.InternetArchiveReportDTO;
import fr.progilone.pgcn.domain.dto.statistics.StatisticsProcessedDocUnitDTO;
import fr.progilone.pgcn.domain.exchange.internetarchive.InternetArchiveReport;
import fr.progilone.pgcn.service.document.ui.UIDocUnitService;
import fr.progilone.pgcn.service.exchange.internetarchive.InternetArchiveReportService;
import fr.progilone.pgcn.service.exchange.internetarchive.mapper.InternetArchiveReportMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service dédié à les gestion des vues des unités documentaires
 *
 * @author jbrunet
 */
@Service
public class UIInternetArchiveReportService {

    private static final Logger LOG = LoggerFactory.getLogger(UIInternetArchiveReportService.class);

    @Autowired
    private InternetArchiveReportService iaReportService;

    /**
     * Récupère la liste des rapports CINES liés à une unité doc
     *
     * @param docUnitId
     * @return
     */
    @Transactional(readOnly = true)
    public List<InternetArchiveReportDTO> findAllByDocUnitIdentifier(String docUnitId) {
        final List<InternetArchiveReport> reports = iaReportService.findByDocUnit(docUnitId);
        return reports.stream()
                      .map(InternetArchiveReportMapper.INSTANCE::internetArchiveReportToInternetArchiveReportDTO)
                      .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StatisticsProcessedDocUnitDTO> findAll(final List<String> libraries, final LocalDate fromDate, final boolean failures) {
        return iaReportService.findAll(libraries, fromDate, failures).stream().map(iaReport -> {
            final StatisticsProcessedDocUnitDTO dto = new StatisticsProcessedDocUnitDTO();
            dto.setStatus(iaReport.getStatus().name());
            dto.setDate(iaReport.getDateSent());
            dto.setMessage(iaReport.getMessage());

            dto.setIdentifier(iaReport.getDocUnit().getIdentifier());
            dto.setPgcnId(iaReport.getDocUnit().getPgcnId());

            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Interroge Internet Archive pour recuperer l'url ARK.
     *
     * @param aiIdentifier
     * @return
     */
    public String getIaArkUrl(final String aiIdentifier) {
        final String idArkUrl = "https://archive.org/metadata/" + aiIdentifier + "/metadata/identifier-ark";
        final HttpClient client = HttpClientBuilder.create().build();
        final HttpGet request = new HttpGet(idArkUrl);

        String arkUrl = null;
        try {
            final HttpResponse response = client.execute(request);
            final String json = EntityUtils.toString(response.getEntity());
            final JSONObject jsonObject = new JSONObject(json);

            if (jsonObject.has("error")) {
                LOG.error("L'appel de l'url Internet Archive {} a renvoyé une erreur, détail: {}", idArkUrl, jsonObject.get("error"));

            } else if (jsonObject.has("result")) {
                arkUrl = (String) jsonObject.get("result");
            }

        } catch (final IOException e) {
            LOG.error("Erreur de communication avec Internet Archive lors de l'appel de l'url {}, détail: {}", idArkUrl, e.getMessage());

        } catch (final JSONException e) {
            LOG.error("Erreur de lecture de la réponse d'Internet Archive lors de l'appel de l'url {}, détail: {} ", idArkUrl, e.getMessage());
        }

        return arkUrl;
    }
}
