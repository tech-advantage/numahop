package fr.progilone.pgcn.service.exchange.ssh;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.HostKey;
import com.jcraft.jsch.HostKeyRepository;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import fr.progilone.pgcn.domain.administration.SftpConfiguration;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.util.CryptoService;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Created by Sébastien on 28/12/2016.
 */
@Disabled
@ExtendWith(MockitoExtension.class)
public class SftpServiceTest {

    private SftpService service;

    @Mock
    private CryptoService cryptoService;

    @BeforeEach
    public void setUp() throws PgcnTechnicalException {
        service = new SftpService(cryptoService);
        ReflectionTestUtils.setField(service, "knownHosts", "C:\\Users\\Sébastien\\Desktop\\known_hosts");
        ReflectionTestUtils.setField(service, "strictHostKeyChecking", "yes");

        service.init();

        when(cryptoService.decrypt(any())).thenAnswer(new ReturnsArgumentAt(0));
    }

    @Test
    public void test() throws JSchException, SftpException, IOException {
        final JSch jSch = new JSch();

        final String knownHosts = "C:\\Users\\Sébastien\\Desktop\\known_hosts";
        jSch.setKnownHosts(knownHosts);

        final HostKeyRepository hkr = jSch.getHostKeyRepository();
        final HostKey[] hks = hkr.getHostKey();
        if (hks != null) {
            System.out.println("Host keys in " + hkr.getKnownHostsRepositoryID());
            for (final HostKey hk : hks) {
                System.out.println(hk.getHost() + " "
                                   + hk.getType()
                                   + " "
                                   + hk.getFingerPrint(jSch));
            }
            System.out.println("");
        }

        final Session session = jSch.getSession("progilone", "pgcn-dev.progilone.lan", 22);
        session.setPassword("progilone");

        // Set StrictHostKeyChecking = yes
        final Properties config = new Properties();
        config.put("StrictHostKeyChecking", "yes");
        session.setConfig(config);
        session.connect();

        final Channel channel = session.openChannel("sftp");
        channel.connect();
        final ChannelSftp sftpChannel = (ChannelSftp) channel;

        try (final InputStream in = sftpChannel.get("/opt/pgcn/logs/numahop.2016-12-28.log")) {

            final LineNumberReader reader = new LineNumberReader(new InputStreamReader(in));
            String line;
            int i = 1;
            while ((line = reader.readLine()) != null) {
                System.out.println("[" + (i < 10 ? "0"
                                                 : "")
                                   + (i++)
                                   + "]\t"
                                   + line);
            }
            // try {
            // sftpChannel.put("C:\\Temp\\pgcn\\cines\\5c0d2c29-2e1a-4b43-b15d-bda10c80378a\\SIP.xml", "SIP.xml");

        } finally {
            sftpChannel.exit();
            session.disconnect();
        }
    }

    @Test
    public void test2() throws PgcnTechnicalException {
        final SftpConfiguration conf = new SftpConfiguration();
        conf.setTargetDir("cines_test");
        conf.setUsername("progilone");
        conf.setPassword("progilone");
        conf.setHost("pgcn-dev.progilone.lan");
        conf.setPort(22);

        final Path source = Paths.get("D:\\Projets\\PGCN\\Exemples");

        service.sftpPut(conf, source);
    }
}
