package fr.progilone.pgcn.domain.administration;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.exportftpconfiguration.ExportFTPConfiguration;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.*;

@Entity
@Table(name = ExportFTPDeliveryFolder.TABLE_NAME)
public class ExportFTPDeliveryFolder extends AbstractDomainObject {

    public static final String TABLE_NAME = "conf_delivery_folder";

    @Column(name = "name", nullable = false)
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String name;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "conf_export_ftp")
    private ExportFTPConfiguration confExportFtp;

    public ExportFTPConfiguration getConfExportFtp() { return confExportFtp; }

    public void setConfExportFtp(ExportFTPConfiguration confExportFtp) { this.confExportFtp = confExportFtp; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}
