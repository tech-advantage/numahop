package fr.progilone.pgcn.service.document;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.domain.document.DocPage;
import fr.progilone.pgcn.domain.jaxb.mets.FileType;
import fr.progilone.pgcn.domain.jaxb.mets.Mets;
import fr.progilone.pgcn.domain.jaxb.mets.StructMapType;
import fr.progilone.pgcn.domain.storage.StoredFile;
import fr.progilone.pgcn.repository.document.DocPageRepository;
import fr.progilone.pgcn.repository.storage.BinaryRepository;
import fr.progilone.pgcn.service.exchange.iiif.manifest.Ranges;
import fr.progilone.pgcn.service.exchange.iiif.manifest.Structures;

@Service
public class TableOfContentsService {

    private static final Logger LOG = LoggerFactory.getLogger(TableOfContentsService.class);

    private final DocPageRepository docPageRepository;
    private final BinaryRepository binaryRepository;

    @Autowired
    public TableOfContentsService(final DocPageRepository docPageRepository,
                         final BinaryRepository binaryRepository) {

        this.docPageRepository = docPageRepository;
        this.binaryRepository = binaryRepository;
    }

    /**
     * Construction table des matieres depuis 1 METS.
     *
     * @param identifier
     * @return
     */
    public List<Structures> getTableOfContent(final String identifier, final Mets mets) {

        final List<Structures> structList = new ArrayList<>();
        final String TYPE_STRUCT_PHYSICAL = "physical";

        final List<StoredFile> storedMasters = getMasterStoredFiles(identifier);
        final Map<String, StoredFile> mastersMap = storedMasters.stream().map(sf -> Pair.of(sf.getFilename(), sf))
                                                                              .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
        // Table des matieres
        LOG.info("Construction table des matieres depuis 1 METS.");

        structList.add(initializeStructure(identifier));

        // Niveau 0
        final Structures r0Struct = new Structures();
        final List<Ranges> ranges = new ArrayList<>();
        r0Struct.setId(ViewerService.URI_WS_VIEWER.concat(identifier) + "/range/r");
        r0Struct.setType("sc:range");
        r0Struct.setAdditionalProperty("ranges", ranges);

        // extract Structures from Mets.
        final List<StructMapType> metsStructures = mets.getStructMap();
        metsStructures.forEach((smt) -> {

            // On utilise la structure physique.
            if (StringUtils.equals(smt.getTYPE(), TYPE_STRUCT_PHYSICAL)) {

                final String title = smt.getDiv().getLABEL();
                r0Struct.setAdditionalProperty("label", title);
                structList.add(r0Struct);

                smt.getDiv()
                    .getDiv()
                        .stream()
                            .forEach((page) -> {

                    page.getFptr()
                        .stream()
                            .filter((fptr) -> fptr.getFILEID()!= null )
                                .forEach((fptr) -> {

                        final FileType fileType = (FileType)fptr.getFILEID();
                        fileType.getFLocat().stream()
                                .findFirst()
                                .ifPresent(location -> {

                                    final String[] split = location.getHref().split("/");
                                    final String fileName = Stream.of( split )
                                                    .reduce( (first,last) -> last ).get();
                                    final StoredFile masterSf = mastersMap.get(fileName);
                                    if (masterSf != null) {
                                        final int index = storedMasters.indexOf(masterSf);
                                        final String pageOrder = String.valueOf(index + 1);
                                        final Structures s = buildStructure(identifier, pageOrder, page.getTYPE(), page.getORDERLABEL(), page.getLABEL(), masterSf, ranges);
                                        structList.add(s);
                                    }
                        });

                    });
                });
            }
        });
        return structList;
    }

    /**
     * Construction d'une TOC depuis les infos saisies au contrôle.
     * (Pas de fichier Mets ni d'Excel à la livraison)
     *
     * @param identifier
     * @return
     */
    public List<Structures> getTableOfContentFromDb(final String identifier) {

        final List<Structures> structList = new ArrayList<>();
        final List<StoredFile> storedMasters = getMasterStoredFiles(identifier);
        // Table des matieres depuis infos de la DB.
        LOG.info("Construction table des matieres depuis DB si saisie au contrôle.");
        final boolean tocExists = storedMasters.stream()
                                    .anyMatch(sf -> ! StringUtils.isAllBlank(sf.getTitleToc(), sf.getTypeToc(), sf.getOrderToc()));

        if (tocExists) {

            structList.add(initializeStructure(identifier));
            // Niveau 0
            final Structures r0Struct = new Structures();
            final List<Ranges> ranges = new ArrayList<>();
            r0Struct.setId(ViewerService.URI_WS_VIEWER.concat(identifier) + "/range/r");
            r0Struct.setType("sc:range");
            r0Struct.setAdditionalProperty("ranges", ranges);
            r0Struct.setAdditionalProperty("label", "Table des matières");
            structList.add(r0Struct);

            storedMasters.stream()
                            .forEach(sf -> {
                                final String pageOrder = String.valueOf(sf.getPage().getNumber().intValue());
                                final Structures s = buildStructure(identifier, pageOrder, null, null, null, sf, ranges);
                                structList.add(s);
                            });
        }
        return structList;
    }

