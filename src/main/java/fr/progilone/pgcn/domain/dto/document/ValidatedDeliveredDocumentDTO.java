package fr.progilone.pgcn.domain.dto.document;


public class ValidatedDeliveredDocumentDTO {

    private String digitalId;
    private String method;
    private String folderPath;
    private String address;
    private String login;
    private String password;
    private String deliveryFolder;
    
    public String getDigitalId() {
        return digitalId;
    }
    public void setDigitalId(String digitalId) {
        this.digitalId = digitalId;
    }
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    public String getFolderPath() {
        return folderPath;
    }
    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getDeliveryFolder() {
        return deliveryFolder;
    }
    public void setDeliveryFolder(String deliveryFolder) {
        this.deliveryFolder = deliveryFolder;
    }
    
    
}
