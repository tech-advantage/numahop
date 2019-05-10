package fr.progilone.pgcn.service.exchange.z3950;

import fr.progilone.pgcn.domain.administration.exchange.z3950.DataFormat;
import fr.progilone.pgcn.domain.administration.exchange.z3950.Z3950Server;
import fr.progilone.pgcn.domain.dto.administration.z3950.Z3950ServerDTO;
import fr.progilone.pgcn.domain.dto.exchange.Z3950RecordDTO;
import fr.progilone.pgcn.domain.exchange.DataEncoding;
import fr.progilone.pgcn.exception.PgcnBusinessException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.repository.administration.z3950.Z3950ServerRepository;
import fr.progilone.pgcn.service.administration.mapper.Z3950ServerMapper;
import fr.progilone.pgcn.service.exchange.marc.MarcUtils;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.converter.CharConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yaz4j.Connection;
import org.yaz4j.PrefixQuery;
import org.yaz4j.Query;
import org.yaz4j.Record;
import org.yaz4j.ResultSet;
import org.yaz4j.exception.ConnectionUnavailableException;
import org.yaz4j.exception.ZoomException;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * Service gérant l'import de notices à partir d'un serveur Z3950
 */
@Service
public class Z3950Service {

    private static final Logger LOG = LoggerFactory.getLogger(Z3950Service.class);

    private static final String ELEMENT_SET_NAME = "elementSetName";
    private static final String FIELD_010 = "010";
    private static final String FIELD_011 = "011";
    private static final String FIELD_200 = "200";

    private final Z3950ServerRepository z3950ServerRepository;

    @Autowired
    public Z3950Service(final Z3950ServerRepository z3950ServerRepository) {
        this.z3950ServerRepository = z3950ServerRepository;
    }

    /**
     * Pré-chargement des dll yaz4j par la classe {@link Connection}
     */
    @PostConstruct
    public void init() {
        try {
            LOG.info("Initialisation de la librarie yaz4j...");
            Class.forName("org.yaz4j.Connection");
        } catch (ClassNotFoundException | LinkageError e) {
            LOG.error("Le chargement de la librairie yaz4j a échoué sur l'erreur \"{}\"", e.getMessage());
        }
    }