    /**
     * Construit un element Structures.
     *
     * @param identifier
     * @param pageOrder
     * @param pgLabel
     * @param pgOrderLabel
     * @param ranges
     * @return
     */
    private Structures buildStructure(final String identifier, final String pageOrder,
                                      final String type, final String orderLabel, final String label,
                                      final StoredFile sf, final List<Ranges> ranges) {

        final String SEPAR = " - ";

        final Structures s = new Structures();
        final List<String> canvases = new ArrayList<>();
        final String idRange = ViewerService.URI_WS_VIEWER.concat(identifier)
                .concat("/range/r")
                .concat(pageOrder);
        s.setId(idRange);
        s.setType("sc:range");

        final StringBuilder lib = new StringBuilder();
        /* label => TOC */
        /* type, order, title => Metadatas */
        if (StringUtils.isAllBlank(sf.getTitleToc(), sf.getTypeToc(), sf.getOrderToc())) {
            // données du Mets
            if (StringUtils.isAllBlank(type, orderLabel, label)) {
                s.setAdditionalProperty("label", "Page non chiffrée");
            } else {
                if (StringUtils.isNotBlank(type)) {
                    lib.append(type);
                }
                if (StringUtils.isNotBlank(orderLabel)) {
                    if (StringUtils.isNotBlank(lib.toString())) {
                        lib.append(SEPAR);
                    }
                    lib.append(orderLabel);
                }
                if (StringUtils.isNotBlank(label)) {
                    if (StringUtils.isNotBlank(lib.toString())
                            && ! StringUtils.endsWith(lib.toString(), SEPAR)) {
                        lib.append(SEPAR);
                    }
                    lib.append(label);
                }
                s.setAdditionalProperty("label", lib.toString());
                s.setAdditionalProperty("title", label);
                s.setAdditionalProperty("order", orderLabel);
                s.setAdditionalProperty("type", type);
            }
        } else {
            // données de la table storedFile
            if (StringUtils.isNotBlank(sf.getTypeToc())) {
                lib.append(sf.getTypeToc());
            }
            if (StringUtils.isNotBlank(sf.getOrderToc())) {
                if (StringUtils.isNotBlank(lib.toString())) {
                    lib.append(SEPAR);
                }
                lib.append(sf.getOrderToc());
            }
            if (StringUtils.isNotBlank(sf.getTitleToc())) {
                if (StringUtils.isNotBlank(lib.toString())
                        && ! StringUtils.endsWith(lib.toString(), SEPAR)) {
                    lib.append(SEPAR);
                }
                lib.append(sf.getTitleToc());
            }
            s.setAdditionalProperty("label", lib.toString());
            s.setAdditionalProperty("title", sf.getTitleToc());
            s.setAdditionalProperty("order", sf.getOrderToc());
            s.setAdditionalProperty("type", sf.getTypeToc());
        }

        final Ranges r = new Ranges();
        r.setId(idRange);
        ranges.add(r);
        canvases.add(pageOrder);
        s.setAdditionalProperty("canvases", canvases);
        return s;
    }

    /**
     * Renvoie une Structure sans réelle correspondance
     * afin de permettre l'affichage du titre en 1er niveau.
     *
     * @param identifier
     * @return
     */
    private Structures initializeStructure(final String identifier) {
        final Structures toc = new Structures();
        toc.setId(ViewerService.URI_WS_VIEWER.concat(identifier) + "/range/r_root");
        final Ranges rToc = new Ranges();
        rToc.setId(ViewerService.URI_WS_VIEWER.concat(identifier) + "/range/r");
        toc.setAdditionalProperty("ranges", rToc);
        return toc;
    }

    /**
     * Retourne la liste des master storedFile du doc.
     *
     * @param identifier
     * @return
     */
    private List<StoredFile> getMasterStoredFiles(final String identifier) {

        final List<DocPage> dps = docPageRepository.getAllByDigitalDocumentIdentifier(identifier);
        if (dps.isEmpty()) {
            return Collections.emptyList();
        }

        final List<String> ids = dps.stream().map(AbstractDomainObject::getIdentifier).collect(Collectors.toList());
        return binaryRepository.getAllByPageIdentifiersAndFileFormat(ids, ViewsFormatConfiguration.FileFormat.MASTER);
    }

    /**
     * Construction table des matieres depuis 1 classeur excel.
     *
     * @param identifier
     * @param excel
     * @return
     */
    public List<Structures> getTableOfContentExcel(final String identifier, final InputStream input, final String docTitle) throws IOException, InvalidFormatException {

        LOG.info("Construction table des matieres depuis 1 classeur EXCEL.");

        final List<StoredFile> storedMasters = getMasterStoredFiles(identifier);
        final List<Structures> structList = new ArrayList<>();
        structList.add(initializeStructure(identifier));

        // Niveau 0
        final Structures r0Struct = new Structures();
        final List<Ranges> ranges = new ArrayList<>();
        r0Struct.setId(ViewerService.URI_WS_VIEWER.concat(identifier) + "/range/r");
        r0Struct.setType("sc:range");
        r0Struct.setAdditionalProperty("ranges", ranges);
        r0Struct.setAdditionalProperty("label", docTitle);
        structList.add(r0Struct);

        // Lecture du classeur excel.
        new SheetReader().stream(input).process(identifier, structList, ranges, storedMasters);
        return structList;
    }


