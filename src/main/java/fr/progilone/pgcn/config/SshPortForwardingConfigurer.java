package fr.progilone.pgcn.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * Gestion des tunnels SSH
 * 
 * 
 * Créé le 6 févr. 2018
 */
@WebListener
public class SshPortForwardingConfigurer implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(SshPortForwardingConfigurer.class);

    /**
     * Enable SSH forwarding
     */
    @Value("${sshForwarding.enabled}")
    private boolean port_forwarding_enabled;
    
    /**
     * Host checking by key
     */
    @Value("${sshForwarding.strictHostKeyChecking}")
    private String strictHostKeyChecking;
    @Value("${sshForwarding.knownHosts}")
    private String knownHosts;
    
    /**
     * SSH server config
     */
    @Value("${sshForwarding.server}")
    private String ssh_remote_server;
    @Value("${sshForwarding.port}")
    private int ssh_remote_port;
    @Value("${sshForwarding.user}")
    private String ssh_user;
    @Value("${sshForwarding.password}")
    private String ssh_password;
    
    /**
     * Elasticsearch config
     */
    @Value("${sshForwarding.elasticsearch.remoteServer}")
    private String es_remote_server;
    @Value("${sshForwarding.elasticsearch.localPort}")
    private int es_local_port;
    @Value("${sshForwarding.elasticsearch.remotePort}")
    private int es_remote_port;
    
    /**
     * Database config
     */
    @Value("${sshForwarding.database.remoteServer}")
    private String db_remote_server;
    @Value("${sshForwarding.database.localPort}")
    private int db_local_port;
    @Value("${sshForwarding.database.remotePort}")
    private int db_remote_port;

    private Session sshSession;

    /**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        if (port_forwarding_enabled) {
            try {
                LOG.info("Initialisation de la connection SSH");
                sshSession = new JSch().getSession(ssh_user, ssh_remote_server, ssh_remote_port);
                sshSession.setPassword(ssh_password);
                java.util.Properties config = new java.util.Properties();
                // Never automatically add new host keys to the host file
                config.put("StrictHostKeyChecking", "no");
                sshSession.setConfig(config);
                sshSession.connect();
                // Db tunneling
                sshSession.setPortForwardingL(db_local_port, db_remote_server, db_remote_port);
                // Elasticsearch tunneling
                sshSession.setPortForwardingL(es_local_port, es_remote_server, es_remote_port);
            } catch (JSchException e) {
                LOG.error("Erreur lors de l'initialisation de la connection SSH", e);
            }
        }
    }

    /**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        if (port_forwarding_enabled && sshSession != null && sshSession.isConnected()) {
            LOG.info("Destruction de la connection SSH");
            sshSession.disconnect();
        }
    }
}
