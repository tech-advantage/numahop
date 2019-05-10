package fr.progilone.pgcn.web.websocket.dto;

public class NotificationDTO {

    public enum NotificationCode {
        INDEX_BORROWER_TERMINATED,
        INDEX_BORROWER_ERROR,
        INDEX_CATALOG_TERMINATED,
        INDEX_CATALOG_ERROR,
        INDEX_CONCEPTS_TERMINATED,
        INDEX_CONCEPTS_ERROR,
        SYNCHRO_BORROWERS_TERMINATED,
        SYNCHRO_BORROWERS_ERROR,
        SYNCHRO_CATALOG_TERMINATED,
        SYNCHRO_CATALOG_ERROR,
        SYNCHRO_CONCEPTS_TERMINATED,
        SYNCHRO_CONCEPTS_ERROR;
    }

    public enum NotificationLevel {
        INFO, ERROR, WARN, SUCCESS;
    }

    private String userLogin;

    private NotificationCode code;

    private NotificationLevel level;

    public NotificationDTO(final String userLogin, final NotificationCode code, final NotificationLevel level) {
        super();
        this.userLogin = userLogin;
        this.code = code;
        this.level = level;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(final String userLogin) {
        this.userLogin = userLogin;
    }

    public NotificationCode getCode() {
        return code;
    }

    public void setCode(final NotificationCode code) {
        this.code = code;
    }

    public NotificationLevel getLevel() {
        return level;
    }

    public void setLevel(final NotificationLevel level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "NotificationDTO{userLogin='" + userLogin + "', code='" + code + "', level='\" + level + \"'}";
    }

}