    // MOCK à garder...
    // final String xml =
    // "<?xml version=\"1.0\" encoding=\"UTF-8\"?><marcxml:collection xmlns:marcxml=\"http://www.loc.gov/MARC21/slim\">\r\n  <marcxml:record>\r\n    <marcxml:leader>01096cam  2200265   450 </marcxml:leader>\r\n    <marcxml:controlfield tag=\"001\">FRBNF437004190000002</marcxml:controlfield>\r\n    <marcxml:controlfield tag=\"003\">http://catalogue.bnf.fr/ark:/12148/cb43700419m</marcxml:controlfield>\r\n    <marcxml:datafield tag=\"010\" ind1=\" \" ind2=\" \">\r\n      <marcxml:subfield code=\"a\">978-2-35287-505-5</marcxml:subfield>\r\n      <marcxml:subfield code=\"b\">rel.</marcxml:subfield>\r\n      <marcxml:subfield code=\"d\">12 EUR</marcxml:subfield>\r\n    </marcxml:datafield>\r\n    <marcxml:datafield tag=\"020\" ind1=\" \" ind2=\" \">\r\n      <marcxml:subfield code=\"a\">FR</marcxml:subfield>\r\n      <marcxml:subfield code=\"b\">01363771</marcxml:subfield>\r\n    </marcxml:datafield>\r\n    <marcxml:datafield tag=\"073\" ind1=\" \" ind2=\"0\">\r\n      <marcxml:subfield code=\"a\">9782352875055</marcxml:subfield>\r\n    </marcxml:datafield>\r\n    <marcxml:datafield tag=\"100\" ind1=\" \" ind2=\" \">\r\n      <marcxml:subfield code=\"a\">20131028d2013    m  y0frey50      ba</marcxml:subfield>\r\n    </marcxml:datafield>\r\n    <marcxml:datafield tag=\"101\" ind1=\"0\" ind2=\" \">\r\n      <marcxml:subfield code=\"a\">fre</marcxml:subfield>\r\n    </marcxml:datafield>\r\n    <marcxml:datafield tag=\"102\" ind1=\" \" ind2=\" \">\r\n      <marcxml:subfield code=\"a\">FR</marcxml:subfield>\r\n    </marcxml:datafield>\r\n    <marcxml:datafield tag=\"105\" ind1=\" \" ind2=\" \">\r\n      <marcxml:subfield code=\"a\">    z   00 a </marcxml:subfield>\r\n    </marcxml:datafield>\r\n    <marcxml:datafield tag=\"106\" ind1=\" \" ind2=\" \">\r\n      <marcxml:subfield code=\"a\">r</marcxml:subfield>\r\n    </marcxml:datafield>\r\n    <marcxml:datafield tag=\"200\" ind1=\"1\" ind2=\" \">\r\n      <marcxml:subfield code=\"a\"> Les  trois mousquetaires</marcxml:subfield>\r\n      <marcxml:subfield code=\"b\">Texte imprim\u00E9</marcxml:subfield>\r\n      <marcxml:subfield code=\"f\">Alexandre Dumas</marcxml:subfield>\r\n    </marcxml:datafield>\r\n    <marcxml:datafield tag=\"210\" ind1=\" \" ind2=\" \">\r\n      <marcxml:subfield code=\"a\">Paris</marcxml:subfield>\r\n      <marcxml:subfield code=\"c\">[Archipoche]</marcxml:subfield>\r\n      <marcxml:subfield code=\"d\">cop. 2013</marcxml:subfield>\r\n      <marcxml:subfield code=\"e\">59-Villeneuve-d'Ascq</marcxml:subfield>\r\n      <marcxml:subfield code=\"g\">Impr. Imago</marcxml:subfield>\r\n    </marcxml:datafield>\r\n    <marcxml:datafield tag=\"215\" ind1=\" \" ind2=\" \">\r\n      <marcxml:subfield code=\"a\">1 vol. (947 p.)</marcxml:subfield>\r\n      <marcxml:subfield code=\"c\">jaquette ill. en coul.</marcxml:subfield>\r\n      <marcxml:subfield code=\"d\">16 cm</marcxml:subfield>\r\n    </marcxml:datafield>\r\n    <marcxml:datafield tag=\"225\" ind1=\"|\" ind2=\" \">\r\n      <marcxml:subfield code=\"a\"> La  biblioth\u00E8que du collectionneur</marcxml:subfield>\r\n      <marcxml:subfield code=\"v\">21</marcxml:subfield>\r\n    </marcxml:datafield>\r\n    <marcxml:datafield tag=\"410\" ind1=\" \" ind2=\"0\">\r\n      <marcxml:subfield code=\"0\">42537932</marcxml:subfield>\r\n      <marcxml:subfield code=\"t\"> La  Biblioth\u00E8que du collectionneur (Paris)</marcxml:subfield>\r\n      <marcxml:subfield code=\"x\">2264-3168</marcxml:subfield>\r\n      <marcxml:subfield code=\"v\">21</marcxml:subfield>\r\n    </marcxml:datafield>\r\n    <marcxml:datafield tag=\"517\" ind1=\"1\" ind2=\" \">\r\n      <marcxml:subfield code=\"a\"/>\r\n    </marcxml:datafield>\r\n    <marcxml:datafield tag=\"686\" ind1=\" \" ind2=\" \">\r\n      <marcxml:subfield code=\"a\">803</marcxml:subfield>\r\n      <marcxml:subfield code=\"2\">Cadre de classement de la Bibliographie nationale fran\u00E7aise</marcxml:subfield>\r\n    </marcxml:datafield>\r\n    <marcxml:datafield tag=\"700\" ind1=\" \" ind2=\"|\">\r\n      <marcxml:subfield code=\"3\">11901063</marcxml:subfield>\r\n      <marcxml:subfield code=\"a\">Dumas</marcxml:subfield>\r\n      <marcxml:subfield code=\"b\">Alexandre</marcxml:subfield>\r\n      <marcxml:subfield code=\"f\">1802-1870</marcxml:subfield>\r\n      <marcxml:subfield code=\"4\">070</marcxml:subfield>\r\n    </marcxml:datafield>\r\n    <marcxml:datafield tag=\"801\" ind1=\" \" ind2=\"0\">\r\n      <marcxml:subfield code=\"a\">FR</marcxml:subfield>\r\n      <marcxml:subfield code=\"b\">FR-751131015</marcxml:subfield>\r\n      <marcxml:subfield code=\"c\">20131028</marcxml:subfield>\r\n      <marcxml:subfield code=\"g\">AFNOR</marcxml:subfield>\r\n      <marcxml:subfield code=\"h\">FRBNF437004190000002</marcxml:subfield>\r\n      <marcxml:subfield code=\"2\">intermrc</marcxml:subfield>\r\n    </marcxml:datafield>\r\n    <marcxml:datafield tag=\"930\" ind1=\" \" ind2=\" \">\r\n      <marcxml:subfield code=\"5\">FR-751131010:43700419001001</marcxml:subfield>\r\n      <marcxml:subfield code=\"a\">2013-391006</marcxml:subfield>\r\n      <marcxml:subfield code=\"b\">759999999</marcxml:subfield>\r\n      <marcxml:subfield code=\"c\">Tolbiac - Rez de Jardin - Litt\u00E9rature et art - Magasin</marcxml:subfield>\r\n      <marcxml:subfield code=\"d\">O</marcxml:subfield>\r\n    </marcxml:datafield>\r\n  </marcxml:record>\r\n</marcxml:collection>";
    // final Z3950ServerDTO z3950ServerDTO = null;
    //
    // final Z3950RecordDTO z3950RecordDTO = new Z3950RecordDTO();
    // z3950RecordDTO.setMarcXml(xml);
    // z3950RecordDTO.setZ3950Server(z3950ServerDTO);
    //
    // z3950records.add(z3950RecordDTO);
    //
    // return new Z3950ResultDTO(z3950records, 1);
    @Transactional(readOnly = true)
    public Page<Z3950RecordDTO> search(final Map<String, String> fields, final List<String> serverIdentifiers, final int page, final int size) throws
                                                                                                                                               PgcnBusinessException {
        final List<Z3950RecordDTO> z3950records = new ArrayList<>();
        final List<Z3950Server> servers = z3950ServerRepository.findAll(serverIdentifiers);
        long totalCount = 0;

        LOG.debug("Recherche Z39.50 de {} sur les serveurs: {}, page {}, taille {}",
                  fields.entrySet().stream().map(a -> a.getKey() + " = " + a.getValue()).reduce((a, b) -> a + ", " + b).orElse("[recherche vide !]"),
                  servers.stream()
                         .map(srv -> srv.getName() + " (" + srv.getIdentifier() + ")")
                         .reduce((a, b) -> a + ", " + b)
                         .orElse("[aucun serveur sélectionné !]"),
                  page,
                  size);

        for (final Z3950Server z3950Server : servers) {
            try (Connection connection = createConnection(z3950Server)) {
                final Query query = createQuery(fields, z3950Server.getDataEncoding());
                final ResultSet result = connection.search(query); // NOSONAR
                totalCount = result.getHitCount();
                LOG.debug("Le serveur {} ({}) a renvoyé {} résultat(s)", z3950Server.getName(), z3950Server.getIdentifier(), totalCount);

                // Lecture des résultats
                long count = size;
                if ((page + 1) * size > totalCount) {
                    count = totalCount - page * size;
                }
                result.getRecords(page * size, (int) count).stream()
                      // Conversion des résultat en record marc4j
                      .map(yazRecord -> convertToMarcRecord(yazRecord, z3950Server.getDataEncoding())).filter(Optional::isPresent).map(Optional::get)
                      // Création des DTOs
                      .map(rec -> getResult(rec, z3950Server)).forEach(z3950records::add);

                break;  // On s'arrête au premier serveur qui répond

            } catch (final ConnectionUnavailableException e) {
                LOG.error(e.getMessage(), e);
                final PgcnError.Builder builder = new PgcnError.Builder();
                throw new PgcnBusinessException(builder.reinit()
                                                       .setCode(PgcnErrorCode.Z3950_CONNECTION_FAILURE)
                                                       .addComplement(e.getMessage())
                                                       .build());
            } catch (final ZoomException e) {
                LOG.error(e.getMessage(), e);
                final PgcnError.Builder builder = new PgcnError.Builder();
                throw new PgcnBusinessException(builder.reinit().setCode(PgcnErrorCode.Z3950_SEARCH_FAILURE).addComplement(e.getMessage()).build());
            }
        }
        return new PageImpl<>(z3950records, new PageRequest(page, size), totalCount);
    }

