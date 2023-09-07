package fr.progilone.pgcn.service.document.conditionreport;

import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReport;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportAttachment;
import fr.progilone.pgcn.repository.document.conditionreport.ConditionReportAttachmentRepository;
import fr.progilone.pgcn.repository.document.conditionreport.ConditionReportRepository;
import fr.progilone.pgcn.service.storage.FileStorageManager;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ConditionReportAttachmentService {

    private static final Logger LOG = LoggerFactory.getLogger(ConditionReportAttachmentService.class);

    private final ConditionReportAttachmentRepository conditionReportAttachmentRepository;
    private final ConditionReportRepository conditionReportRepository;
    private final FileStorageManager fm;

    // Stockage des fichiers importés
    @Value("${uploadPath.condition_report}")
    private String reportDir;

    @Autowired
    public ConditionReportAttachmentService(final ConditionReportAttachmentRepository conditionReportAttachmentRepository,
                                            final ConditionReportRepository conditionReportRepository,
                                            final FileStorageManager fm) {
        this.conditionReportAttachmentRepository = conditionReportAttachmentRepository;
        this.conditionReportRepository = conditionReportRepository;
        this.fm = fm;
    }

    @PostConstruct
    public void init() {
        fm.initializeStorage(reportDir);
    }

    @Transactional
    public void delete(final String attachmentId) {
        final ConditionReportAttachment attachment = conditionReportAttachmentRepository.findByIdentifier(attachmentId);

        final File attachmentFile = downloadAttachmentFile(attachment);
        if (attachmentFile != null) {
            FileUtils.deleteQuietly(attachmentFile);
        }
        final File thumbnailFile = downloadAttachmentThumbnail(attachment);
        if (thumbnailFile != null) {
            FileUtils.deleteQuietly(thumbnailFile);
        }
        conditionReportAttachmentRepository.deleteById(attachmentId);
    }

    @Transactional(readOnly = true)
    public ConditionReportAttachment findByIdentifier(final String attachmentId) {
        return conditionReportAttachmentRepository.findByIdentifier(attachmentId);
    }

    @Transactional(readOnly = true)
    public List<ConditionReportAttachment> findByReport(final String reportId) {
        return conditionReportAttachmentRepository.findByReportIdentifier(reportId);
    }

    /**
     * Recherche de l'unité documentaire d'un constat d'état
     *
     * @param reportId
     * @return
     */
    @Transactional(readOnly = true)
    public DocUnit findDocUnitByIdentifier(final String reportId) {
        return conditionReportAttachmentRepository.findDocUnitByIdentifier(reportId);
    }

    @Transactional
    public List<ConditionReportAttachment> uploadAttachment(final List<MultipartFile> files, final String reportId) {
        final List<ConditionReportAttachment> attachments = new ArrayList<>();
        final ConditionReport report = conditionReportRepository.findById(reportId).orElse(null);

        for (final MultipartFile file : files) {
            if (file.getSize() == 0) {
                LOG.warn("La pièce jointe {} est vide", file.getOriginalFilename());
                continue;
            }

            // Sauvegarde de ConditionReportAttachment
            final ConditionReportAttachment attachment = new ConditionReportAttachment();
            attachment.setReport(report);
            attachment.setFileSize(file.getSize());
            attachment.setOriginalFilename(new File(file.getOriginalFilename()).getName());
            LOG.debug("Téléversement de la pièce jointe {}", attachment.getOriginalFilename());

            final ConditionReportAttachment savedAttachment = conditionReportAttachmentRepository.save(attachment);
            attachments.add(savedAttachment);

            // Copie du fichier uploadé
            File attachmentFile;
            try (final InputStream in = file.getInputStream()) {
                attachmentFile = uploadAttachmentFile(in, attachment);

            } catch (final IOException e) {
                LOG.error(e.getMessage(), e);
                attachmentFile = null;
            }
            if (attachmentFile == null) {
                LOG.error("Une erreur s'est produite lors de la sauvegarde de la pièce jointe {} (ConditionReport {})", file.getOriginalFilename(), reportId);
                continue;
            }
            LOG.debug("Fichier {} créé", attachmentFile.getAbsolutePath());

            // Création de l'aperçu
            final File thumbnail = fm.createThumbnail(attachmentFile,
                                                      file.getContentType(),
                                                      ViewsFormatConfiguration.FileFormat.THUMB,
                                                      reportDir,
                                                      attachment.getReport(),
                                                      ViewsFormatConfiguration.FileFormat.THUMB.label() + "."
                                                                              + attachment.getOriginalFilename());
            if (thumbnail != null) {
                LOG.debug("Fichier aperçu {} créé", thumbnail.getAbsolutePath());
            }
        }
        return attachments;
    }

    /**
     * Récupère le fichier importé
     *
     * @param attachment
     * @return
     */
    @Transactional(readOnly = true)
    public File downloadAttachmentFile(final ConditionReportAttachment attachment) {
        return fm.retrieveFile(reportDir, attachment.getReport(), attachment.getOriginalFilename());
    }

    /**
     * Récupère le fichier importé
     *
     * @param attachment
     * @return
     */
    @Transactional(readOnly = true)
    public File downloadAttachmentThumbnail(final ConditionReportAttachment attachment) {
        return fm.retrieveFile(reportDir, attachment.getReport(), ViewsFormatConfiguration.FileFormat.THUMB.label().concat(".").concat(attachment.getOriginalFilename()));
    }

    /**
     * Répertoire de stockage des pièces jointes du constat d'état
     *
     * @param report
     * @return
     */
    @Transactional(readOnly = true)
    public File getAttachmentDir(final ConditionReport report) {
        return FileUtils.getFile(reportDir, report.getIdentifier());
    }

    /**
     * Sauvegarde le fichier importé
     *
     * @param source
     * @param attachment
     * @return
     */
    private File uploadAttachmentFile(final InputStream source, final ConditionReportAttachment attachment) {
        return fm.copyInputStreamToFile(source, reportDir, attachment.getReport(), attachment.getOriginalFilename());
    }
}
