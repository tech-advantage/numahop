package fr.progilone.pgcn.domain.administration;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.exportftpconfiguration.ExportFTPConfiguration;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = ExportFTPDeliveryFolder.TABLE_NAME)
public class ExportFTPDeliveryFolder extends AbstractDomainObject {

    public static final String TABLE_NAME = "conf_delivery_folder";

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "conf_export_ftp")
    private ExportFTPConfiguration confExportFtp;

    public ExportFTPConfiguration getConfExportFtp() {
        return confExportFtp;
    }

    public void setConfExportFtp(final ExportFTPConfiguration confExportFtp) {
        this.confExportFtp = confExportFtp;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
