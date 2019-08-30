package fr.progilone.pgcn.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Service for sending e-mails.
 */
@Service
public class MailService {

    private static final Logger LOG = LoggerFactory.getLogger(MailService.class);

    @Inject
    private JavaMailSenderImpl javaMailSender;

    @Value("${spring.mail.activated}")
    private boolean activated;

    @Value("${spring.mail.from}")
    private String defaultFrom;

    public boolean sendEmail(final String from,
                             final String to[],
                             final String subject,
                             final String content,
                             final boolean isMultipart,
                             final boolean isHtml) {
        return sendEmail(from, to, null, subject, content, null, null, null, isMultipart, isHtml);
    }

    public boolean sendEmailWithAttachment(final String from,
                                           final String[] to,
                                           final String subject,
                                           final String content,
                                           final File attachment,
                                           final String contentType,
                                           final boolean isMultipart,
                                           final boolean isHtml) {

        try (final InputStream in = new FileInputStream(attachment)) {
            return sendEmail(from, to, null, subject, content, attachment.getName(), in, contentType, isMultipart, isHtml);

        } catch (final IOException e) {
            LOG.warn("Unable to create inputstream for attachment", e);
            return false;
        }
    }

    public boolean sendEmailWithStreamAttachment(final String from,
                                                 final String to[],
                                                 final String cc,
                                                 final String subject,
                                                 final String content,
                                                 final String attachmentName,
                                                 final InputStream attachment,
                                                 final String contentType,
                                                 final boolean isMultipart,
                                                 final boolean isHtml) {
        return sendEmail(from, to, cc, subject, content, attachmentName, attachment, contentType, isMultipart, isHtml);
    }

    private boolean sendEmail(final String from,
                              final String to[],
                              final String cc,
                              final String subject,
                              final String content,
                              final String attachmentName,
                              final InputStream attachment,
                              final String contentType,
                              final boolean isMultipart,
                              final boolean isHtml) {

        LOG.debug("Send e-mail[multipart '{}' and html '{}'] from '{}' to '{}' with subject '{}'",
                  isMultipart,
                  isHtml,
                  from,
                  to.length > 0 ? to[0] : "",
                  subject);
        if (activated) {
            // Prepare message using a Spring helper
            final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            try {
                final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
                message.setTo(to);
                if (StringUtils.isNotEmpty(cc)) {
                    message.setCc(cc);
                }
                message.setFrom(StringUtils.isNotBlank(from) ? from : defaultFrom);
                message.setSubject(subject);
                message.setText(content, isHtml);
                // Handle attachment if present
                if (attachment != null) {
                    LOG.debug("Sent e-mail with attachment '{}'", attachmentName);
                    message.addAttachment(attachmentName, new ByteArrayDataSource(attachment, contentType));
                }
                javaMailSender.send(mimeMessage);
                LOG.debug("Sent e-mail to User '{}'", to.length > 0 ? to[0] : "");
                return true;
            } catch (final Exception e) {
                LOG.error("E-mail could not be sent, exception is: {}", e.getMessage(), e);
                return false;
            }
        } else {
            LOG.warn("Email sending not activated !");
            LOG.trace("Email content {}", content);
            return true;
        }
    }
}