    /**
     * Classe gérant la lecture du fichier excel, et la création de la structure => TOC.
     */
    private class SheetReader {

        private Workbook wb;

        /**
         * Initialisation du workbook à partir d'un flux
         *
         * @param in
         * @return
         * @throws IOException
         * @throws InvalidFormatException
         */
        public SheetReader stream(final InputStream in) throws IOException, InvalidFormatException {
            this.wb = WorkbookFactory.create(in);
            return this;
        }


        /**
         *
         * @param identifier
         * @param structList
         * @param ranges
         * @return
         */
        public SheetReader process(final String identifier, final List<Structures> structList,
                                   final List<Ranges> ranges, final List<StoredFile> storedMasters) {
            this.wb.sheetIterator().forEachRemaining(sheet -> readSheet(sheet, identifier, structList, ranges, storedMasters));
            return this;
        }


        /**
         * Lecture des feuilles du classeur excel.
         *
         * @param sheet
         * @param identifier
         * @param structList
         * @param ranges
         */
        private void readSheet(final Sheet sheet, final String identifier, final List<Structures> structList,
                               final List<Ranges> ranges, final List<StoredFile> storedMasters) {

            final Optional<String> value = getCell(sheet, 0, 0)
                    .filter(cell->cell!=null && CellType.STRING.equals(cell.getCellTypeEnum()))
                    .map(Cell::getStringCellValue);

            if (value.isPresent() && "Fichier".equalsIgnoreCase(value.get())) {
                LOG.debug("ok : TOC excel valorisée !");
                // on traite
                parseSheet(sheet, identifier, structList, ranges, storedMasters);
            }
        }

        /**
         * Lecture des valeurs et valorisation des structures correspondantes.
         *
         * @param sheet
         * @param identifier
         * @param structList
         * @param ranges
         */
        private void parseSheet(final Sheet sheet, final String identifier, final List<Structures> structList, final List<Ranges> ranges, final List<StoredFile> storedMasters) {

            /* Le classeur doit etre structuré en colonnes comme suit :  */
            /* Fichier  ||  Type  ||  Page  ||  Chapitre  ||  Titre */

            final Iterator<Row> rowIterator = sheet.rowIterator();
            while (rowIterator.hasNext()) {
                final Row row = rowIterator.next();
                final int pgNum = row.getRowNum();
                // on bypass la ligne d'entetes.
                if (pgNum == 0) {
                    continue;
                }
                final Optional<String> optFile = Optional.ofNullable(row.getCell(0)).map(Cell::getStringCellValue);
                // Le nom de fichier doit etre renseigné.
                if (!optFile.isPresent()
                        || StringUtils.isBlank(optFile.get())) {
                    LOG.warn("TDM excel : nom de fichier non renseigné");
                    return;
                }

                // trop d'entrees dans le tableau => on se limite au nbre de fichiers.
                if (pgNum > storedMasters.size()) {
                    return;
                }

                final StringBuilder title = new StringBuilder();
                // pour parer aux conversions automatiques d'excel qui peuvent faire planter, on passe par le formatteur...
                final DataFormatter format = new DataFormatter();
                final Optional<String> optType = Optional.ofNullable(format.formatCellValue(row.getCell(1)));
                final Optional<String> optPage = Optional.ofNullable(format.formatCellValue(row.getCell(2)));
                final Optional<String> optChap = Optional.ofNullable(format.formatCellValue(row.getCell(3)));
                final Optional<String> optTitle = Optional.ofNullable(format.formatCellValue(row.getCell(4)));

                if (optChap.isPresent() && StringUtils.isNotBlank(optChap.get())) {
                    title.append(optChap.get());
                }
                if (optTitle.isPresent() && StringUtils.isNotBlank(optTitle.get())) {
                    if (StringUtils.isNoneBlank(title)){
                        title.append(" - ");
                    }
                    title.append(optTitle.get());
                }
                final String type = optType.isPresent() && StringUtils.isNotBlank(optType.get())?optType.get():"";
                final String orderPg = optPage.isPresent() && StringUtils.isNotBlank(optPage.get())?optPage.get():"";

                final Structures s = buildStructure(identifier, String.valueOf(pgNum), type, orderPg, title.toString(), storedMasters.get(pgNum-1), ranges);
                structList.add(s);
            }
        }


         private Optional<Cell> getCell(final Sheet sheet, final int rowNb, final int cellNb) {
            final Row row = sheet.getRow(rowNb);
            if (row == null) {
                return Optional.empty();
            }
            final Cell cell = row.getCell(cellNb);
            return Optional.ofNullable(cell);
        }

    }

}