    /**
     * Etablit et retourne une connection à un serveur Z39.50
     *
     * @param z3950Server
     *         le serveur Z39.50
     * @return
     * @throws ZoomException
     */
    private Connection createConnection(final Z3950Server z3950Server) throws ZoomException {
        final int port = z3950Server.getPort();
        final String host = z3950Server.getHost();
        final String database = z3950Server.getDatabase();
        final String username = z3950Server.getUserId();
        final String password = z3950Server.getPassword();
        final DataFormat dataFormat = z3950Server.getDataFormat();

        /**
         * Un plantage a ce niveau peut arriver si les librairies yaz ne sont pas installées, ou absentes du serveur
         * Elles doivent être accessibles par l'application, par la propriété de configuration "nativeLibraries.path" (application-xxx.yml)
         *
         * => Install sous debian: apt-get install libyaz4
         * => Install sous windows: http://www.indexdata.com/yaz
         *    + La librairie yaz4j.dll et ses dépendances sont accessibles via la variable d'environnement PATH
         */
        final Connection c = new Connection(host, port);

        c.setDatabaseName(database);
        c.option(ELEMENT_SET_NAME, "F");
        c.setUsername(username);
        c.setPassword(password);
        c.setSyntax(dataFormat.getFormatCode());

        c.connect();
        return c;
    }

