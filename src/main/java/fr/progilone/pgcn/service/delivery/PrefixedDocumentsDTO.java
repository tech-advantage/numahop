package fr.progilone.pgcn.service.delivery;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Liste des données associées à un préfixe pour une livraison :
 * <ul>
 * <li>Fichiers correspondants</li>
 * <li>document physique associé : identifiant</li>
 * <li>document numérique associé : identifiant</li>
 * </ul>
 *
 * @author jbrunet
 *         Créé le 27 mars 2017
 */
public class PrefixedDocumentsDTO {

    private List<File> files = new ArrayList<>();
    private List<String> physicalDocuments = new ArrayList<>();
    private List<String> digitalDocuments = new ArrayList<>();

    public PrefixedDocumentsDTO(List<File> files, List<String> physicalDocuments, List<String> digitalDocuments) {
        this.files = files;
        this.physicalDocuments = physicalDocuments;
        this.digitalDocuments = digitalDocuments;
    }

    public PrefixedDocumentsDTO() {
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files.clear();
        if (files != null) {
            files.forEach(this::addFile);
        }
    }

    public void addFile(File file) {
        if (file != null) {
            files.add(file);
        }
    }

    public List<String> getPhysicalDocuments() {
        return physicalDocuments;
    }

    public void setPhysicalDocuments(List<String> physicalDocuments) {
        this.physicalDocuments.clear();
        if (physicalDocuments != null) {
            physicalDocuments.forEach(this::addPhysicalDocument);
        }
    }

    public void addPhysicalDocument(String doc) {
        if (doc != null) {
            physicalDocuments.add(doc);
        }
    }

    public List<String> getDigitalDocuments() {
        return digitalDocuments;
    }

    public void setDigitalDocuments(List<String> digitalDocuments) {
        this.digitalDocuments.clear();
        if (digitalDocuments != null) {
            digitalDocuments.forEach(this::addDigitalDocument);
        }
    }

    public void addDigitalDocument(String doc) {
        if (doc != null) {
            digitalDocuments.add(doc);
        }
    }

}
