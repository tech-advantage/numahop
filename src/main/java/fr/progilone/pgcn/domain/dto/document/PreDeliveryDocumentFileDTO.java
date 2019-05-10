package fr.progilone.pgcn.domain.dto.document;

import fr.progilone.pgcn.domain.dto.AbstractDTO;

/**
 * Fichier avec rôle
 * 
 * @author jbrunet
 * Créé le 10 mars 2017
 */
public class PreDeliveryDocumentFileDTO extends AbstractDTO {

    private String name;
    private FileRoleEnum role;

    public PreDeliveryDocumentFileDTO(final String name, final FileRoleEnum role) {
        this.name = name;
        this.role = role;
    }
      
    public PreDeliveryDocumentFileDTO() {
    }
    
    public String getName() {
        return name;
    }


    public void setName(final String name) {
        this.name = name;
    }


    public FileRoleEnum getRole() {
        return role;
    }


    public void setRole(final FileRoleEnum role) {
        this.role = role;
    }


    public enum FileRoleEnum {
        NO_ROLE,
        OTHER,
        METS,
        EXCEL,
        PDF_MULTI
    }
}
