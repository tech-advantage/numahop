package fr.progilone.pgcn.domain.storage;

import fr.progilone.pgcn.service.util.FileUtils.CheckSumType;

/**
 * Permet d'optimiser les exports en ne chargeant les dépendances qu'une fois
 * et évite de rechercher les fichiers uniquement pour le checksum
 * 
 * @author jbrunet
 * Créé le 15 févr. 2017
 */
public class CheckSummedStoredFile
{
    public CheckSummedStoredFile() {
        
    }
    
    private StoredFile storedFile;
    
    private CheckSumType checkSumType;
    
    private String checkSum;

    public  StoredFile getStoredFile() {
        return storedFile;
    }

    public  void setStoredFile(StoredFile storedFile) {
        this.storedFile = storedFile;
    }

    public  CheckSumType getCheckSumType() {
        return checkSumType;
    }

    public  void setCheckSumType(CheckSumType checkSumType) {
        this.checkSumType = checkSumType;
    }

    public  String getCheckSum() {
        return checkSum;
    }

    public  void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }
    
    
}
