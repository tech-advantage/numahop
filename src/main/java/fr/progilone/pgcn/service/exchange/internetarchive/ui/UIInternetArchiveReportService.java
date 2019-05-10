package fr.progilone.pgcn.service.exchange.internetarchive.ui;

import fr.progilone.pgcn.domain.dto.exchange.InternetArchiveReportDTO;
import fr.progilone.pgcn.domain.dto.statistics.StatisticsProcessedDocUnitDTO;
import fr.progilone.pgcn.domain.exchange.internetarchive.InternetArchiveReport;
import fr.progilone.pgcn.service.exchange.internetarchive.InternetArchiveReportService;
import fr.progilone.pgcn.service.exchange.internetarchive.mapper.InternetArchiveReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
