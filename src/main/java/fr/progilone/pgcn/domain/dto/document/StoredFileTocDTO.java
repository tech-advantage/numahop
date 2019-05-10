package fr.progilone.pgcn.domain.dto.document;

/**
 * Structure simple pour echanges de donnees de la table des matieres.
 * 
 */
public class StoredFileTocDTO {
    
    private String typeToc;
    
    private String orderToc;
    
    private String titreToc;

    /**
     * @return the typeToc
     */
    public String getTypeToc() {
        return typeToc;
    }

    /**
     * @param typeToc the typeToc to set
     */
    public void setTypeToc(String typeToc) {
        this.typeToc = typeToc;
    }

    /**
     * @return the orderToc
     */
    public String getOrderToc() {
        return orderToc;
    }

    /**
     * @param orderToc the orderToc to set
     */
    public void setOrderToc(String orderToc) {
        this.orderToc = orderToc;
    }

    /**
     * @return the titreToc
     */
    public String getTitreToc() {
        return titreToc;
    }

    /**
     * @param titreToc the titreToc to set
     */
    public void setTitreToc(String titreToc) {
        this.titreToc = titreToc;
    }
    
    

}
