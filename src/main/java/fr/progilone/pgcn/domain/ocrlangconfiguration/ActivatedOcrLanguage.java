package fr.progilone.pgcn.domain.ocrlangconfiguration;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.progilone.pgcn.domain.AbstractDomainObject;


@Entity
@Table(name = ActivatedOcrLanguage.TABLE_NAME)
public class ActivatedOcrLanguage extends AbstractDomainObject {

    public static final String TABLE_NAME = "conf_activated_ocr_lang";
    
    /**
     * Entités liés
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ocr_lang_configuration")
    private OcrLangConfiguration ocrLangConfiguration;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ocr_language")
    private OcrLanguage ocrLanguage;
    
    

    public OcrLangConfiguration getOcrLangConfiguration() {
        return ocrLangConfiguration;
    }

    public void setOcrLangConfiguration(final OcrLangConfiguration ocrLangConfiguration) {
        this.ocrLangConfiguration = ocrLangConfiguration;
    }

    public OcrLanguage getOcrLanguage() {
        return ocrLanguage;
    }

    public void setOcrLanguage(final OcrLanguage ocrLanguage) {
        this.ocrLanguage = ocrLanguage;
    }
    
}
