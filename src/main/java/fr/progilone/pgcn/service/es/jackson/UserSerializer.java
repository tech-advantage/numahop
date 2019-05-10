package fr.progilone.pgcn.service.es.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.user.User_;

import java.io.IOException;

public class UserSerializer extends StdSerializer<User> {

    public UserSerializer() {
        super(User.class);
    }

    @Override
    public void serialize(final User user, final JsonGenerator jgen, final SerializerProvider serializerProvider) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(User_.identifier.getName(), user.getIdentifier());
        jgen.writeStringField(User_.login.getName(), user.getLogin());
        jgen.writeStringField("fullName", user.getFullName());
        jgen.writeEndObject();
    }
}