    private Query createQuery(final Map<String, String> fields, final DataEncoding dataEncoding) {
        // Map de champs pour la recherche Z3950
        // http://www.bnf.fr/documents/profilZ3950_bnf.pdf
        final Map<String, String> fieldsForZ3950Query = new HashMap<>();
        fieldsForZ3950Query.put("isbn", "@attr 1=7 ");
        fieldsForZ3950Query.put("issn", "@attr 1=8 ");
        fieldsForZ3950Query.put("title", "@attr 1=4 ");
        fieldsForZ3950Query.put("author", "@attr 1=1003 ");

        final CharConverter charConverter = MarcUtils.getCharConverterFromUnicode(dataEncoding);
        final StringBuilder pqfQuery = new StringBuilder();
        int i = 0;

        for (final Entry<String, String> entry : fields.entrySet()) {
            String field = entry.getValue();
            if (charConverter != null) {
                // Conversion de la chaîne UTF-8 à recherche dans le format attendu par le serveur Z39.50
                field = charConverter.convert(field);
            }

            if (StringUtils.isNotBlank(field)) {
                final String prefix = fieldsForZ3950Query.get(entry.getKey());
                if (prefix != null) {
                    pqfQuery.append(prefix).append("\"").append(field).append("\"").append(" ");
                    i++;
                }
            }
        }
        for (int j = 0; j < i - 1; j++) {
            pqfQuery.insert(0, " @and ");
        }
        return new PrefixQuery(pqfQuery.toString());
    }

    private Z3950RecordDTO getResult(final org.marc4j.marc.Record marcXmlRecord, final Z3950Server server) {
        final String xml = MarcUtils.getRecordInXml(marcXmlRecord, server.getDataEncoding());
        final org.marc4j.marc.Record marcXmlRecordTranscoded = MarcUtils.getRecord(xml);

        final String isbn = MarcUtils.getSubfieldFirstValue(marcXmlRecordTranscoded, FIELD_010, 'a');
        final String issn = MarcUtils.getSubfieldFirstValue(marcXmlRecordTranscoded, FIELD_011, 'a');
        final String title = MarcUtils.getSubfieldFirstValue(marcXmlRecordTranscoded, FIELD_200, 'a');
        final String author = MarcUtils.getSubfieldFirstValue(marcXmlRecordTranscoded, FIELD_200, 'f');

        final Z3950ServerDTO z3950ServerDTO = Z3950ServerMapper.INSTANCE.z3950ServerToDto(server);

        final Z3950RecordDTO z3950ResultDTO = new Z3950RecordDTO();
        z3950ResultDTO.setZ3950Server(z3950ServerDTO);
        z3950ResultDTO.setTitle(title);
        z3950ResultDTO.setIsbn(isbn);
        z3950ResultDTO.setIssn(issn);
        z3950ResultDTO.setAuthor(author);
        z3950ResultDTO.setMarcXml(xml);
        return z3950ResultDTO;
    }

    /**
     * Convertit un Record yaz en Record marc4j
     *
     * @param yazRecord
     * @param dataEncoding
     * @return
     */
    private Optional<org.marc4j.marc.Record> convertToMarcRecord(Record yazRecord, final DataEncoding dataEncoding) {
        final String encoding = dataEncoding == DataEncoding.UTF_8 ?
                                StandardCharsets.UTF_8.name() :
                                dataEncoding == DataEncoding.ISO_8859_1 ? StandardCharsets.ISO_8859_1.name() : null;
        final InputStream stream = new ByteArrayInputStream(yazRecord.getContent());
        final MarcReader marcStreamReader = new MarcStreamReader(stream, encoding);

        if (marcStreamReader.hasNext()) {
            return Optional.of(marcStreamReader.next());
        }
        return Optional.empty();
    }
}
