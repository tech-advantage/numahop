package fr.progilone.pgcn.domain.es.user;

import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.user.User.Category;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Usager
 */
public class EsUser {

    @Field(type = FieldType.Keyword)
    private String identifier;

    /**
     * identifiant
     */
    @Field(type = FieldType.Keyword)
    private String login;

    @Field(type = FieldType.Keyword)
    private String fullName;

    public static EsUser from(final User user) {
        final EsUser esUser = new EsUser();
        esUser.setIdentifier(user.getIdentifier());
        esUser.setLogin(user.getLogin());

        final StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(user.getFirstname())) {
            builder.append(user.getFirstname());
        }
        if (StringUtils.isNotBlank(user.getSurname())) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(user.getSurname());
        }
        // Pour les prestataires, on ajoute le nom de la société, et on met le nom entre parenthèses
        if (StringUtils.isNotBlank(user.getCompanyName()) && user.getCategory() == Category.PROVIDER) {
            if (builder.length() > 0) {
                builder.insert(0, " (").insert(0, user.getCompanyName()).append(')');
            }
        }
        esUser.setFullName(builder.toString());

        return esUser;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(final String login) {
        this.login = login;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

}
