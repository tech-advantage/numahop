package fr.progilone.pgcn.domain.filesgestion;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.library.Library;


@Entity
@Table(name = FilesGestionConfig.TABLE_NAME)
public class FilesGestionConfig extends AbstractDomainObject {
    
  public static final String TABLE_NAME = "conf_files_gestion";
    
  @Column(name = "trigger_type")
  private String triggerType;
  
  @Column(name = "delay")
  private int delay;
  
  @Column(name = "export_ftp")
  private boolean useExportFtp;
  
  @Column(name = "destination_dir")
  private String destinationDir;
  
  @Column(name = "delete_master")
  private boolean deleteMaster;
  
  @Column(name = "delete_pdf")
  private boolean deletePdf;
  
  @Column(name = "delete_print")
  private boolean deletePrint;
  
  @Column(name = "delete_view")
  private boolean deleteView;
  
  @Column(name = "delete_thumb")
  private boolean deleteThumb;
  
  @Column(name = "save_master")
  private boolean saveMaster;
  
  @Column(name = "save_pdf")
  private boolean savePdf;
  
  @Column(name = "save_print")
  private boolean savePrint;
  
  @Column(name = "save_view")
  private boolean saveView;
  
  @Column(name = "save_thumb")
  private boolean saveThumb;
  
  @Column(name = "save_aip_sip")
  private boolean saveAipSip;
  
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "library")
  private Library library;


public String getTriggerType() {
    return triggerType;
}


public void setTriggerType(final String triggerType) {
    this.triggerType = triggerType;
}


public int getDelay() {
    return delay;
}


public void setDelay(final int delay) {
    this.delay = delay;
}


public boolean isUseExportFtp() {
    return useExportFtp;
}


public void setUseExportFtp(final boolean useExportFtp) {
    this.useExportFtp = useExportFtp;
}


public String getDestinationDir() {
    return destinationDir;
}


public void setDestinationDir(final String destinationDir) {
    this.destinationDir = destinationDir;
}


public boolean isDeleteMaster() {
    return deleteMaster;
}


public void setDeleteMaster(final boolean deleteMaster) {
    this.deleteMaster = deleteMaster;
}


public boolean isDeletePrint() {
    return deletePrint;
}


public void setDeletePrint(final boolean deletePrint) {
    this.deletePrint = deletePrint;
}


public boolean isDeleteView() {
    return deleteView;
}


public void setDeleteView(final boolean deleteView) {
    this.deleteView = deleteView;
}


public boolean isDeleteThumb() {
    return deleteThumb;
}


public void setDeleteThumb(final boolean deleteThumb) {
    this.deleteThumb = deleteThumb;
}


public boolean isSaveMaster() {
    return saveMaster;
}


public void setSaveMaster(final boolean saveMaster) {
    this.saveMaster = saveMaster;
}


public boolean isSavePrint() {
    return savePrint;
}


public void setSavePrint(final boolean savePrint) {
    this.savePrint = savePrint;
}


public boolean isSaveView() {
    return saveView;
}


public void setSaveView(final boolean saveView) {
    this.saveView = saveView;
}


public boolean isSaveThumb() {
    return saveThumb;
}


public void setSaveThumb(final boolean saveThumb) {
    this.saveThumb = saveThumb;
}


public boolean isDeletePdf() {
    return deletePdf;
}


public void setDeletePdf(final boolean deletePdf) {
    this.deletePdf = deletePdf;
}


public boolean isSavePdf() {
    return savePdf;
}


public void setSavePdf(final boolean savePdf) {
    this.savePdf = savePdf;
}


public boolean isSaveAipSip() {
    return saveAipSip;
}


public void setSaveAipSip(final boolean saveAipSip) {
    this.saveAipSip = saveAipSip;
}


public Library getLibrary() {
    return library;
}


public void setLibrary(final Library library) {
    this.library = library;
}
  


}
