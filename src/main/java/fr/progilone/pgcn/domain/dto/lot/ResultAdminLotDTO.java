package fr.progilone.pgcn.domain.dto.lot;

/**
 * DTO minimaliste pour retour outils admin.
 *
 * @author progilone
 */
public class ResultAdminLotDTO {

    private String identifier;
    private String msg;
    private boolean success;

    public ResultAdminLotDTO() {
    }

    public ResultAdminLotDTO(final String identifier,
                             final String msg,
                             final boolean success) {
        super();
        this.identifier = identifier;
        this.msg = msg;
        this.success = success;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(final boolean success) {
        this.success = success;
    }
}
