package fr.progilone.pgcn.service.check;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.mail.MessagingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fr.progilone.pgcn.domain.jaxb.facile.ValidatorType;
import fr.progilone.pgcn.service.util.FileUtils;
import fr.progilone.pgcn.service.util.FileUtils.CheckSumType;

/**
 * Service de Validation FACILE
 *
 * @author jbrunet
 * Créé le 10 février 2017
 */
@Service
public class FacileCinesService {

    private static final Logger LOG = LoggerFactory.getLogger(FacileCinesService.class);
    private static final String PART_TYPE_FILE = "file";

    @Value("${services.cines.facile}")
    private String facileApiUrl;

    public FacileCinesService() {
    }

    public List<ValidatorType> checkFilesAgainstFacile(final File... files) {
        final List<ValidatorType> results = new ArrayList<>();
        Stream.of(files).sequential().map(this::checkFileAgainstFacile).forEach(results::add);
        return results;
    }

    
    public ValidatorType checkFileAgainstFacile(final File file) {
        LOG.debug("Check Facile du fichier {}", file.getAbsolutePath());
        String errorMsg = null;

        if (!file.exists()) {
            errorMsg = "Le fichier " + file.getAbsolutePath() + " n'existe pas";
            LOG.error(errorMsg);
        } else {
            final HttpClient httpClient = initializeHttpClient();
            final HttpPost httpPost = initializeHttpPostRequest();
            addFileToHttpPostRequest(file, httpPost);

            try {
                final HttpResponse response = httpClient.execute(httpPost);
                final HttpEntity resEntity = response.getEntity();
                final String result = EntityUtils.toString(resEntity);

                final ValidatorType resultFacile = parseXmlResponse(result).getValue();
                return validateResultFromFacile(resultFacile, file);

            } catch (IOException | ParseException | JAXBException | MessagingException e) {
                errorMsg = "Erreur lors de la vérification de conformité FACILE : " + file.getName();
                LOG.error(errorMsg, e);
            }
        }
        return buildDefaultErrorValidatorType(errorMsg, file);
    }

    private ValidatorType validateResultFromFacile(final ValidatorType resultFacile, final File file) {
        String checkSum;
        try {
            checkSum = FileUtils.checkSum(file, CheckSumType.MD5);
        } catch (NoSuchAlgorithmException | IOException e) {
            LOG.warn("Vérification du checksum impossible : les résultats peuvent ne pas être corrects", e);
            return resultFacile;
        }

        if (resultFacile != null && resultFacile.getMd5Sum().equals(checkSum)) {
            return resultFacile;
        }
        return buildDefaultErrorValidatorType("Le fichier est corrompu, la checksum ne correspond pas", file);
    }

    private ValidatorType buildDefaultErrorValidatorType(final String reason, final File file) {
        final ValidatorType errorValidator = new ValidatorType();
        errorValidator.setFileName(file.getName());
        errorValidator.setMessage(reason);
        errorValidator.setArchivable(false);
        errorValidator.setWellformed(false);
        errorValidator.setValid(false);
        return errorValidator;
    }

    private void addFileToHttpPostRequest(final File file, final HttpPost httpPost) {
        final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        final FileBody fileBody = new FileBody(file);
        builder.addPart(PART_TYPE_FILE, fileBody);
        httpPost.setEntity(builder.build());
    }

    private HttpClient initializeHttpClient() {
        final HttpClientBuilder builder = HttpClientBuilder.create();
        return builder.build();
    }

    private HttpPost initializeHttpPostRequest() {
        return new HttpPost(facileApiUrl);
    }

    @SuppressWarnings("unchecked")
    private JAXBElement<ValidatorType> parseXmlResponse(final String xmlResponse) throws JAXBException, IOException, MessagingException {
        final JAXBContext context = JAXBContext.newInstance(fr.progilone.pgcn.domain.jaxb.facile.ObjectFactory.class);
        final Unmarshaller unmarshaller = context.createUnmarshaller();
        final StringReader reader = new StringReader(xmlResponse);
        return (JAXBElement<ValidatorType>) unmarshaller.unmarshal(reader);
    }
}
