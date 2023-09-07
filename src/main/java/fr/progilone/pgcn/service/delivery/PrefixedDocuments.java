package fr.progilone.pgcn.service.delivery;

import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.document.PhysicalDocument;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Liste des données associées à un préfixe pour une livraison :
 * <ul>
 * <li>Fichiers correspondants</li>
 * <li>document physique associé (FIXME : un pour l'instant ?)</li>
 * <li>document numérique associé (FIXME : un pour l'instant ?)</li>
 * </ul>
 *
 * @author jbrunet
 *         Créé le 27 mars 2017
 */
public class PrefixedDocuments {

    private List<File> files = new ArrayList<>();
    private List<PhysicalDocument> physicalDocuments = new ArrayList<>();
    private List<DigitalDocument> digitalDocuments = new ArrayList<>();

    public PrefixedDocuments(List<File> files, List<PhysicalDocument> physicalDocuments, List<DigitalDocument> digitalDocuments) {
        this.files = files;
        this.physicalDocuments = physicalDocuments;
        this.digitalDocuments = digitalDocuments;
    }

    public PrefixedDocuments() {
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

    public List<PhysicalDocument> getPhysicalDocuments() {
        return physicalDocuments;
    }

    public void setPhysicalDocuments(List<PhysicalDocument> physicalDocuments) {
        this.physicalDocuments.clear();
        if (physicalDocuments != null) {
            physicalDocuments.forEach(this::addPhysicalDocument);
        }
    }

    public void addPhysicalDocument(PhysicalDocument doc) {
        if (doc != null) {
            physicalDocuments.add(doc);
        }
    }

    public List<DigitalDocument> getDigitalDocuments() {
        return digitalDocuments;
    }

    public void setDigitalDocuments(List<DigitalDocument> digitalDocuments) {
        this.digitalDocuments.clear();
        if (digitalDocuments != null) {
            digitalDocuments.forEach(this::addDigitalDocument);
        }
    }

    public void addDigitalDocument(DigitalDocument doc) {
        if (doc != null) {
            digitalDocuments.add(doc);
        }
    }

}
