package fr.progilone.pgcn.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfiguration implements EnvironmentAware {

    private static final String ENV_SPRING_MAIL = "spring.mail.";
    private static final String DEFAULT_HOST = "127.0.0.1"; //NOSONAR default
    private static final String PROP_HOST = "host";
    private static final String DEFAULT_PROP_HOST = "localhost";
    private static final String PROP_PORT = "port";
    private static final String PROP_USER = "user";
    private static final String PROP_PASS = "password";
    private static final String PROP_PROTO = "protocol";
    private static final String PROP_TLS = "tls";
    private static final String PROP_AUTH = "auth";
    private static final String PROP_SMTP_AUTH = "mail.smtp.auth";
    private static final String PROP_STARTTLS = "mail.smtp.starttls.enable";
    private static final String PROP_TRANSPORT_PROTO = "mail.transport.protocol";

    private static final Logger LOG = LoggerFactory.getLogger(MailConfiguration.class);

    private RelaxedPropertyResolver propertyResolver;

    @Override
    public void setEnvironment(final Environment environment) {
        this.propertyResolver = new RelaxedPropertyResolver(environment, ENV_SPRING_MAIL);
    }

    @Bean
    public JavaMailSenderImpl javaMailSender() {
        LOG.debug("Configuring mail server");
        final String host = propertyResolver.getProperty(PROP_HOST, DEFAULT_PROP_HOST);
        final int port = propertyResolver.getProperty(PROP_PORT, Integer.class, 0);
        final String user = propertyResolver.getProperty(PROP_USER);
        final String password = propertyResolver.getProperty(PROP_PASS);
        final String protocol = propertyResolver.getProperty(PROP_PROTO);
        final Boolean tls = propertyResolver.getProperty(PROP_TLS, Boolean.class, false);
        final Boolean auth = propertyResolver.getProperty(PROP_AUTH, Boolean.class, false);

        final JavaMailSenderImpl sender = new JavaMailSenderImpl();
        if (host != null && !host.isEmpty()) {
            sender.setHost(host);
        } else {
            LOG.warn("Warning! Your SMTP server is not configured. We will try to use one on localhost.");
            LOG.debug("Did you configure your SMTP settings in your application.yml?");
            sender.setHost(DEFAULT_HOST);
        }
        sender.setPort(port);
        sender.setUsername(user);
        sender.setPassword(password);

        final Properties sendProperties = new Properties();
        sendProperties.setProperty(PROP_SMTP_AUTH, auth.toString());
        sendProperties.setProperty(PROP_STARTTLS, tls.toString());
        sendProperties.setProperty(PROP_TRANSPORT_PROTO, protocol);
        sender.setJavaMailProperties(sendProperties);
        return sender;
    }
}
