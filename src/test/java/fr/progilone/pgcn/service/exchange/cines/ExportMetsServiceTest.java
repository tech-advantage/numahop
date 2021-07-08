package fr.progilone.pgcn.service.exchange.cines;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xml.sax.SAXException;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.document.BibliographicRecordDcDTO;
import fr.progilone.pgcn.domain.jaxb.mets.Mets;
import fr.progilone.pgcn.domain.storage.CheckSummedStoredFile;
import fr.progilone.pgcn.service.check.MetaDatasCheckService;
import fr.progilone.pgcn.service.document.TableOfContentsService;
import fr.progilone.pgcn.service.exchange.cines.GenerateDocUnitUtil.GenerateDocUnitUtilEnum;
import fr.progilone.pgcn.service.exchange.ead.ExportEadService;

/**
 * Created by Sébastien on 28/12/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class ExportMetsServiceTest {


    @Mock
    private ExportEadService exportEadService;

    private ExportMetsService service;
    
    @Mock
    private MetaDatasCheckService mdCheckService;
    
    @Mock
    private TableOfContentsService tocService;

    @Before
    public void setUp() {
        service = new ExportMetsService(exportEadService, mdCheckService, tocService);
    }

    @Test
    public void testWriteMetadataDocUnit() throws JAXBException, IOException, SAXException {
        final DocUnit docUnit = GenerateDocUnitUtil.getDocUnit(GenerateDocUnitUtilEnum.COMPLIANT);
        final File eadTmpFile = createEadTmpFile();

        final BibliographicRecordDcDTO dcDto = new BibliographicRecordDcDTO();
        dcDto.getIdentifier().add("TEST-001");
        
        final fr.progilone.pgcn.domain.jaxb.mets.ObjectFactory METS_FACTORY = new fr.progilone.pgcn.domain.jaxb.mets.ObjectFactory();
        final Mets mets = METS_FACTORY.createMets();

        when(exportEadService.retrieveEad(docUnit.getIdentifier())).thenReturn(eadTmpFile);
        when(mdCheckService.getMetaDataMetsFile(any(String.class), any(String.class))).thenReturn(Optional.of(mets));
        when(mdCheckService.getMetaDataExcelFile(any(String.class), any(String.class))).thenReturn(Optional.empty());

        try (final OutputStream out = new ByteArrayOutputStream(); final OutputStream bufOut = new BufferedOutputStream(out)) {

            final List<CheckSummedStoredFile> sums = GenerateDocUnitUtil.getCheckSummedList();
            service.writeMetadata(bufOut, docUnit, dcDto, false, sums);
            bufOut.flush();

            final String xml = out.toString();

            // Vérification de la génération des données bib
            assertTrue(xml.contains("<mets:dmdSec ID=\"DMD-DC\">"));
            // Métadonnées EAD
            assertFalse(xml.contains("<ead:ead>"));
            // Vérification de la présence des fichiers
            assertTrue(xml.contains("<mets:file ID=\"master_0000\""));
            assertTrue(xml.contains("<mets:file ID=\"master_0001\""));
            //mets:structMap ID="structmap_physical"
            // Vérification de la présence d'une structure physique
            assertTrue(xml.contains("<mets:structMap ID=\"structmap_physical\""));
            // Vérification de la présence du lien
            assertTrue(xml.contains("<mets:div DMDID=\"DMD-DC\""));

        } finally {
            FileUtils.deleteQuietly(eadTmpFile);
        }
    }

    @Test
    public void testWriteMetadataDC() throws JAXBException, IOException, SAXException {
        final DocUnit docUnit = GenerateDocUnitUtil.getDocUnit(GenerateDocUnitUtilEnum.COMPLIANT);
        final File eadTmpFile = createEadTmpFile();

        final BibliographicRecordDcDTO dcDto = new BibliographicRecordDcDTO();
        dcDto.getIdentifier().add("TEST-001");
        
        final fr.progilone.pgcn.domain.jaxb.mets.ObjectFactory METS_FACTORY = new fr.progilone.pgcn.domain.jaxb.mets.ObjectFactory();
        final Mets mets = METS_FACTORY.createMets();

        when(exportEadService.retrieveEad(docUnit.getIdentifier())).thenReturn(eadTmpFile);
        when(mdCheckService.getMetaDataMetsFile(any(String.class), any(String.class))).thenReturn(Optional.of(mets));
        when(mdCheckService.getMetaDataExcelFile(any(String.class), any(String.class))).thenReturn(Optional.empty());
        
        try (final OutputStream out = new ByteArrayOutputStream(); final OutputStream bufOut = new BufferedOutputStream(out)) {

            final List<CheckSummedStoredFile> sums = GenerateDocUnitUtil.getCheckSummedList();
            service.writeMetadata(bufOut, docUnit, dcDto, false, sums);
            bufOut.flush();

            final String xml = out.toString();

            // Vérification de la génération des données bib
            assertTrue(xml.contains("<mets:dmdSec ID=\"DMD-DC\">"));
            // Métadonnées EAD
            assertFalse(xml.contains("<ead:ead>"));
            // Vérification de la présence des fichiers
            assertTrue(xml.contains("<mets:file ID=\"master_0000\""));
            assertTrue(xml.contains("<mets:file ID=\"master_0001\""));
            // Vérification de la présence du lien
            assertTrue(xml.contains("<mets:div DMDID=\"DMD-DC\""));

        } finally {
            FileUtils.deleteQuietly(eadTmpFile);
        }
    }

    @Test
    public void testWriteMetadataEAD() throws JAXBException, IOException, SAXException {
        final DocUnit docUnit = GenerateDocUnitUtil.getDocUnit(GenerateDocUnitUtilEnum.COMPLIANT);
        final File eadTmpFile = createEadTmpFile();

        final fr.progilone.pgcn.domain.jaxb.mets.ObjectFactory METS_FACTORY = new fr.progilone.pgcn.domain.jaxb.mets.ObjectFactory();
        final Mets mets = METS_FACTORY.createMets();
        
        when(exportEadService.retrieveEad(docUnit.getIdentifier())).thenReturn(eadTmpFile);
        when(mdCheckService.getMetaDataMetsFile(any(String.class), any(String.class))).thenReturn(Optional.of(mets));
        when(mdCheckService.getMetaDataExcelFile(any(String.class), any(String.class))).thenReturn(Optional.empty());
        
        try (final OutputStream out = new ByteArrayOutputStream(); final OutputStream bufOut = new BufferedOutputStream(out)) {

            final List<CheckSummedStoredFile> sums = GenerateDocUnitUtil.getCheckSummedList();
            service.writeMetadata(bufOut, docUnit, null, true, sums);
            bufOut.flush();

            final String xml = out.toString();

            // Vérification de la génération des données bib
            assertTrue(xml.contains("<mets:dmdSec ID=\"DMD-EAD\">"));
            // Métadonnées EAD
            assertTrue(xml.contains("<ead:ead>"));
            // Vérification de la présence des fichiers
            assertTrue(xml.contains("<mets:file ID=\"master_0000\""));
            assertTrue(xml.contains("<mets:file ID=\"master_0001\""));
            // Vérification de la présence du lien
            assertTrue(xml.contains("<mets:div DMDID=\"DMD-EAD\""));

        } finally {
            FileUtils.deleteQuietly(eadTmpFile);
        }
    }

    private File createEadTmpFile() throws IOException {
        final File tmpFile = new File(FileUtils.getTempDirectory(), "ExportMetsServiceTest_EAD_" + System.currentTimeMillis() + ".xml");
        FileUtils.writeStringToFile(tmpFile, EAD_XML, StandardCharsets.UTF_8);
        return tmpFile;
    }

    private static final String EAD_XML = "<ead xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns=\"urn:isbn:1-931666-22-9\">\n"
                                          + "  <eadheader>\n"
                                          + "    <profiledesc>\n"
                                          + "      <langusage>Instrument de recherche rédigé en<language>français</language></langusage>\n"
                                          + "    </profiledesc>\n"
                                          + "  </eadheader>\n"
                                          + "  <archdesc level=\"class\" id=\"ligeo-31164\">"
                                          + "    <dsc type=\"in-depth\">"
                                          + "      <c id=\"ligeo-31165\"></c>"
                                          + "    </dsc>"
                                          + "  </archdesc>"
                                          + "</ead>\n";
}
