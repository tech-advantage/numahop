package fr.progilone.pgcn.domain.ocrlangconfiguration;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import fr.progilone.pgcn.domain.AbstractDomainObject;

/**
 * Langage utilisé pour l'OCRisation.
 * @author ert
 *
 */

@Entity
@Table(name = OcrLanguage.TABLE_NAME)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class OcrLanguage extends AbstractDomainObject {
    
    public static final String TABLE_NAME = "conf_language_ocr";
    
    @Column(name = "code")
    private String code;
    
    @Column(name = "label")
    private String label;

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

}
