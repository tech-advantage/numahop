package fr.progilone.pgcn.service.exchange.cines;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.document.DocPage;
import fr.progilone.pgcn.domain.document.DocProperty;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.storage.CheckSummedStoredFile;
import fr.progilone.pgcn.domain.storage.StoredFile;
import fr.progilone.pgcn.service.util.FileUtils.CheckSumType;

/**
 * Génère des docUnit de test complet et/ou abregés
 * @author jbrunet
 * Créé le 17 févr. 2017
 */
public final class GenerateDocUnitUtil {

    public static DocUnit getDocUnit(GenerateDocUnitUtilEnum xsdCompliant) {

        final BibliographicRecord record1 = new BibliographicRecord();
        record1.setIdentifier("REC-001");
        
        record1.addProperty(generateProperty(generateType("title"), "American Psycho", "eng"));
        if(xsdCompliant != GenerateDocUnitUtilEnum.NON_COMPLIANT_FULL) addCompliantDataToRecord(record1);
        if(xsdCompliant != GenerateDocUnitUtilEnum.NON_COMPLIANT_AUTHOR_PUBLISHER) addCompliantPublisherAndCreatorToRecord(record1);

        final BibliographicRecord record2 = new BibliographicRecord();
        record2.setIdentifier("REC-002");
        record2.addProperty(generateProperty(generateType("title"), "American Psycho 2", "eng"));
        addCompliantPublisherAndCreatorToRecord(record2);
        
        final DigitalDocument record3 = new DigitalDocument();
        record3.setIdentifier("REC-003");
        record3.setDigitalId("DIGIT_REC-003");

        final DocUnit docUnit = new DocUnit();
        docUnit.setIdentifier("DOC-UNIT-001");
        docUnit.setLabel("toto fait du vélo");
        docUnit.addRecord(record1);
        docUnit.addRecord(record2);
        docUnit.addDigitalDocument(record3);
        
        
        // Library (service versant cines)
        Library lib = new Library();
        lib.setCinesService("service versant");
        docUnit.setLibrary(lib);
        
        // Dates
        docUnit.setCreatedDate(LocalDateTime.of(2017, Month.JANUARY, 6, 15, 15, 15));
        docUnit.setLastModifiedDate(LocalDateTime.now());
        
        return docUnit;
    }
    
    public static List<CheckSummedStoredFile> getCheckSummedList() {
        List<CheckSummedStoredFile> sums = new ArrayList<>();
        CheckSummedStoredFile sum = new CheckSummedStoredFile();
        StoredFile sto = new StoredFile();
        sto.setFilename("test_filename.png");
        sto.setMimetype("image/png");
        DocPage page = new DocPage();
        page.setNumber(0);
        sto.setPage(page);
        sum.setStoredFile(sto);
        sum.setCheckSumType(CheckSumType.MD5);
        sum.setCheckSum("whatever");
        sums.add(sum);
        
        CheckSummedStoredFile sum2 = new CheckSummedStoredFile();
        StoredFile sto2 = new StoredFile();
        sto2.setFilename("test_filename2.png");
        sto2.setMimetype("image/png");
        DocPage page1 = new DocPage();
        page1.setNumber(1);
        sto2.setPage(page1);
        sum2.setStoredFile(sto2);
        sum2.setCheckSumType(CheckSumType.MD5);
        sum2.setCheckSum("whatever2");
        sums.add(sum2);
        return sums;
    }
    
    private static void addCompliantDataToRecord(BibliographicRecord record) {
        record.addProperty(generateProperty(generateType("language"), "fra", null));
        record.addProperty(generateProperty(generateType("subject"), "Subject", "eng"));
        record.addProperty(generateProperty(generateType("description"), "Description", null));
        record.addProperty(generateProperty(generateType("date"), "s.d.", null));
        record.addProperty(generateProperty(generateType("type"), "Type de document", "fra"));
        record.addProperty(generateProperty(generateType("format"), "Format de document", null));
        record.addProperty(generateProperty(generateType("rights"), "Droits du document", null));
    }
    
    private static void addCompliantPublisherAndCreatorToRecord(BibliographicRecord record) {
        record.addProperty(generateProperty(generateType("publisher"), "Publisher", null));
        record.addProperty(generateProperty(generateType("creator"), "Bret Easton Ellis", null));
    }
    
    private static DocPropertyType generateType(String id) {
        final DocPropertyType type = new DocPropertyType();
        type.setIdentifier(id);
        type.setSuperType(DocPropertyType.DocPropertySuperType.DC);
        return type;
    }
    
    private static DocProperty generateProperty(DocPropertyType type, String value, String language) {
        final DocProperty property = new DocProperty();
        property.setValue(value);
        property.setType(type);
        property.setLanguage(language);
        return property;
    }
    
    public enum GenerateDocUnitUtilEnum {
        COMPLIANT,
        NON_COMPLIANT_FULL,
        NON_COMPLIANT_AUTHOR_PUBLISHER
    }
}
